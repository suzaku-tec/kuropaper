package sentence.clause;

import exception.ParagraphException;
import org.junit.Test;
import sentence.phrase.Phrase;
import sentence.Word;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ClauseTest {

    private static final String PARAGRAPH_CHAR = "/";

    @Test
    public void testParagraph1() throws ParagraphException {
        Clause c = new Clause("山路を登りながら、こう考えた。");

        final String paragraphListStr = getParagraphListStr(c);
        assertEquals("文節数不正 : " + paragraphListStr, 4, c.getPhraseList().size());

        assertEquals("文節区切り位置不正 : " + paragraphListStr
                , Arrays.asList("山路を", "登りながら、", "こう", "考えた。")
                , c.getPhraseList().stream().map(paragraph -> paragraph.getParagraph()).collect(Collectors.toList()));
    }

    /**
     * 付属語（助動詞）を含む
     *
     * @throws ParagraphException
     */
    @Test
    public void testParagraph2() throws ParagraphException {
        Clause c = new Clause("すぐに出発しなければならない。");
        final String paragraphListStr = getParagraphListStr(c);
        assertEquals("文節数不正 : " + paragraphListStr, 3, c.getPhraseList().size());

        assertEquals("文節区切り位置不正 : " + paragraphListStr
                , Arrays.asList("すぐに", "出発しなければ", "ならない。")
                , c.getPhraseList().stream().map(paragraph -> paragraph.getParagraph()).collect(Collectors.toList()));
    }

    /**
     * 自立語を含む動詞の文節分け確認
     *
     * @throws ParagraphException
     */
    @Test
    public void testParagraph3() throws ParagraphException {
        Clause c = new Clause("朝に散歩することにしている。");
        final String paragraphListStr = getParagraphListStr(c);
        assertEquals("文節数不正 : " + paragraphListStr, 5, c.getPhraseList().size());
        assertEquals("文節区切り位置不正 : " + paragraphListStr
                , Arrays.asList("朝に", "散歩する", "ことに", "して", "いる。")
                , c.getPhraseList().stream().map(paragraph -> paragraph.getParagraph()).collect(Collectors.toList()));
    }

    /**
     * どっちかというと、kuromojiが動くことのテスト
     */
    @Test
    public void testWordList1() {
        List<Word> wordList = Clause.createWordList("山椒魚は悲しんだ");
        assertEquals("単語：" + wordList.stream().map(word -> word.getSurface()).collect(Collectors.joining(PARAGRAPH_CHAR)), 4, wordList.size());
    }

    /**
     * どっちかというと、kuromojiが動くことのテスト
     */
    @Test
    public void testWordList2() {
        List<Word> wordList = Clause.createWordList("菜の花が咲く季節になりました");
        assertEquals("単語：" + wordList.stream().map(word -> word.getSurface()).collect(Collectors.joining(PARAGRAPH_CHAR)), 8, wordList.size());
    }

    /**
     * どっちかというと、kuromojiが動くことのテスト
     */
    @Test
    public void testWordList3() {
        List<Word> wordList = Clause.createWordList("大人らしく落ち着いて話してください");
        assertEquals("単語：" + wordList.stream().map(word -> word.getSurface()).collect(Collectors.joining(PARAGRAPH_CHAR)), 7, wordList.size());
    }

    @Test
    public void testPredicate1() throws Exception {
        Clause c = new Clause("誰も僕の言うことを信じない。");
        assertEquals("述語数エラー", 1, c.getPredicate().size());
        assertEquals("述語判定エラー", "信じない。", c.getPredicate().get(0).getParagraph());
        assertEquals("主語数エラー:" + getParagraphListStr(c), 1, c.getSubjects().size());
        assertEquals("主語判定エラー" + getParagraphListStr(c), "誰も", c.getSubjects().get(0).getParagraph());
    }

    @Test
    public void testPredicate2() throws Exception {
        Clause c = new Clause("あの映画のファンが続編を待ち望んでいる。");
        assertEquals("述語数エラー", 1, c.getPredicate().size());
        assertEquals("述語判定エラー", "いる。", c.getPredicate().get(0).getParagraph());
        assertEquals("主語数エラー:" + getParagraphListStr(c), 1, c.getSubjects().size());
        assertEquals("主語判定エラー" + getParagraphListStr(c), "ファンが", c.getSubjects().get(0).getParagraph());
    }

    @Test
    public void testPredicate3() throws Exception {
        Clause c = new Clause("ああ、きれいだなあ、あの人は。");
        assertEquals("述語数エラー", 1, c.getPredicate().size());
        assertEquals("述語判定エラー", "きれいだなあ、", c.getPredicate().get(0).getParagraph());
        assertEquals("主語数エラー:" + getParagraphListStr(c), 1, c.getSubjects().size());
        assertEquals("主語判定エラー:" + getParagraphListStr(c), "人は。", c.getSubjects().get(0).getParagraph());
    }

    @Test
    public void testPredicate4() throws Exception {
        Clause c = new Clause("山道を登りながら、こう考えた。");
        assertEquals("述語数エラー", 2, c.getPredicate().size());
        assertEquals("述語判定エラー", Arrays.asList("登りながら、", "考えた。"), c.getPredicate().stream().map(Phrase::getParagraph).collect(Collectors.toList()));
        assertEquals("主語判定エラー", 0, c.getSubjects().size());
    }

    @Test
    public void testSimilarity1() throws Exception {
        Clause c = new Clause("これは、もっとも優れた作品の一つだ。");
        List<Phrase> similarities = c.getSimilarities();
        assertEquals("修飾語数エラー:" + getParagraphListStr(c), 3, similarities.size());

        Phrase phrase = c.getPhraseList().get(2);// 「優れた」の文節を取得
        assertEquals("修飾語判定エラー", "もっとも", phrase.getSimilarities().stream().map(p -> p.getParagraph()).collect(Collectors.joining()));

        phrase = c.getPhraseList().get(3);// 「作品の」の文節を取得
        assertEquals("修飾語判定エラー", "優れた", phrase.getSimilarities().stream().map(p -> p.getParagraph()).collect(Collectors.joining()));

        phrase = c.getPhraseList().get(4);// 「一つだ。」の文節を取得
        assertEquals("修飾語判定エラー", "作品の", phrase.getSimilarities().stream().map(p -> p.getParagraph()).collect(Collectors.joining()));

    }

    @Test
    public void testMultiSubject() throws ParagraphException {
        Clause c = new Clause("姉は家の掃除を手伝い、私は部屋でゲームをする。");

        assertEquals("主語数エラー：" + getParagraphListStr(c), 2, c.getSubjects().size());

        assertEquals("述語数エラー：" + getParagraphListStr(c), 2, c.getPredicate().size());

        Phrase phrase = c.getPhraseList().get(2); // 「掃除を」の文節取得
        assertEquals("修飾語判定エラー", "家の", phrase.getSimilarities().stream().map(p -> p.getParagraph()).collect(Collectors.joining()));

        phrase = c.getPhraseList().get(3); // 「手伝い、」の文節取得
        assertEquals("修飾語判定エラー", "掃除を", phrase.getSimilarities().stream().map(p -> p.getParagraph()).collect(Collectors.joining()));

        phrase = c.getPhraseList().get(6); // 「ゲームを」の文節取得
        assertEquals("修飾語判定エラー", "部屋で", phrase.getSimilarities().stream().map(p -> p.getParagraph()).collect(Collectors.joining()));

        phrase = c.getPhraseList().get(7); // 「する。」の文節取得
        assertEquals("修飾語判定エラー", "ゲームを", phrase.getSimilarities().stream().map(p -> p.getParagraph()).collect(Collectors.joining()));
    }

    @Test
    public void testPartsSubject() throws ParagraphException {
        Clause c = new Clause("私が生まれたふるさとは、リンゴの名産地です。");
        assertEquals("主語数エラー：" + getParagraphListStr(c), 1, c.getSubjects().size());
        assertEquals("述語数エラー：" + getParagraphListStr(c) + ": 述語文節 = " + c.getPredicate().stream().map(Phrase::getParagraph).collect(Collectors.joining("/")), 1, c.getPredicate().size());

        Phrase phrase = c.getPhraseList().get(2); // 「ふるさとは、」の文節取得
        assertEquals("修飾語判定エラー", "生まれた", phrase.getSimilarities().stream().map(p -> p.getParagraph()).collect(Collectors.joining()));

        phrase = c.getPhraseList().get(4); // 「名産地です。」の文節取得
        assertEquals("修飾語判定エラー", "リンゴの", phrase.getSimilarities().stream().map(p -> p.getParagraph()).collect(Collectors.joining()));
    }

    /**
     * 被修飾語のテスト
     */
    @Test
    public void testModifiedWord() throws ParagraphException {
        Clause c = new Clause("非常に多くの人間が出たり入ったりする。");

        // テスト対象の修飾語である「非常に」を取得
        Phrase similate = c.getPhraseList().get(0);

        List<Phrase> result = c.getPhraseList().stream().parallel()
                .filter(paragraph -> paragraph.getSimilarities().stream().anyMatch(similate::equals))
                .collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals("多くの", result.get(0).getParagraph());
    }

    /**
     * 被修飾語のテスト
     */
    @Test
    public void testModifiedWord2() throws ParagraphException {
        Clause c = new Clause("上空を大きな鳥がゆっくりと飛ぶ。");

        // テスト対象の修飾語である「上空を」を取得
        Phrase similate = c.getPhraseList().get(0);

        List<Phrase> result = c.getPhraseList().stream().parallel()
                .filter(paragraph -> paragraph.getSimilarities().stream().anyMatch(similate::equals))
                .collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals("飛ぶ。", result.get(0).getParagraph());
    }

    /**
     * 被修飾語のテスト
     */
    @Test
    public void testModifiedWord3() throws ParagraphException {
        Clause c = new Clause("非常に多くの人間が出たり入ったりする。");

        // テスト対象の修飾語である「非常に」を取得
        Phrase similate = c.getPhraseList().get(0);

        List<Phrase> result = c.getPhraseList().stream().parallel()
                .filter(paragraph -> paragraph.getSimilarities().stream().anyMatch(similate::equals))
                .collect(Collectors.toList());

        assertEquals("修飾語数エラー:" + result.stream().map(Phrase::getParagraph).collect(Collectors.joining("-")), 1, result.size());
        assertEquals("多くの", result.get(0).getParagraph());
    }

    private String getParagraphListStr(Clause c) {
        return c.getPhraseList().stream().map(paragraph -> paragraph.getParagraph()).collect(Collectors.joining(PARAGRAPH_CHAR));
    }


}