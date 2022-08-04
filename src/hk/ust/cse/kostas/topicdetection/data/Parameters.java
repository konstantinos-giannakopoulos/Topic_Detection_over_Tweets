package data;

import java.util.Vector;

/**
 *
 *
 * 
 */
public class Parameters {

    private int windowNumber; /* current window */
    private boolean windowIncremental; /* incremental or reconstruction */

    /* incremental mode parameters */
    public int M; //number of tweets
    public int K; // number of topics
    public int V; // size of Vocabulary
    public Vector<Integer> [] z; // topic assignments for words, size M x doc.size()
    public int [][] nw; //nw[i][j]: number of instances of word i assigned to topic j, size V x K
    public int [][] nd; //nd[i][j]: number of words in tweet i assigned to topic j, size M x K
    public int [] nwsum; //nwsum[j]: total number of words assigned to topic j, size K
    public int [] ndsum; //ndsum[i]: total number of words in document i, size M
    public String modelName;
    private int lastRemovedCounter; // index in tweets;

    /* */
    private int numTopics;
    private int numWordsPerTopic;
    private int windowSizeLength;
    private int windowSlideLength;
    private int windowSplitIndex;

    private int similarTopics;

    /* */
    private double previousSamplingRate;
    
    private static Parameters INSTANCE;
    private Parameters() {
	this.numTopics = 15;
	this.numWordsPerTopic = 10;
	
	this.windowIncremental = true;
	this.windowSizeLength = 2;  // 3 //// 2
	this.windowSlideLength = 1; // 1 //// 1
    } // Parameters()
    public static Parameters getInstance() {
	if(INSTANCE == null) {
	    INSTANCE = new Parameters();
	}
	return INSTANCE;
    } // getInstance()

    
    /** */
    public void setWindowNumber(int num) { this.windowNumber = num; }
    public int getWindowNumber() { return this.windowNumber; }
    public void incrementWindowNumber() { this.windowNumber++; }
    public void decrementWindowNumber() { this.windowNumber--; }

    /** */
    public boolean getWindowIncremental() { return this.windowIncremental; }
    public void makeWindowIncrementalTrue() { this.windowIncremental = true; }
    public void makeWindowIncrementalFalse() { this.windowIncremental = false; }

    /* * /
    public void changeWindowIncremental() {
	System.out.println("before: " + this.windowIncremental);
	if(this.windowIncremental == true)
	    this.windowIncremental = false;
	else if(this.windowIncremental == false)
	    this.windowIncremental = true;
	System.out.println("after: " + this.windowIncremental);
    }
*/
    /* * /
    public void toggleWindowParameters() {
	if ( (this.windowSizeLength == 3) ) {//&& (this.windowSlideLength == 2) ) {
	    this.windowSizeLength = 6;
	    //this.windowSlideLength = 3;
	} else if ( (this.windowSizeLength == 6) ) {//&& (this.windowSlideLength == 3) ) {
	    this.windowSizeLength = 3;
	    //this.windowSlideLength = 2;
	}
    } // toggleWindowParameters()
*/
    /*
    public void changeWindowParameters() {
	if(this.windowSizeLength < 6) 
	    increaseWindowParameters();
	else
	    decreaseWindowParameters();
    }
    */
    public void increaseWindowParameters() {
	if(this.windowSizeLength < 8) {  // 7 //// 8
	    this.windowSizeLength *= 2; // +=2  3-1=2, 5-2=3, 7-3=4 //// *= 2
	    //this.windowSlideLength += 1; // +=1 //// //
	}
    }

    public void decreaseWindowParameters() {
	if(this.windowSizeLength > 2) {  // 3 //// 2
	    this.windowSizeLength /= 2; // -=2 //// /=2
	    //this.windowSlideLength -= 1; // -=1 //// //
	}
    }
    
    /** */
    public void setLastRemovedCounter(int counter) { this.lastRemovedCounter = counter; }
    public int getLastRemovedCounter() { return this.lastRemovedCounter; }

    public void setWindowSplitIndex(int index) { this.windowSplitIndex = index; }
    public int getWindowSplitIndex() { return this.windowSplitIndex; }

    public void setIncrementalParameters(int[][] nw, int[] nwsum) {
	this.nw = nw; ////
	//this.nd = nd; //
	this.nwsum = nwsum; ////
	//this.ndsum = ndsum; //
    } // 
    
    /** */
    public void setIncrementalParameters(int M, int K, int V, Vector<Integer> [] z,
					 int[][] nw, int[][] nd, int[] nwsum, int[] ndsum,
					 String modelName) {
	this.modelName = modelName;
	this.M = M;
	this.K = K;
	this.V = V;
	this.z = z; //
	this.nw = nw; ////
	this.nd = nd; //
	this.nwsum = nwsum; ////
	this.ndsum = ndsum; //

	/*
	this.z = new Vector[M];
	for (int m = 0; m < M; m++) {
	    this.z[m] = new Vector<Integer>();
	    this.z[m].addAll(z[m]);
	}
	
	//this.nw = new int[V][K];
	//for (int w = 0; w < V; w++){
	//    for (int k = 0; k < K; k++){
	//	this.nw[w][k] = nw[w][k];
	//    }
	//}

	this.nd = new int[M][K];
	for (int m = 0; m < M; m++){
	    for (int k = 0; k < K; k++){
		this.nd[m][k] = nd[m][k];
	    }
	}
	
	//this.nwsum = new int[K];
	//for (int k = 0; k < K; k++){
	//    this.nwsum[k] = nwsum[k];
	//}

	this.ndsum = new int[M];
	for (int m = 0; m < M; m++){
	    this.ndsum[m] = ndsum[m];
	}
	*/	
    } // setIncrementalParameters()

    /**
     * load
     */
    public void prepareData() {

	this.M = M - this.lastRemovedCounter;
	//this.K = K;
	//this.V = V;

      if(this.lastRemovedCounter != 0) {
	int index = this.lastRemovedCounter;
	for (int m = 0; m < M; m++) {
	    this.z[m] = new Vector<Integer>();
	    //this.z[m].clear(); // 
	    this.z[m].addAll(this.z[index]);
	    index++;
	}
	
	//this.nw = new int[V][K];
	//for (int w = 0; w < V; w++){
	//    for (int k = 0; k < K; k++){
	//	this.nw[w][k] = nw[w][k];
	//    }
	//}
	/*
	index = this.lastRemovedCounter;
	//this.nd = new int[M][K];
	for (int m = 0; m < M; m++){
	    for (int k = 0; k < K; k++){
		this.nd[m][k] = this.nd[index][k]; // shift to the beginning
	    }
	    index++;
	}
	
	//this.nwsum = new int[K];
	//for (int k = 0; k < K; k++){
	//    this.nwsum[k] = nwsum[k];
	//}
	
	index = this.lastRemovedCounter;
	//this.ndsum = new int[M];
	for (int m = 0; m < M; m++){
	    this.ndsum[m] = this.ndsum[index];
	    index++;
	}
	*/
      }
    } // prepareData()

    /** */
    public void setNumTopics(int num) {	this.numTopics = num; }
    public int getNumTopics() {	return this.numTopics; }
    public void setNumWordsPerTopic(int num) { this.numWordsPerTopic = num; }
    public int getNumWordsPerTopic() { return this.numWordsPerTopic; }
    
    public void setWindowSizeLength(int size) {	this.windowSizeLength = size; }
    public int getWindowSizeLength() { return this.windowSizeLength; }
    public void setWindowSlideLength(int slide) { this.windowSlideLength = slide; }
    public int getWindowSlideLength() { return this.windowSlideLength; }

    //public void setSimilarTopics(int similarTopics) {this.similarTopics = similarTopics;}
    //public int getSimilarTopics() {return this.similarTopics;}
    
} // Parameters
