import java.util.Comparator;

/*
 * Used to sort words by count (high to low)
 */

public class WordComparator implements Comparator<Word> {

	@Override
	public int compare(Word a, Word b) {	
		return -a.getCount().compareTo(b.getCount());
	}
	
}