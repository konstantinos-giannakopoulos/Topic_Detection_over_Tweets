/*
 * Created on October 15, 2015 by kostas
 *
 * This is part of the TweetOntology project. 
 * Any subsequent modification
 * of the file should retain this disclaimer. 
 *
 * The Hong Kong University of Science and Technology,
 * Department of Computer Science and Engineering.
 */
package lexical;

import org.tartarus.snowball.EnglishSnowballStemmerFactory;
import org.tartarus.snowball.util.StemmerException;

/**
 * Snowball stemmer 
 * http://trimc-nlp.blogspot.in/2013/08/snowball-stemmer-for-java.html?m=1
 */
public class Stemmer {
    
    public Stemmer() {
	
    }

    public String stem(String word) {
	try {
	    String result = 
		EnglishSnowballStemmerFactory.getInstance().process(word);
	    return result;
	} catch(StemmerException ex) {
	    return "";
	}	
    } // getStem()

} // Stemmer