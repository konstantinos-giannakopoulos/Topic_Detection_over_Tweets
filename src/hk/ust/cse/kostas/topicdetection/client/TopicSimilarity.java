package client;

//
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
//
import java.io.File;
import java.io.InputStream; 
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
//
import data.Parameters;
import client.Statistics;

public class TopicSimilarity {

    //private int commonTopics;
    Map<Integer,Integer> topicsMap;
    
    public TopicSimilarity() {
	//this.commonTopics = 0;
	this.topicsMap = new HashMap<Integer,Integer>();
    }

    public Map<Integer,Integer> getTopicsMap() { return this.topicsMap; }

    public int check(String gtModelName, int gtWin, String iModelName, int iWin) {
	//System.out.println(Parameters.getInstance().getWindowIncremental());
	int commonTopics = similar(gtModelName, gtWin, iModelName, iWin);
	//if(commonTopics == 0) {
	    //Parameters.getInstance().makeWindowIncrementalFalse();
	    ////mywriter.write("Re-estimation on next ("+ (this.windowID+1) + ") window.\n");
	    ////System.out.println("Topic Similarity: Re-estimation on next window.\n");
	//} else {
	//Parameters.getInstance().makeWindowIncrementalTrue();
	//}
	//Statistics.getInstance().appendFile(Parameters.getInstance().getWindowNumber() + "\t" +
	//				    commonTopics + "\n");
	//System.out.println(Parameters.getInstance().getWindowNumber() + "\t" +
			   //Parameters.getInstance().getWindowIncremental() + "\t" +
			   //commonTopics + "\n");
	return commonTopics;
    } // check()

    
    // ------------------------------------------------------------------------------- //
    // ******************************************************************************* //
    // ******************************************************************************* //
    // ------------------------------------------------------------------------------- //
    // ˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆ //

    /**
     *
     * @param gtModelName:
     * @param gtWin: 
     * @param iModelName:
     * @param iWin: 
     */
    private final int similar(String gtModelName, int gtWin, String iModelName, int iWin) {
	String methodName = Thread.currentThread().getStackTrace()[1].getClassName() + "."
	    + Thread.currentThread().getStackTrace()[1].getMethodName();
	//System.out.println("Method: [" + methodName + "] Start comparing data.");
	//System.out.println("Window\tCommonTopics");

	if(Parameters.getInstance().getWindowIncremental() == false)
	    return 0;
	
	int numTopics = Parameters.getInstance().getNumTopics();
	int wordsPerTopic = Parameters.getInstance().getNumWordsPerTopic();
	
	// threshold
	int thresCommonWords = 3; // 3/17 or 4/16 or 5/15
	double threshold = (1.0 * thresCommonWords / ((2.0 * wordsPerTopic)-thresCommonWords));// 3/17
	
	String[][] gtWordsTopic; 
	String[][] iWordsTopic;
	int commonTopics = 0;	
	try {
	    gtWordsTopic = new String[numTopics][wordsPerTopic];
	    iWordsTopic = new String[numTopics][wordsPerTopic];
	    
	    File checkFileprev = new File("ldaOutFiles/" + gtModelName + "-"+ gtWin +".twords.txt");//
	    File checkFilecur = new File("ldaOutFiles/" + iModelName + "-"+ iWin +".twords.txt");
	    if((checkFilecur.exists() == false) || (checkFileprev.exists() == false) )
		return -1;

	    FileReader gtWordIndexReader =
		new FileReader("ldaOutFiles/" + gtModelName + "-" + gtWin + ".twords.txt");
	    FileReader iWordIndexReader =
		new FileReader("ldaOutFiles/" + iModelName + "-" + iWin + ".twords.txt");
		
	    gtWordsTopic = readWordsTopicsFile(gtWordIndexReader, numTopics, wordsPerTopic);
	    iWordsTopic = readWordsTopicsFile(iWordIndexReader, numTopics, wordsPerTopic);
	    
	    List<String> gtWords = new ArrayList<String>();
	    List<String> iWords = new ArrayList<String>();
	    for(int t1 = 0; t1 < numTopics; t1++) {
		gtWords.clear();
		for(int i = 0; i < wordsPerTopic; i++) {
		    gtWords.add(gtWordsTopic[t1][i]);
		}
		for(int t2 = 0; t2 < numTopics; t2++) {
		    iWords.clear();
		    for(int j = 0; j < wordsPerTopic; j++) {
			iWords.add(iWordsTopic[t2][j]);
		    }
 		    // Jaccard 
		    Set<String> intersection = new HashSet<String>(gtWords);
		    Set<String> union = new HashSet<String>(gtWords);
		    intersection.retainAll(iWords);
		    union.addAll(iWords);
		    double jaccard = (1.0 * intersection.size() / union.size());
		    if(jaccard >= threshold) {
			commonTopics++;
			this.topicsMap.put(t1,t2); //(gt,i)
			//System.out.println(intersection.size() + "\t" + union.size() + "\t" + jaccard);
			break;
		    }
		}
	    }
	    // similar topic between models
	    //printTopicsMap(); 
	    return commonTopics;
	} catch(FileNotFoundException fnfex) {
	    System.err.println("File not found!");
	    return 0;
	}
    } // similar()

    
    // ------------------------------------------------------------------------------- //
    // ******************************************************************************* //
    // ******************************************************************************* //
    // ------------------------------------------------------------------------------- //
    // ˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆ //
    
    /* Reads *.twords.txt file */
    private final String[][] readWordsTopicsFile(FileReader filereader, int numTopics, int topWords) {
	BufferedReader br = null;
	String[][] topicsWords = new String[numTopics][topWords];
	//double prob = 0.0;
	try {
	    br = new BufferedReader(filereader);  
	    String line;
	    int topicIndex = -1;
	    int wordIndex = -1;
	    while ((line = br.readLine()) != null) {  
		if(line.startsWith("Topic") && (line.endsWith("th:"))) {
		    topicIndex++;
		    wordIndex = -1;
		} else {
		    String word = line.split(" ")[0];
		    wordIndex++;
		    topicsWords[topicIndex][wordIndex] = word;
		}		
	    }
	} catch (IOException e) { 
	    e.printStackTrace();
	} finally {   
	    try { if (br != null) br.close(); } catch (IOException ex) { ex.printStackTrace(); }
	    return topicsWords;
	}
    } // readWordsTopicsFile()

    public void printTopicsMap() {
	for (Map.Entry<Integer, Integer> entry : this.topicsMap.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + " ~ " + value);  
	} 
    }
        
} // TopicSimilarity
