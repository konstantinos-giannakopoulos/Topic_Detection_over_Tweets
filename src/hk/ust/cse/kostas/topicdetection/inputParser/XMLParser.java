package inputParser; 

// java
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
//import java.util.NoSuchElementException;

// read XML file
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

// read file
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;

// Date - time
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;

// lib

// mine
import client.Tweet;
import data.Data;
import data.Dictionary;
import model.MultinomialDistribution;
import lexical.Lexical;


/**
 * XMLParser: Parses XML imput files and collect all tweets in a list.
 * Also, collects data: locations, timezones, dicrtionary.
 *
 * @author kostas
 */
public class XMLParser {

    /* Path where input XML files are stored. */
    private final String INPUT_FOLDER = "input/";

    Calendar calendar = GregorianCalendar.getInstance(); 
    DateFormat print = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy",Locale.ENGLISH);

    /* filename for mapping English to Chinese locations; locmap.txt */
    private Map<String,String> englishLocationsMap; 
    private Lexical lexical;

    /* list where all tweets are stored from the XML files. */
    List<Tweet> fileTweets = new ArrayList<Tweet>();

    /* filename for mapping words; wordmap.txt */
    private String wordMapFile;
    private Dictionary dictionary;

    private List<BigDecimal> filteredTweets;

    public XMLParser() {
	/* merge chinese with english location names */
	this.englishLocationsMap = new HashMap<String,String>();  
	readMapLocations();
	this.lexical = new Lexical();
	this.wordMapFile = "ldaFiles/wordmap.txt";
	this.dictionary = Data.getInstance().getDictionary();

	this.filteredTweets = new ArrayList<BigDecimal>();
	this.filteredTweets = readFileWithFilteredTweets();
    } // XMLParser()

    /**
     * Merges english with chinese location names, saved in locmap.txt file
     */
    private void readMapLocations() {
	try {
	    BufferedReader reader = new BufferedReader(new InputStreamReader
						       (new FileInputStream("locmap.txt"), "UTF-8"));
	    String line;
	    while((line = reader.readLine()) != null) { 
		java.util.StringTokenizer tokens = new java.util.StringTokenizer(line,":");
		String chineseTerm = tokens.nextToken(); //tokens[0];
		String englishTerm = tokens.nextToken(); //tokens[1];
		
		this.englishLocationsMap.put(chineseTerm,englishTerm);
	    }	        
	    reader.close();
	} catch (java.io.IOException ioe) {
	    System.out.println("Error in mapping locations file"); 
	}
	fillinDataLists();
    } // readMapLocations()


    /*
    private String convertTwitterDate(String old_date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        SimpleDateFormat old = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy",Locale.ENGLISH);
        old.setLenient(true);	
	Date date = null;
	try {	    
	    date = old.parse(old_date);	    
	} catch (ParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}	
        return sdf.format(date);    
	}*/

    private List<BigDecimal> readFileWithFilteredTweets() {
	List<BigDecimal> filteredTweets = new ArrayList<BigDecimal>();
	try {
	    BufferedReader reader =
		new BufferedReader(new InputStreamReader
				   (new FileInputStream("filteredDocIDs.txt"), "UTF-8"));
	    String line;
	    while((line = reader.readLine()) != null) { 
		BigDecimal tid = new BigDecimal(line);
		filteredTweets.add(tid);
	    }	        
	    reader.close();
	} catch (java.io.IOException ioe) {
	    System.out.println("Error in mapping locations file"); 
	}
	return filteredTweets;
    }
    
    /**
     *
     */
    public List<Tweet> readXMLFileWithTweets() {
	//List<Tweet> tweets = new ArrayList<Tweet>();
	try {
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = null;
	    NodeList tweetsList = null;

	    String[] filesArray = new String[5]; // 16
	    /*
	    // (A) [8] [1-6b]: 21/12 - 3/1
	    filesArray[0] = "dataset1.xml"; filesArray[1] = "dataset2.xml";  //[1-6b]  21/12-3/1
	    filesArray[2] = "dataset3.xml"; //filesArray[3] = "dataset4.xml"; 
	    filesArray[3] = "dataset4a.xml"; filesArray[4] = "dataset4b.xml"; 
	    filesArray[5] = "dataset5.xml"; //filesArray[5] = "dataset6.xml"; 
	    filesArray[6] = "dataset6a.xml"; filesArray[7] = "dataset6b.xml";
	    */
	    //
	    //// filesArray[] = "dataset7a.xml"; filesArray[] = "dataset7b.xml"; //[7] 4/1-9/1
	    //// filesArray[] = "dataset8.xml"; // [8] 11/1(3am)-14/1
	    //
	    
	    // (B) [5] [9-12]: 15/1 - 25/1
	    filesArray[0] = "dataset9.xml";  // [9-12] 15/1-25/1	    
	    filesArray[1] = "dataset10.xml"; filesArray[2] = "dataset11.xml"; //[10-12] 16/1-25/1
	    filesArray[3] = "dataset12a.xml"; filesArray[4] = "dataset12b.xml"; 
            
	    // (C) [1] [16]: 28/1 - 14/2
	    //filesArray[0] = "dataset16.xml"; // 28/1- 14/2
	    
	    for(String file : filesArray) { 
		String filepath = INPUT_FOLDER + file; 
		doc = dBuilder.parse(filepath);    
		tweetsList = doc.getElementsByTagName("tweet");    
		readXMLFile(tweetsList);
		doc = null;
		tweetsList = null;
		for(int i = 0; i < 10; i++)
		    System.gc();
		System.out.println("Num of tweets: " + this.fileTweets.size());
	    } // end for-loop
	    // write dictionary file
	    this.dictionary.writeWordMap(this.wordMapFile);
	    System.out.println(this.dictionary.getSize());
	    //System.exit(-1);
	} catch(javax.xml.parsers.ParserConfigurationException pcex) {
	    //
	} catch(org.xml.sax.SAXException saxex) {
	    //
	} catch (java.io.IOException ioex) {
	    //
	}
	return this.fileTweets;
    } // readXMLFileWithTweets()
    
    /**
     * Fills in locations and timezones in data.Data  
     */
    private void fillinDataLists() {
	for (Map.Entry<String, String> entry : this.englishLocationsMap.entrySet()) {
	    String key = entry.getKey(); // in chinese
	    String value = entry.getValue(); // in english
	    Data.getInstance().addLocation(value);
	}
	Data.getInstance().addTimezone("d");
	Data.getInstance().addTimezone("n");
	//System.out.println(Data.getInstance().getLocations() + "\t"
	//+ Data.getInstance().getLocations().size());
	//System.out.println(Data.getInstance().getTimezones() + "\t"
	//+ Data.getInstance().getTimezones().size());
	//System.exit(0);
    } //

    /**
     *
     */
    private void fillinDataDictionary(String tweetmessage) {
	String msg = tweetmessage.replace("\n"," ");
	List<String> tokens = lexical.process(msg);
	for(String token : tokens) {
	    if(!token.equals("rt") && (!token.startsWith("https://")) && (!token.startsWith("@")) )
		if(token.length() < 15)
		    this.dictionary.addWord(token);
	}
    } //

    /**
     *
     * fills this.fileTweets arraylist
     */
    private void readXMLFile(NodeList tweetsList) {//(Document doc) {//(String filepath) {
	//String className = this.getClass().getSimpleName();
	String className = getClass().getName();//.substring(0, getClass().getName().indexOf("$"));
	System.out.println("[class]" + className);
	/* */
	//NodeList tweetsList = doc.getElementsByTagName("tweet");    
	for (int counter = 0; counter < tweetsList.getLength(); counter++) {
	    Node tweetNode = tweetsList.item(counter);
	    if (tweetNode.getNodeType() == Node.ELEMENT_NODE) {
		Element tweetsElement = (Element) tweetNode;
		// message
		NodeList message = tweetsElement.getElementsByTagName("message"); //
		Element messageElement = (Element) message.item(0);//
		// message - id
		NodeList messageId = messageElement.getElementsByTagName("id");//
		String strMessageId = messageId.item(0).getTextContent();  //
		BigDecimal tid = new BigDecimal(strMessageId);
		if(!this.filteredTweets.contains(tid))
		    continue;
		// message - timestamp
		NodeList messageTs = messageElement.getElementsByTagName("timestamp");//
		String strMessageTs = messageTs.item(0).getTextContent();//
		// message - text
		NodeList messageText = messageElement.getElementsByTagName("text");//
		//String strMessageText = "";
		String strMessageText = messageText.item(0).getTextContent();//
		message = null;
		messageElement = null;
		messageId = null;
		messageTs = null;
		messageText = null;
		// location
		NodeList location = tweetsElement.getElementsByTagName("location");
		Element locationElement = (Element) location.item(0);
		// location - name
		NodeList locationName = locationElement.getElementsByTagName("name");
		String strLocationName = locationName.item(0).getTextContent();		    
		location = null;
		locationElement = null;
		locationName = null;
		
		Tweet tweet = new Tweet();
		tweet.id = new BigDecimal(strMessageId);
		tweet.message = strMessageText;
		// time
		try {
		    tweet.timestamp = print.parse(strMessageTs);		    
		    calendar.setTime(tweet.timestamp);
		    int hour = calendar.get(Calendar.HOUR_OF_DAY);
		    int minute = calendar.get(Calendar.MINUTE);   
		    if(hour >= 6 && hour < 18) {
			tweet.time = "d";
		    } else {
			tweet.time = "n";
		    }
		} catch(Exception pex) { 
		    System.out.println("parse error (1)");
		    System.out.println(strMessageTs + "\t" + tweet.timestamp);
		    System.exit(1);
		}
		
		for (Map.Entry<String, String> entry : this.englishLocationsMap.entrySet()) { 
		    String key = entry.getKey(); 
		    String value = entry.getValue(); 
		    if(key.equals(strLocationName)) {  
			strLocationName = value; 
			break;
		    }
		}
		if(Data.getInstance().getLocations().contains(strLocationName))
		    tweet.location = strLocationName;
		else
		    continue;

		// add tweet
		this.fileTweets.add(tweet);
		// add tweet words to dictionary
		fillinDataDictionary(tweet.message);
	    }
	} // end for loop
	//System.out.println(Data.getInstance().getDictionarySize());
    } // readXMLFile()

    /*
     * Debug main.
     *
    public static void main(String [] args) {
	System.out.println(">>>   XMLParser.main()   <<<");
	XMLParser xmlparser = new XMLParser();
	List<Tweet> tweets = xmlparser.readXMLFileWithTweets();
	System.out.println("#tweets: " + tweets.size());
	//for(Tweet tweet : tweets)
	//  System.out.println(tweet.shortTweetToString()); 
	System.out.println("#timezones: " + xmlparser.getTimezones().size());
	System.out.println("#locations: " + xmlparser.getLocations().size());
	System.out.println(">>>   XMLParser close.   <<<");
    } // main() 
*/

} // JSONParser
