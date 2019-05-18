package sentence.clause;

import sentence.phrase.Phrase;

import java.util.ArrayList;
import java.util.List;

/**
 * 修飾子の情報
 *
 * 修飾子の情報を一時的に保持するためのクラス
 */
public class Modification {

    List<Phrase> taigen;

    List<Phrase> yougen;

    Modification() {
        taigen = new ArrayList<>();
        yougen = new ArrayList<>();
    }
}