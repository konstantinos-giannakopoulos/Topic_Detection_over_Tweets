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

import java.util.List;
import java.util.ArrayList;

import lexical.Tokenizer;
import lexical.Trimmer;
import lexical.Fixer;
import lexical.Stemmer;

//import lexical.Jaws;

public class Lexical {

    Tokenizer tokenizer;
    Trimmer trimmer;
    Fixer fixer;
    Stemmer stemmer;
    //Jaws jaws;

    public Lexical() {
	this.tokenizer = new Tokenizer();
	this.trimmer = new Trimmer();
	this.fixer = new Fixer();
	this.stemmer = new Stemmer();
	//this.jaws = new jaws();
    }

    private List<String> tokenize(String message) {
	return (this.tokenizer.tokenizeText(message));
    }

    /**
     * Tokenizes the tweet message. Keeps untangled 
     * hashtags and meantined users. 
     */
    public List<String> process(String message) {
	/*
	  1) Tokenize the input string.
	  2) Trim it, by removing spaces, and punctuation.
	  3) Fix it; Slightly modify it.
	  //4) Stem it.
	 */
	List<String> result = new ArrayList<String>();
	List<String> tokens = this.tokenize(message);
	for(String token : tokens) {
	    if(token.startsWith("#")) {
		result.add(token);
	    } else if (token.contains("@")) {
		while(!token.startsWith("@")) {
		    token = token.substring(1, token.length());
		}
		if(token.endsWith(":")) {
		    token = token.substring(0, token.length() - 1);
		}
		result.add(token);
	    } else {
		String trimstr = this.trimmer.trim(token);
		String fixstr = this.fixer.fix(trimstr);
		if(!fixstr.equals(""))
		    result.add(fixstr);		
		//String stemsrt = this.stemmer.stem(fixstr);
		//if(!stemsrt.equals(""))
		//  result.add(stemsrt);		
	    }
	}
	return result;
    } // process

    /**
     * Debug main.
     */
    public static void main(String[] args) {
	Lexical lexical = new Lexical();
	List<String> messages = new ArrayList<String>();
	messages.add( "Hi! My name, is Kostas... How are u?");
	messages.add("http://www.hello.com...");
	messages.add("heeeelllllooooooo");
	messages.add("#sleep.");
	messages.add("was");
	messages.add("I went to the @gym at 3 o'clock. Then I came home.");
	messages.add("where");
	messages.add("where...are");
	messages.add("Cant I wanna going.. there? with you???");
	messages.add("hi hey");
	System.out.println("--> Tokenizer");

	for(String message : messages) {
	    System.out.println(lexical.process(message));
	}
    }

} // Lexical