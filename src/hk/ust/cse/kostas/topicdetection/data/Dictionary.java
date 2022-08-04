package data;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
// read file
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
// write file
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

/**
 * Dictionary: 
 *
 */
public class Dictionary {

    private Map<String,Integer> word2id;
    private Map<Integer, String> id2word;
    
    protected Dictionary() {
	word2id = new HashMap<String, Integer>();
	id2word = new HashMap<Integer, String>();
    } //

        	
    //---------------------------------------------------
    // get/set methods
    //---------------------------------------------------
    
    protected String getWord(int id){
	return id2word.get(id);
    }
    
    protected Integer getID (String word){
	return word2id.get(word);
    }
	
    //----------------------------------------------------
    // checking methods
    //----------------------------------------------------
    /**
     * check if this dictionary contains a specified word
     */
    private boolean contains(String word){
	return word2id.containsKey(word);
    }
    
    private boolean contains(int id){
	return id2word.containsKey(id);
    }

    //---------------------------------------------------
    // manupulating methods
    //---------------------------------------------------
    /**
     * add a word into this dictionary
     * return the corresponding id
     */
    public int addWord(String word){
	if (!contains(word)){
	    int id = this.word2id.size();
	    
	    this.word2id.put(word, id);
	    this.id2word.put(id,word);
	    
	    return id;
	}
	else return getID(word);		
    } // addWord()

    /**
     *
     */
    public int getSize() {
	return this.word2id.size();
    }

    //---------------------------------------------------
    // I/O methods
    //---------------------------------------------------
    /**
     * read dictionary from file
     *
     * @param
     */
    protected boolean readWordMap(String wordMapFile){		
	try{
	    BufferedReader reader = new BufferedReader(new InputStreamReader
						       (new FileInputStream(wordMapFile), "UTF-8"));
	    String line;
	    
	    //read the number of words
	    line = reader.readLine();			
	    int nwords = Integer.parseInt(line);
	    
	    //read map
	    for (int i = 0; i < nwords; ++i){
		line = reader.readLine();
		StringTokenizer tknr = new StringTokenizer(line, " \t\n\r");
		
		if (tknr.countTokens() != 2) continue;
		
		String word = tknr.nextToken();
		String id = tknr.nextToken();
		int intID = Integer.parseInt(id);
		
		id2word.put(intID, word);
		word2id.put(word, intID);
	    }
	    
	    reader.close();
	    return true;
	} catch (Exception e){
	    System.out.println("Error while reading dictionary:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}		
    } // 
    
    /** */
    public boolean writeWordMap(String wordMapFile){
	try{
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter
						       (new FileOutputStream(wordMapFile), "UTF-8"));
	    
	    //write number of words
	    writer.write(word2id.size() + "\n");
	    
	    //write word to id
	    Iterator<String> it = word2id.keySet().iterator();
	    while (it.hasNext()){
		String key = it.next();
		Integer value = word2id.get(key);
		
		writer.write(key + " " + value + "\n");
	    }
	    
	    writer.close();
	    return true;
	} catch (Exception e){
	    System.out.println("Error while writing word map " + e.getMessage());
	    e.printStackTrace();
	    return false;
	}		
    } //
  
} // Dictionary
