package sentence;

import exception.ParagraphException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ClauseTest {

	private static final String PARAGRAPH_CHAR = "/";

	@Test
	public void testParagraph1() throws ParagraphException {
		Clause c = new Clause("山路を登りながら、こう考えた。");

		final String paragraphListStr = getparaParagraphListStr(c);
		assertEquals("文節数不正 : " + paragraphListStr, 4, c.getParagraphList().size());

		assertEquals("文節区切り位置不正 : " + paragraphListStr
				, Arrays.asList("山路を", "登りながら、", "こう", "考えた。")
				, c.getParagraphList().stream().map(paragraph -> paragraph.getParagraph()).collect(Collectors.toList()));
	}

	/**
	 * 付属語（助動詞）を含む
	 * @throws ParagraphException
	 */
	@Test
	public void testParagraph2() throws ParagraphException {
		Clause c = new Clause("すぐに出発しなければならない。");
		final String paragraphListStr = getparaParagraphListStr(c);
		assertEquals("文節数不正 : " + paragraphListStr, 3, c.getParagraphList().size());

		assertEquals("文節区切り位置不正 : " + paragraphListStr
				, Arrays.asList("すぐに", "出発しなければ", "ならない。")
				, c.getParagraphList().stream().map(paragraph -> paragraph.getParagraph()).collect(Collectors.toList()));
	}

	/**
	 * 自立語を含む動詞の文節分け確認
	 * @throws ParagraphException
	 */
	@Test
	public void testParagraph3() throws ParagraphException {
		Clause c = new Clause("朝に散歩することにしている。");
		final String paragraphListStr = getparaParagraphListStr(c);
		assertEquals("文節数不正 : " + paragraphListStr, 5, c.getParagraphList().size());
		assertEquals("文節区切り位置不正 : " + paragraphListStr
				, Arrays.asList("朝に", "散歩する", "ことに", "して", "いる。")
				, c.getParagraphList().stream().map(paragraph -> paragraph.getParagraph()).collect(Collectors.toList()));
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
		assertEquals("述語判定エラー", "信じない。", c.getPredicate().getParagraph());
		assertEquals("主語判定エラー", "誰も", c.getSubject().getParagraph());
	}

	@Test
	public void testPredicate2() throws Exception {
		Clause c = new Clause("あの映画のファンが続編を待ち望んでいる。");
		assertEquals("述語判定エラー", "いる。", c.getPredicate().getParagraph());
		assertEquals("主語判定エラー", "ファンが", c.getSubject().getParagraph());
	}

	@Test
	public void testPredicate3() throws Exception {
		Clause c = new Clause("ああ、きれいだなあ、あの人は。");
		assertEquals("述語判定エラー", "きれいだなあ、", c.getPredicate().getParagraph());
		assertEquals("主語判定エラー:"+ getparaParagraphListStr(c), "人は。", c.getSubject().getParagraph());
	}

	@Test
	public void testPredicate4() throws Exception {
		Clause c = new Clause("山道を登りながら、こう考えた。");
		assertEquals("述語判定エラー", "考えた。", c.getPredicate().getParagraph());
		assertEquals("主語判定エラー", null, c.getSubject());
	}

	@Test
	public void testSimilarity1() throws Exception {
		Clause c = new Clause("これは、もっとも優れた作品の一つだ。");
		List<Paragraph> similarities = c.getSimilarities();
		assertEquals("修飾語数エラー:" + getparaParagraphListStr(c), 3, similarities.size());
	}

	private String getparaParagraphListStr(Clause c) {
		return c.getParagraphList().stream().map(paragraph -> paragraph.getParagraph()).collect(Collectors.joining(PARAGRAPH_CHAR));
	}


}