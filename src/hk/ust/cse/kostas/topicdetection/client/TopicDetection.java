package client;

//
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
//
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
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
import lda.LDATemplate;
import lda.LDATemplateConcrete;
//
import model.MultinomialDistribution;
import inputParser.XMLParser;
//
import client.Window;
import model.MultinomialDistribution;
import data.Parameters;
import util.cpu.CPU;
//import util.mth.MTH;

/**
 *
 * @author konstantinos
 */
public class TopicDetection {

    CPU cpuIncrementalSampleTime;
    CPU cpuIncrementalTime;
    CPU cpuNormalTime;
    //MTH memory;
    List<Tweet> tweets;
    int pointerTweetIndex = 0;
    Window window;
    
    /** 
     *
     */
    public TopicDetection() {
	this.cpuIncrementalSampleTime = new CPU();
	this.cpuIncrementalTime = new CPU();
	this.cpuNormalTime = new CPU();
	//this.memory = new MTH();
    } // TopicDetection()

    /*
     * true: process
     * false: end of queue
     * /
    private boolean fillinWindow(Date windowEndDate) {
	Parameters.getInstance().incrementWindowNumber();
	for(int i = pointerTweetIndex; i < this.tweets.size(); i++) {
	    Tweet t = this.tweets.get(i);
	    Date tweetDate = this.tweets.get(i).timestamp;
	    if(tweetDate.before(windowEndDate)) {
		// Queue ended
		//if (i == tweets.size()-1) {
		//    endQueue = true;
		//    break;
		//} else {  // More in the queue
		    // fill-in window 
		    this.window.add(t);		
		    //}
	    } else { // out-of-window boundaries
		pointerTweetIndex = i;
		return true;
	    }
	} // end-for
	return false;
    } // fillinWindow()
*/
    /**
     *
     */
    public void run() {
	//this.memory.from();
	//load data
	/* [1] parse input file - fillin Data: dictionary, locations, timezones	*/
	XMLParser xmlparser = new XMLParser();  
	//List<Tweet>
	this.tweets = xmlparser.readXMLFileWithTweets();
	Parameters.getInstance().makeWindowIncrementalFalse();

	// execution time
	//this.cpuNormalTime.startOrResume();
	//this.cpuIncrementalTime.startOrResume();

	/* [2] create window */
	int windowSizeLength = Parameters.getInstance().getWindowSizeLength();
	int windowSlideLength = Parameters.getInstance().getWindowSlideLength();
	//int pointerTweetIndex = 0;
	Date d = tweets.get(0).timestamp;
	long firstTimestamp = d.getTime();
	Calendar from = new GregorianCalendar();
	from.setTimeInMillis(firstTimestamp);
	Calendar to = new GregorianCalendar();
	Date fromDate = from.getTime();
	to.setTime(fromDate);
	to.add(Calendar.HOUR, windowSizeLength); // window size
	
	//Window window = new Window(from, to);
	this.window = new Window(from, to);
	
	/* [3] fillin window */
	Parameters.getInstance().setWindowNumber(0);
	boolean endQueue = false;	
	while (!endQueue) { // while more tweets in stream
	    Date windowEndDate = window.getEndTime().getTime(); 
	    Parameters.getInstance().incrementWindowNumber();
	    //for(int i = 0; i < 10; i++) {
	    //	System.gc();
		//System.runFinalization();
	    //}
	    //System.out.println(">>>> " + window.splitTime.getTime());
	    //System.out.println(">>>> " + windowEndDate);
	    for(int i = pointerTweetIndex; i < tweets.size(); i++) {
		Tweet t = tweets.get(i);
		Date tweetDate = tweets.get(i).timestamp;
		//System.out.println(window.splitTime.getTime() + "\t" + windowEndDate);
		if(tweetDate.before(windowEndDate)) {
		    // Queue ended
		    if (i == tweets.size()-1) {
			endQueue = true;
			break;
		    } else {  // More in the queue
			// fill-in window 
			window.add(t);
		    }
		} else { // out-of-window boundaries
		    pointerTweetIndex = i;
		    break;
		}
       	    } // end-for
	
	    /* process window */
	    if(!window.isEmpty()) {		
		process();//window);
	    } else {
		//Parameters.getInstance().makeWindowIncrementalFalse();
		Parameters.getInstance().setLastRemovedCounter(0);
	    //	Parameters.getInstance().decrementWindowNumber();
	    }

	    /* evaluation * /
	    //estimateMetrics(windowCounter);

	    //if(Parameters.getInstance().getWindowNumber() == 30)
	    //	break;
	    
	    /* [4] slide window */
	    if(endQueue) { // no more data
		break;
	    } else {// slide window    
		window.slide();//windowSlideLength);
	    }
	} // end-while
	// end

	//this.memory.to();
	
	// execution time
	this.cpuIncrementalSampleTime.stop("Sample mode");
	this.cpuIncrementalTime.stop("Increm mode");
	this.cpuNormalTime.stop("Estim mode");
	
	System.out.println("\n");	
	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");	
	System.out.println(">>>   Program ended.   <<<");
	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<");	
    } // run()

    
    /* *********************************************************************************** */
    /* *********************************************************************************** */
    /* *********************************************************************************** */
    /* *********************************************************************************** */
    /* *********************************************************************************** */
    /* *********************************************************************************** */
    /* *********************************************************************************** */

    /** */
    private void process() {//Window window) {
	//window.process();
	List<Tweet> windowTweets = new ArrayList<Tweet>(this.window.getItems());
	List<Tweet> samples = new ArrayList(this.window.sample(Parameters.getInstance().getWindowSizeLength()));
	
	int windowCounter = Parameters.getInstance().getWindowNumber();
	String estimModelName = "estim"; 
	String incremModelName = "increm";
	String estimSampleModelName = "estim-sample";
	Map<Integer,Integer> mapIncremSample = new HashMap<Integer,Integer>();

	System.out.println("\n\n------------- Start Window -------------");
	//window.prepare();
	System.out.println(window);
	//System.out.println("sample: " + samples.size());

	String line = "Counter: " + windowCounter
	    + "\tLength: " + Parameters.getInstance().getWindowSizeLength();
	
	/* [A] */
	// * "estim model [ground truth] * /
	//estim(windowTweets, estimModelName);
	/* [B] */
	// * "increm" model [] */
	increm(windowCounter, windowTweets, samples, line,
	       estimModelName, incremModelName, estimSampleModelName,
	       mapIncremSample);

	System.out.println("------------- End Window -------------");	   
    } // process

    /** [A]:  "estim" mode [ground truth]  */
    private void estim(List<Tweet> windowTweets, String estimModelName) {       
	this.cpuNormalTime.startOrResume();
	processEstim(windowTweets, estimModelName);
	this.cpuNormalTime.pause();
    } // estim()

    /** [B]:  "increm" mode []  */
    private void increm(int windowCounter, List<Tweet> windowTweets, List<Tweet> samples, String line, 
			String estimModelName, String incremModelName, String estimSampleModelName,
			Map<Integer,Integer> mapIncremSample) {

	if((windowCounter == 1) || (Parameters.getInstance().getWindowIncremental() == false)) {
	    //this.cpuNormalTime.startOrResume();
	    //this.cpuIncrementalTime.startOrResume();
	    // * "estim model * /
	    processEstim(windowTweets, estimModelName);
	    // copy estim files the first time in "ldaFiles/" directory. 
	    processInitializeIncremModel(estimModelName, incremModelName);
	    // --->  Save (Copy) "increm" topics result file in "ldaOutFiles/" directory.
	    processSaveModelResults(estimModelName,windowCounter);
	    processSaveModelResults(incremModelName,windowCounter);
	    // update flag
	    Parameters.getInstance().makeWindowIncrementalTrue();
	    //this.cpuNormalTime.pause();
	    //this.cpuIncrementalTime.pause();
	} else {	    
	    // execution time 
	    //this.cpuIncrementalTime.startOrResume();
	    this.cpuIncrementalSampleTime.startOrResume();
	    // * - Adaptive - * /
	    //System.out.println("Sampling ... ");
	    // find similar topics between sampling and incremental
	    mapIncremSample = processSampling(samples, estimSampleModelName, windowCounter,
					      incremModelName, (windowCounter-1));
	    //System.out.println("commonTopics with sampling: " + mapIncremSample.size());

	    /* - Incremental - not in use * 
	    System.out.println("Incremental ... ");
	    commonTopics = processIncremental(windowTweets, incremModelName);
	    System.out.println("commonTopics in incremental: " + commonTopics);

	    / * - Adaptive - not in use *
	    System.out.println("Adaptive ... ");
	    commonTopics = processSampling(samples, estimSampleModelName, incremModelName);
	    System.out.println("commonTopics with sampling: " + commonTopics
			       + " |threshold: " + thresholdSimTopics
			       + " |comp: " + (commonTopics < thresholdSimTopics)); */

	    // * - Chi-Square - * /
	    // collect docs-per-topic distribution in sample
	    TopicDistribution topicDistribution = new TopicDistribution();
	    //System.out.println("Sample Doc-Topic distr:");	    
	    int[] numTweetsPerTopicSample = topicDistribution.collect(estimSampleModelName, windowCounter);
	    // collect docs-per-topic distribution in incr
	    topicDistribution = new TopicDistribution();
	    //System.out.println("Increm prev Doc-Topic distr:");	    
	    int[] numTweetsPerTopicIncrem = topicDistribution.collect(incremModelName, (windowCounter-1));
	    // estimate chi-square test
	    ChiSquare chiSquare = new ChiSquare(mapIncremSample, numTweetsPerTopicSample, numTweetsPerTopicIncrem);
	    // / *
	    //  int probComp = chiSquare.prepare();
	    //if(probComp == 1) { // high correlation -> smaller window
	//	Parameters.getInstance().decreaseWindowParameters();
	  //  } else if (probComp == -1) { // low correlation -> larger window
	//	Parameters.getInstance().increaseWindowParameters();
	  //  } else {
	//	// same window size
	//	}* /
	    boolean rejectH0 = chiSquare.prepare();
	    if(rejectH0) {  // not similar distr.
		//Parameters.getInstance().toggleWindowParameters();
		if(this.window.isMoreDense()) { // more dense -> smaller window
		    Parameters.getInstance().decreaseWindowParameters();
		} else {  // more sparse -> larger window
		    Parameters.getInstance().increaseWindowParameters();
		}
	    } else {  // similar distr.
		// same window size
	    }

	    this.cpuIncrementalSampleTime.pause();
	    //loadData();
	    
	    // * - Incremental - * /
	    //System.out.println("Incremental ... ");
	    // execution time
	    this.cpuIncrementalTime.startOrResume();
	    processIncremental(windowTweets, incremModelName, windowCounter, (windowCounter-1));
	    this.cpuIncrementalTime.pause();
	    
	    // * [A] - needed for time comparison * /
	    // * "estim model [ground truth] * /
	    // execution time 
	    this.cpuNormalTime.startOrResume();
	    processEstim(windowTweets, estimModelName);
	    this.cpuNormalTime.pause();

	    //
	    double perplexity = Double.NaN;
	    double kldivergence = Double.NaN;
	    if(windowCounter > 2) {
		perplexity = Statistics.getInstance().estimatePerplexity();
		kldivergence = Statistics.getInstance().estimateKLDivergence();
		Statistics.getInstance().reset();
	    }
	    TopicSimilarity topicSimilarity = new TopicSimilarity();
	    int commonTopics =
		topicSimilarity.check(incremModelName, windowCounter, estimModelName, windowCounter);
	    line += "\tSimilar: " + commonTopics + "\tPerplexity: " + perplexity
		+ "\tKL-divergence: " + kldivergence +"\n";
	    
	    Statistics.getInstance().appendFile(line);
	    System.out.println(line);	   
	    
	} // end of window
    } // process()

    /** 
     * [A] 
     * "estim model [ground truth] 
     */
    private void processEstim(List<Tweet> windowTweets, String estimModelName) {
	int windowCounter = Parameters.getInstance().getWindowNumber();
	MultinomialDistribution multDistr = new MultinomialDistribution(windowTweets);
	
	LDATemplate ldatemplate = new LDATemplateConcrete("estimate", windowTweets, multDistr);
	ldatemplate.runTemplate(estimModelName);
	// ---> Save topics result file in "ldaOutFiles/" directory.
	processSaveModelResults(estimModelName, windowCounter);
    } // processEstim()

    /**  
     *
     * @param samples:
     * @param estimSampleModelName: sample mode 
     * @param winEstim: window number of sample mode
     * @param incremModelName: increm mode
     * @param winIncrem: window number of increm mode
     */
    private Map<Integer,Integer> processSampling(List<Tweet> samples, String estimSampleModelName, int winSample, String incremModelName, int winIncrem) {
	int commonTopics = 0;
	MultinomialDistribution multDistrSamples = new MultinomialDistribution(samples);
	
	LDATemplate ldatemplateSampling = new LDATemplateConcrete("estimate", samples, multDistrSamples);
	ldatemplateSampling.runTemplate(estimSampleModelName);
	// ---> Save topics result file in "ldaOutFiles/" directory.
	processSaveModelResults(estimSampleModelName, winSample);
	// Topic Similarity with sampling estim-estimSample 
	TopicSimilarity topicSimilarity = new TopicSimilarity();
	commonTopics =
	    topicSimilarity.check(incremModelName, winIncrem, estimSampleModelName, winSample);
	// similar topic between models prevIncrem vs. curSampling
	//System.out.println("Increm_" + winIncrem + " ~ " + "Sample_" + winSample);
	//topicSimilarity.printTopicsMap();
	return topicSimilarity.getTopicsMap();
	//return commonTopics;
    } // processSampling()

    /**   
     *
     * @param incremModelName: name of the incremental mode
     * @param curWindow: current window number
     * @param prevWindow: previous window number
     **/
    private void processIncremental(List<Tweet> windowTweets, String incremModelName, int curWindow, int prevWindow){
	MultinomialDistribution multDistr = new MultinomialDistribution(windowTweets);
	
	LDATemplate ldatemplate = new LDATemplateConcrete("estimateCont", windowTweets, multDistr);
	ldatemplate.runTemplate(incremModelName);
	// --->  Save (Copy) "increm" topics result file in "ldaOutFiles/" directory.
	processSaveModelResults(incremModelName, curWindow);
    } // processIncremental

    
    /**
     * Saves model results from "ldaFiles/" directory 
     * to "ldaOutFiles/" directory.
     */
    private void processSaveModelResults(String modelName, int windowCounter) {
	try {
	    File fin; File fout;
	    fin = new File("ldaFiles/" + modelName + ".twords");
	    fout = new File("ldaOutFiles/" + modelName + "-" + windowCounter + ".twords.txt");
	    copyFileUsingStream(fin, fout);
	    fin  = new File("ldaFiles/" + modelName + ".tassign");
	    fout = new File("ldaOutFiles/" + modelName + "-" + windowCounter + ".tassign.txt");
	    copyFileUsingStream(fin, fout);
	} catch (IOException ioex) { 
	    System.out.println("Error in copying files.");
	}
    } //

    /**
     * Initializes the "increm" model only on the first window.
     * Copies the "estim" model to the "increm" model.
     */
    private void processInitializeIncremModel(String gtModelName, String iModelName) {
	String inFileName = "ldaFiles/" + gtModelName;
	String outFileName = "ldaFiles/" + iModelName;
	try {
	    File fin; File fout;
	    fin = new File(inFileName + ".others");
	    fout = new File(outFileName + ".others");
	    copyFileUsingStream(fin, fout);
	    fin = new File(inFileName + ".phi");
	    fout = new File(outFileName + ".phi");
	    copyFileUsingStream(fin, fout);
	    fin = new File(inFileName + ".tassign");
	    fout = new File(outFileName + ".tassign");
	    copyFileUsingStream(fin, fout);
	    fin = new File(inFileName + ".theta");
	    fout = new File(outFileName + ".theta");
	    copyFileUsingStream(fin, fout);
	    fin = new File(inFileName + ".twords");
	    fout = new File(outFileName + ".twords");
	    copyFileUsingStream(fin, fout);
	    fin = new File("ldaFiles/input-" + gtModelName + ".txt");
	    fout = new File("ldaFiles/input-" + iModelName + ".txt");
	    copyFileUsingStream(fin, fout);
	} catch (IOException ioex) { 
	    System.out.println("Error in copying files.");
	}
    } // 
    
    /** */
    private static void copyFileUsingStream(File source, File dest) throws IOException {
	InputStream is = null; OutputStream os = null; 
	try {
	    is = new FileInputStream(source); os = new FileOutputStream(dest);
	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = is.read(buffer)) > 0) {
		os.write(buffer, 0, length);
	    }
	} finally {
	    is.close(); os.close();
	}
    } // copyFileUsingStream()




    /*
    public static void evaluateAccuracy() {
	String incremModelName = "increm";
	String estimModelName = "estim";
	TopicSimilarity topicSimilarity = new TopicSimilarity();
	for(int i = 0; i < 50; i++) {
	    int commonTopics = topicSimilarity.check(incremModelName, i, estimModelName, i);
	    System.out.println(i + "\t" + commonTopics);
	}
    } // evaluateAccuracy()

    */


    //---------------------------------------------------------------
    //	Main
    //---------------------------------------------------------------
    
    /**
     * Debug main.
     */
    public static void main(String[] args){
	System.out.println("TopicDetection.java main class");
	TopicDetection topicDetection = new TopicDetection();
	topicDetection.run();
	//evaluateAccuracy();
    } // main()

    
} // TopicDetection


    /*

      ***************************
      ***************************
      ˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆ
      ---------------------------
      ---------  Draft  ---------
      ___________________________
      ...........................



    //---------------------------------------------------------------
    //	Process()
    //---------------------------------------------------------------
    	/* Works * /
	/* application of the topic model * / 	
	String modelName = "";
	/* [A] */
	/* "estim model [ground truth] * /
	modelName = "estim";
	LDATemplate ldatemplate = new LDATemplateConcrete("estimate", windowTweets, multDistr);
	ldatemplate.runTemplate(modelName);
	// ---> Save topics result file in "ldaOutFiles/" directory.
	processSaveModelResults(modelName,windowCounter);
	//System.out.println("model results saved");
	
	/* [B] */
	/* "increm" model [] * /
	modelName = "increm";
	if( (Parameters.getInstance().getWindowNumber() == 1) || 
	    (Parameters.getInstance().getWindowIncremental() == false) ) {
	    /* rerun the model in incremental mode */
	    //ldatemplate = new LDATemplateConcrete("estimate", windowTweets, multDistr);
	    //  ldatemplate.runTemplate(modelName); 
	    /* copy estim files the first time in "ldaFiles/" directory. * /
	    processInitializeIncremModel("estim", "increm");
	    //System.out.println("init increm done");	    
	    // --->  Save (Copy) "increm" topics result file in "ldaOutFiles/" directory.
	    processSaveModelResults(modelName,windowCounter);

	    Parameters.getInstance().makeWindowIncrementalTrue();
	} else {
	    ldatemplate = new LDATemplateConcrete("estimateCont", windowTweets, multDistr);
	    ldatemplate.runTemplate(modelName);
	    // --->  Save (Copy) "increm" topics result file in "ldaOutFiles/" directory.
	    processSaveModelResults(modelName,windowCounter);
	    
	    /* Topic Similarity * /
	    TopicSimilarity topicSimilarity = new TopicSimilarity();
	    topicSimilarity.check("estim","increm");	    
	    
	    //if(Parameters.getInstance().getWindowNumber() == 3)
	    //System.exit(0);
	}
	




     */
   
