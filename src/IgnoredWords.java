import java.util.ArrayList;
import java.util.Arrays;

// A hard-coded list of common words that are uninteresting

public class IgnoredWords {
	
	ArrayList<String> ignored;
	
	public IgnoredWords() {
		ignored = new ArrayList<String>(
			Arrays.asList("", " ", "the", "be", "to", "of",
				"and", "a", "in", "that", "have", "i",
				"it", "for", "not", "on", "with", "he",
				"as", "you", "do", "at", "this", "but",
				"his", "by", "from", "they", "we", "say",
				"her", "she", "or", "an", "will", "my", "one",
				"all", "would", "there", "their", "what",
				"so", "up", "out", "if", "about", "who", "get",
				"which", "go", "me", "when", "make", "can",
				"like", "time", "no", "just", "him", "know",
				"take", "people", "into", "year", "your",
				"good", "some", "could", "them", "see",
				"other", "than", "then", "now", "look", "only",
				"come", "its", "over", "think", "also", "back",
				"after", "use", "two", "how", "our", "work",
				"first", "well", "way", "even", "new", "want",
				"because", "any", "these", "give", "day",
				"most", "us", "am", "is", "professor", "via", 
				"too", "many", "such", "edu", "cv", "are", "lecture",
				"courses", "taught", "teach", "assistant", "info", 
				"information", "pdf", "field", "semester", "semeseters"));
	}
	
	public ArrayList<String> getIgnoredWords() {
		return ignored;
	}
	
	public void printIgnoredWords() {
		for (String s : ignored) {
			System.out.println(s);
		}
	}

}
