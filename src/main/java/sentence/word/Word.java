package sentence.word;

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

    /**
     * 自立語判定
     *
     * @return true:自立語 false:付属語
     */
    public boolean isIndependence() {

        if (isPunctuationMark()) {
            return false;
        }

        if (token.getConjugationType().equals("サ変・スル")) {
            return false;
        }

        return !(getPartOfSpeechLevel1().equals("助詞") || getPartOfSpeechLevel1().equals("助動詞"));
    }

    /**
     * 活用がある自立語か判定
     *
     * @return
     */
    public boolean isIndependenceUse() {

        if (isAdjunct()) {
            return false;
        }

        if (getPartOfSpeechLevel1().equals("動詞") || getPartOfSpeechLevel1().equals("形容詞") || getPartOfSpeechLevel1().equals("形容動詞")) {
            return true;
        }

        return false;
    }

    public boolean isAdjunct() {
        return getPartOfSpeechLevel1().equals("助詞") || getPartOfSpeechLevel1().equals("助動詞");
    }

    /**
     * 句読点判定
     *
     * @return true:句読点 false:句読点以外
     */
    public boolean isPunctuationMark() {
        if (getPartOfSpeechLevel2().equals("読点")) {
            return true;
        }

        if (getPartOfSpeechLevel2().equals("句点")) {
            return true;
        }

        return false;
    }

    /**
     * 読点判定
     *
     * @return
     */
    public boolean isReadingPoint() {
        return getPartOfSpeechLevel2().equals("読点");
    }

    /**
     * 助詞判定
     *
     * @return true:助詞 false:助詞以外
     */
    public boolean isParticle() {
        return getPartOfSpeechLevel1().equals("助詞");
    }

    /**
     * 副詞判定
     *
     * @return
     */
    public boolean isAdverbs() {
        return getPartOfSpeechLevel1().equals("副詞");
    }

    /**
     * 名詞判定
     *
     * @return
     */
    public boolean isNoun() {
        return "名詞".equals(getPartOfSpeechLevel1());
    }

    /**
     * 動詞判定
     *
     * @return
     */
    public boolean isVerb() {
        return "動詞".equals(getPartOfSpeechLevel1());
    }

    /**
     * 接続詞判定
     * @return true:接続詞 false:接続詞以外
     */
    public boolean isConjunction() {
        return "接続詞".equals(getPartOfSpeechLevel1()) || ("助詞".equals(getPartOfSpeechLevel1()) && "接続助詞".equals(getPartOfSpeechLevel2()));
    }

    /**
     * 独立語判定
     * @return true:独立語 false:独立語以外
     */
    public boolean isDokuritsu() {
        return "感動詞".equals(getPartOfSpeechLevel1());
    }

    /**
     * 指示語判定
     * @return true:指示語 false:指示語以外
     */
    public boolean isInstruction() {
        return "名詞".equals(getPartOfSpeechLevel1()) && "代名詞".equals(getPartOfSpeechLevel2());
    }
}
