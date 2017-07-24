package teabagml.problems;

import java.util.ArrayList;
import java.util.List;
import teabagml.bayesnet.BayesNet;
import teabagml.datasets.Queryable;
import teabagml.bnlearn.*;
import teabagml.pack.Debug;
import teabagml.bayesnet.ConditionalProbTable.Mode;

public class BayesNetStatus extends StatusNode {
    
    private BayesNet bayesNet;
    private List<BayesNetStatus> parentsBayesNets;
    private double score;
    private Queryable dataset;
    private List<IStatusNode> children = new ArrayList<IStatusNode>();
    
    public BayesNetStatus(StatusType type, BayesNet startBN, Queryable ds) {
	super(type);
	bayesNet = startBN;
	dataset = ds;
	score = Double.MAX_VALUE;
	// BNScoreFunction bicFunc = new BICScoreFunction();
	// if (startBN.noEdges()) {
	//     score = Integer.MAX_VALUE;
	// } else {
	//     score = bicFunc.getScore(dataset, bayesNet);
	//     score *= (-1);
	// }
	parentsBayesNets = new ArrayList<BayesNetStatus>();
	children.clear();
    }

    @Override
    public int hashCode() {
	return bayesNet.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if(obj instanceof BayesNetStatus)
	    return bayesNet.equals(((BayesNetStatus)obj).bayesNet);
	else
	    return false;
    }

    @Override
    public List<IStatusNode> expand() {
	children.clear();
	for (int j=0; j<bayesNet.getDimension(); j++) {
	    for (int i=0; i<bayesNet.getDimension(); i++) {
		if (i != j) {
		    BayesNet newBayesNet = bayesNet.makeCopy();
		    boolean[][] adjMatrix = newBayesNet.getAdjMatrix();

		    if (adjMatrix[i][j] == true && adjMatrix[j][i] == false) { // already linkded, remove
			boolean flag = newBayesNet.removeEdge(i, j, Mode.Evenly);
			if (!flag) {
			    System.out.println("removing edge " + i + " -> " + j + " failed!");
			    return null;
			}

			boolean newStruct = true;
			for (BayesNetStatus eachParentBN : parentsBayesNets)
			    if (bayesNet.equals(eachParentBN.bayesNet))
				newStruct = false;

			if (newStruct) {
			    BayesNetStatus newBayesNetStatus = new BayesNetStatus(statusType, newBayesNet, dataset);
			    newBayesNetStatus.parentsBayesNets.add(this);
			    for (BayesNetStatus eachParentBN : parentsBayesNets)
				newBayesNetStatus.parentsBayesNets.add(eachParentBN);
			    newBayesNet.updateAllMarginalProb();
			    children.add(newBayesNetStatus);
			}
		    } else if (adjMatrix[i][j] == false && adjMatrix[j][i] == false) { // unlinked yet, add
			boolean flag = newBayesNet.addEdge(i, j, Mode.Evenly);
			if (!flag) {
			    System.out.println("adding edge " + i + " -> " + j + " failed!");
			    return null;
			}

			boolean newStruct = true;
			for (BayesNetStatus eachParentBN : parentsBayesNets) {
			    if (bayesNet.equals(eachParentBN.bayesNet))
				newStruct = false;
			}

			if (newStruct) {
			    BayesNetStatus newBayesNetStatus = new BayesNetStatus(statusType, newBayesNet, dataset);
			    newBayesNetStatus.parentsBayesNets.add(this);
			    for (BayesNetStatus eachParentBN : parentsBayesNets)
				newBayesNetStatus.parentsBayesNets.add(eachParentBN);
			    newBayesNet.updateAllMarginalProb();
			    children.add(newBayesNetStatus);
			}
		    }
		}
	    }
	}
	return children;
    }

    public void displayNode() {
	bayesNet.printNodesInfo();
	System.out.println("Score: " + score);
    }
    
    @Override
    public int costTo(IStatusNode otherNode) throws Exception {
	for (IStatusNode eachIStatusNode : children)
	    if (eachIStatusNode.equals(otherNode))
		return 0;
	throw new Exception();
    }

    @Override
    public void setCost(int c) {
	// set the cost as the fixed BIC score for the current BN
	// if (bayesNet.noEdges()) {
	//     cost = Integer.MAX_VALUE;
	// } else {
	//     cost = (int)score;
	// }
	cost = 0;
    }
    
    @Override
    public int h(IStatusNode GOAL) {
	if(score == Double.MAX_VALUE) {
	    // edit the following line to change scoring functions
	    // AICScoreFunction, BICScoreFunction, HQCScoreFunction, MDLScoreFunction
	    //BNScoreFunction bnFunc = new BICScoreFunction();
	    //BNScoreFunction bnFunc = new AICScoreFunction();
	    //BNScoreFunction bnFunc = new HQCScoreFunction();
	    BNScoreFunction bnFunc = new MDLScoreFunction();
	    if (bayesNet.noEdges()) {
		score = Double.MAX_VALUE;
	    } else {
		score = bnFunc.getScore(dataset, bayesNet);
		score *= (-1);
	    }
	}
	System.out.println("SCORE:" + score);
	return (int)score;
    }
    
    @Override
    public boolean isSolved(IStatusNode GOAL) {
	boolean solved = false;
	if (parentsBayesNets.size() != 0) {
	    BayesNetStatus parentBNStatus = parentsBayesNets.get(0);
	    solved = (score > parentBNStatus.score);
	}
	solved = solved || Debug.isSolved();
	return solved;
    }

    public double getScore() {
	return score;
    }
    
    @Override
    public String toString() {
	return bayesNet.toString() + "\nScore: " + score;
    }

    public BayesNet getBayesNet() {
	return bayesNet;
    }

}
