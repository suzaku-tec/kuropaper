package sentence.paragraph;

import sentence.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文節
 */
public class Paragraph {

    private List<Word> wordList;

    private WorkType workType;

    /**
     * 修飾語リスト
     */
    private List<Paragraph> similarities = Collections.emptyList();

    private Paragraph() {
        wordList = new ArrayList<>();
    }

    private void addWord(Word word) {
        this.wordList.add(word);

        try {
            workType = analyse();
        } catch (Exception e) {
            workType = null;
        }
    }

    public static List<Paragraph> convertParagraph(List<Word> words) {
        List<Paragraph> list = new ArrayList<>();
        Paragraph end = words.stream().reduce(
                new Paragraph(), // 文節情報を溜めるオブジェクトを生成
                (Paragraph p, Word word) -> {
                    if (isSpritParagraph(word)) {
                        if (word.isIndependence()) {
                            if (p.getWordList().stream().anyMatch(Word::isIndependence)) {
                                list.add(p);
                                p = new Paragraph();
                            }
                        } else {
                            p.addWord(word);
                            list.add(p);
                            return new Paragraph();
                        }
                    } else if (word.getPartOfSpeechLevel2().equals("読点") || word.getPartOfSpeechLevel2().equals("句点")) {
                        if (p.getParagraph().isEmpty()) {
                            // 前の文節の一部として扱う
                            list.get(list.size() - 1).addWord(word);
                            return p;
                        }
                    }
                    p.addWord(word);
                    return p;
                }, (o1, o2) -> null);

        if (!end.getParagraph().isEmpty()) {
            list.add(end);
        }

        return list;
    }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    /**
     * 文節の区切り判定
     *
     * @param word 単語
     * @return true:文節の区切り false:文節の区切りではない
     */
    private static boolean isSpritParagraph(Word word) {

        if (word.getPartOfSpeechLevel1().equals("助詞")) {
            return true;
        }

        if (word.getPartOfSpeechLevel1().equals("副詞") && word.getPartOfSpeechLevel2().equals("助詞類接続")) {
            return true;
        }

        if (word.isIndependence()) {
            return true;
        }

        return false;
    }

    public List<Word> getWordList() {
        return Collections.unmodifiableList(this.wordList);
    }

    public String getParagraph() {
        return wordList.stream().map(word -> word.getSurface()).collect(Collectors.joining());
    }

    private WorkType analyse() throws Exception {
        return WorkType.getEnum(wordList);
    }

    /**
     * 主語判定
     *
     * @param wordList 文節の文言
     * @return 判定結果
     */
    public static boolean isSubject(List<Word> wordList) {
        return wordList.stream().filter(Word::isParticle).anyMatch(word -> {
            if (word.getSurface().equals("は") || word.getSurface().equals("が") || word.getSurface().equals("も") && !word.getPartOfSpeechLevel1().equals("動詞")) {
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * 述語判定
     *
     * @param wordList 文節の文言
     * @return 判定結果
     */
    public static boolean isPredicate(List<Word> wordList) {
        return wordList.stream().anyMatch(word -> {
            if (word.getPartOfSpeechLevel1().equals("動詞")) {
                return true;
            } else if (word.getPartOfSpeechLevel1().equals("助動詞")) {
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * 単語の活用タイプ
     */
    public enum WorkType {
        SUBJECT("主語"),
        PREDICATE("述語"),
        MODIFIER("修飾語"),
        CONNECTION_WORD("接続語"),
        INDEPENDENT_WORD("独立語"),;

        public String japanease;

        private WorkType(String japanease) {
            this.japanease = japanease;
        }

        public static WorkType getEnum(List<Word> wordList) throws Exception {
            if (Paragraph.isSubject(wordList)) {
                return WorkType.SUBJECT;
            }

            if (Paragraph.isPredicate(wordList)) {
                return WorkType.PREDICATE;
            }

            throw new Exception("該当なし");
        }
    }

    /**
     * 修飾語の取得
     * <p>
     * この文節にかかっている修飾語を取得する。
     * 存在しない場合、空のリストが返される
     *
     * @return 修飾語リスト
     */
    public List<Paragraph> getSimilarities() {
        return similarities;
    }

    /**
     * 修飾語の設定
     * <p>
     * この文節にかかっている修飾語を設定する
     *
     * @param similarities 修飾語
     */
    public void setSimilarities(List<Paragraph> similarities) {
        this.similarities = similarities;
    }

    /**
     * 副詞の存在判定
     *
     * @return true:副詞あり false:副詞なし
     */
    public boolean existAdverbs() {
        return wordList.stream().anyMatch(Word::isAdverbs);
    }

    public boolean existReadingPoint() {
        return wordList.stream().anyMatch(Word::isReadingPoint);
    }

    public boolean isConsecutiveForm() {
        return wordList.stream().anyMatch(word -> {
            if("連体詞".equals(word.getPartOfSpeechLevel1())) {
                return  true;
            }

            if("連体化".equals(word.getPartOfSpeechLevel2())) {
                return true;
            }

            return false;
        });
    }
}
