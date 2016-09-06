/*
 * Word object keeps track of a word's
 * spelling and the amount of times it
 * has been used by a given researcher
 */

public class Word {
	
	private String name;
	private Integer count;
	
	public Word(String n) {
		name = n;
		count = 1;
	}
	
	public Word(String n, int c) {
		name = n;
		count = c;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void countUp() {
		count = count + 1;
	}
	
	public void addCount(int x) {
		count = count + x;
	}

}
