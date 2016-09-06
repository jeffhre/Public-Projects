import java.util.ArrayList;

/*
 * Compare 2 researchers to each other to determine their similarity
 */
public class ResearcherCompare {
	
	private double rareThreshold = 1;
	private Researcher r1;
	private Researcher r2;
	private ArrayList<Word> list1;
	private ArrayList<Word> list2;
	private ArrayList<Word> rareWords;
	private double similarity;
	
	public ResearcherCompare(Researcher r01, Researcher r02) {
		r1 = r01;
		r2 = r02;
		list1 = r1.getWords();
		list2 = r2.getWords();
		similarity = (compare());

		// Optional print messages used for debugging
//		System.out.println("(all word compare)" + r1.getName() + " and " + r2.getName() + " are " +
//				(compare()*100) + "% similar");
//		System.out.println("(rare word compare)" + r1.getName() + " and " + r2.getName() + " are " +
//				(compareRare()*100) + "% similar");
	}
	
	public double getSimilarity() {
		return similarity;
	}
	
	// Find number of word matches between researchers
	// Matches found for a particular word equals min(count(r1), count(r2))
	private double compare() {
		int similarityCount = 0;
		for (Word w1 : list1) {
			for (Word w2 : list2) {
				if (w1.getName().equals(w2.getName())) {
					similarityCount = similarityCount + Math.min(w1.getCount(), w2.getCount());
				}
			}
		}
		return (double) similarityCount;
	}
	
	// CURRENTLY UNUSED
	
	// only evaluate words with low frequency
	private double compareRare() {
		int similarityCount = 0;
		for (Word w1 : list1) {
			for (Word w2 : list2) {
				if (w1.getName().equals(w2.getName())) {
					for (Word w3 : rareWords) {
						if (w1.getName().equals(w3.getName())) {
							similarityCount = similarityCount + Math.min(w1.getCount(), w2.getCount());
							break;
						}
					}
				}
			}
		}
		return ((double)similarityCount / (double)r1.uniqueWordCount());
	}
	
	// END CURRENTLY UNUSED

}
