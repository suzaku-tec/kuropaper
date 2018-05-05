package sentence.chapter;

import sentence.paragraph.Paragraph;
import sentence.phrase.Phrase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章管理クラス
 */
public class Chapter {

    private List<Paragraph> paragraphList;

    public Chapter(String str) {
        paragraphList = Arrays.stream(str.replaceAll("^ |\r\n|\n", "").split("(?<=。)[ |　]")).map(Paragraph::new).collect(Collectors.toList());
    }

    public int paragraphSize() {
        return paragraphList.size();
    }

    public List<Paragraph> getParagraphList() {
        return paragraphList;
    }
}
