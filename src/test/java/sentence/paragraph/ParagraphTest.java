package sentence.paragraph;

import org.junit.Test;

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

}