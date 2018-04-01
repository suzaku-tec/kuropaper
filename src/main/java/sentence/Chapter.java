package sentence;

import sentence.paragraph.Paragraph;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章管理クラス
 */
public class Chapter {

    private List<Paragraph> paragraphList;

    public Chapter() {
        paragraphList = new ArrayList<>();
    }
}
