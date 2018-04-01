package sentence.clause;

import sentence.paragraph.Paragraph;

/**
 * 文の骨格情報
 *
 * 主語と述語のセット情報を持つ。
 * 主語は、ない場合がある。
 */
public class Skeleton {

    /** 主語 */
    public Paragraph subject;

    /** 述語 */
    public Paragraph predicate;

    /**
     * 主語・述語が設定されているか判定する
     * @return
     */
    public boolean isPerfect() {
        return  subject != null && predicate != null;
    }
}
