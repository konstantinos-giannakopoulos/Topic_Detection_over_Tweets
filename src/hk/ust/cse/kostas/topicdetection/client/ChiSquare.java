package client;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import data.Parameters;

/**
 *
 *
 */ 
public class ChiSquare {

    Map<Integer,Integer> mapTopicSimIncremSample; // <increm,sample>
    int[] numTweetsPerTopicSample;
    int[] numTweetsPerTopicIncrem;
    double[][] table; // [df][prob]
    double[] prob;
    
    
    /* */
    public ChiSquare(Map<Integer,Integer> mapTopicSimIncremSample,
		     int[] numTweetsPerTopicSample,
		     int[] numTweetsPerTopicIncrem) {
	this.mapTopicSimIncremSample = mapTopicSimIncremSample;
	this.numTweetsPerTopicSample = numTweetsPerTopicSample;
	this.numTweetsPerTopicIncrem = numTweetsPerTopicIncrem;
	initTable();
    } // ChiSquare()

    private void initTable() {
	// p-value
	this.prob = new double[]
	    {  0.995,  0.99 ,  0.975,  0.95 ,  0.9  ,  0.75 ,  0.5  ,  0.25 ,  0.2  ,  0.1  ,  0.05 ,  0.025,  0.02 ,  0.01 ,  0.005,  0.002,  0.001};
	// chi-square
	this.table = new double[][]{ //[30][17]
	    {0.0000393,0.000157,0.000982,0.00393,0.0158,0.102, 0.455,  1.323,  1.642,  2.706,  3.841,  5.024,  5.412,  6.635,  7.879,  9.550, 10.828}, //1
	    {  0.010,  0.020, 0.0506,  0.103,  0.211,  0.575,  1.386,  2.773,  3.219,  4.605,  5.991,  7.378,  7.824,  9.210, 10.597, 12.429, 13.816},
	    { 0.0717,  0.115,  0.216,  0.352,  0.584,  1.212,  2.366,  4.108,  4.642,  6.251,  7.815,  9.348,  9.837, 11.345, 12.838, 14.796, 16.266},
	    {  0.207,  0.297,  0.484,  0.711,  1.064,  1.923,  3.357,  5.385,  5.989,  7.779,  9.488, 11.143, 11.668, 13.277, 14.860, 16.924, 18.467},
	    {  0.412,  0.554,  0.831,  1.145,  1.610,  2.675,  4.351,  6.626,  7.289,  9.236, 11.070, 12.833, 13.388, 15.086, 16.750, 18.907, 20.515},
	    {  0.676,  0.872,  1.237,  1.635,  2.204,  3.455,  5.348,  7.841,  8.558, 10.645, 12.592, 14.449, 15.033, 16.812, 18.548, 20.791, 22.458},
	    {  0.989,  1.239,  1.690,  2.167,  2.833,  4.255,  6.346,  9.037,  9.803, 12.017, 14.067, 16.013, 16.622, 18.475, 20.278, 22.601, 24.322},
	    {  1.344,  1.646,  2.180,  2.733,  3.490,  5.071,  7.344, 10.219, 11.030, 13.362, 15.507, 17.535, 18.168, 20.090, 21.955, 24.352, 26.124},
	    {  1.735,  2.088,  2.700,  3.325,  4.168,  5.899,  8.343, 11.389, 12.242, 14.684, 16.919, 19.023, 19.679, 21.666, 23.589, 26.056, 27.877},
	    {  2.156,  2.558,  3.247,  3.940,  4.865,  6.737,  9.342, 12.549, 13.442, 15.987, 18.307, 20.483, 21.161, 23.209, 25.188, 27.722, 29.588}, //10
	    {  2.603,  3.053,  3.816,  4.575,  5.578,  7.584, 10.341, 13.701, 14.631, 17.275, 19.675, 21.920, 22.618, 24.725, 26.757, 29.354, 31.264},
	    {  3.074,  3.571,  4.404,  5.226,  6.304,  8.438, 11.340, 14.845, 15.812, 18.549, 21.026, 23.337, 24.054, 26.217, 28.300, 30.957, 32.909},
	    {  3.565,  4.107,  5.009,  5.892,  7.042,  9.299, 12.340, 15.984, 16.985, 19.812, 22.362, 24.736, 25.472, 27.688, 29.819, 32.535, 34.528},
	    {  4.075,  4.660,  5.629,  6.571,  7.790, 10.165, 13.339, 17.117, 18.151, 21.064, 23.685, 26.119, 26.873, 29.141, 31.319, 34.091, 36.123},
	    {  4.601,  5.229,  6.262,  7.261,  8.547, 11.037, 14.339, 18.245, 19.311, 22.307, 24.996, 27.488, 28.259, 30.578, 32.801, 35.628, 37.697},
	    {  5.142,  5.812,  6.908,  7.962,  9.312, 11.912, 15.338, 19.369, 20.465, 23.542, 26.296, 28.845, 29.633, 32.000, 34.267, 37.146, 39.252},
	    {  5.697,  6.408,  7.564,  8.672, 10.085, 12.792, 16.338, 20.489, 21.615, 24.769, 27.587, 30.191, 30.995, 33.409, 35.718, 38.648, 40.790},
	    {  6.265,  7.015,  8.231,  9.390, 10.865, 13.675, 17.338, 21.605, 22.760, 25.989, 28.869, 31.526, 32.346, 34.805, 37.156, 40.136, 42.312},
	    {  6.844,  7.633,  8.907, 10.117, 11.651, 14.562, 18.338, 22.718, 23.900, 27.204, 30.144, 32.852, 33.687, 36.191, 38.582, 41.610, 43.820},
	    {  7.434,  8.260,  9.591, 10.851, 12.443, 15.452, 19.337, 23.828, 25.038, 28.412, 31.410, 34.170, 35.020, 37.566, 39.997, 43.072, 45.315}, //20
	    {  8.034,  8.897, 10.283, 11.591, 13.240, 16.344, 20.337, 24.935, 26.171, 29.615, 32.671, 35.479, 36.343, 38.932, 41.401, 44.522, 46.797},
	    {  8.643,  9.542, 10.982, 12.338, 14.041, 17.240, 21.337, 26.039, 27.301, 30.813, 33.924, 36.781, 37.659, 40.289, 42.796, 45.962, 48.268},
	    {  9.260, 10.196, 11.689, 13.091, 14.848, 18.137, 22.337, 27.141, 28.429, 32.007, 35.172, 38.076, 38.968, 41.638, 44.181, 47.391, 49.728},
	    {  9.886, 10.856, 12.401, 13.848, 15.659, 19.037, 23.337, 28.241, 29.553, 33.196, 36.415, 39.364, 40.270, 42.980, 45.559, 48.812, 51.179},
	    { 10.520, 11.524, 13.120, 14.611, 16.473, 19.939, 24.337, 29.339, 30.675, 34.382, 37.652, 40.646, 41.566, 44.314, 46.928, 50.223, 52.620}, //25
	    { 11.160, 12.198, 13.844, 15.379, 17.292, 20.843, 25.336, 30.435, 31.795, 35.563, 38.885, 41.923, 42.856, 45.642, 48.290, 51.627, 54.052}, //26
	    { 11.808, 12.879, 14.573, 16.151, 18.114, 21.749, 26.336, 31.528, 32.912, 36.741, 40.113, 43.195, 44.140, 46.963, 49.645, 53.023, 55.476}, //27
	    { 12.461, 13.565, 15.308, 16.928, 18.939, 22.657, 27.336, 32.620, 34.027, 37.916, 41.337, 44.461, 45.419, 48.278, 50.993, 54.411, 56.892}, //28
	    { 13.121, 14.256, 16.047, 17.708, 19.768, 23.567, 28.336, 33.711, 35.139, 39.087, 42.557, 45.722, 46.693, 49.588, 52.336, 55.792, 58.301}, //29
	    { 13.787, 14.953, 16.791, 18.493, 20.599, 24.478, 29.336, 34.800, 36.250, 40.256, 43.773, 46.979, 47.962, 50.892, 53.672, 57.167, 59.703}  //30
	};
    } // initTable()

    /**
     * //@returns true if high prob
     * H0 : sample & increm come from the same distribution.
     * H1 : not H0
     *
     * // checkChanceProb(double chisquare, int df) {
     * //@returns 1, very high prob, for smaller window
     * //@returns 0, high prob, same window
     * //@returns -1, very low prob, for larger window
     * @returns true: to reject H0
     * @returns false: insufficient evidence to reject H0.
     */
    private boolean rejectH0(double chisquare, int df) {
	/*	int high = 5; // 2
	int low  = 6; // 4
	int out = 16;      
	//if(chisquare > this.table[df][out]) 
	//  Parameters.getInstance().makeWindowIncrementalFalse();
	// prob > 0.975 - smaller window needed
	if(chisquare < this.table[df][high]) 
	    return 1;
	// prob < 0.90 - larger window needed
	if(chisquare > this.table[df][low]) 
	    return -1;
	// 0.90 < prob < 0.975 - same window
	if((chisquare >= this.table[df][high]) && (chisquare <= this.table[df][low])) 
	    return 0;
	return 0;
	*/
	int conf = 10; // 5% that we are wrong
	if(chisquare > this.table[df][conf])
	    return true;
	else
	    return false;
    } // checkChanceProb()
    
    /** */
    public boolean prepare() {
	int common =  this.mapTopicSimIncremSample.size();
	int[] sampleIndex = new int[common];
	int[] incremIndex = new int[common];
	int in = 0;
	for (Map.Entry<Integer, Integer> entry : this.mapTopicSimIncremSample.entrySet()) {
	    Integer increm = entry.getKey();
	    Integer sample = entry.getValue();
	    sampleIndex[in] = sample;
	    incremIndex[in] = increm;
	    in++;
	}

	//for(int i = 0; i < this.mapTopicSimIncremSample.size(); i++) {
	//    System.out.println(i + "\t" + sampleIndex[i] + "\t" + incremIndex[i]);
	//}
	//System.out.println("\n");
	//System.exit(1);

	// create new arrays	
	int l = (this.numTweetsPerTopicSample.length + this.numTweetsPerTopicIncrem.length) -
	    this.mapTopicSimIncremSample.size();
	int[] sampleNewNumDocs = new int[l];
	int[] incremNewNumDocs = new int[l]; 
	int sampleNumDocs = 1;
	int incremNumDocs = 1;
	int n = 0;	
	for(int i = 0; i < this.numTweetsPerTopicSample.length; i++) {
	    int compos = findinArray(sampleIndex, i);  // similar topic
	    if(compos != -1) {
		//System.out.println("found " + i + " in pos: " + compos);
		int sp = sampleIndex[compos];
		int ip = incremIndex[compos];
		sampleNumDocs = numTweetsPerTopicSample[sp];
		incremNumDocs = numTweetsPerTopicIncrem[ip];
	    } else {
		sampleNumDocs = numTweetsPerTopicSample[i];
		incremNumDocs = 1;
	    }
	    if(sampleNumDocs == 0) sampleNumDocs++;
	    sampleNewNumDocs[n] = sampleNumDocs;
	    incremNewNumDocs[n] = incremNumDocs;
	    n++;
	}
	int remainingPos = this.numTweetsPerTopicIncrem.length - this.mapTopicSimIncremSample.size();
	for(int i = 0; i < remainingPos; i++) {
	    if(Arrays.asList(incremIndex).contains(i) ) {
		// ignore
	    } else {
		incremNumDocs = numTweetsPerTopicIncrem[i];
		sampleNumDocs = 1;
	    }
	    sampleNewNumDocs[n] = sampleNumDocs;
	    incremNewNumDocs[n] = incremNumDocs;
	    n++;
	}

	//for(int i = 0; i < n; i++) {
	//    System.out.println(i + "\t" + sampleNewNumDocs[i] + "\t" + incremNewNumDocs[i]);
	//}
	
	// estimate sums
	int sumSampleDocs = 0;
	int sumIncremDocs = 0;
	for(int i = 0; i < sampleNewNumDocs.length; i++) {
	    sumSampleDocs += sampleNewNumDocs[i];
	}
	for(int i = 0; i < incremNewNumDocs.length; i++) {	    
	    sumIncremDocs += incremNewNumDocs[i];
	}

	//System.out.println("sum sample: " + sumSampleDocs + " sum increm: " + sumIncremDocs);
	//System.exit(1);

	double chisquare = estimateChiSquare(sampleNewNumDocs, incremNewNumDocs, l,
					     sumSampleDocs, sumIncremDocs);
	//
	int df = l - 1;
	boolean rejectH0 = rejectH0(chisquare, df);
	//int probComp = checkChanceProb(chisquare, df);
	//System.out.println("chisquare: " + chisquare + "\tHigh Prob: " + probComp);
	//System.out.println("chisquare: " + chisquare + "\tReject H0: " + rejectH0);

	//return probComp;
	return rejectH0;
    } // prepare()
    
    /**   */
    private double estimateChiSquare(int[] sampleNewNumDocs, int[] incremNewNumDocs, int l,
				     int sumSampleDocs, int sumIncremDocs) {
	double chisquare = 0.0;
	for(int i = 0; i < l; i++) {
	    // find num of docs
	    int sampleNumDocs = sampleNewNumDocs[i];
	    int incremNumDocs = incremNewNumDocs[i];

	    double e = 1.0 * incremNumDocs / sumIncremDocs; // b4 exp: e 
	    double o = 1.0 * sampleNumDocs / sumSampleDocs; // b4 exp: o 
	    double d = 1.0 * Math.pow((o - e), 2) / e;
	    chisquare += d;
	}
	return chisquare;
    } // prepare()


    /**   */
    private int findinArray(int[] array, int probe) {
	for(int i = 0; i < array.length; i++)
	    if(array[i] == probe)
		return i;
	return -1;
    }
    
} // ChiSquare
