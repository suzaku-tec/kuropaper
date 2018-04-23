package sentence.paragraph;

import sentence.word.Word;
import sentence.clause.Clause;
import sentence.phrase.Phrase;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 段落
 * <p>
 * 主な役割は、文の意味同士のつながり等を管理する。
 * 接続語の管理がメイン
 */
public class Paragraph {
    private List<Clause> clauseList;

    /**
     * 段落情報作成
     * <p>
     * 文字列の改行コードを失くし、句点で文を分けてClauseクラスのインスタンスを作成する。
     *
     * @param paragraph
     */
    public Paragraph(String paragraph) {
        clauseList = Stream.of(
                paragraph.replaceAll("\r\n|\n", "")
                        .split("(?<=。)"))
                .map(Clause::new)
                .collect(Collectors.toList());

        analizeInstructTarget();
    }

    public List<Clause> getClauseList() {
        return clauseList;
    }

    public boolean existConnection() {
        return clauseList.stream().anyMatch(clause -> clause.existConjunction());
    }

    public List<Phrase> getDokuritsuPhraseList() {
        return clauseList.stream().peek(clause -> System.out.println(clause.getPhraseStr())).flatMap(clause -> clause.getDokuritsuPhraseList().stream()).collect(Collectors.toList());
    }

    public List<Phrase> getInstructionPhraseList() {
        return clauseList.stream().flatMap(clause -> clause.getInstructionPhraseList().stream()).collect(Collectors.toList());
    }

    /**
     * 指示語の対象を選別
     */
    private void analizeInstructTarget() {
        // 指示語リスト取得
        List<Phrase> instructionList = clauseList.stream()
                .filter(Clause::existInstruction)
                .flatMap(clause -> clause.getInstructionPhraseList().stream())
                .filter(phrase -> phrase.existInstruction())
                .collect(Collectors.toList());

        instructionList.stream().forEach(phrase ->
                {
                    List<Word> target = clauseList.stream()
                            .filter(clause -> clause.getPhraseList().stream().anyMatch(phrase::equals))
                            .map(clauseList::indexOf)
                            .filter(index -> 0 < index)
                            .map(index -> index - 1)
                            .map(clauseList::get)
                            .flatMap(clause -> clause.getSubjects().stream())
                            .flatMap(p -> p.getWordList().stream())
                            .filter(word -> word.isNoun())
                            .collect(Collectors.toList());

                    phrase.setTartgetList(target);
                }
        );
    }
}
