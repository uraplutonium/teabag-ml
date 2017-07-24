package teabagml.datasets;

import teabagml.bayesnet.BayesNet;

public class FakeDataset implements Queryable {
    private int dimension;
    private int dataSize;
    private Arity arity;
    private BayesNet bayesNet;

    public FakeDataset(BayesNet bn, int ds, int d, Arity a) {
	bayesNet = bn;
	dataSize = ds;
	dimension = d;
	arity = a;
    }

    @Override
    public Arity getArity() {
	return arity;
    }

    @Override
    public int getDimension() {
	return dimension;
    }

    @Override
    public int getDataSize() {
	return dataSize;
    }

    @Override
    public boolean isSymbolic() {
	return true;
    }

    @Override
    public boolean isLabelled() {
	return false;
    }

    public boolean isQueryValid(int[] query) {
	if (query.length != dimension)
	    return false;

	boolean qValid = true;
	for (int i=0; i<dimension; i++)
	    if (query[i] < -1 || query[i] >= arity.values(i)) {
		qValid = false;
		break;
	    }
	return qValid;
    }

    @Override
    public int count(int[] query) {
	if (!isQueryValid(query))
	    return -1;
	return (int)(bayesNet.getJointProb(query)*(double)dataSize);
    }
}
