package sentence;

import com.atilika.kuromoji.ipadic.Token;

public class Word {

    public String getPartOfSpeechLevel1() {
        return token.getPartOfSpeechLevel1();
    }

    public String getPartOfSpeechLevel2() {
        return token.getPartOfSpeechLevel2();
    }

    public String getPartOfSpeechLevel3() {
        return token.getPartOfSpeechLevel3();
    }

    public String getPartOfSpeechLevel4() {
        return token.getPartOfSpeechLevel4();
    }

    public String getConjugationType() {
        return token.getConjugationType();
    }

    public String getConjugationForm() {
        return token.getConjugationForm();
    }

    public String getBaseForm() {
        return token.getBaseForm();
    }

    public String getReading() {
        return token.getReading();
    }

    public String getPronunciation() {
        return token.getPronunciation();
    }

    public String getSurface() {
        return token.getSurface();
    }

    public boolean isKnown() {
        return token.isKnown();
    }

    public boolean isUser() {
        return token.isUser();
    }

    public int getPosition() {
        return token.getPosition();
    }

    public String getAllFeatures() {
        return token.getAllFeatures();
    }

    public String[] getAllFeaturesArray() {
        return token.getAllFeaturesArray();
    }

    private Token token;

    public Word(Token token) {
        this.token = token;
    }
}
