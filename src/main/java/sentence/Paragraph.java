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

	public Paragraph(List<Word> words) throws ParagraphException {
		if (words == null) {
			throw new ParagraphException("words is null");
		}

		this.wordList = words;
	}

	public Paragraph() {
		wordList = new ArrayList<>();
	}

	public void addWord(Word word) {
		this.wordList.add(word);
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
				list.get(list.size() - 2).addWord(word);
			} else {
				if (list.size() == 0) {
					list.add(new Paragraph());
				}
				list.get(list.size() - 1).addWord(word);
			}
		});

		return list;
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

	public boolean isPredicatable() {
		return predicatable;
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
}
