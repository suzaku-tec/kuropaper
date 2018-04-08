import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import java.util.List;

public class Sample {
    public static void main(String[] args) {
        Tokenizer tokenizer = new Tokenizer() ;
        List<Token> tokens = tokenizer.tokenize("遅く起きた。だから、授業に 遅刻した。");
        for (Token token : tokens) {
            System.out.println(token.getSurface() + "\t" + token.getAllFeatures());
        }
    }
}
