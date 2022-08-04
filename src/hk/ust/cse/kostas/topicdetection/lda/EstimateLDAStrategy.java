package lda;

import java.util.List;
import java.io.File;
import jgibblda.*;

import client.Tweet;
import model.MultinomialDistribution;
import data.Parameters;

public class EstimateLDAStrategy implements ILDAStrategy {
    private static final String CURDIR = System.getProperty("user.dir"); 
    private static final String LDA_FOLDER = CURDIR + "/ldaFiles";

    public void doRunStrategy(String modelName, MultinomialDistribution multDistr) {
	String infile = "input-" + modelName + ".txt";
	String folder = LDA_FOLDER;

	System.out.println("[est] Model with name '" + modelName + "' started ... ");
	
	LDACmdOption ldaOption = new LDACmdOption(); 
	ldaOption.est = true;
	ldaOption.estc = false; 
	ldaOption.inf  = false; 
	
	ldaOption.dir       = folder;
	ldaOption.dfile     = infile;
	ldaOption.modelName = modelName;

	ldaOption.alpha    = 0.9;
	ldaOption.beta     = 0.001;
	ldaOption.K        = Parameters.getInstance().getNumTopics();//15;   
	ldaOption.twords   = Parameters.getInstance().getNumWordsPerTopic();//10;
	ldaOption.niters   = 1000;
	ldaOption.savestep = 1000;
	
	Estimator estimator = new Estimator();
	//estimator.init(ldaOption);
	estimator.init(ldaOption, multDistr);
	estimator.estimate();

	System.out.println("[est] Model with name '" + modelName + "' finished .- ");
    } // doRunStrategy()
} 
