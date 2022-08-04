/*
 * Created on February 25, 2015 by kostas-κγ 
 *
 * This is part of the InfoTweetsLexical project. 
 * Any subsequent modification
 * of the file should retain this disclaimer. 
 *
 * The Hong Kong University of Science and Technology,
 * School of Computer Science and Engineering
 */
package infotweetslexical.lexical;

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