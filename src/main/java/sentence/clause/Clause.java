package sentence.clause;

import com.atilika.kuromoji.ipadic.Tokenizer;
import exception.CauseException;
import sentence.phrase.Phrase;
import sentence.Word;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文
 */
public class Clause {

    private static final String PARAGRAPH_CHAR = "/";

    List<Phrase> phraseList;

    private List<Phrase> subjects = null;

    private List<Phrase> predicate = null;

    public Clause(String clause) {
        List<Word> list = Clause.createWordList(clause);

        this.phraseList = Collections.unmodifiableList(Phrase.convertParagraph(list));

        analyse(phraseList);
    }

    private void analyse(List<Phrase> phraseList) {
        subjects = new ArrayList<>();

        predicate = analysePredicate(phraseList);

        var skeleton = analyseSkeleton(phraseList);

        subjects = skeleton.stream()
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .filter(sk -> sk.subject != null)
                .map(sk -> sk.subject).collect(Collectors.toList());

        analyseSimilarity(phraseList);
    }


    private Set<List<Skeleton>> analyseSkeleton(List<Phrase> phraseList) {
        Set<List<Skeleton>> skeletonsSet = new HashSet<>();
        List<Skeleton> skeletons = new ArrayList<>();
        skeletonsSet.add(skeletons);
        var target = new Skeleton();
        skeletons.add(target);
        for (ListIterator<Phrase> it = phraseList.listIterator(phraseList.size()); it.hasPrevious(); ) {
            var paragraph = it.previous();

            if (paragraph.existReadingPoint()) {
                if (isParfectSkeletons(skeletons)) {
                    // 並列の読点と判定する。
                    skeletons = new ArrayList<>();
                    target = new Skeleton();
                    skeletons.add(target);
                    skeletonsSet.add(skeletons);
                }

                if (target.subject == null && target.predicate != null) {
                    var index = skeletons.indexOf(target);
                    if (0 < index) {
                        target = skeletons.get(index - 1);
                    }
                }
            }

            if (Phrase.WorkType.SUBJECT.equals(paragraph.getWorkType())) {
                if (target.subject == null) {
                    target.subject = paragraph;
                } else {
                    target = new Skeleton();
                    target.subject = paragraph;
                    skeletons.add(target);
                }
            } else if (Phrase.WorkType.PREDICATE.equals(paragraph.getWorkType())) {
                if (target.predicate == null) {
                    target.predicate = paragraph;
                } else {
                    target = new Skeleton();
                    target.predicate = paragraph;
                    skeletons.add(target);
                }
            }

            if (target.isPerfect()) {
                var index = skeletons.indexOf(target) - 1;
                while (0 < index && skeletons.get(index).isPerfect()) {
                    index--;
                }
                if (0 < index) {
                    target = skeletons.get(index);
                }
            }
        }

        return skeletonsSet;
    }

    /**
     * 全文が主語述語のセットになっているか判定する
     *
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
     * @param phraseList 文節リスト
     */
    private void analyseSimilarity(List<Phrase> phraseList) {
        Modification lastSimilar = phraseList.stream().reduce(new Modification(), (result, paragraph) -> {

            if (Phrase.WorkType.SUBJECT.equals(paragraph.getWorkType())) {
                paragraph.setSimilarities(result.taigen);
                result.taigen = new ArrayList<>();

                // 句読点があれば、連体・連用関係なく修飾子として扱う
                if (paragraph.existReadingPoint() || paragraph.existPunctuationMark()) {
                    paragraph.setSimilarities(result.yougen);
                    result.yougen = new ArrayList<>();
                }

                return result;
            } else if (Phrase.WorkType.PREDICATE.equals(paragraph.getWorkType())) {
                if (paragraph.existReadingPoint() || paragraph.existPunctuationMark()) {
                    List<Phrase> temp = new ArrayList<>();
                    temp.addAll(result.taigen);
                    temp.addAll(result.yougen);
                    paragraph.setSimilarities(temp);
                    result.taigen = new ArrayList<>();
                    result.yougen = new ArrayList<>();
                    return result;
                }

                List<Phrase> list = result.yougen.stream().filter(p -> !p.isConsecutiveForm()).collect(Collectors.toList());
                if (!list.isEmpty()) {
                    paragraph.setSimilarities(list);
                    result.yougen = new ArrayList<>();
                }
                return result;
            } else {
                paragraph.setWorkType(Phrase.WorkType.MODIFIER);

                if (paragraph.existAdverbs()) {
                    result.yougen.add(paragraph);
                } else if (paragraph.existNoun()) {
                    List<Phrase> temp = new ArrayList<>();
                    temp.addAll(result.taigen);
                    temp.addAll(result.yougen);
                    paragraph.setSimilarities(temp);
                    result.taigen = new ArrayList<>();
                    result.yougen = new ArrayList<>();

                    if (paragraph.isConjugationForm()) {
                        result.yougen.add(paragraph);
                    } else if (paragraph.isConsecutiveForm()) {
                        result.taigen.add(paragraph);
                    }
                } else if (result.yougen.stream().allMatch(Phrase::existAdverbs)) {
                    paragraph.setSimilarities(result.yougen);
                    result.yougen = new ArrayList<>();

                    if (paragraph.isConjugationForm()) {
                        result.yougen.add(paragraph);
                    } else if (paragraph.isConsecutiveForm()) {
                        result.taigen.add(paragraph);
                    }
                } else {
                    result.taigen.add(paragraph);
                }

                return result;
            }
        }, (Modification result1, Modification result2) -> null);

        // 最後の修飾子設定
        if (!lastSimilar.taigen.isEmpty() || !lastSimilar.yougen.isEmpty()) {
            List<Phrase> temp = lastSimilar.taigen;
            temp.addAll(lastSimilar.yougen);
            phraseList.get(phraseList.size() - 1).setSimilarities(temp);
        }
    }

    /**
     * 述語の分析
     * <p>
     * 文レベルでの分析を行う。
     *
     * @param phraseList 文節リスト
     * @return 述語リスト
     */
    private List<Phrase> analysePredicate(List<Phrase> phraseList) {
        List<Phrase> list = phraseList.stream().filter(paragraph -> Phrase.WorkType.PREDICATE.equals(paragraph.getWorkType())).collect(Collectors.toList());
        if (list == null || list.size() == 0) {
            throw new CauseException("predicate is not exsist");
        }

        list.stream().forEach(paragraph -> {
            var index = phraseList.indexOf(paragraph);

            if (index < 0) {
                return;
            }

            if (index == phraseList.size() - 1) {
                // 述語として確定する
                return;
            }

            Phrase nextPhrase = phraseList.get(index + 1);
            if (nextPhrase.getWordList().stream().anyMatch(word -> "こと".equals(word.getSurface()))) {
                // 「こと」になっているので、用途としては名詞になる。そのため、述語として扱わない
                paragraph.setWorkType(null);
            } else if (nextPhrase.getWordList().stream().anyMatch(word -> "いる".equals(word.getSurface()))) {
                // 「いる」の場合、セットで述語の動きをするので、「いる」を述語として採用して、今の単語を無効化する。
                paragraph.setWorkType(null);
            }
        });

        // 不要な述語を無効化
        list.stream().filter(paragraph -> {
            if (paragraph.existReadingPoint()) {
                return false;
            } else if (paragraph.existPunctuationMark()) {
                return false;
            }
            return true;
        }).forEach(phrase -> {
            phrase.setWorkType(null);
        });

        list = phraseList.stream().filter(paragraph -> Phrase.WorkType.PREDICATE.equals(paragraph.getWorkType())).collect(Collectors.toList());
        if (list == null || list.size() == 0) {
            throw new CauseException("predicate is not exsist");
        }

        return list;
    }

    /**
     * 文節リストを解析して主語を見つける
     *
     * @param phraseList 文節リスト
     * @return 主語がない場合は、empty
     */
    private Optional<List<Phrase>> analyseSubject(List<Phrase> phraseList) {
        List<Phrase> list = phraseList.stream().filter(paragraph -> Phrase.WorkType.SUBJECT.equals(paragraph.getWorkType())).collect(Collectors.toList());

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
    public List<Phrase> getPhraseList() {
        return phraseList;
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
    public List<Phrase> getSubjects() {
        return subjects;
    }

    /**
     * 述語取得
     *
     * @return 述語
     */
    public List<Phrase> getPredicate() {
        return predicate;
    }

    public List<Phrase> getSimilarities() {
        return phraseList.stream()
                .filter(paragraph -> Phrase.WorkType.MODIFIER.equals(paragraph.getWorkType()))
                .collect(Collectors.toList());
    }

    /**
     * 文節分け文字列表現
     *
     * 各文節を"/"で連結した文字列を返す
     *
     * @return 文節分けの文字列表現
     */
    public String phraseSplitStr() {
        return phraseList.stream().map(Phrase::getPharseStr).collect(Collectors.joining(PARAGRAPH_CHAR));
    }

    public String getPhraseStr() {
        return phraseList.stream().map(Phrase::getPharseStr).collect(Collectors.joining());
    }

    public boolean existConjunction() {
        return phraseList.stream().anyMatch(Phrase::existConjunction);
    }

    /**
     * 独立語の存在判定
     * @return
     */
    public boolean existDokuritsu() {
        return phraseList.stream().anyMatch(Phrase::existDokuritsu);
    }

    /**
     * 独立語を含む文節リストの取得
     *
     * @return
     */
    public List<Phrase> getDokuritsuPhraseList() {
        return phraseList.stream().filter(Phrase::existDokuritsu).collect(Collectors.toList());
    }

    /**
     * 指示語を含む文節リストの取得
     * @return 指示語リスト
     */
    public List<Phrase> getInstructionPhraseList() {
        return phraseList.stream().filter(Phrase::existInstruction).collect(Collectors.toList());
    }
}

/**
 * 修飾子の情報
 *
 * 修飾子の情報を一時的に保持するためのクラス
 */
class Modification {

    List<Phrase> taigen;

    List<Phrase> yougen;

    Modification() {
        taigen = new ArrayList<>();
        yougen = new ArrayList<>();
    }
}