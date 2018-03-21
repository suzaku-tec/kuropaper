package sentence;

import exception.ParagraphException;
import org.junit.Test;

import java.util.Arrays;
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


	private String getparaParagraphListStr(Clause c) {
		return c.getParagraphList().stream().map(paragraph -> paragraph.getParagraph()).collect(Collectors.joining(PARAGRAPH_CHAR));
	}
}