import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* This is the class that interacts with the NCBI PubMed database
 * Class communicates with NCBI's Entrez XML interface
 * Program receives XML output in form of article IDs based on a given query
 * Program then sends queries to get more information about these articles
 * Program lastly stores Researcher objects locally in the form of text, since
 * it takes lots of time to communicate with NCBI
 * Therefore, this class only needs to be created periodically for updating data
 */
public class EntrezGen {
	
	// These are for concatenating Entrez query strings, which return the needed XML
	static String base = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
	static String db = "pubmed";
	static String query = "\"University of Rochester\"";
	static String search = "esearch";
	static String url = base + search + ".fcgi?db=" + db + "&term=" + query + "&retmax=100000" 
							+ "&usehistory=y" + "&reldate=3650"; // articles written within past 10 years
	static String summaryUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id=";
	static String eFetchUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&retmode=xml&id=";
	
	// To make the initial researcher objects
	static ArrayList<Researcher> researchers;
	
	// To store global word data
	static AllWords allWords;
	
	// To eliminate uninteresting words
	static ArrayList<String> ignoredWords;
	
	
	ArrayList<String> IdList = new ArrayList<String>();

	public EntrezGen() {
		
		researchers = new ArrayList<Researcher>();
		allWords = new AllWords();
		ignoredWords = new IgnoredWords().ignored;
		
		
		// Prepare and read XML data from Entrez
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dbuilder = builderFactory.newDocumentBuilder();

			Document document = dbuilder.parse(new URL(url).openStream());
//			Document document = dbuilder.parse("src/test.xml");		// test document (much quicker)
			document.normalize();
			
			NodeList countList = document.getElementsByTagName("Count");
			Node count = countList.item(0);
			Element c = (Element) count;
			
			// Print concatenated URL (debug)
			System.out.println(url);
			// Print total returned papers (debug)
			System.out.println("Total papers found : " + c.getTextContent());
			
			// Get all Id tag elements
			NodeList ids = document.getElementsByTagName("Id");
			System.out.println("nodelist length is " + ids.getLength());
			
			// Get all document IDs, print and add to IdList
			for (int i = 0; i < ids.getLength(); i++) {
				Node n = ids.item(i);
				Element e = (Element) n;
				IdList.add(e.getTextContent());
				System.out.println(e.getTextContent());
				
			}
			
			// Take all document IDs and fetch information for each article
			for (String id : IdList) {
				String sumUrl = summaryUrl + id;
				System.out.println(sumUrl);
				Document sumDoc = dbuilder.parse(new URL(sumUrl).openStream());
				sumDoc.normalize();
				NodeList nodeAuthor = sumDoc.getElementsByTagName("Item");
				
				ArrayList<String> authorList = new ArrayList<String>();
				
				String title = "";
				
				// Keep track of how many authors each article has
				int acount = 0;
				for (int i = 0; i < nodeAuthor.getLength(); i++) {
					Node n = nodeAuthor.item(i);
					Element e = (Element) n;
					if (e.getAttribute("Name") != null && e.getAttribute("Name").equals("Author")) {
						System.out.println(e.getTextContent());
						authorList.add(e.getTextContent());
						acount++;
					}
					else if (e.getAttribute("Name") != null && e.getAttribute("Name").equals("Title")) {
						System.out.println("TITLE :   " + e.getTextContent());
						title = e.getTextContent();
					}
				}
				System.out.println(acount + " authors");
				
				// Read in paper for each Researcher object
				for (String a : authorList) {
					// normalize author names s.t. there are no middle initials (McCall MN = McCall M)
					
					String aName = a;
					String[] arr = a.split(" ");
					if (arr.length == 2) {
						aName = arr[0] + " " + arr[1].charAt(0);
					}
					// read() method adds article title information to each included researcher's object
					read(title, aName);
				}
				try {
					// Entrez limits queries to 3 per second
					Thread.sleep(340);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* All articles are scanned, print researchers and words for programmer's information.
		 * Also write all researcher objects to file for future use (much faster than running
		 * this each time)
		*/ 																
		printResearchers();
		printWords();
		writeToFile();
		
	}
	
	private static void read(String u, String name) {

		String[] ary = u.split(" ");		// split input into separate words
		Researcher curr = new Researcher(name);
		
		// Check if researcher already has an object
		boolean newResearcher = true;
		for (Researcher r : researchers) {
			if (r.getName().equals(name)) {
				curr = r;
				newResearcher = false;
			}
		}

		
		for (int i = 0; i < ary.length; i++) {
			// Get element in lowercase with no punctuation. Only consider non-ignored words.
		    String s = ary[i].replaceAll("[^A-Za-z]+", "").toLowerCase();
			boolean isIgnored = false;
			for (String str : ignoredWords) {
			    if (!s.equals(str)) {
			    	continue;
			    }
			    else {
			    	isIgnored = true;
			    	break;
			    }
			}
			if (!isIgnored) {
				curr.addWord(s);
				allWords.addWord(s);
			}
		}
        if (newResearcher) {
        	researchers.add(curr);
        }

	}
	
	public String getURL() {
		return url;
	}
	
	// Print all researcher names
	public void printResearchers() {
		for (Researcher r : researchers) {
			System.out.println("name : " + r.getName());
		}
	}
	
	// Print all words with their frequency
	public void printWords() {
		for (Word w : allWords.getWords()) {
			float freq = ((float)w.getCount()*100) / allWords.totalWordCount();
			System.out.println(w.getName() + "  :  " + freq + "%");
		}
	}
	
	
	
	
	// Write to external files to avoid re-reading
	public void writeToFile() {
		for (Researcher r : researchers) {
			try {
				String content = "This is the content to write into file";
				
				// Print to my system locally
				File file = new File("/Users/jhrebena/researchLink2/" + r.getName());
	
				// If file doesn't exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
	
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				for (Word w : r.getWords()) {
					bw.write(w.getName() + " " + w.getCount() + '\n');
				}
				bw.close();

	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// TESTING METHODS
	
	// Compare first researcher to all others
	public void compareToFirstTest() {
		Researcher r = researchers.get(0);
		for (int i = 0; i < researchers.size(); i++) {
			new ResearcherCompare(r, researchers.get(i));
		}
	}
	
	// Compare a query list to all researchers
	public void compareToQueryTest() {
		ArrayList<String> queryList = new ArrayList<String>();		// making the test query...
		queryList.add("fertility");									// ...
		queryList.add("abortion");									// ...
		queryList.add("intervention");								// ...
		QueryCompare qc;
		ArrayList<AuthorRank> rankList = new ArrayList<AuthorRank>();
		for (int i = 0; i < researchers.size(); i++) {
			qc = new QueryCompare(queryList, researchers.get(i));	// compare each researcher to query
			double sim = qc.getSimilarity();
			String name = researchers.get(i).getName();
			rankList.add(new AuthorRank(name, sim));
		}
		Collections.sort(rankList, new SimilarityComparator());		// Sort researchers by similarity to query
		// Print top 30 most relevant researchers
		for (int i = 0; i < 30; i++) {
			System.out.println((i+1) + " : " + rankList.get(i).getName() + " : " + rankList.get(i).getSimilarity() + "%");
		}
	}
	
	// END TESTING METHODS
	
}
