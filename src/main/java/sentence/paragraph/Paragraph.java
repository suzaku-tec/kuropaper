package sentence.paragraph;

import sentence.phrase.PhraseException;
import sentence.clause.Clause;
import sentence.phrase.Phrase;

import java.util.Comparator;
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
        return clauseList.stream().anyMatch(Clause::existConjunction);
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

        // 同一文内に指示語対象があれば設定する
        clauseList.stream().forEach(clause -> {
            List<Phrase> instructionPhraseList = clause.getPhraseList().stream().filter(Phrase::existInstruction).collect(Collectors.toList());
            List<Phrase> subjectPhraseList = clause.getPhraseList().stream().filter(phrase -> phrase.isSubject()).collect(Collectors.toList());

            instructionPhraseList.stream().forEach(phrase -> {
                subjectPhraseList.stream()
                        .filter(suject -> suject.getMaxWordPosition() < phrase.getMinWordPosition())
                        .max(Comparator.comparingInt(Phrase::getMaxWordPosition))
                        .ifPresent(subject -> phrase.setTartgetPhrase(subject));
            });
        });


        // 指示語リスト取得
        List<Phrase> instructionList = clauseList.stream()
                .filter(Clause::existInstruction)
                .flatMap(clause -> clause.getInstructionPhraseList().stream())
                .filter(Phrase::existInstruction)
                .filter(phrase -> phrase.getTartget() == null)
                .collect(Collectors.toList());

        List<Phrase> subjectList = clauseList.stream()
                .flatMap(clause -> clause.getPhraseList().stream())
                .filter(phrase -> phrase.isSubject())
                .collect(Collectors.toList());

        instructionList.stream().forEach(phrase -> {
            Integer index = clauseList.stream().filter(clause -> clause.getPhraseList().stream().anyMatch(phrase::equals)).map(clauseList::indexOf).findFirst().orElseThrow(() -> new PhraseException("max word position error"));

            List<Phrase> slist = clauseList.stream().limit(index).flatMap(clause -> clause.getPhraseList().stream()).filter(p -> p.isSubject()).collect(Collectors.toList());

            if (!slist.isEmpty()) {
                phrase.setTartgetPhrase(slist.get(slist.size() - 1));
            }
        });

    }
}
