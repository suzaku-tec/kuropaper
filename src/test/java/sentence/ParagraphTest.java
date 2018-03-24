package sentence;

import exception.ParagraphException;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ParagraphTest {

	@Test
	public void testPredicate() throws ParagraphException {
		Clause c = new Clause("誰も僕の言うことを信じない。");
		List<Paragraph> paragraphList = c.getParagraphList();
		paragraphList.stream().forEach(paragraph -> paragraph.getWordList().stream().forEach(word -> System.out.println(word.getSurface())));
		System.out.println("=======================");
		paragraphList.stream().forEach( p -> System.out.println(p.getParagraph() + ":" + p.getWorkType()));
		List<Paragraph> list = paragraphList.stream().filter(paragraph -> Paragraph.WorkType.PREDICATE.equals(paragraph.getWorkType())).collect(Collectors.toList());
		assertEquals("述語判定エラー", "信じない。", list.get(list.size() - 1).getParagraph());
	}

}