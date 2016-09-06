import java.util.Comparator;

/*
 * Used to sort AuthorRank objects by similarity
 */

public class SimilarityComparator implements Comparator<AuthorRank> {

	@Override
	public int compare(AuthorRank a, AuthorRank b) {	
		if (a.getSimilarity() > b.getSimilarity()) {
			return -1;
		}
		else if (a.getSimilarity() < b.getSimilarity()) {
			return 1;
		}
		return 0;
	}
	
}