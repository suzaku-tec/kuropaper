package sentence;

import com.atilika.kuromoji.ipadic.Tokenizer;
import exception.CauseException;
import exception.ParagraphException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文
 */
public class Clause {

	List<Paragraph> paragraphList;

	private Paragraph subject = null;

	private Paragraph predicate = null;

	public Clause(String clause) throws ParagraphException {
		List<Word> list = Clause.createWordList(clause);

		this.paragraphList = Collections.unmodifiableList(Paragraph.convertParagraph(list));

		analize(paragraphList);
	}

	private void analize(List<Paragraph> paragraphList) {
		analizeSubject(paragraphList).ifPresent(paragraph -> subject = paragraph);
		predicate = analizePredicate(paragraphList).orElseThrow(() -> new CauseException("predicate is not exsist"));
		paragraphList.stream()
				.filter(paragraph -> Paragraph.WorkType.PREDICATE.equals(paragraph.getWorkType()))
				.filter(paragraph -> !predicate.equals(paragraph))
				.forEach(paragraph -> paragraph.setWorkType(null));

		analizeSimilarity(paragraphList);
	}

	/**
	 * 修飾語の設定
	 *
	 * フェールセーフではない。。。
	 *
	 * @param paragraphList
	 */
	private void analizeSimilarity(List<Paragraph> paragraphList) {
		paragraphList.stream().reduce(new ArrayList<Paragraph>(), (result, paragraph) -> {
			if (Paragraph.WorkType.SUBJECT.equals(paragraph.getWorkType()) || Paragraph.WorkType.PREDICATE.equals(paragraph.getWorkType())) {
				if (!result.isEmpty()) {
					paragraph.setSimilaritys(result);
				}
				return new ArrayList<>();
			} else {

				if(result.stream().anyMatch(Paragraph::existAdverbs)) {
					paragraph.setSimilaritys(result);
					result = new ArrayList<>();
				}

				paragraph.setWorkType(Paragraph.WorkType.MODIFIER);
				result.add(paragraph);
				return result;
			}
		}, (result1, result2) -> {
			result1.addAll(result2);
			return result1;
		});
	}

	/**
	 * 述語の設定
	 * @param paragraphList
	 * @return
	 */
	private Optional<Paragraph> analizePredicate(List<Paragraph> paragraphList) {
		List<Paragraph> list = paragraphList.stream().filter(paragraph -> Paragraph.WorkType.PREDICATE.equals(paragraph.getWorkType())).collect(Collectors.toList());
		if (list == null || list.size() == 0) {
			Optional.empty();
		}

		return Optional.of(list.get(list.size() - 1));
	}

	/**
	 * 文節リストを解析して主語を見つける
	 *
	 * @param paragraphList
	 * @return 主語がない場合は、empty
	 */
	private Optional<Paragraph> analizeSubject(List<Paragraph> paragraphList) {
		List<Paragraph> list = paragraphList.stream().filter(paragraph -> Paragraph.WorkType.SUBJECT.equals(paragraph.getWorkType())).collect(Collectors.toList());

		if (list == null || list.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(list.get(0));
	}

	/**
	 * 文節リスト
	 * <p>
	 * 文の文節リストを返す。
	 *
	 * @return 文節リスト(変更不可)
	 */
	public List<Paragraph> getParagraphList() {
		return paragraphList;
	}

	/**
	 * Wordリスト作成
	 * <p>
	 * 形態素解析した結果をWordクラスにラッピングして、リスト化して返す。
	 *
	 * @param clause 文
	 * @return Wordリスト
	 */
	public static List<Word> createWordList(String clause) {
		Tokenizer tokenizer = new Tokenizer();
		List<Word> list = tokenizer.tokenize(clause).stream().map(Word::new).collect(Collectors.toList());
		return list;
	}

	/**
	 * 主語取得
	 *
	 * @return 主語
	 */
	public Paragraph getSubject() {
		return subject;
	}

	/**
	 * 述語取得
	 *
	 * @return 述語
	 */
	public Paragraph getPredicate() throws Exception {
		return predicate;
	}

	public List<Paragraph> getSimilarities() {
		return paragraphList.stream()
				.filter(paragraph -> Paragraph.WorkType.MODIFIER.equals(paragraph.getWorkType()))
				.collect(Collectors.toList());
	}
}
