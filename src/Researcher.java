import java.util.ArrayList;
import java.util.Collections;

/*
 * Researcher class that keeps track of
 * each individual researcher's name and
 * words used
 */
public class Researcher {
	
	private String name;
	private ArrayList<Word> words;
	
	private Integer totalWords;
	private Integer uniqueWords;
	
	public Researcher(String n) {
		name = n;
		words = new ArrayList<Word>();
		totalWords = 0;
		uniqueWords = 0;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Word> getWords() {
		return words;
	}
	
	// Add a word to the researcher object
	// If new add new word object
	// If already present, increment count
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
	
	// Used when reading in researcher object from local file
	public void addWordWithCount(String name, int count) {
		totalWords = totalWords + count;
		Word newWord = new Word(name, count);
		words.add(newWord);
		uniqueWords = uniqueWords + 1;
	}
	
	public void sortWords() {
		Collections.sort(words, new WordComparator());
	}
	
	public Integer uniqueWordCount() {
		return uniqueWords;
	}
	
	public Integer totalWordCount() {
		return totalWords;
	}
	
	public void printWords() {
		System.out.println("\n " + name);
		sortWords();
		for (Word w : words) {
			System.out.println(w.getName() + " : " + w.getCount());
		}
		System.out.println("Unique Words : " + uniqueWordCount());
		System.out.println("Total Words : " + totalWordCount());
	}

}
