package sentence;

import exception.ParagraphException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文節
 */
public class Paragraph {

	private List<Word> wordList;

	private boolean predicatable;

	private WorkType workType;

	public Paragraph() {
		wordList = new ArrayList<>();
	}

	public void addWord(Word word) {
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

	/**
	 * 文節の区切り判定
	 *
	 * @param word
	 * @return
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

	public void setPredicatable(boolean predicatable) {
		this.predicatable = predicatable;
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
}
