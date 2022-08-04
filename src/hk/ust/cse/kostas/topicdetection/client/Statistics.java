package client;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
//
import data.Parameters;

/**
 *
 * Steps:
 * [1]: reset() in LocationTime.java
 * [2]: initParamsLTZ() in Builder.java
 * [3]: initParamsVK() in Model.java
 * [4]: update() in Estimator.java
 * [5]: estimateSum() in Estimator.java
 * [6]: estimateLog() in Estimator.java
 */
public class Statistics {

    private double gtP[];
    private int gtN[];
    private double approxP[];
    private int approxN[];
    
    /*
    private double gtPhi[][];
    private double gtTheta[][];
    private double gtOmega[][];
    private double gtPsi[][];

    private double approxPhi[][];
    private double approxTheta[][];
    private double approxOmega[][];
    private double approxPsi[][];
    */
    
    // 
    //private double product;
    //private double previousProduct;
    //private double estimProduct;
    //private double logValue;
    //private double threshold;
    
    private int words; 
    private int topics; 
    private int timezones; 
    private int locations;
    
    private BufferedWriter mywriter = null;
    private int windowID;
    
    private static Statistics instance;
    private Statistics() {
	try {
	    mywriter = new BufferedWriter
		(new OutputStreamWriter
		 (new FileOutputStream("Statistics-log.txt"), "UTF-8"));
	    //this.threshold = 4.5;
	} catch(Exception e){
	    System.out.println("Error in Statistics file: " + e.getMessage());
	    e.printStackTrace();
	}
    } // Statistics()
    public static Statistics getInstance() {
	if(instance == null) {
	    instance = new Statistics();
	}
	return instance;
    } // getInstance()

    public void appendFile(String line) {
	try {
	    mywriter.write(line);
	    mywriter.flush();
	} catch(IOException e){
	    System.out.println("Error in Statistics file: " + e.getMessage());
	    e.printStackTrace();
	}
    }
    
    /*
    public double getLogValue() {
	return this.logValue;
    } //
    */
    public void init() {
	this.windowID = Parameters.getInstance().getWindowNumber();
    }

    public void setGtDistr(double[] p, int[] n) {
	this.gtP = p;
	this.gtN = n;
    }

    public void setApproxDistr(double[] p, int[] n) {
	this.approxP = p;
	this.approxN = n;
    }

    public double estimatePerplexity() {
	double sum = 0.0;
	int sumN = 0;
	int M = this.gtP.length;
	if(M != this.approxP.length) System.exit(-1);	
	for(int m = 0; m < M; m++) {
	    double log = Math.log(this.approxP[m]) / (Math.log(2));
	    sum += 1.0*log;
	    sumN += approxN[m];
	}
	System.out.println(sum + "\t" + sumN);
	double value = -1.0*(sum/sumN);
	double perplexity = Math.exp(value);
	return perplexity;
	/*
	for(int m = 0; m < M; m++) {
	    //if( (this.gtP[m] <= 0.0) ) {
	    //	this.gtP[m] = 0.0000000001;
	    //}
	    //if( (this.approxP[m] <= 0.0) ) {
	    //	this.approxP[m] = 0.0000000001;
	    //}
	    double approxPLog = 1.0 * Math.log(this.approxP[m]) / (Math.log(2));
	    sum += (1.0*this.gtP[m]) * approxPLog;
	}
	//for(int m = 0; m < 10; m++) 
	// System.out.println("> " +  this.gtP[m]  + "\t" + (this.approxP[m]) + "\t" );
	//this.gtP = null; this.approxP = null;
	double perplexity = 1.0*Math.pow(2,-(sum));
	//return (-sum);	
	return perplexity;*/
    } // estimatePerplexity()

    public double estimateKLDivergence() {
	double sum = 0.0;
	int M = this.gtP.length;
	if(M != this.approxP.length) System.exit(-1);
	for(int m = 0; m < M; m++) {
	    //if( (this.gtP[m] <= 0.0) ) {
	    //	this.gtP[m] = 0.0000000001;
	    //}
	    //if( (this.approxP[m] <= 0.0) ) {
	    //	this.approxP[m] = 0.0000000001;
	    //}
	    //double ratio = 1.0*this.approxP[m] / this.gtP[m];
	    double approxPLog = 1.0 * Math.log(this.gtP[m] / this.approxP[m]) / (Math.log(2));
	    sum += (1.0*this.gtP[m]) * approxPLog;
	}
	double kldivergence = sum;
	//for(int m = 0; m < 10; m++) 
	// System.out.println("> " +  this.gtP[m]  + "\t" + (this.approxP[m]) + "\t" );
	//this.gtP = null; this.approxP = null;
	return kldivergence;
    } // estimateKLDivergence()

    public void reset() {
	this.gtP = null;
	this.approxP = null;
    }
    
    /*
    public void setGtDistr(double[][] phi, double[][] theta, double[][] psi, double[][] omega) {
	setGtPhi(phi);
	setGtTheta(theta);
	setGtPsi(psi);
	setGtOmega(omega);
    } // setGtDistr()

    public void setApproxDistr(double[][] phi, double[][] theta, double[][] psi, double[][] omega) {
	setApproxPhi(phi);
	setApproxTheta(theta);
	setApproxPsi(psi);
	setApproxOmega(omega);
    } // setApproxDistr()
    */
    
    /*
     *
     * [1]
     * Call in LocationTime.java
     * /
    public void reset() {
	this.phi = null;
	this.theta = null;
	this.psi = null;
	this.omega = null;
	//this.logValue = 0.0;
	this.previousProduct = 0.0;
	this.product = 0.0;
	//this.windowID = windowCounter;
	//this.estimProduct = 0.0;
    } // reset()
*/
    /*
     *
     * [2]
     * Call in Builder.java
     * Related with setPsi(), setOmega()
     *
    public void initLTZ(int L, int TZ, double[][] psi, double[][] omega) {
	this.locations = L;
	this.timezones = TZ;

	setPsi(psi);
	setOmega(omega);
    } // initLTZ()

    /*
     *
     * [3]
     * Call in Model.java
c     * Related with setPhi(), setTheta()
     *
    public void initVK(int V, int K, double[][] phi, double[][] theta) {
	this.words = V;
	this.topics = K;
	setPhi(phi);
	setTheta(theta);
    } // initVK()
*/
    /*
     *
     * [4]
     * Call in Estimator.java
     *
    public void update() {
	this.previousProduct = this.product;
	this.product = 0.0;
	//this.logValue = 1.0;
    } // update()
*/
	
    //public void updateEstim() {
    //	this.estimProduct = this.product;
    //	this.product = 0.0;
    //}


    
    /* *
     *
     * [5]
     * Call in Estimator.java
     * /
    public void estimateSum(int M, int V, int K, int L, int T) {
	//System.out.println(this.words + "\t" + this.topics + "\t" + this.locations + "\t" + this.timezones);
	double sum = 0.0;
	double gtP = 1.0; double approxP = 1.0; double approxPLog = 0.0;
	for(int m = 0; m < M; m++) {
	    for(int v = 0; v < V; v++) {
		for(int k = 0; k < K; k++) {
		    for(int l = 0; l < L; l++) {
			//double temp = phi[i][j] * theta[j][k];
			for(int t = 0; t < T; t++) {
			    //result += temp * omega[k][l] * psi[l];
			    gtP = gtPhi[k][v] * gtTheta[m][k] * gtOmega[t][m] * gtPsi[l][m];
			    approxP = approxPhi[k][v] * approxTheta[m][k] * approxOmega[t][m] * approxPsi[l][m];
			    approxPLog = Math.log(approxP);
			    sum += gtP * approxPLog;
			}
		    }
		}
	    }
	}
    } // estimateSum()

    */    
    /*
     *
     * [6]
     * Call in Estimator.java
     *
    public void estimateLog() {
	int previousWindowID = this.windowID - 1; 
	double prod = 1.0 * (this.previousProduct / this.product);
	double log = Math.log(prod);
	//this.logValue = this.previousProduct * log;
	double logValue = this.previousProduct * log;

	try {
	    // comparison with previous
	    System.out.println("[" + this.windowID + " comparison with "+ previousWindowID + "]\t" + logValue + "\n");
	    mywriter.write(this.windowID + "\t" + logValue + "\n");
	    if(greaterThanThreshold(logValue)) {
		Parameters.getInstance().makeWindowIncrementalFalse();
		mywriter.write("Re-estimation on next ("+ (this.windowID+1) + ") window.\n");
		System.out.println("Re-estimation on next ("+ (this.windowID+1) + ") window.\n");
		reset();
	    } else {
		update();
	    }
	    mywriter.flush();
	    // comparison with estim
	    //double prodEstim = 1.0 * (this.estimProduct / this.product);
	    //double logEstim = Math.log(prodEstim);
	    //double logValueEstim = this.estimProduct * logEstim;
	    //System.out.println("[" + this.windowID + " comparison with estim\t" + logValueEstim + "\n");
	} catch(IOException e){
	    System.out.println("Error in Statistics file: " + e.getMessage());
	    e.printStackTrace();
	    //} finally {
	    //return logValue;
	}
    } // estimateLog()
*/
	
    /**
     *
     * [7]
     * Call in Estimator.java
     */
    public void closeFile() {
	try {
	    mywriter.close();
	} catch(IOException e){
	    System.out.println("Error in Statistics file: " + e.getMessage());
	    e.printStackTrace();
	}
    } // closeFile()

    /* ********************************************************************** */
    /* ********************************************************************** */
    /* ********************************************************************** */
    /* ********************************************************************** */
    /* ********************************************************************** */

    /*
    public boolean greaterThanThreshold(double logValue) {
	System.out.println((Math.abs(logValue) + " > " + this.threshold + " = " +
			    ((Math.abs(logValue) - this.threshold)) + " "
			    + (Math.abs(logValue) > this.threshold)));
	if((Math.abs(logValue) - this.threshold) > 0)
	    return true;
	else
	    return false;
    }

    /** φ : P(w|z) *
    private void setPhi(double[][] phi) {
	////this.phi = new double[this.words][this.topics];
	this.phi = phi;
	////int a = this.words; int b = this.topics;
	//System.out.println("--- Phi ---");
	//for(int i = 0; i < a; i++)
	//for(int j = 0; j < b; j++)
	      //System.out.println("(" + i + " of " + a + " ," + j + " of " + b + ")\t" + phi[i][j]);
    } //

    /** θ : Pr(z|t) *
    private void setTheta(double[][] theta) {
	////this.theta = new double[this.topics][this.locations];
	this.theta = theta;
	////int a = this.topics; int b = this.locations;
	//System.out.println("--- Theta ---");
	//for(int i = 0; i < a; i++) 
	//for(int j = 0; j < b; j++) 
	//System.out.println("(" + i + "," + j + ")\t" + theta[i][j]); 
    } // 

    /** ω : Pr(t|l) * /
    private void setOmega(double[][] omega) {
	////this.omega = new double[this.locations][this.timezones];
	this.omega = omega;
	////int a = this.locations; int b = this.timezones;
	//System.out.println("--- Omega ---");
	//for(int i = 0; i < a; i++) 
	//for(int j = 0; j < b; j++) 
	//System.out.println("(" + i + "," + j + ")\t" + omega[i][j]); 
    } //

    /* ψ : Pr(l)*
    private void setPsi(double[][] psi) {
	////this.psi = new double[this.timezones];
	this.psi = psi;
	//System.out.println("--- Psi ---");
	//for(int i = 0; i < this.timezones; i++) 
	//System.out.println("(" + i + ")\t" + psi[i]); 
    } //
    */
} // Statistics
