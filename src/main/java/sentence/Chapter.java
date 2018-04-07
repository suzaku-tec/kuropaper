package sentence;

import sentence.phrase.Phrase;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章管理クラス
 */
public class Chapter {

    private List<Phrase> phraseList;

    public Chapter() {
        phraseList = new ArrayList<>();
    }
}
