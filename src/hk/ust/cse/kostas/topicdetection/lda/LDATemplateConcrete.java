package lda;

import java.util.List;

import client.Tweet;
import model.MultinomialDistribution;

public class LDATemplateConcrete extends LDATemplate {

    private ILDAStrategy ldastrategy;  

    /** 
     * Input: Strategy for LDA. (estimate, estimateCont, infer)
     */
    public LDATemplateConcrete(String ldaStrategy, List<Tweet> tweets, MultinomialDistribution multDistr) {
	this.tweets = tweets;
	this.multDistr = multDistr;
	if(ldaStrategy.equals("estimate")) {
	    setLDAStrategy(new EstimateLDAStrategy());
	} else if(ldaStrategy.equals("estimateCont")) {
	    setLDAStrategy(new EstimateContinuedLDAStrategy());
	} else if(ldaStrategy.equals("infer")) {
	    // setLDAStrategy(new InferLDAStrategy());
	}
    }

    private void setLDAStrategy(ILDAStrategy strategy) { 
	this.ldastrategy = strategy;
    }


    @Override public void doRunLDAAlgorithm(String modelName) {
	this.ldastrategy.doRunStrategy(modelName, this.multDistr);
    }

} // LDATemplateConcrete
