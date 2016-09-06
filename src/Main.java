/*
 * Research Link
 * Created by Jeffrey Hrebenach
 * December 2015
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.io.*;
import org.jsoup.Jsoup;


/*
 * Aside from calling other classes, this main class has
 * methods to read all saved researcher files in as objects
 */
public class Main {

	// Define needed objects and a rare words threshold
	static ArrayList<Researcher> researchers;
	static ArrayList<String> ignoredWords;
	static AllWords allWords;
	static double rareThreshold = .01;

	public static void main(String[] args) {
		
		ignoredWords = new IgnoredWords().getIgnoredWords();
		researchers = new ArrayList<Researcher>();
		allWords = new AllWords();
		

		// Force user to choose to either run a comparison search or
		// to update the local files
		Scanner in = new Scanner(System.in);
		String choice = "0";
		while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4")) {
			System.out.println("Enter 1 to run a researcher comparison \nEnter 2 to run "
					+ "a query comparison \nEnter 3 to scan PubMed to update local files "
					+ "\nEnter 4 to exit");
			choice = in.next();
		}
		// Researcher compare
		if (choice.equals("1")) {
			readFromFile();										// read saved researcher files in
			printResearchers();
			runResearcherComparison();
		}
		// Query compare
		else if (choice.equals("2")) {
			readFromFile();										// read saved researcher files in
			runQueryComparison();
		}
		// Update local files
		else if (choice.equals("3")) {
			System.out.println(new EntrezGen().getURL());		// for generating researcher from Entrez
		}
		// Exit
		else {
			System.out.println("Program terminated");
			return;
		}

	}
	
	// Run to do a researcher compare
	private static void runResearcherComparison() {
		Scanner in = new Scanner(System.in);
		String rName = "0";
		System.out.println("Enter the name of the researcher you'd like"
				+ " to find matches for (e.g. mccall mn)");
		rName = in.nextLine();
		researcherCompare(rName);
	}
	
	
	// Method compares an inputed research to all others researchers
	public static void researcherCompare(String rName) {
		int z = -1;
		ResearcherCompare rc;
		Researcher r1 = new Researcher("not found");
		boolean found = false;
		ArrayList<AuthorRank> rankList = new ArrayList<AuthorRank>();
		int counter = -1;
		
		// Find primary researcher
		for (int i = 0; i < researchers.size(); i++) {
			counter++;
			if (counter % 10000 == 0) {
				System.out.println("Finding inputted researcher...");
			}
			if (researchers.get(i).getName().equalsIgnoreCase(rName)) {
				r1 = researchers.get(i);
				z = i;
				found = true;
				break;
			}
		}
		if (!found) {
			System.out.println("Researcher not found!");
			runResearcherComparison();						// Recurse backwards, let user enter new name
			return;
		}
		// Compare researcher to all others
		for (int i = 0; i < researchers.size(); i++) {
			counter++;
			if (counter % 10000 == 0) {
				System.out.println("Comparing query to researchers...");
			}
			if (i != z) {
				rc = new ResearcherCompare(r1, researchers.get(i));	// compare each other researcher to ours
				double sim = rc.getSimilarity();
				String name = researchers.get(i).getName();
				rankList.add(new AuthorRank(name, sim));
			}
		}
		
		// Print top 30 matches
		System.out.println("Closest matches to " + rName);
		Collections.sort(rankList, new SimilarityComparator());
		for (int i = 0; i < 30; i++) {
			System.out.println((i+1) + " : " + rankList.get(i).getName() + " : " + (int)rankList.get(i).getSimilarity() + " matches");
		}
	}
	
	// Run to do a query compare
	private static void runQueryComparison() {

		ArrayList<String> queryList = new ArrayList<String>();
		Scanner in = new Scanner(System.in);
		String term = "";
		while (!term.equals("s")) {
			
			// Ask for query terms
			System.out.println("Enter query term \nEnter s to finish"
					+ " building the query list \nEnter r to rebuild"
					+ " your list");
			term = in.next();
			if (!term.equals("") && !term.equals("s") && !term.equals("r")) {
				queryList.add(term.toLowerCase());
			}
			if (term.equals("r")) {
				runQueryComparison();								// Restart process
				return;
			}
			// Print the current list
			System.out.println("Current Query List:");
			for (String s : queryList) {
				System.out.println(s);
			}
			System.out.println();
		}
		if (queryList.isEmpty()) {
			System.out.println("No queries entered. Program terminated");
			return;
		}
		else {
			queryCompare(queryList);		// For generating researcher from Entrez
		}
	}
	
	// Search for Researchers based on a query
	public static void queryCompare(ArrayList<String> queryList) {
		QueryCompare qc;
		ArrayList<AuthorRank> rankList = new ArrayList<AuthorRank>();
		int counter = -1;
		for (int i = 0; i < researchers.size(); i++) {
			counter++;
			if (counter % 10000 == 0) {
				System.out.println("Comparing query to researchers...");
			}
			qc = new QueryCompare(queryList, researchers.get(i));	// compare each researcher to query
			double sim = qc.getSimilarity();
			String name = researchers.get(i).getName();
			rankList.add(new AuthorRank(name, sim));
		}
		Collections.sort(rankList, new SimilarityComparator());
		for (int i = 0; i < 30; i++) {
			System.out.println((i+1) + " : " + rankList.get(i).getName() + " : " + (int)rankList.get(i).getSimilarity() + " matches");
		}
		
	}
	
	
	
	// Read in all researcher objects from local directory
	private static void readFromFile() {	
		final File folder = new File("researcherFile");		// my personal directory is /Users/jhrebena/researchLink
		int counter = -1;
	    for (File fileEntry : folder.listFiles()) {
	    	counter++;
	    	if (counter % 10000 == 0) {
	    		System.out.println("Reading in files...");
	    	}
	    	// OS X specific hidden file
	    	if (fileEntry.getName().equals(".DS_Store")) {
	    		continue;
	    	}
	    	Researcher r = new Researcher(fileEntry.getName());
	    	
	    	// This is where files are read in as Researcher objects
	    	Scanner scan;
			try {
				scan = new Scanner(fileEntry);
				
		        while (scan.hasNextLine()) {
		        	String line = scan.nextLine();
		        	String[] res = line.split(" ");
					r.addWordWithCount(res[0], Integer.parseInt(res[1]));
					allWords.addWordWithCount(res[0], Integer.parseInt(res[1]));
		        }
		        
				scan.close();
				r.sortWords();
				researchers.add(r);
		        
		        
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    }
	}
	
	// PRINT METHODS
	
	// Print all researchers along with their words
	private static void printAll() {
		for (Researcher r : researchers) {
			r.printWords();
		}
	}
	
	// Print all researcher names
	public static void printResearchers() {
		for (Researcher r : researchers) {
			System.out.println(r.getName());
		}
	}
	
	// Print data on all scanned words from AllWords class object
	public static void printWords() {
		float total = 0;
		for (Word w : allWords.getWords()) {
			float freq = ((float)w.getCount()*100) / allWords.totalWordCount();
			total += freq;
			System.out.println(w.getName() + "  :  " + freq + "%");
			System.out.println(total);
		}
	}
	
	// END PRINT METHODS
	
	

	// TEST METHODS
	
	// Test a query comparison
	// compare a query list to all researchers
	public static void queryCompareTest() {
		ArrayList<String> queryList = new ArrayList<String>();		// making a query...
		queryList.add("fertility");									// ...
		queryList.add("abortion");									// ...
		queryList.add("intervention");								// ...
	}
	
	// END TEST METHODS
	
	// CURRENTLY UNUSED METHODS

	private static void read(String u, String name) {

		try {
		
			URL url = new URL(u);
			InputStream in = url.openStream();
			Scanner scan = new Scanner(in);
			Researcher curr = new Researcher(name);
			

			
			while (scan.hasNextLine())
			{
				String line = scan.nextLine();
			    String s = Jsoup.parse(line).body().text().toLowerCase();
				String[] res = s.split("[\\p{Punct}\\s]+");
				for (int i = 0; i < res.length; i++) {
					boolean isIgnored = false;
					for (String str : ignoredWords) {
					    if (!res[i].equals(str)) {
					    	continue;
					    }
					    else {
					    	isIgnored = true;
					    	break;
					    }
					}
					if (!isIgnored) {
						curr.addWord(res[i]);
						allWords.addWord(res[i]);
					}
				}

			}
	            
	        in.close();
			scan.close();
			curr.sortWords();
			researchers.add(curr);

		}
		
		
		
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// END CURRENTLY UNUSED METHODS

}
