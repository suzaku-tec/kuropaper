package sentence;

import exception.ParagraphException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClauseTest {

    @Test
    public void testClause() throws ParagraphException {
        Clause c = new Clause("鳥が鳴く。");
    }
}