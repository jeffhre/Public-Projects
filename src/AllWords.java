import java.util.ArrayList;
import java.util.Collections;

/* This class is here to keep track of the 
 * overall patterns of word usage throughout 
 * all researchers. This gives me an overall
 * word usage rate so that I can easily
 * identify unique and interesting words
 * elsewhere.
 */

public class AllWords {
	
	// ArrayList of word objects
	private ArrayList<Word> words;
	
	// Counts for statistics
	private Integer totalWords;
	private Integer uniqueWords;
	
	// Initialize the class
	public AllWords() {
		words = new ArrayList<Word>();
		totalWords = 0;
		uniqueWords = 0;
	}
	
	
	public ArrayList<Word> getWords() {
		return words;
	}
	
	public Integer uniqueWordCount() {
		return uniqueWords;
	}
	
	public Integer totalWordCount() {
		return totalWords;
	}
	
	// Add word to list if first occurrence
	// Modify word object if already seen
	public void addWord(String name) {
		totalWords = totalWords + 1;
		for (Word word : words) {
			if (word.getName().equals(name)) {
				word.countUp();
				return;
			}
		}
		Word newWord = new Word(name);
		words.add(newWord);
		uniqueWords = uniqueWords + 1;
	}
	
	
	// Override method, if a word must be added with an
	// initial count > 1
	// Used when reading in locally saved files
	public void addWordWithCount(String name, int count) {
		totalWords = totalWords + count;
		for (Word word : words) {
			if (word.getName().equals(name)) {
				word.addCount(count);
				return;
			}
		}
		Word newWord = new Word(name, count);
		words.add(newWord);
		uniqueWords = uniqueWords + 1;
	}
	
	public void sortWords() {
		Collections.sort(words, new WordComparator());
	}
	

	// Print all words
	public void printWords() {
		System.out.println("\n All Words");
		sortWords();
		for (Word w : words) {
			System.out.println(w.getName() + " : " + w.getCount());
		}
		System.out.println("Unique Words : " + uniqueWordCount());
		System.out.println("Total Words : " + totalWordCount());
	}
	
	
	/* Option to print rare words
	 * Rare words are words whose frequency is
	 * less than or equal to a given threshold
	 */
	public void printRareWords(double threshold) {
		ArrayList<Word> rare = rareWords(threshold);
		for (Word w : rare) {
			System.out.println(w.getName() + " : " + 
				((float)100*w.getCount()/totalWords) + "%");
		}
	}
	
	// Used by printRareWords(double)
	public ArrayList<Word> rareWords(double threshold) {
		ArrayList<Word> rare = new ArrayList<Word>();
		for (Word w : words) {
			if (w.getCount() <= threshold * totalWords) {
				rare.add(w);
			}
		}
		return rare;
	}
}
