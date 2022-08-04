package client;
//java
import java.util.Date;
import java.math.BigDecimal;
// lib/

//mine


/**
 * Tweet: Encapsulates a tweet message.
 *
 * @author kostas
 */
public class Tweet {

    // extra
    public String time;
    public String location;
    public String message;


    //public Status status;
    // text
    public BigDecimal id;
    public Date timestamp;
    // location
    //public String geoLocation;
    //public String country;
    //public String countryCode;
    //public String name;
    //public String fullname;
    //public String placetype;
    public Tweet() {

    }

    public Tweet(String location, String time, String message) {
	this.location = location;
	this.time = time;
	this.message = message;
    }

    @Override public int hashCode() {
	int result = 17;
	result = 31 * result + this.id.intValue();
	return result;
    } // hashCode()

    @Override public boolean equals(Object o) {
	if(o == this)
	    return true;
	if(!(o instanceof Tweet)) 
	    return false;
	Tweet t = (Tweet)o;
	return t.id.equals(this.id);
    } // equals()

    public String shortTweetToString() {
	StringBuilder sb = new StringBuilder();
	sb.append("<tweet>\n"); 
	sb.append("\t[id]" + this.id + "\n");
	sb.append("\t[time]" + this.time + "\n");
	sb.append("\t[message]" + this.message + "\n");
	sb.append("\t[location]" + this.location + "\n");
	sb.append("</tweet>\n"); 
	return sb.toString();
    } // shortTweetToString()

} // Tweet