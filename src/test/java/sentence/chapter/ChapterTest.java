package sentence.chapter;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChapterTest {

    @Test
    public void testSplitParagraph() {
        Chapter c = new Chapter("UFOの目撃談は、すでに伝説を作りあげている。何千という新聞報道の他に、これに関するシリーズ本などが出版され、UFOを肯定するものもあれば、否定するものもあり、まじめなものもあればインチキもあるというありさまである。\n" +
                " UFO現象そのものは、最近も目撃の報告が続いているように、それによって影響を受けた様子はない。それは、依然として続いているのである。その正体が何であるにせよ、ひとつ確かなことは、それが「神話」になったということである。");

        assertEquals("段落数エラー", 2, c.paragraphSize());
    }
}