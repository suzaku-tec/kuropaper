package sentence.paragraph;

import java.util.Arrays;
import java.util.stream.Stream;

public enum ConnectionWord {

    /**
     * 順接
     */
    PROCRASTINATION("それで", "そこで", "だから", "すると", "したがって", "ゆえに", "よって"),

    /**
     * 逆接
     */
    REVERSE("しかし", "だが", "ところが", "けれども", "だけど", "でも", "なのに", "が", "しかるに", "けれど"),

    /**
     * 累加（添加）
     */
    CUMULATIVE("そして", "それから", "なお", "しかも", "それに", "そのうえ"),

    /**
     * 並立（並列）
     */
    PARALLEL("また", "および", "ならびに"),

    /**
     * 説明・補足
     */
    DESCRIPTION("つまり", "すなわち", "なぜなら", "ただし", "もっとも"),

    /**
     * 対比・選択
     */
    COMPARISON("それとも", "あるいは", "または", "もしくは"),

    /**
     * 転換
     */
    CONVERSION("さて", "ところで", "では", "ときに"),;

    private String[] conditionsWords;

    ConnectionWord(String... conditionsWords) {
        this.conditionsWords = conditionsWords;
    }

    private Stream<String> getConditionsWordsStream() {
        return Arrays.stream(conditionsWords);
    }

    public static ConnectionWord getEnum(String connectionWord) {
        return Arrays.stream(ConnectionWord.values()).filter(e -> e.getConditionsWordsStream().anyMatch(s -> s.equals(connectionWord))).findFirst().orElseThrow(ConnectionWordException::new);
    }
}
