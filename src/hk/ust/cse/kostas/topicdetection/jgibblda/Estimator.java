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

import java.util.List;
//import java.util.ArrayList;
//import java.util.Set;
//import java.util.HashSet;
import java.io.File;
import java.util.Vector;
//
import model.MultinomialDistribution;
import client.Statistics;
import data.Parameters;
import client.TopicSimilarity;

public class Estimator {

    //private static int windowCounter = 2;
    // output model
    protected Model trnModel;
    LDACmdOption option;
    //MultinomialDistribution timezoneDistr;
    //MultinomialDistribution locationDistr;
    MultinomialDistribution multDistr;
    
    //public Estimator() { //(List<String> locations, List<String> timezones) {
	//this.timezoneDistr = new MultinomialDistribution(timezones);
	//this.locationDistr = new MultinomialDistribution(locations);
    //} // 
	
    public boolean init(LDACmdOption option, MultinomialDistribution multDistr){
	this.option = option;
	this.multDistr = multDistr;
	trnModel = new Model(this.multDistr);
	
	if (option.est){
	    if (!trnModel.initNewModel(option))
		return false;
	    //trnModel.data.localDict.writeWordMap(option.dir + File.separator + option.wordMapFileName);
	}
	else if (option.estc){
	    if (!trnModel.initEstimatedModel(option))
		return false;
	}
	
	return true;
    } // init()
	
    /**
     *
     */
    public void estimate(){
	//System.out.println("Sampling " + trnModel.niters + " iteration!");
	double[] p = new double[trnModel.M];
	int[] pn = new int[trnModel.M];
	int lastIter = trnModel.liter;	
	for (trnModel.liter = lastIter + 1; trnModel.liter < trnModel.niters + lastIter; trnModel.liter++){
	    //System.out.println("Iteration " + trnModel.liter + " ...");	    
	    // for all z_i
	    for (int m = 0; m < trnModel.M; m++){
		p[m] = 1.0;
		int N = trnModel.data.docs[m].length;
		pn[m] = N;
		for (int n = 0; n < N; n++){
		    // z_i = z[m][n]
		    // sample from p(z_i|z_-i, w)
		    double[] result = sampling(m, n);
		    p[m] *= result[0];
		    int topic = (int)result[1];
		    trnModel.z[m].set(n, topic);
		}// end for each word
		//p[m] /= pn[m];
	    }// end for each document	    
	}// end iterations		

	//System.out.println("Gibbs sampling completed!\n");
	//System.out.println("Saving the final model!\n");
	computeTheta();
	computePhi();
	trnModel.liter--;
	trnModel.saveModel(this.option.modelName);

	// KL-divergence
	/*	if (this.option.modelName.equals("increm")) {
	    trnModel.statsCont();	    
	    }*/

	/* save intermediate results */ 
	if ( (Parameters.getInstance().getWindowNumber() == 1) ||
	     (this.option.modelName.equals("increm")) ) {
	    trnModel.save();
	    //System.out.println("saved");
	}

	saveDistributions(p,pn);	
    } // estimate()
	
    /**
     * Do sampling
     * @param m document number
     * @param n word number
     * @return topic id
     */
    public double[] sampling(int m, int n) {
	double[] result = new double[2];
	// remove z_i from the count variable
	int topic = trnModel.z[m].get(n);
	int w = trnModel.data.docs[m].words[n];

	//extra
	// draw timezone
	//double omega = this.multDistr.drawTimezone();
	double[] tm = this.multDistr.drawTimezone();
	int timeIndex = (int)tm[0];
	double omega = tm[1];
	// draw location
	//double psi = this.multDistr.drawLocation();
	double[] loc = this.multDistr.drawLocation();
	int locationIndex = (int)loc[0];
	double psi = loc[1];
	//
	trnModel.omega[timeIndex][m] += 1;
	trnModel.psi[locationIndex][m] += 1;
	
	trnModel.nw[w][topic] -= 1;
	trnModel.nd[m][topic] -= 1;
	trnModel.nwsum[topic] -= 1;
	trnModel.ndsum[m] -= 1;

	//extra
	//trnModel.td[timezone][m] -= 1;
	//trnModel.ld[location][m] -= 1;
	//trnModel.tdsum[m] -= 1;
	//trnModel.ldsum[m] -= 1;
	
	double Vbeta = trnModel.V * trnModel.beta;
	double Kalpha = trnModel.K * trnModel.alpha;

	//extra
	//double Tgamma = trnModel.T * trnModel.gamma;
	//double Ldelta = trnModel.L * trnModel.delta;

	double[] tmp = new double[trnModel.K];
	//do multinominal sampling via cumulative method
	for (int k = 0; k < trnModel.K; k++) {
	    //new
	    trnModel.p[k] = (trnModel.nw[w][k] + trnModel.beta)/(trnModel.nwsum[k] + Vbeta) *
	    	(trnModel.nd[m][k] + trnModel.alpha)/(trnModel.ndsum[m] + Kalpha) *
		omega * psi;
	    tmp[k] = trnModel.p[k];
	}

	// cumulate multinomial parameters
	for (int k = 1; k < trnModel.K; k++){
	    trnModel.p[k] += trnModel.p[k - 1];
	}
		
	// scaled sample because of unnormalized p[]
	double u = Math.random() * trnModel.p[trnModel.K - 1];
	
	for (topic = 0; topic < trnModel.K; topic++){
	    if (trnModel.p[topic] > u) { //sample topic w.r.t distribution p
		break;
	    }
	}
	if(topic == trnModel.K) topic--;
	
	// add newly estimated z_i to count variables
	trnModel.nw[w][topic] += 1;
	trnModel.nd[m][topic] += 1;
	trnModel.nwsum[topic] += 1;
	trnModel.ndsum[m] += 1;

	result[0] = tmp[topic];
	result[1] = topic; 
	//return topic;
	return result;
    } // sampling()
    
    /** */
    public void computeTheta(){
	for (int m = 0; m < trnModel.M; m++){
	    for (int k = 0; k < trnModel.K; k++){
		trnModel.theta[m][k] = (trnModel.nd[m][k] + trnModel.alpha) / (trnModel.ndsum[m] + trnModel.K * trnModel.alpha);
	    }
	}
    }
    
    /** */
    public void computePhi(){
	for (int k = 0; k < trnModel.K; k++){
	    for (int w = 0; w < trnModel.V; w++){
		trnModel.phi[k][w] = (trnModel.nw[w][k] + trnModel.beta) / (trnModel.nwsum[k] + trnModel.V * trnModel.beta);
	    }
	}
    }
    
    //
    /* *
    public void computeOmega(){
	for (int t = 0; t < trnModel.T; t++){
	    for (int m = 0; m < trnModel.M; m++){
		trnModel.omega[t][m] = ;//(trnModel.td[t][m] + trnModel.gamma) / (trnModel.tdsum[m] + trnModel.T * trnModel.gamma);
	    }
	}
    }
    
    /** *
    public void computePsi(){
	for (int l = 0; l < trnModel.L; l++){
	    for (int m = 0; m < trnModel.M; m++){
		trnModel.psi[l][m] = ;//(trnModel.ld[l][m] + trnModel.delta) / (trnModel.ldsum[m] + trnModel.L * trnModel.delta);
	    }
	}
    }*/

    private void saveDistributions(double[] p, int[] pn) {
	if (Parameters.getInstance().getWindowNumber() > 1) {
	    if (this.option.modelName.equals("estim")) {
		Statistics.getInstance().setGtDistr(p, pn);
		//System.out.println("estim");
	    } else if (this.option.modelName.equals("increm")) {
		Statistics.getInstance().setApproxDistr(p, pn);
		//System.out.println("increm");
	    }
	}
    }
    
} // 
