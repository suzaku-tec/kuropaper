package sentence.clause;

import com.atilika.kuromoji.ipadic.Tokenizer;
import exception.CauseException;
import exception.ParagraphException;
import sentence.paragraph.Paragraph;
import sentence.Word;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文
 */
public class Clause {

    List<Paragraph> paragraphList;

    private List<Paragraph> subjects = null;

    private Paragraph predicate = null;

    public Clause(String clause) throws ParagraphException {
        List<Word> list = Clause.createWordList(clause);

        this.paragraphList = Collections.unmodifiableList(Paragraph.convertParagraph(list));

        analyse(paragraphList);
    }

    private void analyse(List<Paragraph> paragraphList) {
        subjects = new ArrayList<>();

        predicate = analysePredicate(paragraphList);

        var skeleton = analyseSkeleton(paragraphList);

        subjects = skeleton.stream()
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .filter(sk -> sk.subject != null)
                .map(sk -> sk.subject).collect(Collectors.toList());

        paragraphList.stream()
                .filter(paragraph -> Paragraph.WorkType.PREDICATE.equals(paragraph.getWorkType()))
                .filter(paragraph -> !predicate.equals(paragraph))
                .forEach(paragraph -> paragraph.setWorkType(null));

        analyseSimilarity(paragraphList);
    }

    private Set<List<Skeleton>> analyseSkeleton(List<Paragraph> paragraphList) {
        Set<List<Skeleton>> skeletonsSet = new HashSet<>();
        List<Skeleton> skeletons = new ArrayList<>();
        skeletonsSet.add(skeletons);
        var target = new Skeleton();
        skeletons.add(target);
        for (ListIterator<Paragraph> it = paragraphList.listIterator(paragraphList.size()); it.hasPrevious(); ) {
            var paragraph = it.previous();

            if(paragraph.existReadingPoint()) {
                if(isParfectSkeletons(skeletons)) {
                    // 並列の読点と判定する。
                    skeletons = new ArrayList<>();
                    target = new Skeleton();
                    skeletons.add(target);
                    skeletonsSet.add(skeletons);
                }

                if(target.subject == null && target.predicate != null) {
                    var index = skeletons.indexOf(target);
                    if(0 < index) {
                        target = skeletons.get(index - 1);
                    }
                }
            }

            if(Paragraph.WorkType.SUBJECT.equals(paragraph.getWorkType())) {
                if(target.subject == null) {
                    target.subject = paragraph;
                } else {
                    target = new Skeleton();
                    target.subject = paragraph;
                    skeletons.add(target);
                }
            } else if(Paragraph.WorkType.PREDICATE.equals(paragraph.getWorkType())) {
                if(target.predicate == null) {
                    target.predicate = paragraph;
                } else {
                    target = new Skeleton();
                    target.predicate = paragraph;
                    skeletons.add(target);
                }
            }

            if(target.isPerfect()) {
                var index = skeletons.indexOf(target) - 1;
                while (0 < index && skeletons.get(index).isPerfect()) {
                    index--;
                }
                if(0 < index) {
                    target = skeletons.get(index);
                }
            }
        }

        return skeletonsSet;
    }

    /**
     * 全文が主語述語のセットになっているか判定する
     * @param skeletons 主語述語セットのリスト
     * @return 判定結果
     */
    private boolean isParfectSkeletons(List<Skeleton> skeletons) {
        return skeletons.stream().parallel().allMatch(Skeleton::isPerfect);
    }

    /**
     * 修飾語の設定
     * <p>
     * フェールセーフではない。。。
     *
     * @param paragraphList 文節リスト
     */
    private void analyseSimilarity(List<Paragraph> paragraphList) {
        ArrayList<Paragraph> lastSimilarit = paragraphList.stream().reduce(new ArrayList<Paragraph>(), (result, paragraph) -> {
            if (Paragraph.WorkType.SUBJECT.equals(paragraph.getWorkType()) || Paragraph.WorkType.PREDICATE.equals(paragraph.getWorkType())) {
                if (result.isEmpty()) {
                    return result;
                } else {
                    paragraph.setSimilarities(result);
                    return new ArrayList<>();
                }
            } else {
                paragraph.setWorkType(Paragraph.WorkType.MODIFIER);

                if (paragraph.isConsecutiveForm() || !paragraph.existAdverbs()) {
                    paragraph.setSimilarities(result);
                    result = new ArrayList<>();
                    result.add(paragraph);
                } else {
                    result.add(paragraph);
                }

                return result;
            }
        }, (result1, result2) -> {
            result1.addAll(result2);
            return result1;
        });

        // 最後の修飾子設定
        if(!lastSimilarit.isEmpty()) {
            paragraphList.get(paragraphList.size() -1).setSimilarities(lastSimilarit);
        }
    }

    /**
     * 述語の分析
     *
     * 文レベルでの分析を行う。
     *
     * @param paragraphList 文節リスト
     * @return 述語リスト
     */
    private Paragraph analysePredicate(List<Paragraph> paragraphList) {
        List<Paragraph> list = paragraphList.stream().filter(paragraph -> Paragraph.WorkType.PREDICATE.equals(paragraph.getWorkType())).collect(Collectors.toList());
        if (list == null || list.size() == 0) {
            throw new CauseException("predicate is not exsist");
        }

        list.stream().forEach(paragraph -> {
            var index = paragraphList.indexOf(paragraph);

            if(index < 0) {
                return;
            }

            if(index == paragraphList.size() - 1) {
                // 述語として確定する
                return;
            }

            Paragraph nextParagraph = paragraphList.get(index + 1);
            if(nextParagraph.getWordList().stream().anyMatch(word -> "こと".equals(word.getSurface()))) {
                // 「こと」になっているので、用途としては名詞になる。そのため、述語として扱わない
                paragraph.setWorkType(null);
            } else if(nextParagraph.getWordList().stream().anyMatch(word -> "いる".equals(word.getSurface()))) {
                // 「いる」にの場合、セットで述語の動きをするので、「いる」を述語として採用して、今の単語を無効化する。
                paragraph.setWorkType(null);
            }
        });

        list = paragraphList.stream().filter(paragraph -> Paragraph.WorkType.PREDICATE.equals(paragraph.getWorkType())).collect(Collectors.toList());
        if (list == null || list.size() == 0) {
            throw new CauseException("predicate is not exsist");
        }

        return list.get(list.size() - 1);
    }

    /**
     * 文節リストを解析して主語を見つける
     *
     * @param paragraphList 文節リスト
     * @return 主語がない場合は、empty
     */
    private Optional<List<Paragraph>> analyseSubject(List<Paragraph> paragraphList) {
        List<Paragraph> list = paragraphList.stream().filter(paragraph -> Paragraph.WorkType.SUBJECT.equals(paragraph.getWorkType())).collect(Collectors.toList());

        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(list);
    }

    /**
     * 文節リスト
     * <p>
     * 文の文節リストを返す。
     *
     * @return 文節リスト(変更不可)
     */
    public List<Paragraph> getParagraphList() {
        return paragraphList;
    }

    /**
     * Wordリスト作成
     * <p>
     * 形態素解析した結果をWordクラスにラッピングして、リスト化して返す。
     *
     * @param clause 文
     * @return Wordリスト
     */
    public static List<Word> createWordList(String clause) {
        Tokenizer tokenizer = new Tokenizer();
        List<Word> list = tokenizer.tokenize(clause).stream().map(Word::new).collect(Collectors.toList());
        return list;
    }

    /**
     * 主語取得
     *
     * @return 主語
     */
    public List<Paragraph> getSubjects() {
        return subjects;
    }

    /**
     * 述語取得
     *
     * @return 述語
     */
    public Paragraph getPredicate() throws Exception {
        return predicate;
    }

    public List<Paragraph> getSimilarities() {
        return paragraphList.stream()
                .filter(paragraph -> Paragraph.WorkType.MODIFIER.equals(paragraph.getWorkType()))
                .collect(Collectors.toList());
    }
}
