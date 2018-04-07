package sentence.phrase;

import exception.ParagraphException;
import org.junit.Test;
import sentence.clause.Clause;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class PhraseTest {

	@Test
	public void testPredicate() throws ParagraphException {
		Clause c = new Clause("誰も僕の言うことを信じない。");
		List<Phrase> phraseList = c.getPhraseList();
		phraseList.stream().forEach(paragraph -> paragraph.getWordList().stream().forEach(word -> System.out.println(word.getSurface())));
		System.out.println("=======================");
		phraseList.stream().forEach(p -> System.out.println(p.getPharseJpnStr() + ":" + p.getWorkType()));
		List<Phrase> list = phraseList.stream().filter(paragraph -> Phrase.WorkType.PREDICATE.equals(paragraph.getWorkType())).collect(Collectors.toList());
		assertEquals("述語判定エラー", "信じない。", list.get(list.size() - 1).getPharseJpnStr());
	}

}