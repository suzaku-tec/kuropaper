package sentence.phrase;

import sentence.word.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文節
 */
public class Phrase {

    private List<Word> wordList;

    private WorkType workType;

    /**
     * 修飾語リスト
     */
    private List<Phrase> similarities = Collections.emptyList();

    /**
     * 指示語の対象
     */
    private List<Word> tartgetList = Collections.emptyList();

    private Phrase() {
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

    /**
     * 文節分け
     *
     * @param words 単語
     * @return 文節リスト
     */
    public static List<Phrase> convertParagraph(List<Word> words) {
        List<Phrase> list = new ArrayList<>();
        Phrase end = words.stream().reduce(
                new Phrase(), // 文節情報を溜めるオブジェクトを生成
                (Phrase p, Word word) -> {
                    if (isSpritParagraph(word)) {
                        if (word.isIndependence()) {
                            if (word.isNoun() && p.getWordList().stream().allMatch(Word::isNoun)) {
                                // すべて名詞の場合、複数の名詞が連結して名詞となっているだけなので、一緒の文節として使う。
                            } else if (p.getWordList().stream().anyMatch(Word::isIndependence)) {
                                list.add(p);
                                p = new Phrase();
                            }
                        } else {
                            p.addWord(word);
                            list.add(p);
                            return new Phrase();
                        }
                    } else if (word.getPartOfSpeechLevel2().equals("読点") || word.getPartOfSpeechLevel2().equals("句点")) {
                        if (p.getPharseStr().isEmpty()) {
                            // 前の文節の一部として扱う
                            list.get(list.size() - 1).addWord(word);
                            return p;
                        }
                    }
                    p.addWord(word);
                    return p;
                }, (o1, o2) -> null);

        if (!end.getPharseStr().isEmpty()) {
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

    public String getPharseStr() {
        return wordList.stream().map(Word::getSurface).collect(Collectors.joining());
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
    private static boolean isSubject(List<Word> wordList) {
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
    private static boolean isPredicate(List<Word> wordList) {
        return wordList.stream().anyMatch(word -> {
            if (word.getPartOfSpeechLevel1().equals("動詞")) {
                return true;
            } else if (word.getPartOfSpeechLevel1().equals("助動詞")) {
                return true;
            } else if (word.getPartOfSpeechLevel1().equals("形容詞")) {
                return true;
            } else if (word.getPartOfSpeechLevel1().equals("形容動詞")) {
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

        WorkType(String japanease) {
            this.japanease = japanease;
        }

        public static WorkType getEnum(List<Word> wordList) throws Exception {
            if (Phrase.isSubject(wordList)) {
                return WorkType.SUBJECT;
            }

            if (Phrase.isPredicate(wordList)) {
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
    public List<Phrase> getSimilarities() {
        return similarities;
    }

    /**
     * 修飾語の設定
     * <p>
     * この文節にかかっている修飾語を設定する
     *
     * @param similarities 修飾語
     */
    public void setSimilarities(List<Phrase> similarities) {
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

    /**
     * 読点存在判定
     *
     * @return 判定結果 true:存在 false:存在しない
     */
    public boolean existReadingPoint() {
        return wordList.stream().anyMatch(Word::isReadingPoint);
    }

    /**
     * 句点の存在判定
     *
     * @return 判定結果 true:存在 false:存在しない
     */
    public boolean existPunctuationMark() {
        return wordList.stream().anyMatch(Word::isPunctuationMark);
    }

    /**
     * 連体詞判定
     *
     * @return true:連体詞 false:連体詞以外
     */
    public boolean isConsecutiveForm() {
        return wordList.stream().anyMatch(word -> {
            if ("連体詞".equals(word.getPartOfSpeechLevel1())) {
                return true;
            }

            if ("連体化".equals(word.getPartOfSpeechLevel2())) {
                return true;
            }

            if ("助詞".equals(word.getPartOfSpeechLevel1())) {
                return true;
            }

            return false;
        });
    }

    /**
     * 連用形か判定する
     *
     * @return true:連用形 false:連用形以外
     */
    public boolean isConjugationForm() {
        return wordList.stream().anyMatch(word -> {
            if ("連用形".equals(word.getConjugationForm())) {
                return true;
            }

            if ("動詞".equals(word.getPartOfSpeechLevel1())) {
                return true;
            }

            if ("形容詞".equals(word.getPartOfSpeechLevel2())) {
                return true;
            }

            if ("形容動詞".equals(word.getPartOfSpeechLevel1())) {
                return true;
            }

            if ("助動詞".equals(word.getPartOfSpeechLevel1())) {
                return true;
            }

            if ("格助詞".equals(word.getPartOfSpeechLevel2()) && (
                    "を".equals(word.getSurface()) ||
                            "に".equals(word.getSurface()) ||
                            "へ".equals(word.getSurface()) ||
                            "で".equals(word.getSurface()))) {
                return true;
            }

            return false;
        });
    }

    /**
     * 用言判定
     *
     * @return true:用言 false:用言ではない
     */
    public boolean isYougen() {
        return wordList.stream().anyMatch(word -> {
            if ("動詞".equals(word.getPartOfSpeechLevel1())) {
                return true;
            }

            if ("形容詞".equals(word.getPartOfSpeechLevel2())) {
                return true;
            }

            if ("形容動詞".equals(word.getPartOfSpeechLevel1())) {
                return true;
            }

            if ("助動詞".equals(word.getPartOfSpeechLevel1())) {
                return true;
            }

            return false;
        });
    }

    /**
     * 体言判定
     *
     * @return 判定結果 true:体言 false:体言ではない
     */
    public boolean isTaigen() {
        return wordList.stream().allMatch(word -> {
            if (word.isNoun()) {
                return true;
            }

            if (word.isParticle()) {
                return true;
            }

            return false;
        });
    }

    /**
     * 動詞の存在判定
     *
     * @return 判定結果 true:存在 false:存在しない
     */
    public boolean existsVerb() {
        return wordList.stream().anyMatch(Word::isVerb);
    }

    /**
     * 名詞の存在判定
     *
     * @return 判定結果 true:存在 false:存在しない
     */
    public boolean existNoun() {
        return wordList.stream().anyMatch(Word::isNoun);
    }

    public boolean existConjunction() {
        return wordList.stream().anyMatch(Word::isConjunction);
    }

    public boolean existDokuritsu() {
        return wordList.stream().anyMatch(Word::isDokuritsu);
    }

    /**
     * 指示語の存在判定
     * @return 判定結果 true:存在 false:存在しない
     */
    public boolean existInstruction() {
        return wordList.stream().anyMatch(Word::isInstruction);
    }

    public void setTartgetList(List<Word> wordList) {
        tartgetList = wordList;
    }

    public List<Word> getTartgetList() {
        return tartgetList;
    }

    /**
     * ？マークの存在判定
     * @return 判定結果 true:存在 false:存在しない
     */
    public boolean existQuestionWord() {
        return wordList.stream().anyMatch(Word::isQuestion);
    }

    /**
     * 感動詞の存在判定
     * @return 判定結果 true:存在 false:存在しない
     */
    public boolean existExcitement() {
        return wordList.stream().anyMatch(Word::isExcitement);
    }

    public boolean existOrder() {
        return wordList.stream().anyMatch(Word::isOrder);
    }

    public String getAllFeatures() {
        String str = wordList.stream().map(word -> word.getSurface() + ":" + word.getAllFeatures()).collect(Collectors.joining("/ "));

        String detail = " - ";
        if(isSubject(wordList)) {
            detail = " S ";
        } else if(isPredicate(wordList)) {
            detail = " P ";
        }

        return getPharseStr() + detail + "{ " + str + " }";
    }

}
