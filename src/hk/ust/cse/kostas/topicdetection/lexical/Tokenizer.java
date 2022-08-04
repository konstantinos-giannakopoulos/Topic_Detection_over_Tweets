/*
 * Created on October 15, 2015 by kostas
 *
 * This is part of the TweetOntology project. 
 * Any subsequent modification
 * of the file should retain this disclaimer. 
 *
 * The Hong Kong University of Science and Technology,
 * Department of Computer Science and Engineering
 */
package lexical;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Tokenizer {
    public Tokenizer() {

    } // Tokenizer()

    /**
     *
     */
    public List<String> tokenizeText(String text) {
	List<String> resultlist = new ArrayList<String>();
	//String delimilters = "[\\.{3}\\]\\[,\\s+\n\\?!()'\">\\*\\+\\-]+";
	String delimilter = " ";
	String[] result = text.split(delimilter);
	/*for(String str : result) {
	    if( (str.contains("...")) && 
	       (!str.startsWith("...") || (!str.endsWith("..."))) ) {
		String[] res = str.split("...");
		resultlist.addAll(Arrays.asList(res));
		System.out.println(">>" + resultlist);
	    } else
		resultlist.add(str);	    
	}
	return resultlist;*/
	return Arrays.asList(result);
    } // tokenizeText()
    
} // Tokenizer