package teabagml.bnlearn;

import teabagml.datasets.Queryable;
import teabagml.bayesnet.BayesNet;

public interface BNScoreFunction {

    /**
     * Evaluate how much the structure of bayesnet fit the dataset.
     * @param dataset the dataset that the bayesnet is modeling
     * @param bayesNet the bayesian network
     * @return a number representing how much the structure of bayesnet fit the dataset
     */
    public double getScore(Queryable dataset, BayesNet bayesNet);

}
