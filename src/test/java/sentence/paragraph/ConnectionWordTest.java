package sentence.paragraph;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConnectionWordTest {

    @Test
    public void testそれで() {
        assertEquals("判定エラー", ConnectionWord.PROCRASTINATION, ConnectionWord.getEnum("それで"));
    }

    @Test
    public void testそこで() {
        assertEquals("判定エラー", ConnectionWord.PROCRASTINATION, ConnectionWord.getEnum("そこで"));
    }

    @Test
    public void testだから() {
        assertEquals("判定エラー", ConnectionWord.PROCRASTINATION, ConnectionWord.getEnum("だから"));
    }

    @Test
    public void testすると() {
        assertEquals("判定エラー", ConnectionWord.PROCRASTINATION, ConnectionWord.getEnum("すると"));
    }

    @Test
    public void testしたがって() {
        assertEquals("判定エラー", ConnectionWord.PROCRASTINATION, ConnectionWord.getEnum("したがって"));
    }

    @Test
    public void testゆえに() {
        assertEquals("判定エラー", ConnectionWord.PROCRASTINATION, ConnectionWord.getEnum("ゆえに"));
    }

    @Test
    public void testよって() {
        assertEquals("判定エラー", ConnectionWord.PROCRASTINATION, ConnectionWord.getEnum("よって"));
    }

    @Test
    public void testしかし() {
        assertEquals("判定エラー", ConnectionWord.REVERSE, ConnectionWord.getEnum("しかし"));
    }

    @Test
    public void testだが() {
        assertEquals("判定エラー", ConnectionWord.REVERSE, ConnectionWord.getEnum("だが"));
    }

    @Test
    public void testところが() {
        assertEquals("判定エラー", ConnectionWord.REVERSE, ConnectionWord.getEnum("ところが"));
    }

    @Test
    public void testけれども() {
        assertEquals("判定エラー", ConnectionWord.REVERSE, ConnectionWord.getEnum("けれども"));
    }

    @Test
    public void testけれど() {
        assertEquals("判定エラー", ConnectionWord.REVERSE, ConnectionWord.getEnum("けれど"));
    }

    @Test
    public void testだけど() {
        assertEquals("判定エラー", ConnectionWord.REVERSE, ConnectionWord.getEnum("だけど"));
    }

    @Test
    public void testでも() {
        assertEquals("判定エラー", ConnectionWord.REVERSE, ConnectionWord.getEnum("でも"));
    }

    @Test
    public void testなのに() {
        assertEquals("判定エラー", ConnectionWord.REVERSE, ConnectionWord.getEnum("なのに"));
    }

    @Test
    public void testが() {
        assertEquals("判定エラー", ConnectionWord.REVERSE, ConnectionWord.getEnum("が"));
    }

    @Test
    public void testしかるに() {
        assertEquals("判定エラー", ConnectionWord.REVERSE, ConnectionWord.getEnum("しかるに"));
    }

    @Test
    public void testそして() {
        assertEquals("判定エラー", ConnectionWord.CUMULATIVE, ConnectionWord.getEnum("そして"));
    }

    @Test
    public void testそれから() {
        assertEquals("判定エラー", ConnectionWord.CUMULATIVE, ConnectionWord.getEnum("それから"));
    }

    @Test
    public void testなお() {
        assertEquals("判定エラー", ConnectionWord.CUMULATIVE, ConnectionWord.getEnum("なお"));
    }

    @Test
    public void testしかも() {
        assertEquals("判定エラー", ConnectionWord.CUMULATIVE, ConnectionWord.getEnum("しかも"));
    }

    @Test
    public void testそれに() {
        assertEquals("判定エラー", ConnectionWord.CUMULATIVE, ConnectionWord.getEnum("それに"));
    }

    @Test
    public void testそのうえ() {
        assertEquals("判定エラー", ConnectionWord.CUMULATIVE, ConnectionWord.getEnum("そのうえ"));
    }

    @Test
    public void testまた() {
        assertEquals("判定エラー", ConnectionWord.PARALLEL, ConnectionWord.getEnum("また"));
    }

    @Test
    public void testおよび() {
        assertEquals("判定エラー", ConnectionWord.PARALLEL, ConnectionWord.getEnum("および"));
    }

    @Test
    public void testならびに() {
        assertEquals("判定エラー", ConnectionWord.PARALLEL, ConnectionWord.getEnum("ならびに"));
    }

    @Test
    public void testつまり() {
        assertEquals("判定エラー", ConnectionWord.DESCRIPTION, ConnectionWord.getEnum("つまり"));
    }

    @Test
    public void testすなわち() {
        assertEquals("判定エラー", ConnectionWord.DESCRIPTION, ConnectionWord.getEnum("すなわち"));
    }

    @Test
    public void testなぜなら() {
        assertEquals("判定エラー", ConnectionWord.DESCRIPTION, ConnectionWord.getEnum("なぜなら"));
    }

    @Test
    public void testただし() {
        assertEquals("判定エラー", ConnectionWord.DESCRIPTION, ConnectionWord.getEnum("ただし"));
    }

    @Test
    public void testもっとも() {
        assertEquals("判定エラー", ConnectionWord.DESCRIPTION, ConnectionWord.getEnum("もっとも"));
    }

    @Test
    public void testそれとも() {
        assertEquals("判定エラー", ConnectionWord.COMPARISON, ConnectionWord.getEnum("それとも"));
    }

    @Test
    public void testあるいは() {
        assertEquals("判定エラー", ConnectionWord.COMPARISON, ConnectionWord.getEnum("あるいは"));
    }

    @Test
    public void testまたは() {
        assertEquals("判定エラー", ConnectionWord.COMPARISON, ConnectionWord.getEnum("または"));
    }

    @Test
    public void testもしくは() {
        assertEquals("判定エラー", ConnectionWord.COMPARISON, ConnectionWord.getEnum("もしくは"));
    }

    @Test
    public void testさて() {
        assertEquals("判定エラー", ConnectionWord.CONVERSION, ConnectionWord.getEnum("さて"));
    }

    @Test
    public void testところで() {
        assertEquals("判定エラー", ConnectionWord.CONVERSION, ConnectionWord.getEnum("ところで"));
    }

    @Test
    public void testでは() {
        assertEquals("判定エラー", ConnectionWord.CONVERSION, ConnectionWord.getEnum("では"));
    }

    @Test
    public void testときに() {
        assertEquals("判定エラー", ConnectionWord.CONVERSION, ConnectionWord.getEnum("ときに"));
    }

}