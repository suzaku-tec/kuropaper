package sentence.Paragraph;

import exception.ParagraphException;
import sentence.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文節
 */
public class Paragraph {

	private List<Word> wordList;

	private WorkType workType;

	/** 修飾語リスト */
	private List<Paragraph> similarities = Collections.emptyList();

	private Paragraph() {
		wordList = new ArrayList<>();
	}

	private void addWord(Word word) {
		this.wordList.add(word);

		try {
			workType = analyse();
		} catch (Exception e) {
			workType = null;
		}
	}

	public Word getLastWord() {

		if (wordList == null) {
			return null;
		}

		return wordList.get(wordList.size() - 1);
	}

	public static List<Paragraph> convertParagraph(List<Word> words) throws ParagraphException {
		int startIndex = 0;

		List<Paragraph> list = new ArrayList<>();
		words.stream().forEach(word -> {
			if (isSpritParagraph((word))) {

				if (list.size() == 0) {
					Paragraph p = new Paragraph();
					p.addWord(word);
					list.add(p);
				} else {
					if (word.isIndependence()) {
						if (1 <= list.size() && list.get(list.size() - 1).getWordList().stream().anyMatch(Word::isIndependence)) {
							Paragraph p = new Paragraph();
							p.addWord(word);
							list.add(p);
						} else {
							if (word.getPartOfSpeechLevel2().equals("読点")) {
								list.remove(list.size() - 1);
							}
							list.get(list.size() - 1).addWord(word);
						}
					} else {
						list.get(list.size() - 1).addWord(word);
						Paragraph p = new Paragraph();
						list.add(p);
					}
				}
			} else if (word.getPartOfSpeechLevel2().equals("読点")) {
				// 読点は前の文字として扱う
				if(2 <= list.size()) {
					list.get(list.size() - 2).addWord(word);
				} else if(1 < list.size()) {
					list.get(list.size() - 1).addWord(word);
				}
			} else if (word.getPartOfSpeechLevel2().equals("句点")) {
				if(list.get(list.size()-1).wordList.isEmpty()) {
					list.get(list.size() - 2).addWord(word);
				} else {
					list.get(list.size() - 1).addWord(word);
				}
			} else {
				if (list.size() == 0) {
					list.add(new Paragraph());
				}
				list.get(list.size() - 1).addWord(word);
			}
		});

		return list;
	}

	public WorkType getWorkType() {
		return workType;
	}

	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	/**
	 * 文節の区切り判定
	 *
	 * @param word 単語
	 * @return true:文節の区切り false:文節の区切りではない
	 */
	private static boolean isSpritParagraph(Word word) {

		if (word.getPartOfSpeechLevel1().equals("助詞")) {
			return true;
		}

		if (word.getPartOfSpeechLevel1().equals("副詞") && word.getPartOfSpeechLevel2().equals("助詞類接続")) {
			return true;
		}

		if (word.isIndependence()) {
			return true;
		}

		return false;
	}

	public List<Word> getWordList() {
		return Collections.unmodifiableList(this.wordList);
	}

	public String getParagraph() {
		return wordList.stream().map(word -> word.getSurface()).collect(Collectors.joining());
	}

	private WorkType analyse() throws Exception {
		return WorkType.getEnum(wordList);
	}

	/**
	 * 主語判定
	 *
	 * @param wordList 文節の文言
	 * @return 判定結果
	 */
	public static boolean isSubject(List<Word> wordList) {
		return wordList.stream().filter(Word::isParticle).anyMatch(word -> {
			if (word.getSurface().equals("は") || word.getSurface().equals("が") || word.getSurface().equals("も") && !word.getPartOfSpeechLevel1().equals("動詞")) {
				return true;
			} else {
				return false;
			}
		});
	}

	/**
	 * 述語判定
	 *
	 * @param wordList 文節の文言
	 * @return 判定結果
	 */
	public static boolean isPredicate(List<Word> wordList) {
		return wordList.stream().anyMatch(word -> {
			if (word.getPartOfSpeechLevel1().equals("動詞")) {
				return true;
			} else if(word.getPartOfSpeechLevel1().equals("助動詞")) {
				return true;
			} else {
				return false;
			}
		});
	}

	/**
	 * 単語の活用タイプ
	 */
	public enum WorkType {
		SUBJECT("主語"),
		PREDICATE("述語"),
		MODIFIER("修飾語"),
		CONNECTION_WORD("接続語"),
		INDEPENDENT_WORD("独立語"),;

		public String japanease;

		private WorkType(String japanease) {
			this.japanease = japanease;
		}

		public static WorkType getEnum(List<Word> wordList) throws Exception {
			if (Paragraph.isSubject(wordList)) {
				return WorkType.SUBJECT;
			}

			if (Paragraph.isPredicate(wordList)) {
				return WorkType.PREDICATE;
			}

			throw new Exception("該当なし");
		}
	}

	/**
	 * 修飾語の取得
	 *
	 * この文節にかかっている修飾語を取得する。
	 * 存在しない場合、空のリストが返される
	 *
	 * @return 修飾語リスト
	 */
	public List<Paragraph> getSimilarities() {
		return similarities;
	}

	/**
	 * 修飾語の設定
	 *
	 * この文節にかかっている修飾語を設定する
	 *
	 * @param similarities 修飾語
	 */
	public void setSimilarities(List<Paragraph> similarities) {
		this.similarities = similarities;
	}

	/**
	 * 副詞の存在判定
	 * @return true:副詞あり false:副詞なし
	 */
	public boolean existAdverbs() {
		return wordList.stream().anyMatch(Word::isAdverbs);
	}
}
