/*
 * Copyright (C) 2007 by
 * 
 * 	Xuan-Hieu Phan
 *	hieuxuan@ecei.tohoku.ac.jp or pxhieu@gmail.com
 * 	Graduate School of Information Sciences
 * 	Tohoku University
 * 
 *  Cam-Tu Nguyen
 *  ncamtu@gmail.com
 *  College of Technology
 *  Vietnam National University, Hanoi
 *
 * JGibbsLDA is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JGibbsLDA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JGibbsLDA; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package jgibblda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
//
import model.MultinomialDistribution;
import data.Data;
//import data.Dictionary;
import client.Statistics;
import data.Parameters;

/**
 *
 */
public class Model {  
	
    //---------------------------------------------------------------
    //	Class Variables
    //---------------------------------------------------------------
    
    public static String tassignSuffix;	//suffix for topic assignment file
    public static String thetaSuffix;		//suffix for theta (topic - document distribution) file
    public static String phiSuffix;		//suffix for phi file (topic - word distribution) file
    public static String othersSuffix; 	//suffix for containing other parameters
    public static String twordsSuffix;		//suffix for file containing words-per-topics
    
    //---------------------------------------------------------------
    //	Model Parameters and Variables
    //---------------------------------------------------------------
    
    public String wordMapFile; 		//file that contain word to id map
    public String trainlogFile; 	//training log file	
    
    public String dir;
    public String dfile;
    public String modelName;
    public LDADataset data;			// link to a dataset
    
    public int M; //dataset size (i.e., number of docs)
    public int V; //vocabulary size
    public int K; //number of topics
    public double alpha, beta; //LDA  hyperparameters
    public int niters; //number of Gibbs sampling iteration
    public int liter; //the iteration at which the model was saved
    public int savestep; //saving period
    public int twords; //print out top words per each topic
    public int withrawdata;
    
    // Estimated/Inferenced parameters
    public double [][] theta; //theta: document - topic distributions, size M x K
    public double [][] phi; // phi: topic-word distributions, size K x V
    
    // Temp variables while sampling
    public Vector<Integer> [] z; //topic assignments for words, size M x doc.size()
    protected int [][] nw; //nw[i][j]: number of instances of word/term i assigned to topic j, size V x K
    protected int [][] nd; //nd[i][j]: number of words in document i assigned to topic j, size M x K
    protected int [] nwsum; //nwsum[j]: total number of words assigned to topic j, size K
    protected int [] ndsum; //ndsum[i]: total number of words in document i, size M
	
    // temp variables for sampling
    protected double [] p;

    // extra
    public int L; //number of locations
    public int T; //number of timezones
    /*
    public double gamma, delta; // hypermarameters
    protected int [][] td; //td[i][j]: number of instances of timezone i assigned to document j, size T x M
    protected int [][] ld; //ld[i][j]: number of instances of location i assigned to document j, size L x M
    protected int [] tdsum; //tdsum[j]: total number of timezones assigned to document j, size M
    protected int [] ldsum; //ldsum[j]: total number of locations assigned to document j, size M*/
    public double [][] omega; //omega: timezone-document distributions, size T x M
    public double [][] psi;   //psi: location-document distributions, size L x M
    
    //---------------------------------------------------------------
    //	Constructors
    //---------------------------------------------------------------	
    
    public Model(MultinomialDistribution multDistr) {
	setDefaultValues();
    }
    
    /**
     * Set default values for variables
     */
    public void setDefaultValues() {
	wordMapFile = "wordmap.txt";
	trainlogFile = "trainlog.txt";
	tassignSuffix = ".tassign";
	thetaSuffix = ".theta";
	phiSuffix = ".phi";
	othersSuffix = ".others";
	twordsSuffix = ".twords";
	
	dir = "./";
	dfile = "trndocs.dat";
	modelName = "model-final";
	
	M = 0;
	V = 0;
	K = 100;
	alpha = 50.0 / K;
	beta = 0.1;
	niters = 2000;
	liter = 0;
		
	z = null;
	nw = null;
	nd = null;
	nwsum = null;
	ndsum = null;
	theta = null;
	phi = null;

	// extra
	this.L = Data.getInstance().getLocations().size();
	this.T = Data.getInstance().getTimezones().size();
	/*this.gamma = 0.9;
	this.delta = 0.9;
	
	this.td = null;
	this.ld = null;
	this.tdsum = null;
	this.ldsum = null;*/
	this.omega = null;
	this.psi = null;
    }
	
    //---------------------------------------------------------------
    //	I/O Methods
    //---------------------------------------------------------------
    /**
     * read other file to get parameters
     */
    protected boolean readOthersFile(String otherFile){
	//open file <model>.others to read:
	
	try {
	    BufferedReader reader = new BufferedReader(new FileReader(otherFile));
	    String line;
	    while((line = reader.readLine()) != null){
		StringTokenizer tknr = new StringTokenizer(line,"= \t\r\n");
		
		int count = tknr.countTokens();
		if (count != 2)
		    continue;
		
		String optstr = tknr.nextToken();
		String optval = tknr.nextToken();
		
		if (optstr.equalsIgnoreCase("alpha")){
		    alpha = Double.parseDouble(optval);					
		}
		else if (optstr.equalsIgnoreCase("beta")){
		    beta = Double.parseDouble(optval);
		}
		else if (optstr.equalsIgnoreCase("ntopics")){
		    K = Integer.parseInt(optval);
		}
		else if (optstr.equalsIgnoreCase("liter")){
		    liter = Integer.parseInt(optval);
		}
		else if (optstr.equalsIgnoreCase("nwords")){
		    V = Integer.parseInt(optval);
		}
		else if (optstr.equalsIgnoreCase("ndocs")){
		    M = Integer.parseInt(optval);
		}
		else {
		    // any more?
		}
			}
	    
	    reader.close();
	} catch (Exception e){
	    System.out.println("Error while reading other file:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    } // readOthersFile()
    
    protected boolean readTAssignFile(String tassignFile){
	try {
	    int i,j;
	    BufferedReader reader = new BufferedReader(new InputStreamReader
						       (new FileInputStream(tassignFile), "UTF-8"));
	    
	    String line;
	    z = new Vector[M];			
	    data = new LDADataset(M);
	    data.V = V;			
	    for (i = 0; i < M; i++){
		line = reader.readLine();
		StringTokenizer tknr = new StringTokenizer(line, " \t\r\n");
		
		int length = tknr.countTokens();
		
		Vector<Integer> words = new Vector<Integer>();
		Vector<Integer> topics = new Vector<Integer>();
		
		for (j = 0; j < length; j++){
		    String token = tknr.nextToken();
		    
		    StringTokenizer tknr2 = new StringTokenizer(token, ":");
		    if (tknr2.countTokens() != 2){
			System.out.println("Invalid word-topic assignment line\n");
			return false;
		    }
		    
		    words.add(Integer.parseInt(tknr2.nextToken()));
		    topics.add(Integer.parseInt(tknr2.nextToken()));
		}//end for each topic assignment
		
		//allocate and add new document to the corpus
		Document doc = new Document(words);
		data.setDoc(doc, i);
		
		//assign values for z
		z[i] = new Vector<Integer>();
		for (j = 0; j < topics.size(); j++){
		    z[i].add(topics.get(j));
		}
		
	    }//end for each doc
	    
	    reader.close();
	} catch (Exception e){
	    System.out.println("Error while loading model: " + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    } // readTAssignFile()
	
    /**
     * load saved model
     *    
     *   _+_
     *   /|\ change
     *  / ! \
     * +-----+
     */
    public boolean loadModel(){
	/*
	if (!readOthersFile(dir + File.separator + modelName + othersSuffix))
	    return false;
	
	if (!readTAssignFile(dir + File.separator + modelName + tassignSuffix))
	    return false;
	*/
	// read dictionary
	Dictionary dict = new Dictionary();
	if (!dict.readWordMap(dir + File.separator + wordMapFile))
	    return false;
	
	data.localDict = dict;
	
	return true;
    } // loadModel()
	
    /**
     * Save word-topic assignments for this model
     */
    public boolean saveModelTAssign(String filename){
	int i, j;
	
	try{
	    BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	    
	    //write docs with topic assignments for words
	    for (i = 0; i < data.M; i++){
		for (j = 0; j < data.docs[i].length; ++j){
		    writer.write(data.docs[i].words[j] + ":" + z[i].get(j) + " ");	   
		}
		writer.write("\n");
	    }
	    
	    writer.close();
	} catch (Exception e){
	    System.out.println("Error while saving model tassign: " + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    } // saveModelTAssign()
	
    /**
     * Save theta (topic distribution) for this model
     */
    public boolean saveModelTheta(String filename){
	try{
	    BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	    for (int i = 0; i < M; i++){
		for (int j = 0; j < K; j++){
		    writer.write(theta[i][j] + " ");
		}
		writer.write("\n");
	    }
	    writer.close();
	} catch (Exception e){
	    System.out.println("Error while saving topic distribution file for this model: " + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    } // saveModelTheta()
	
    /**
     * Save word-topic distribution
     */	
    public boolean saveModelPhi(String filename){
	try {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	    
	    for (int i = 0; i < K; i++){
		for (int j = 0; j < V; j++){
		    writer.write(phi[i][j] + " ");
		}
		writer.write("\n");
	    }
	    writer.close();
	} catch (Exception e){
	    System.out.println("Error while saving word-topic distribution:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    } // saveModelPhi()
	
    /**
     * Save other information of this model
     */
    public boolean saveModelOthers(String filename){
	try{
	    BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	    
	    writer.write("alpha=" + alpha + "\n");
	    writer.write("beta=" + beta + "\n");
	    writer.write("ntopics=" + K + "\n");
	    writer.write("ndocs=" + M + "\n");
	    writer.write("nwords=" + V + "\n");
	    writer.write("liters=" + liter + "\n");
	    
	    writer.close();
	} catch(Exception e){
	    System.out.println("Error while saving model others:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    } // saveModelOthers()
	
    /**
     * Save model the most likely words for each topic
     */
    public boolean saveModelTwords(String filename){
	try{
	    BufferedWriter writer = new BufferedWriter
		(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
	    
	    if (twords > V){
		twords = V;
	    }
	    
	    for (int k = 0; k < K; k++){
		List<Pair> wordsProbsList = new ArrayList<Pair>(); 
		for (int w = 0; w < V; w++){
		    Pair p = new Pair(w, phi[k][w], false);		    
		    wordsProbsList.add(p);
		}//end foreach word
		
		//print topic
		writer.write("Topic " + k + "th:\n");
		Collections.sort(wordsProbsList);
		
		//mywriter.write("\n<<Topic>> " + k + "\n"); //
		//for (int v = 0; v < V; v++){//
		//  if (data.localDict.contains((Integer)wordsProbsList.get(v).first)){//
		//	String word = data.localDict.getWord((Integer)wordsProbsList.get(v).first); // 
		//	if(word.contains(":")) 
		//	    word = word.replace(":","");
		//	mywriter.write(" " + word + ":" + wordsProbsList.get(v).second);//
		//  }//
		//}//
		
		for (int i = 0; i < twords; i++){
		    if (data.localDict.contains((Integer)wordsProbsList.get(i).first)){
			String word = data.localDict.getWord((Integer)wordsProbsList.get(i).first);			
			writer.write("\t" + word + " " + wordsProbsList.get(i).second + "\n");
		    }
		}
	    } //end foreach topic		    
	    writer.close();
	    //mywriter.close();//
	} catch(Exception e){
	    System.out.println("Error while saving model twords: " + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    } // saveModelTwords()
	
    /**
     * Save model
     */
    public boolean saveModel(String modelName){
	if (!saveModelTAssign(dir + File.separator + modelName + tassignSuffix))
	    return false;	
	
	if (!saveModelOthers(dir + File.separator + modelName + othersSuffix))		
	    return false;	
	
	if (!saveModelTheta(dir + File.separator + modelName + thetaSuffix))
	    return false;	
	
	if (!saveModelPhi(dir + File.separator + modelName + phiSuffix))
	    return false;	
	
	if (twords > 0){
	    if (!saveModelTwords(dir + File.separator + modelName + twordsSuffix))
		return false;
	}
	return true;
    } // saveModel()

    /**
     * 
     */
    public void save() {
    	Parameters.getInstance().setIncrementalParameters
    	    (M, K, V, z, nw, nd, nwsum, ndsum, modelName);
    } // save()
    
    /*  
    public void statsCont() {
	Statistics.getInstance().init();
	//Statistics.getInstance().update();
	Statistics.getInstance().setDistr(phi, theta, psi, omega);//, M, V, K, L, T);
	Statistics.getInstance().estimateSum(M, V, K, L, T);
	//double logValue = ;
	Statistics.getInstance().estimateLog();
	//if(Statistics.getInstance().greaterThanThreshold(logValue)) {
	//  Parameters.getInstance().makeWindowIncrementalFalse();
	//  System.out.println("Re-estimation on next window.\n");
	//  Statistics.getInstance().reset();
	//} else
	//  Statistics.getInstance().update();
	//
	//System.out.println(Parameters.getInstance().getWindowIncremental());
	//if(Parameters.getInstance().getWindowIncremental() == false) {
	//  Statistics.getInstance().reset();
	//} //else {
	// Statistics.getInstance().update();
	     //Parameters.getInstance().makeWindowIncrementalTrue();
	//}
    } // statsCont()
*/
    /*
    public void stats() {
	Statistics.getInstance().init();
	Statistics.getInstance().setDistr(phi, theta, psi, omega);
	Statistics.getInstance().estimateSumEstim(M, V, K, L, T);
	double logValue = Statistics.getInstance().estimateLogEstim();
	Statistics.getInstance().updateEstim();
    } // stats()
    */
    
    //---------------------------------------------------------------
    //	Init Methods
    //---------------------------------------------------------------
    /**
     * initialize the model
     */
    protected boolean init(LDACmdOption option){		
	if (option == null)
	    return false;
	
	modelName = option.modelName;
	//System.out.println(".................>>>>>>>>>>>> " + option.modelName);
	K = option.K;
	
	alpha = option.alpha;
	if (alpha < 0.0)
	    alpha = 50.0 / K;
	
	if (option.beta >= 0)
	    beta = option.beta;
	
	niters = option.niters;
	
	dir = option.dir;
	if (dir.endsWith(File.separator))
	    dir = dir.substring(0, dir.length() - 1);
	
	dfile = option.dfile;
	twords = option.twords;
	wordMapFile = option.wordMapFileName;
	
	return true;
    } // init()
	
    /**
     * Init parameters for estimation
     * Read window data: input.txt
     */
    public boolean initNewModel(LDACmdOption option){
	if (!init(option))
	    return false;

	int m, n, w, k;		
	p = new double[K];

	// Access global dictionary
	data.Dictionary dictionary = Data.getInstance().getDictionary();
	V = dictionary.getSize();
	// Access local window data; input.txt
	data = LDADataset.readDataSet(dir + File.separator + dfile);
	if (data == null){
	    System.out.println("Fail to read training data!\n");
	    return false;
	}
	
	//+ allocate memory and assign values for variables 
	M = data.M;
	//V = data.V;
	dir = option.dir;
	savestep = option.savestep;
	
	// K: from command line or default value
	// alpha, beta: from command line or default values
	// niters, savestep: from command line or default values
	
	nw = new int[V][K];
	for (w = 0; w < V; w++){
	    for (k = 0; k < K; k++){
		nw[w][k] = 0;
	    }
	}
	
	nd = new int[M][K];
	for (m = 0; m < M; m++){
	    for (k = 0; k < K; k++){
		nd[m][k] = 0;
	    }
	}
	
	nwsum = new int[K];
	for (k = 0; k < K; k++){
	    nwsum[k] = 0;
	}
	
	ndsum = new int[M];
	for (m = 0; m < M; m++){
	    ndsum[m] = 0;
	}
	
	z = new Vector[M];     
	//System.out.println("new here: \n");
	for (m = 0; m < data.M; m++){
	    int N = data.docs[m].length;
	    z[m] = new Vector<Integer>();
	    
	    //initilize for z
	    for (n = 0; n < N; n++){
		int topic = (int)Math.floor(Math.random() * K);
		z[m].add(topic);

		// number of instances of word assigned to topic j
		nw[data.docs[m].words[n]][topic] += 1;
		// number of words in document i assigned to topic j
		nd[m][topic] += 1;
		// total number of words assigned to topic j
		nwsum[topic] += 1;
	    }
	    // total number of words in document i
	    ndsum[m] = N;	    
	}

	theta = new double[M][K];		
	phi = new double[K][V];
	
	//extra
	this.omega = new double[this.T][M];
	this.psi = new double[this.L][M];
	/*for(int t = 0; t < T; t++)
	    for(int l = 0; l < L; l++)
		for(m = 0; m < M; m++) {
		    this.omega[t][m] = 0;
		    this.psi[l][m] = 0;
		    }*/
	     
	return true;
    } // initNewModel()
	
    /*
     * Init parameters for inference
     * @param newData DataSet for which we do inference
     *
    public boolean initNewModel(LDACmdOption option, LDADataset newData, Model trnModel){
	if (!init(option))
	    return false;
	
	int m, n, w, k;
	
	K = trnModel.K;
	alpha = trnModel.alpha;
	beta = trnModel.beta;
	
	p = new double[K];
	System.out.println("K:" + K);
	
	data = newData;
	
	//+ allocate memory and assign values for variables	    
	M = data.M;
	V = data.V;
	dir = option.dir;
	savestep = option.savestep;
	System.out.println("M:" + M);
	System.out.println("V:" + V);
	
	// K: from command line or default value
	// alpha, beta: from command line or default values
	// niters, savestep: from command line or default values
	
	nw = new int[V][K];
	for (w = 0; w < V; w++){
	    for (k = 0; k < K; k++){
		nw[w][k] = 0;
	    }
	}
	
	nd = new int[M][K];
	for (m = 0; m < M; m++){
	    for (k = 0; k < K; k++){
		nd[m][k] = 0;
	    }
	}
	
	nwsum = new int[K];
	for (k = 0; k < K; k++){
	    nwsum[k] = 0;
	}
	
	ndsum = new int[M];
	for (m = 0; m < M; m++){
	    ndsum[m] = 0;
	}
	
	z = new Vector[M];
	for (m = 0; m < data.M; m++){
	    int N = data.docs[m].length;
	    z[m] = new Vector<Integer>();
	    
	    //initilize for z
	    for (n = 0; n < N; n++){
		int topic = (int)Math.floor(Math.random() * K);
		z[m].add(topic);
		
		// number of instances of word assigned to topic j
		nw[data.docs[m].words[n]][topic] += 1;
		// number of words in document i assigned to topic j
		nd[m][topic] += 1;
		// total number of words assigned to topic j
		nwsum[topic] += 1;
	    }
	    // total number of words in document i
	    ndsum[m] = N;
	}
	
	theta = new double[M][K];		
	phi = new double[K][V];
	
	return true;
    } // initNewModel()
	*/
    /*
     * Init parameters for inference
     * reading new dataset from file
     *
     public boolean initNewModel(LDACmdOption option, Model trnModel){
         if (!init(option))
	     return false;
		
	 LDADataset dataset = LDADataset.readDataSet(dir + File.separator + dfile, trnModel.data.localDict);
	 if (dataset == null){
	     System.out.println("Fail to read dataset!\n");
	     return false;
	 }
		
	 return initNewModel(option, dataset , trnModel);
     }
     */
    
    /**
     * init parameter for continue estimating or for later inference
     * Read the dictionary. 
     */
    public boolean initEstimatedModel(LDACmdOption option){
	if (!init(option))
	    return false;
	
	int m, n, w, k;	
	p = new double[K];	

	// >_____________________
	// Access global dictionary
	data.Dictionary dictionary = Data.getInstance().getDictionary();
	V = dictionary.getSize();
	
	data = LDADataset.readDataSet(dir + File.separator + dfile);
	if (data == null){
	    System.out.println("Fail to read training data!\n");
	    return false;
	}
	
	//+ allocate memory and assign values for variables 
	M = data.M;
	//V = data.V;

	// Load saved data;
	Parameters.getInstance().prepareData();
	int _M = Parameters.getInstance().M;
	Vector<Integer> [] _z = Parameters.getInstance().z;
	int[][] _nd = new int[_M][K];
	_nd = Parameters.getInstance().nd;
	int[] _ndsum = new int[_M];
	_ndsum = Parameters.getInstance().ndsum;
	int lastRemovedCounter = Parameters.getInstance().getLastRemovedCounter();
	//System.out.println("cntr: " + lastRemovedCounter);       
	
	//System.out.println("Model loaded:");       
	
	nw = new int[V][K];
	for (w = 0; w < V; w++){
	    for (k = 0; k < K; k++){
		nw[w][k] = 0;
	    }
	}

	int index = lastRemovedCounter; //
	nd = new int[M][K];
	for (m = 0; m < M; m++){
	    for (k = 0; k < K; k++){
		if(m < _M) {
		    nd[m][k] = _nd[index][k]; //nd[m][k] = _nd[m][k];
		} else {
		    nd[m][k] = 0;
		}
	    }
	    index++;
	}
	
	//int index = 0;
	//for(int cntr = lastRemovedCounter; cntr < _M; cntr++) {
	//    for (k = 0; k < K; k++) {
	//	nd[index][k] = _nd[cntr][k];
	//    }
	//    index++;
        //}
	
	nwsum = new int[K];
	for (k = 0; k < K; k++) {
	    nwsum[k] = 0;
	}

	index = lastRemovedCounter; //
	ndsum = new int[M];
	for (m = 0; m < M; m++) {
	    if(m < _M) {
		ndsum[m] = _ndsum[index]; //ndsum[m] = _ndsum[m];
	    } else {
		ndsum[m] = 0;
	    }
	    index++;
	}
	
	//index = 0;
	//for(int cntr = lastRemovedCounter; cntr < _M; cntr++) {
	//    ndsum[index] = _ndsum[cntr];
	//    index++;
	//}	

	// >_____________________
	z = new Vector[M];
	// --------------------.
	for (m = 0; m < M; m++) {
	    int N = data.docs[m].length;
	    z[m] = new Vector<Integer>();
	    if(m < _M) {
		z[m].addAll(_z[m]); //
	    } else { 
		for (n = 0; n < N; n++) {
		    w = data.docs[m].words[n];
		    int topic = (int)Math.floor(Math.random() * K);
		    z[m].add(topic);
		    nw[w][topic] += 1; // 
		    nd[m][topic] += 1;
		    nwsum[topic] += 1; // 
		    ndsum[m] += 1;
		}
	    } 
	} // for-loop
	/*
	for (m = 0; m < M; m++) {
	    int N = data.docs[m].length;
	    z[m] = new Vector<Integer>();
	    if(m < _M) {
		z[m].addAll(_z[m]); // 		
		for (n = 0; n < N; n++) {
		    w = data.docs[m].words[n];
		    int topic = (Integer)z[m].get(n);
		    nw[w][topic] += 1; 
		    nwsum[topic] += 1;
		}
	    } else { 
		for (n = 0; n < N; n++) {
		    w = data.docs[m].words[n];
		    int topic = (int)Math.floor(Math.random() * K);
		    z[m].add(topic);
		    nw[w][topic] += 1; // 
		    nd[m][topic] += 1;
		    nwsum[topic] += 1; // 
		    ndsum[m] += 1;
		}
	    } 
	} // for-loop
	*/
	/*
	int splitIndex = Parameters.getInstance().getWindowSplitIndex();
	for(m = 0; m < splitIndex; m++) {
	    // copy previous

	    // add new
	    
	}
	for(m = splitIndex; m < M; m++) {
	    for (n = 0; n < N; n++) {
		w = data.docs[m].words[n];
		int topic = (int)Math.floor(Math.random() * K);
		z[m].add(topic);
		nw[w][topic] += 1; // 
		nd[m][topic] += 1;
		nwsum[topic] += 1; // 
		ndsum[m] += 1;
	    }
	}
	*/
	
	theta = new double[M][K];
	phi = new double[K][V];

	// extra
	this.omega = new double[this.T][M];
	this.psi = new double[this.L][M];
	/*for(int t = 0; t < T; t++)
	    for(int l = 0; l < L; l++)
		for(m = 0; m < M; m++) {
		    this.omega[t][m] = 0;
		    this.psi[l][m] = 0;
		}*/
	
	dir = option.dir;
	savestep = option.savestep;
	
	//Parameters.getInstance().setIncrementalParameters
	//  (M, K, V, z, nw, nd, nwsum, ndsum);

	return true;
    } // initEstimatedModel()
    
} //
