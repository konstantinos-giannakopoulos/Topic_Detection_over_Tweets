package data;

import java.util.List;
import java.util.ArrayList;

/**
 *
 *
 * 
 */
public class Data {

    private List<String> timezones;
    private List<String> locations;
    private Dictionary dictionary;


    private static Data INSTANCE;
    private Data() {
	this.timezones = new ArrayList<String>();
	this.locations = new ArrayList<String>();
	// dictionary
	this.dictionary = new Dictionary();
    } // Data()
    public static Data getInstance() {
	if(INSTANCE == null) {
	    INSTANCE = new Data();
	}
	return INSTANCE;
    } // getInstance()

    /**
     * Return lists.
     */ 
    public List<String> getTimezones() {
	return this.timezones;
    }
    
    public List<String> getLocations() {
	return this.locations;
    }

    public Dictionary getDictionary() {
	return this.dictionary;
    } // getDictionary()

    /**
     * Add new elements.
     */
    public void addTimezone(String t) {
	this.timezones.add(t);
    }

    public void addLocation(String l) {
	this.locations.add(l);
    }

} // Data
