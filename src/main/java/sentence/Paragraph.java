package sentence;

import exception.ParagraphException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文節
 */
public class Paragraph {

    private List<Word> wordList;

    private boolean predicatable;

    public Paragraph(List<Word> words) throws ParagraphException {
        if(words == null) {
            throw new ParagraphException("words is null");
        }

        this.wordList = words;
    }

    public static List<Paragraph> convertParagraph(List<Word> words) throws ParagraphException {
        int startIndex = 0;

        List<Paragraph> list = new ArrayList<>();
        for(int i = 0; i < words.size(); i++) {

            Word nextWord = null;
            try {
                nextWord = words.get(i+1);
            } catch (IndexOutOfBoundsException e) {
                // 握りつぶす
            }

            if(Paragraph.isSpritParagraph(words.get(i), nextWord)) {
                list.add(new Paragraph(words.subList(startIndex, i+1)));
                startIndex = i+1;
            }
        }

        if(startIndex != 0 && startIndex != words.size()) {
            list.add(new Paragraph(words.subList(startIndex, words.size())));
        }

        list.stream().forEach(paragraph -> {
            paragraph.wordList.stream().forEach(word -> {
                System.out.println(word.getSurface());
            });
            System.out.println("=====================");
        });
        return list;
    }

    /**
     * 文節の区切り判定
     *
     * @param word
     * @return
     */
    private static boolean isSpritParagraph(Word word, Word nextWord) {

        if(word.getPartOfSpeechLevel1().equals("助詞")){
            return true;
        }

        if(word.getPartOfSpeechLevel1().equals("動詞") && !nextWord.getPartOfSpeechLevel1().equals("助詞")){
            return true;
        }

        if(word.getPartOfSpeechLevel1().equals("副詞")&& word.getPartOfSpeechLevel2().equals("助詞類接続")){
            return true;
        }

        if(word.getPartOfSpeechLevel1().equals("記号") && word.getPartOfSpeechLevel2().equals("読点")) {
            return true;
        }

        if(word.getPartOfSpeechLevel1().equals("記号") && word.getPartOfSpeechLevel2().equals("読点")) {
            return true;
        }

        return false;
    }

    public boolean isPredicatable() {
        return predicatable;
    }

    public void setPredicatable(boolean predicatable) {
        this.predicatable = predicatable;
    }

    public List<Word> getWordList() {
        return Collections.unmodifiableList(this.wordList);
    }
}
