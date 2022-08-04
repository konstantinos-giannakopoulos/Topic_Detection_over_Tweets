package client;

//
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
//
import data.Parameters;

/**
 *
 *
 * @author kostas
 */
public class TopicDistribution {
    
    /** */
    public TopicDistribution() {
	
    }

    /**
     *
     */
    public int[] collect(String file, int windNum) {
	try {
	    FileReader reader = new FileReader("ldaOutFiles/" + file +  "-" + windNum + ".tassign.txt");

	    int[] numDocsPerTopic = readTassignFile(reader);
	    //for(int i = 0; i < numDocsPerTopic.length; i++)
	    //	System.out.println(i + "\t" + numDocsPerTopic[i]);
	    return numDocsPerTopic;
	} catch(FileNotFoundException fnfex) {
	    System.err.println("File not found!");
	    return null;
	}
    } // collect()

    
    // ------------------------------------------------------------------------------- //
    // ******************************************************************************* //
    // ******************************************************************************* //
    // ------------------------------------------------------------------------------- //
    // ˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆ //
    
    /* Reads *.tasssign file */
    private final int[] readTassignFile(FileReader filereader) {
	int numTopics = Parameters.getInstance().getNumTopics();
	int[] numDocsPerTopic = new int[numTopics];
	BufferedReader br = null;
	try {
	    br = new BufferedReader(filereader);  
	    String line;
	    while ((line = br.readLine()) != null) {
		// for each document
		int[] docWordsTopics = new int[numTopics];
		String [] wordTopics = line.split(" ");
		int docNumWords = wordTopics.length;
		// count how many words in each topic for this tweet - (docWordsTopics[topic]) 
		for(String wordTopic : wordTopics) {
		    // for each word
		    String[] tokens = wordTopic.split(":");
		    String word = tokens[0];
		    int topic = Integer.parseInt(tokens[1]);
		    docWordsTopics[topic]++;
		} // end of a tweet

		// collect max word assignments
		int maxIndex = -1;
		int max = -1;
		for(int i = 0; i < numTopics; i++) {
		    if(docWordsTopics[i] > max) {
			max = docWordsTopics[i];
			maxIndex = i;
		    }
		}
		numDocsPerTopic[maxIndex]++;
	    } // end of all tweets
	    return numDocsPerTopic;
	} catch (IOException e) { 
	    e.printStackTrace();
	} finally {   
	    try { if (br != null) br.close(); } catch (IOException ex) { ex.printStackTrace(); }
	    return numDocsPerTopic;
	}
    } // readWordsTopicsFile()
    
} // TopicDistribution
