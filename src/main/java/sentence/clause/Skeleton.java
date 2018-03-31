package sentence.clause;

import sentence.Paragraph.Paragraph;

/**
 * 文の骨格情報
 */
public class Skeleton {

    public Paragraph subject;

    public Paragraph predicate;

    /**
     * 主語・述語が設定されているか判定する
     * @return
     */
    public boolean isPerfect() {
        return  subject != null && predicate != null;
    }
}
