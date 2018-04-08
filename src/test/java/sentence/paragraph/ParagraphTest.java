package sentence.paragraph;

import org.junit.Test;
import sentence.phrase.Phrase;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ParagraphTest {

    @Test
    public void testClause() {
        Paragraph paragraph = new Paragraph("遅く起きた。だから、授業に 遅刻した。");

        assertEquals("", Arrays.asList("遅く起きた。", "だから、授業に 遅刻した。"), paragraph.getClauseList().stream().map(clause -> clause.getPhraseStr()).collect(Collectors.toList()));
    }

    @Test
    public void testConnectionExist() {
        Paragraph paragraph = new Paragraph("歯が痛かった。そこで、歯医者に行くことにした。");
        assertEquals("接続詞の存在エラー", true, paragraph.existConnection());
    }

    @Test
    public void testConnectionExist1Clause() {
        Paragraph paragraph = new Paragraph("明日は、雨かまたは雪が降るでしょう。");
        assertEquals("接続詞の存在エラー", true, paragraph.existConnection());
    }

    @Test
    public void testConnectionExist1Clause2() {
        Paragraph paragraph = new Paragraph("勉強すると言いながら、ついつい遊んでしまう。");
        assertEquals("接続詞の存在エラー", true, paragraph.existConnection());
    }

    @Test
    public void testDokuritsuWordExist() {
        Paragraph paragraph = new Paragraph("おや、ここにお金が落ちているぞ。");
        assertEquals("接続詞の存在エラー", "おや、", paragraph.getDokuritsuPhraseList().stream().map(Phrase::getPharseStr).collect(Collectors.joining()));
    }

    @Test
    public void testInstructionExist() {
        Paragraph paragraph = new Paragraph("一つ持つと、あれもこれも欲しくなる。");
        assertEquals("接続詞の存在エラー", Arrays.asList("あれも","これも"), paragraph.getInstructionPhraseList().stream().map(Phrase::getPharseStr).collect(Collectors.toList()));
    }

    @Test
    public void testInstructionExist2() {
        Paragraph paragraph = new Paragraph("こちらを立てれば、あちらが立たぬ。");
        assertEquals("接続詞の存在エラー", Arrays.asList("こちらを","あちらが"), paragraph.getInstructionPhraseList().stream().map(Phrase::getPharseStr).collect(Collectors.toList()));
    }

    @Test
    public void testInstructionNotExist() {
        Paragraph paragraph = new Paragraph("ああ、なんてすてきなマンションでしょう。");
        assertEquals("接続詞の存在エラー", true, paragraph.getInstructionPhraseList().stream().map(Phrase::getPharseStr).collect(Collectors.toList()).isEmpty());
    }

}