package teabagml.egonetwork;

import teabagml.datasets.*;
import teabagml.bayesnet.BayesNet;

public class EgoBayesNet {

    BayesNet featNet;
    int kappa;
    double alpha;
    //int numEdge;
    int numAlter;
    Circle[] nodeC; // nodeC[i] represents the ith circle, there number of circles is kappa
    double[][] nodeE; // nodeE[i][j] is the node representing the probability of constructing edge alter_i with alter_j, 0<=i,j<numAlter

    /**
     * @param n the name of egoBayesNet
     * @param nf the number of features (Queryable.getDimension())
     * @param k the hyper-parameter kappa, which indicates the number of circles
     * @param na the number of alters in dataset (EgoNetwork.getDimension())
     * param ne the number of edges in ego-network (EgoNetwork.getNumEdges())
     * @param af the arity of features (Queryable.getArity())
     */
    public EgoBayesNet(String n, int nf, int k, int na, Arity af, double a) {
	kappa = k;
	alpha = a;
	//numEdge = ne;
	numAlter = na;
	featNet = new BayesNet(n, nf, af);
	nodeC = new Circle[kappa];
	for(int i=0; i<kappa; i++)
	    nodeC[i] = new Circle(numAlter, nf);
	// TODO: currently we don't use the ne, and calculate all edge/!edge
	nodeE = new double[numAlter][];
	for(int i=0; i<numAlter; i++)
	    nodeE[i] = new double[numAlter];
    }

    /**
     * @return the hash code of the ego bayesian network
     */
    @Override
    public int hashCode() {
	// TODO: to be edited
	return featNet.hashCode();
    }

    /**
     * @param obj another egoBayesNet Object
     * @return true if two egoBayesNet have:
     *        1. the same dimension of feature bayesNet
     *        2. the same arities of feature bayesNet
     *        3. the same adjacency matrix, meaning the same graph structures, including feature, circle and edge parts
     *        4. the same 
     */
    @Override
    public boolean equals(Object obj) {
	if(obj == null)
	    return false;
	if(obj instanceof EgoBayesNet) {
	    EgoBayesNet thatBN = (EgoBayesNet)obj;
	    // same feature bayesnet
	    if(!thatBN.featNet.equals(featNet))
		return false;
	    // TODO: add comparasion of other variables

	    return true;
	} else
	    return false;
    }

    /**
     * @param alter1 the number of the precursor alter, 0 <= alter1 <= numAlter-1
     * @param alter2 the number of the subsequent alter, 0 <= alter2 <= numAlter-1
     * The probability saved in nodeE[alter1][alter2] is to be updated,
     * which represents the probability of constructing an edge in ego-network
     */
    protected boolean updateEdgeProb(int alter1, int alter2, Queryable ds) {
	if(alter1<0 || alter1>=numAlter || alter2<0 || alter2>=numAlter) {
	    return false;
	}
	// the prob of a particular edge should reflect the following aspects:
	// 1. the more circles an edge is in, the larger prob it has
	// 2. for a certain circle, the prob of an edge depends only on the features the circle associate
	// 3. the value of an edge ranges from 2 to 0, 2 gives a bonus, 1 gives a small bonus, 0 gives a penalty

	double edgeValue = 0;
	// for each circle, calculate TODO?
	for(int i=0; i<kappa; i++) {
	    // edgeValue is 0 if both alters are NOT in the circle
	    // is 1 if one of them is in the circle
	    // is 2 if BOTH of them are in the circle
	    int edgeFlag = nodeC[i].isAlterIn(alter1) + nodeC[i].isAlterIn(alter2);
	    // we use the joint probability of features all having "2" values
	    // a particular value of feature j is the probability of two alters have the same/different values(either has or has not a featuref)

	    // generate a query containing for only those features that associated with circle nodeC[i]
	    int[] query = new int[featNet.getDimension()];
	    for(int j=0; j<featNet.getDimension(); j++) {
		// if the circle cares a particular feature, inquire the probability of taking value "1"
		// fill with "-1" for uncare features
		query[j] = (nodeC[i].isFeatureIn(j) == 1 ? 1 : -1);
	    }
	    // TODO: set the probabilities to the BN first and read them in a proper way,
	    // rather than read the real prob directly from the dataset
	    // double jointProb = featNet.getJointProb(query);
	    double realProb = (double)(ds.count(query)) / ((double)ds.getDataSize());
	    // System.out.println("jointprob " + jointProb);
	    if(edgeFlag != 2)
		realProb *= alpha;
	    edgeValue += realProb;
	    //System.out.println("realprob " + realProb);
	}
	nodeE[alter1][alter2] = edgeValue;
	// System.out.println("edgeValue " + alter1 + "-" + alter2 + " = " + nodeE[alter1][alter2]);
	return true;
    }
    
    /**
     * Update the marginal probabilities of all nodes according to the prior and conditional probabilities in feature bayesNet
     */
    public void updateAllMarginalProb(EgoNetwork egoNet, Queryable ds) {
	int counter = 0;
	//long avg = 0;
	int numEdge = egoNet.getNumEdges();
	featNet.updateAllMarginalProb();
	// TODO change the for into an iteration of all edges in egonetwork
	for(int i=0; i<numAlter; i++) {
	    // System.out.print(" alter_" + i);
	    for(int j=0; j<numAlter; j++) {
		if(i<j && egoNet.isEdgeIn(i, j)) {
		    //long duration = System.currentTimeMillis();
		    updateEdgeProb(i, j, ds); // TODO: we should not use ds, see updateEdgeProb()
		    /* duration = System.currentTimeMillis()-duration;
		    if(counter<10) {
			avg += duration;
			counter++;
		    }else if(counter==10){
			avg/=10;
			System.out.println();
			System.out.println("EST: " + avg*numEdge/1000 + "s (" + (double)avg*(double)numEdge/1000/60 + " min).");
			counter++;
		    }
		    */
		}
	    }
	}
    }

    /**
     * Print all the information of nodes in the feature bayesNet
     */
    public void printNodesInfo() {
	featNet.printNodesInfo();
    }
    
    /**
     * @param nc the number of circle, 0<=nc<=(kappa-1)
     * @param circle the circle to be assign to nodeC[nc]
     * @return true if nodeC[nc] is assigned successfully
     *        false otherwise.
     */
    public boolean setNodeC(int nc, Circle circle) {
	if(nc<0 || nc>=kappa)
	    return false;
	nodeC[nc] = circle;
	return true;
    }

    /**
     * @param circles the set of circles, including kappa circles.
     * @return true if nodeC is assigned successfully
     *        false otherwise.
     */
    public boolean setNodeC(Circle[] circles) {
	if(circles.length > kappa)
	    return false;
	for(int i=0; i<kappa; i++)
	    nodeC[i] = circles[i];
	return true;
    }
    
    /**
     * @param alter1 the number of the precursor alter, 0 <= alter1 <= numAlter-1
     * @param alter2 the number of the subsequent alter, 0 <= alter2 <= numAlter-1
     * @return the probability saved in nodeE[alter1][alter2],
     *        which represents the probability of constructing an edge in ego-network.
     *        return -1 if the inputs are invalid.
     */
    public double getEdgeProb(int alter1, int alter2) {
	if(alter1<0 || alter1>=numAlter || alter2<0 || alter2>=numAlter)
	    return -1;
	return nodeE[alter1][alter2];
    }

    
    /**
     * @param egoNet the egoNetwork that the EgoBayesNet should describe
     * @return the probability of generating the egoNet 
     */
    public double getEgoNetProb(EgoNetwork egoNet) {
	double sumProb = 0;
	for(int i=0; i<egoNet.getNumAlters(); i++) {
	    for(int j=i+1; j<egoNet.getNumAlters(); j++) {
		if(egoNet.isEdgeIn(i, j)) {
		    double edgeProb = getEdgeProb(i, j);
		    sumProb += edgeProb;
		    // System.out.println("P(alter_" + i + ", alter_" + j + ")= " + edgeProb);
		}
	    }
	}
	return sumProb;
    }

    public void setFeatNet(BayesNet bayesNet) {
	featNet = bayesNet;
    }
    
    public BayesNet getFeatNet() {
	return featNet;
    }
}
