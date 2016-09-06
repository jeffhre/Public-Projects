/* This class is used in comparison to pair up author objects with
 * their similarities to the input author / query
 */

public class AuthorRank {
	
	double similarity;
	String name;
	
	public AuthorRank(String n, double s) {
		name = n;
		similarity = s;
	}
	
	public String getName() {
		return name;
	}
	
	public double getSimilarity() {
		return similarity;
	}

}
