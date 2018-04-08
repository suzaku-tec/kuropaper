package sentence.paragraph;

import sentence.clause.Clause;
import sentence.phrase.Phrase;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 段落
 *
 * 主な役割は、文の意味同士のつながり等を管理する。
 * 接続語の管理がメイン
 */
public class Paragraph {
    private List<Clause> clauseList;

    public Paragraph(String paragraph) {
        clauseList = Stream.of(paragraph.split("(?<=。)")).map(Clause::new).collect(Collectors.toList());
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
}
