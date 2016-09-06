import java.util.ArrayList;

/*
 * Class is used to compare a query to all researchers
 */

public class QueryCompare {

		double rareThreshold = 1;
		Researcher researcher;
		ArrayList<String> query;
		ArrayList<Word> rareWords;
		ArrayList<Word> researcherWords;
		double similarity;
		
		public QueryCompare(ArrayList<String> q, Researcher r) {
			query = q;
			researcher = r;
			researcherWords = researcher.getWords();
			similarity = (compare());

			// Optional - print out similarity of all pairs
//			System.out.println("(Query compare) " + researcher.getName() + "matches " +
//					getSimilarity() + "%");
		}
		
		// compare based on all words
		public double compare() {
			int similarityCount = 0;
			for (String s : query) {
				for (Word w : researcherWords) {
					if (w.getName().equals(s)) {
						similarityCount = similarityCount + 1;
					}
				}
			}
//			return ((double)similarityCount / (double)researcher.totalWordCount());
			return ((double)similarityCount);
		}
		
		public double getSimilarity() {
			return similarity;
		}

	}
