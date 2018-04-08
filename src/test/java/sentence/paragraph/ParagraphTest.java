package sentence.paragraph;

import org.junit.Test;
import sentence.clause.Clause;

import java.util.Arrays;
import java.util.concurrent.Phaser;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ParagraphTest {

    @Test
    public void testClause() {
        Paragraph paragraph = new Paragraph("遅く起きた。だから、授業に 遅刻した。");

        assertEquals("", Arrays.asList("遅く 起きた。", "だから、授業に 遅刻した。"), paragraph.getClauseList().stream().map(clause -> clause.getPhraseStr()).collect(Collectors.toList()));
    }
}