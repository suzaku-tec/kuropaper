package sentence;

import com.atilika.kuromoji.ipadic.Tokenizer;
import exception.ParagraphException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文
 */
public class Clause {

    List<Paragraph> paragraphList;

    public Clause(String clause) throws ParagraphException {
        Tokenizer tokenizer = new Tokenizer() ;
        List<Word> list = tokenizer.tokenize(clause).stream().map(Word::new).collect(Collectors.toList());

        this.paragraphList = Paragraph.convertParagraph(list);
        
        analize(paragraphList);
    }

    private void analize(List<Paragraph> paragraphList) {

        // 述語の選定
        for (int i = paragraphList.size() -1; i < 0; i--) {
            if(paragraphList.get(i))
        }

    }
}
