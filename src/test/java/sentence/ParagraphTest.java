package sentence;

import exception.ParagraphException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParagraphTest {
    @Test
    public void testClause() throws ParagraphException {
        Clause c = new Clause("これが私が飼っている猫だ。");
    }

}