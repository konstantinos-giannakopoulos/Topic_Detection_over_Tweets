/*
 * Created on September 25, 2015 by kostas 
 *
 * This is part of the Simulator project. 
 * Any subsequent modification
 * of the file should retain this disclaimer. 
 *
 * The Hong Kong University of Science and Technology,
 * Department of Computer Science and Engineering
 */
package client;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
// Date - time
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
//
import client.Tweet;
import data.Parameters;


/**
 * Window: Implementation of a sliding time-window.
 *
 * @author kostas
 */
public class Window {
    /** The unique identifier of this time-window. */
    private int id;
    /** The list of items that are included in this time-window. */
    private List<Tweet> items;
    /** Starting timestamp of this sliding time-window. */
    private Calendar startTime;
    /** Ending timestamp of this sliding time-window. */
    private Calendar endTime;
    /** Split timestamp of this sliding time-window. */
    public Calendar splitTime;
    public int splitIndex;
    /** The maximum capacity of this time-window. */
    private static int MAX_CAPACITY;

    private int lastRemovedCounter;

    DateFormat print = new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy");

    private double sampleRatio;

    /** true if more dense than previous, false if more sparse than previous. */
    private boolean dense;

    /**
     * Constructor a new <class>Window</class>, given 
     * the starting timestamp, the ending timestamp, and the maximum 
     * capacity.
     *
     * @param startTime this window's starting timestamp 
     * @param endTime this window's ending timestamp 
     * @param maxCapacity this window's maximum capacity 
     */
    public Window(Calendar startTime, Calendar endTime) {
	this.id = 0;
	this.items = new ArrayList<Tweet>();
	this.startTime = startTime;
	this.endTime = endTime;
	this.lastRemovedCounter = 0;
	this.sampleRatio = 0.0;
	this.dense = false;
	//
	int windowSlideLength = Parameters.getInstance().getWindowSlideLength();
	this.splitTime = new GregorianCalendar();
	this.splitTime.setTime(endTime.getTime());
	this.splitTime.add(Calendar.HOUR, (- windowSlideLength)); //(Calendar.MINUTE, 10);
	this.splitIndex = 0;
    } // Window()

    /** 
     * Returns the unique id of this window.
     *
     * @return this window's id.
     */
    public int getID() {return this.id;}

    /** 
     * Retrieves all items that are included in this window.
     *
     * @return list of items contained in this window.
     **/
    public List<Tweet> getItems() {return this.items;}
    public int sizeOf() {return this.items.size();}

    /**
     * Checks if this window's maximum capacity has been reached.
     *
     * @return <code>true</code> if this window is full,
     * <code>false</code> otherwise.
     */
    public boolean isFull() {
	//if(this.items.size() == this.MAX_CAPACITY)
	//return true;
	return false;
    } // isFull()

    public boolean isEmpty() {
	if(this.items.size() == 0)
	    return true;
	return false;
    }

    public boolean isMoreDense() {
	return this.dense;
    }

    public Calendar getStartTime() {return this.startTime;}   
    public Calendar getEndTime() {return this.endTime;}
    public int getLastRemovedCounter() {return this.lastRemovedCounter;}

    public void add(Tweet tweet) {
	this.items.add(tweet);
	Date tweetDate = tweet.timestamp;
    }
    /*
    public void reset() {
	//this.items.clear();	
    }*/

    /*
     * Given an item, check if its timestamp is 
     * between this window's time boundaries.
     *
     * @param item the item
     * @return <code>true</code> if the item belongs to this window,
     * <code>false</code> otherwise.
     *
    private boolean itemIsInTimeBoundaries(Item item) {
	if(item.getTimestamp().equals(this.startTime) || 
	   item.getTimestamp().equals(this.endTime) ||
	   (item.getTimestamp().after(this.startTime) && 
	    item.getTimestamp().before(this.endTime)))
	    return true;
	return false;
    } // itemsIsInTimeBoundaries()
*/
    /*
     * Given a item, check if it belongs to the past,
     * by comparing its timestamp with this window's time boundaries.
     *
     * @param item the item
     * @return <code>true</code> if the item belongs to 
     * previous time-window,
     * <code>false</code> otherwise.
     *
    private boolean itemIsInPast(Item item) {
	if(item.getTimestamp().before(this.startTime))
	    return true;
	return false;
    } // itemIsInPast()
*/
    /*
     * Given a item, check if it belongs to the future,
     * by comparing its timestamp with this window's time boundaries.
     *
     * @param item the item
     * @return <code>true</code> if the item belongs to 
     * next time-window,
     * <code>false</code> otherwise.
     *
    private boolean itemIsInFuture(Item item) {
	if(item.getTimestamp().after(this.endTime))
	    return true;
	return false;
    } // itemIsInFuture()
*/
    /*
     * Adds a given item in this window by also updating the item's 
     * and this window's statuses accordingly, and returns 
     * this window's status.
     *
     * @param item the item
     * @return window status 
     *     <code>IN_WINDOW</code> if the item was already 
     *     previously inserted to this window,
     *     <code>PROCESSING_ADD_ITEM</code> if the item was 
     *     added succeesfully,
     *     <code>PROCESSING_IGNORE_ITEM</code> if the item 
     *     belongs to a past window,
     *     <code>NOT_EMPTY_TIME_LIMIT</code> if the item
     *     belongs to a future window, 
     *     (if one item belongs to next time-window, 
     *     this window's time limit has been reached),
     *     <code>ERROR</code> if the item cannot be proccesed.
     *
    public WindowStatus add(Item item) {
	if(isFull()) return WindowStatus.FULL_CAPACITY_LIMIT;

	if(itemIsInTimeBoundaries(item)) {
	    if(item.getStatus() == SimulatorStatus.IN_WINDOW) 
		return WindowStatus.PROCESSING_ADD_ALREADY_ITEM;//return 3; //ignore   already in
	    this.items.add(item);
	    item.setStatus(SimulatorStatus.IN_WINDOW);
	    //statistics.Statistics.getInstance().
	    //incrementTotalItemssProcessedBySimulator();
	    return WindowStatus.PROCESSING_ADD_ITEM;
	} else if(itemIsInPast(item)) {
	    item.setStatus(SimulatorStatus.OUT_OF_WINDOW);
	    return WindowStatus.PROCESSING_IGNORE_ITEM;
	} else if(itemIsInFuture(item)) {
	    return WindowStatus.NOT_EMPTY_TIME_LIMIT;
	}
	//System.out.println("Undefined error. Cannot compare item's timestamp to time window.");
	return WindowStatus.ERROR;
    } // add()
    */

    // ---------------------------------------------------- //
    /**
     * Slides time boundaries of the window, collects expired items.
     */
    public void slide() {//int windowSlideLength) {
	this.id++;
	int windowSlideLength = Parameters.getInstance().getWindowSlideLength();
	int windowSizeLength = Parameters.getInstance().getWindowSizeLength();
	this.lastRemovedCounter = 0;
	/* slide */
	// this.startTime.add(Calendar.HOUR, windowSlideLength); // (Calendar.MINUTE, 10);
	// this.endTime.add(Calendar.HOUR, windowSizeLength); // (Calendar.MINUTE, 10);
	Date startTimeDateBefore = this.startTime.getTime();
	Date endTimeDateBefore = this.endTime.getTime();
	//
	this.startTime.setTime(endTimeDateBefore);
	this.startTime.add(Calendar.HOUR, (- windowSlideLength)); //(Calendar.MINUTE, 10);
	Date startTimeDateAfter = this.startTime.getTime();
	this.endTime.setTime(startTimeDateAfter);
	this.endTime.add(Calendar.HOUR, windowSizeLength);
	Date endTimeDateAfter = this.endTime.getTime();
	//System.out.println("start: " + startTimeDateAfter + "\t end: " + endTimeDateAfter);
	//
	this.splitTime = new GregorianCalendar();
	this.splitTime.setTime(endTimeDateAfter);
	this.splitTime.add(Calendar.HOUR, (- windowSlideLength)); //(Calendar.MINUTE, 10);
	//System.out.println("splitIndexTime: " + this.splitTime.getTime());
	/* */
	removeExpiredItems();
	//System.out.println("How many items in the window? " + this.items.size());
    } // slide()

    /** */
    private void removeExpiredItems() {
	boolean increm = false;
	jgibblda.LDADataset data = null; java.util.Vector<Integer> [] z = null; int[][] nw = null;
	int[] nwsum = null;
	if(Parameters.getInstance().modelName.equals("increm") &&
	   (Parameters.getInstance().getWindowNumber() > 1))
	    increm = true;
	if(increm) {
	    data = jgibblda.LDADataset.readDataSet("ldaFiles/input-increm.txt");//dir + File.separator + dfile); //
	    if (data == null){
		System.out.println("Fail to read training data!\n");
		System.exit(-1);
	    }
	    z = Parameters.getInstance().z; //
	    nw = Parameters.getInstance().nw; //
	    nwsum = Parameters.getInstance().nwsum; //
	}
	this.lastRemovedCounter = 0; 
	/* collect the expired items. */
	List<Tweet> itemsOutOfWindow = new ArrayList<Tweet>();	
	Date startTS = this.startTime.getTime();
	//for(Tweet item : this.items) {
	for(int m = 0; m < this.items.size(); m++) {
	    Tweet item = this.items.get(m);
	    Date tweetDate = item.timestamp;  
	    if(tweetDate.before(startTS)) {
		itemsOutOfWindow.add(item);
		this.lastRemovedCounter++;
		////item.getTimer().stopTimeInWindow();
		////item.setStatus(SimulatorStatus.OUT_OF_WINDOW);
		if(increm) {
		    int N = data.docs[m].length; //
		    //System.out.println("m: " + m + "\tn: " + N);
		    for (int n = 0; n < N; n++) { //
			int w = data.docs[m].words[n]; //
			int topic = (Integer)z[m].get(n); //
			//System.out.println("w: " + w + "\ttopic: " + topic);
			//System.out.println("counter: " + nw[w][topic]);
			nw[w][topic] -= 1; //
			nwsum[topic] -= 1; //
		    } //
		}
	    } else break;
	}
	if(increm) 
	    Parameters.getInstance().setIncrementalParameters(nw, nwsum);
	//System.out.println("old: " + this.lastRemovedCounter);
	/* remove the expired items from this window. */
	removeExpiredItemsFromWindow(itemsOutOfWindow);
	//System.out.println("Expired items? " + itemsOutOfWindow.size());
	Parameters.getInstance().setLastRemovedCounter(this.lastRemovedCounter);
    } // removeExpiredItems()

    /**
     * Given a list of items that have expired from the current window, 
     * deletes them from this window list of items.
     *
     * @param itemsOutOfWindow the collection of expired items.
     */
    private void removeExpiredItemsFromWindow(List<Tweet> itemsOutOfWindow){
	this.items.removeAll(itemsOutOfWindow);
    } // removeExpiredItemsFromWindow()
    // ---------------------------------------------------- //

    public void prepare() {
	this.splitIndex = 0;
	for(Tweet tweet : this.items) {
	    Date tweetDate = tweet.timestamp;  
	    if(tweetDate.before(splitTime.getTime()))
		this.splitIndex++;
	    else break;
	}
	Parameters.getInstance().setWindowSplitIndex(this.splitIndex);
	//System.out.println("index: " + this.splitIndex + "\t until: " + this.splitTime.getTime());
    } // prepare()
    
    /* *
     * Process the items of the current window.
     *
    public void process() {
	this.id = Parameters.getInstance().getWindowNumber();
	int windowSizeLength = Parameters.getInstance().getWindowSizeLength();
	int windowSlideLength = Parameters.getInstance().getWindowSlideLength();
	System.out.println("\n-----------------------------------");
	System.out.println("| Processing window " + getID() + "|");
	System.out.println("| Window Length " + windowSizeLength + "|");
	System.out.println("| Window Slide " + windowSlideLength + "|");
    } // process()
    */

    /**
     * Samples 10% of this window's tweets.
     */ 
    public List<Tweet> sample(int length) {
	int size = this.items.size();
	double r = (((1.0 * size)/length) * 0.001);
	double min = 0.05;
	double max = 0.35;
	if(r < min) r = min;
	if(r > max) r = max;
	// check with previous window
	if(r > this.sampleRatio)
	    this.dense = true;
	else
	    this.dense = false;
	this.sampleRatio = r; // 0.1
	int numSamples = (int) (this.items.size() * this.sampleRatio);
	List<Tweet> samples = new ArrayList<Tweet>();

	//System.out.println("r " + r + "\tnumSamples: " + numSamples + "\tdense: " + this.dense
	//		   + "\titems: " + size+ "\tl: " + length);
	
	Random random = new Random();
	for(int i = 0; i < numSamples; i++)
	    samples.add(this.items.get(random.nextInt(this.items.size())));
	return samples;
    } // sample()
    
    /*
     * Given a list of items that have expired from current window,
     * collect statistics for them and save them to files.
     *
     * @param itemsOutOfWindow the collection of expired 
     * for this window items.
     *
    private void collectStatisticsForExpiredItems
	(List<Item> itemsOutOfWindow) {
	
	//for(Item expiredItem : itemsOutOfWindow) {
	//
	//}
    } // collectStatisticsForExpiredItems()
    */

    /*
    private void createNewWindow() {
	Calendar from = new GregorianCalendar();
	from.setTimeInMillis();//this.firstItemTimestamp);
	Calendar to = new GregorianCalendar();
	Date fromDate = from.getTime();
	to.setTime(fromDate);
	to.add(Calendar.HOUR, 1);
	this.window = new Window(from, to);//, MAX_CAPACITY_WINDOW);
    } // createNewWindow()
    */
    
    /**
     * Textual representation.
     *
     * @return this window's textual representation.
     */
    @Override public String toString() {
	DateFormat dateFormat = 
	    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	return "\n-----------------------------------" 
	    + "\n| Processing window " + getID() + "|" 
	    + "\n| Window Length " + Parameters.getInstance().getWindowSizeLength() + "|" 
	    + "\n| Window Slide " + Parameters.getInstance().getWindowSlideLength() + "|" 
	    + "\n\t[ID]: " + this.id 
	    + "\n\t[#of Items]: " + this.items.size() 
	    + "\n\t[From ts]: " +  dateFormat.format(this.startTime.getTime())
	    + "\n\t[To ts]: " +  dateFormat.format(this.endTime.getTime())
	    ;
    } // toString()
} // Window
