package teabagml.bayesnet;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.util.Iterator;
import java.util.LinkedList;
import teabagml.datasets.*;
//import teabagml.bayesnet.ConditionalProbTable;
import teabagml.bayesnet.ProbabilityTable.ProbTabType;
import teabagml.bayesnet.ConditionalProbTable.Mode;

import teabagml.pack.Debug;

public class BayesNet {

    private String name;
    private int dimension;
    private Arity arity;
    private boolean bnAdjMatrix[][]; // size: dimension * dimension
    private ProbabilityTable probTable[]; // either prior prob. table or conditional prob. table
    private double marginalProbTable[][]; // saves the current probabilities of all nodes
    private int[] topoSequence; // the topological sequence of the network

    public BayesNet(String n, int d, Arity a) {
	name = n;
	dimension = d;
	arity = a;

	// initialise the adjacency matrix
	bnAdjMatrix = new boolean[dimension][dimension];
	for (int i=0; i<dimension; i++)
	    for (int j=0; j<dimension; j++)
		bnAdjMatrix[i][j] = false;

	// initialise the probTable array
	probTable = new ProbabilityTable[dimension];
	for (int i=0; i<dimension; i++)
	    probTable[i] = new ArrayPriorProbTable(arity, i);

	// initialise the current probability table
	marginalProbTable = new double[dimension][];
	for (int i=0; i<dimension; i++) {
	    marginalProbTable[i] = new double [arity.values(i)];
	    double defaultProb = 1.0/arity.values(i);
	    for (int j=0; j<arity.values(i); j++) {
		marginalProbTable[i][j] = defaultProb;
	    }
	}

	// initialise the topological sequence
	topoSequence = new int[dimension];
	for (int i=0; i<dimension; i++)
	    topoSequence[i] = -1;
    }

    /**
     * @return the hash code of the bayesian network
     */
    @Override
    public int hashCode() {
	String hashStr = "";
	for (int i=0; i<dimension; i++)
	    hashStr = hashStr + String.valueOf(arity.values(i)) + ",";
	hashStr += "$";
	for (int i=0; i<dimension; i++)
	    for (int j=0; j<dimension; j++)
		hashStr = hashStr + (bnAdjMatrix[i][j] ? "1" : "0");
	// probTable information
	// hashStr += "$";
	// for (int i=0; i<dimension; i++) {
	//     hashStr += "#";
	//     if (probTable[i].getTabType() == ProbTabType.PriorTab) {
	// 	PriorProbTable priorTab = (PriorProbTable)probTable[i];
	// 	for (int j=0; j<arity.values(j); j++)
	// 	    hashStr = hashStr + String.valueOf(priorTab.getPriorProb(j)) + ",";
	//     } else {
	// 	ConditionalProbTable conditionalTab = (ConditionalProbTable)probTable[i];
	// 	int[] conAttr = conditionalTab.getConAttributes();
	// 	int[] allAttr = new int[conAttr.length+1];
	// 	allAttr[0] = i;
	// 	for (int j=0; j<conAttr.length; j++)
	// 	    allAttr[j+1] = conAttr[j];
	// 	CartesianProduct allValues = new CartesianProduct(arity, allAttr);
	// 	for (int[] eachValues : allValues)
	// 	    hashStr = hashStr + String.valueOf(conditionalTab.getConProb(eachValues)) + ",";
	//     }
	// }
	return hashStr.hashCode();
    }

    /**
     * @param obj another bayesnet Object
     * @return true if two bayesnets have:
     *         1. the same dimensions
     *         2. the same arities
     *         3. the same adjacency matrix, meaning the same graph structures
     *         4. the same probability tables with all the same values, either prior of conditional
     *         otherwise, return false
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
	if(obj instanceof BayesNet) {
	    BayesNet thatBN = (BayesNet)obj;
	    // same dimension
	    if (dimension != thatBN.dimension)
		return false;
	    // same arity
	    for (int i=0; i<dimension; i++)
		if (arity.values(i) != thatBN.arity.values(i))
		    return false;
	    // same adjacency matrix
	    for (int i=0; i<dimension; i++)
		for (int j=0; j<dimension; j++)
		    if (bnAdjMatrix[i][j] != thatBN.bnAdjMatrix[i][j])
			return false;
	    // same probability tables
	    // for (int i=0; i<dimension; i++) {
	    // 	// same probability table type
	    // 	if (probTable[i].getTabType() != thatBN.probTable[i].getTabType())
	    // 	    return false;
	    // 	// same probabitlites
	    // 	if (probTable[i].getTabType() == ProbTabType.PriorTab) {
	    // 	    PriorProbTable priorProbTable = (PriorProbTable)probTable[i];
	    // 	    PriorProbTable thatPriorProbTable = (PriorProbTable)(thatBN.probTable[i]);
	    // 	    for (int j=0; j<arity.values(i); j++)
	    // 		if (priorProbTable.getPriorProb(j) != thatPriorProbTable.getPriorProb(j))
	    // 		    return false;
	    // 	} else {
	    // 	    ConditionalProbTable conditionalProbTable = (ConditionalProbTable)probTable[i];
	    // 	    ConditionalProbTable thatConditionalProbTable = (ConditionalProbTable)(thatBN.probTable[i]);
	    // 	    int[] conAttr = conditionalProbTable.getConAttributes();
	    // 	    int[] allAttr = new int[conAttr.length+1];
	    // 	    allAttr[0] = i;
	    // 	    for (int j=0; j<conAttr.length; j++)
	    // 		allAttr[j+1] = conAttr[j];
	    // 	    CartesianProduct cartProd = new CartesianProduct(arity, allAttr);
	    // 	    for (int[] eachValues : cartProd)
	    // 		if (conditionalProbTable.getConProb(eachValues) != thatConditionalProbTable.getConProb(eachValues))
	    // 		    return false;
	    // 	}
	    // }
	    return true;
	} else
	    return false;
    }

    public BayesNet makeCopy() {
	BayesNet copyBN = new BayesNet(name, dimension, arity);
	// copy adjacency matrix
	for (int i=0; i<dimension; i++)
	    for (int j=0; j<dimension; j++)
		copyBN.bnAdjMatrix[i][j] = bnAdjMatrix[i][j];
	// copy probability tables
	for (int i=0; i<dimension; i++) {
	    if (probTable[i].getTabType() == ProbTabType.PriorTab) {
		PriorProbTable priorProbTable = (PriorProbTable)probTable[i];
		PriorProbTable copyPriorProbTable = (PriorProbTable)(copyBN.probTable[i]);
		for (int j=0; j<arity.values(i); j++)
		    copyPriorProbTable.setPriorProb(j, priorProbTable.getPriorProb(j));
	    } else {
		ConditionalProbTable conditionalProbTable = (ConditionalProbTable)probTable[i];
		int[] conAttr = conditionalProbTable.getConAttributes();
		int[] copyConAttr = new int[conAttr.length];
		for (int j=0; j<conAttr.length; j++)
		    copyConAttr[j] = conAttr[j];

		ConditionalProbTable copyConditionalProbTable = new MapConProbTable(arity, i, copyConAttr);
		copyBN.probTable[i] = copyConditionalProbTable;
		int[] allAttr = new int[conAttr.length+1];
		allAttr[0] = i;
		for (int j=0; j<conAttr.length; j++)
		    allAttr[j+1] = conAttr[j];
		CartesianProduct cartProd = new CartesianProduct(arity, allAttr);
		for (int[] eachValues : cartProd)
		    conditionalProbTable.setConProb(eachValues, copyConditionalProbTable.getConProb(eachValues));
	    }
	}
	return copyBN;
    }

    /**
     * @return the adjacency matrix of the bayesian network
     */
    public boolean[][] getAdjMatrix() {
	return bnAdjMatrix;
    }

    /**
     * @return the name of the bayesian network
     */
    public String getName() {
	return name;
    }

    /**
     * @return the arity of the bayesian network
     */
    public Arity getArity() {
	return arity;
    }

    /**
     * @return the dimension
     */
    public int getDimension() {
	return dimension;
    }

    /**
     * @param n the arity of a specific node
     * @return an array containing n probabilities whose summation is 1.0
     */
    private double[] getRandomProbs(int n) {
	Random rand = new Random();
	//rand.setSeed(0);
	double remainingProb = 1.0;
	double[] newProbs = new double[n];
	for (int i=0; i<n-1; i++) {
	    newProbs[i] = (double)((int)(remainingProb * rand.nextDouble()*10000))/10000;
	    remainingProb -= newProbs[i];
	}
	newProbs[n-1] = (double)((int)(remainingProb*10000))/10000;
	return newProbs;
    }

    /**
     * Add an edge to the bayesian network.
     * @param parent the attribute index of the parent node
     * @param child the attribute index of the child node
     * @return true if edge is added successfully, otherwise return false
     */
    public boolean addEdge(int parent, int child) {
	return addEdge(parent, child, Mode.Evenly);
    }

    /**
     * Add an edge to the bayesian network.
     * @param parent the attribute index of the parent node
     * @param child the attribute index of the child node
     * @param mode Evenly: initialise all values as 1/n_child where the n_child is the arity of "child"
     *             Random: initialise all values randomly
     *             Inherited: inherit all values from the old conditional prob. table of "child"
     * @return true if edge is added successfully, otherwise return false
     */
    public boolean addEdge(int parent, int child, Mode mode) {
	// System.out.println("Adding edge " + parent + " -> " + child);
	if (parent >= 0 && parent < dimension
	    && child >=0 && child < dimension
	    && parent != child
	    && bnAdjMatrix[child][parent]==false
	    && bnAdjMatrix[parent][child]==false) {
	    bnAdjMatrix[parent][child] = true;
	    
	    // whether the node "child" had any parent nodes or not,
	    // always creates new conditional prob. table,
	    // the old prior/conditional prob. table will be dropped.
	    // this if-else block initialises the parentsList and the empty conditional prob. table only
	    int[] newParentsList = null;
	    int newParentIndex = -1; // indicates the index of new added parent node in the newParentsList
	    if (probTable[child].getTabType() == ProbTabType.PriorTab) {
		newParentsList = new int[1];
		newParentsList[0] = parent;
		newParentIndex = 0;
	    } else {
		ConditionalProbTable conProbTable = (ConditionalProbTable)probTable[child];
		int[] oldParentsList = conProbTable.getConAttributes();

		// generates the new parents list from the old conditional prob. table
		newParentsList = new int[oldParentsList.length + 1];
		int j=0;
		int i=0;
		for (i=0; i<oldParentsList.length; i++) {
		    if (oldParentsList[i] > parent && i==j) {
			newParentsList[j] = parent;
			newParentIndex = j;
			j++;
		    }
		    newParentsList[j] = oldParentsList[i];
		    j++;
		}
		// the case that the new parent node number is larger than any node number in the old parent list
		if (i==j) {
		    newParentsList[j] = parent;
		    newParentIndex = j;
		}
	    }

	    ProbabilityTable oldProbTable = probTable[child];
	    probTable[child] = new MapConProbTable(arity, child, newParentsList);

	    int[] nodeList = new int[newParentsList.length+1];
	    nodeList[0] = child;
	    for (int i=0; i<newParentsList.length; i++)
		nodeList[i+1] = newParentsList[i];
	    CartesianProduct cartProd = new CartesianProduct(arity, nodeList);

	    if (mode == Mode.Evenly) { // same values of 1/n_child
		// System.out.println("ADD: generate EVENLY prob. table for node_" + child);
		double prob = 1.0/arity.values(child);
		for (int[] eachCon : cartProd)
		    setConProb(child, eachCon, prob);
	    } else if (mode == Mode.Random) { // random values
		// System.out.println("ADD: generate RANDOM prob. table for node_" + child);
		Iterator<int[]> conProbIter = cartProd.iterator();
		for (int i=0; i<cartProd.size()/arity.values(child); i++) {
		    double[] randProbs = getRandomProbs(arity.values(child));
		    for (int j=0; j<arity.values(child); j++)
			setConProb(child, conProbIter.next(), randProbs[j]);
		}
	    } else if (mode == Mode.Inherited) { // inherit values from old con. prob. table
		// System.out.println("ADD: generate INHERITED prob. table for node_" + child);
		ConditionalProbTable oldConProbTable = (ConditionalProbTable)oldProbTable;
		for (int[] eachCartProd : cartProd) {
		    // recover the old cart. product from the new one
		    int[] oldCartProd = new int[newParentsList.length];
		    int j=0;
		    for (int i=0; i<newParentsList.length; i++) {
			if (j == newParentIndex)
			    j++;
			oldCartProd[i] = eachCartProd[j];
			j++;
		    }
		    double oldProb = oldConProbTable.getConProb(oldCartProd);
		    setConProb(child, eachCartProd, oldProb);
		}
	    }
	    return true;
	} else
	    return false;
    }

    /**
     * Remove an edge from the bayesian network.
     * @param parent the attribute index of the parent node
     * @param child the attribute index of the child node
     * @return true if edge is removed successfully, otherwise return false
     */
    public boolean removeEdge(int parent, int child) {
	return removeEdge(parent, child, Mode.Inherited);
    }

    /**
     * Remove an edge from the bayesian network.
     * @param parent the attribute index of the parent node
     * @param child the attribute index of the child node
     * @param mode Evenly: initialise all values as 1/n_child where the n_child is the arity of "child"
     *             Random: initialise all values randomly
     *             Inherited: inherit all values from the old conditional prob. table of "child"
     * @return true if edge is removed successfully, otherwise return false
     */
    public boolean removeEdge(int parent, int child, Mode mode) {
	// System.out.println("Removing edge " + parent + " -> " + child);
	if (parent >= 0 && parent < dimension
	    && child >=0 && child < dimension
	    && parent != child
	    && bnAdjMatrix[child][parent]==false
	    && bnAdjMatrix[parent][child]==true) {
	    bnAdjMatrix[parent][child] = false;
	   
	    ConditionalProbTable conProbTable = null;
	    int[] oldParentsList = null;
	    int[] newParentsList = null; // only makes sence for new con. prob. table, not for prior prob. table
	    CartesianProduct cartProd = null; // only makes sence for new con. prob. table, not for prior prob. table
	    int removedParentIndex = -1; // indicates the index of removed parent node in the oldParentsList
	    // if (probTable[child].getTabType() == ProbTabType.ConditionalTab) { // generate the newParentsList
	    if (getParentList(child).length != 0) { // generate the newParentsList
		// generates the new parents list from the old conditional prob. table
		conProbTable = (ConditionalProbTable)probTable[child];
		oldParentsList = conProbTable.getConAttributes();
		newParentsList = new int[oldParentsList.length-1];
		int j=0;
		for (int i=0; i<oldParentsList.length; i++) {
		    if (oldParentsList[i] == parent) {
			removedParentIndex = i;
			continue;
		    }
		    newParentsList[j] = oldParentsList[i];
		    j++;
		}

		// generate the new conditional prob. table
		probTable[child] = new MapConProbTable(arity, child, newParentsList);
		int[] nodeList = new int[newParentsList.length+1];
		nodeList[0] = child;
		for (int i=0; i<newParentsList.length; i++)
		    nodeList[i+1] = newParentsList[i];

		cartProd = new CartesianProduct(arity, nodeList);
	    } else {
		// generate the new prior prob. table
		probTable[child] = new ArrayPriorProbTable(arity, child);
	    }

	    if (mode == Mode.Evenly) { // same values of 1/n_child
		// System.out.println("REMOVE: generate EVENLY prob. table for node_" + child);
		double prob = 1.0/arity.values(child);
		// if (probTable[child].getTabType() == ProbTabType.ConditionalTab) {
		if (getParentList(child).length != 0) {
		    // assign values to the conditional prob. table
		    for (int[] eachCon : cartProd) {
			setConProb(child, eachCon, prob);
		    }
		} else {
		    // assign values to the prior prob. table
		    for (int i=0; i<arity.values(child); i++)
			setPriorProb(child, i, prob);
		}
	    } else if (mode == Mode.Random) { // random values
		// System.out.println("REMOVE: generate RANDOM prob. table for node_" + child);
		// if (probTable[child].getTabType() == ProbTabType.ConditionalTab) {
		if (getParentList(child).length != 0) {
		    // assign values to the conditional prob. table
		    Iterator<int[]> conProbIter = cartProd.iterator();
		    for (int i=0; i<cartProd.size()/arity.values(child); i++) {
			double[] randProbs = getRandomProbs(arity.values(child));
			for (int j=0; j<arity.values(child); j++)
			    setConProb(child, conProbIter.next(), randProbs[j]);
		    }
		} else {
		    // assign values to the prior prob. table
		    double[] randProbs = getRandomProbs(arity.values(child));
		    for (int i=0; i<arity.values(child); i++)
			setPriorProb(child, i, randProbs[i]);
		}

	    } else if (mode == Mode.Inherited) { // inherit values from old con. prob. table
		// System.out.println("REMOVE: generate INHERITED prob. table for node_" + child);
		// if (probTable[child].getTabType() == ProbTabType.ConditionalTab) {
		if (getParentList(child).length != 0) {
		    // assign values to the conditional prob. table
		    // assign the average values of P(child, oldCartProd) to P(child, eachCartProd)
		    int[] oldNodeList = new int[oldParentsList.length+1];
		    oldNodeList[0] = child;
		    for (int i=0; i<oldParentsList.length; i++)
			oldNodeList[i+1] = oldParentsList[i];
		    
		    CartesianProduct oldCartProd = new CartesianProduct(arity, oldNodeList);
		    for (int[] eachCartProd : oldCartProd) {
			// the eachCartProd[removedParentIndex+1] is the removed parent node's value
			double oldProb = conProbTable.getConProb(eachCartProd);
			int[] newCartProd = new int[eachCartProd.length-1];
			int j=0;
			for (int i=0; i<eachCartProd.length; i++) {
			    if (i==removedParentIndex+1) {
				i++;
				continue;
			    }
			    newCartProd[j] = eachCartProd[i];
			    j++;
			}
			double currentProb = getConProb(child, newCartProd);
			double newProd = oldProb+currentProb;
			setConProb(child, newCartProd, newProd);
		    }
		    
		    // divide each prob in the new con. prob. table
		    for (int[] eachCartProd : cartProd) {
			double dividedProd = getConProb(child, eachCartProd)/arity.values(parent);
			dividedProd = (double)((int)(dividedProd*10000))/10000;
			setConProb(child, eachCartProd, dividedProd);
		    }
		} else {
		    // assign values to the prior prob. table
		    for (int i=0; i<arity.values(child); i++) {
			double newProd = 0.0;
			for (int j=0; j<arity.values(parent); j++) {
			    int[] oldCartProd = new int[2];
			    oldCartProd[0] = i;
			    oldCartProd[1] = j;
			    newProd += conProbTable.getConProb(oldCartProd);
			}
			double dividedProd = newProd/arity.values(parent);
			dividedProd = (double)((int)(dividedProd*10000))/10000;
			setPriorProb(child, i, dividedProd);
		    }
		}
	    }
	    return true;
	} else
	    return false;
    }

    /**
     * @param node the attribute index of a node
     * @return a list containing the index of all the parents of node
     * Return null if the index of node is invalid
     * The number of parents can be obtain by using getParentList().length
     */
    public int[] getParentList(int node) {
	if (node >= 0 && node < dimension) {
	    List<Integer> parentList = new ArrayList<Integer>();
	    for (int i=0; i<dimension; i++)
		if (bnAdjMatrix[i][node])
		    parentList.add(i);
	    int parentArray[] = new int[parentList.size()];
	    for (int i=0; i<parentList.size(); i++)
		parentArray[i] = parentList.get(i).intValue();
	    return parentArray;
	} else
	    return null;
    }

    /**
     * @param node the attribute index of a node
     * @return a list containing the index of all the children of node
     * Return null if the index of node is invalid
     * The number of children can be obtain by using getParentList().length
     */
    public int[] getChildrenList(int node) {
	if (node >= 0 && node < dimension) {
	    List<Integer> childrenList = new ArrayList<Integer>();
	    for (int i=0; i<dimension; i++)
		if (bnAdjMatrix[node][i])
		    childrenList.add(i);
	    int childrenArray[] = new int[childrenList.size()];
	    for (int i=0; i<childrenList.size(); i++)
		childrenArray[i] = childrenList.get(i).intValue();
	    return childrenArray;
	} else
	    return null;
    }

    /**
     * Set the prior probability: P(node = attrValue) = priorProb
     * @param node the attribute index of a node
     * @param attrValue the specific possible value of the node
     * @param priorProb the prior probability
     * @return true if the prior probability is set successfully, return false if the node or attrValue are invalid
     */
    public boolean setPriorProb(int node, int attrValue, double priorProb) {
	if (node >=0 && node < dimension // the node number must be valid
	    && attrValue >= 0 && attrValue < arity.values(node)) { // the attribute value must be valid
	    // return false if the node has parents which cannot have prior probabilities
	    for (int i=0; i<dimension; i++)
		if (bnAdjMatrix[i][node] == true)
		    return false;

	    marginalProbTable[node][attrValue] = priorProb;
	    if (probTable[node] != null) {
		PriorProbTable priorProbTable = (PriorProbTable)probTable[node];
		priorProbTable.setPriorProb(attrValue, priorProb);
	    }
	    return true;
	} else
	    return false;
    }

    /**
     * @param node the attribute index of a node
     * @param attrValue the specific possible value of the node
     * @return the prior probability: P(node = attrValue), return -1 if the node or attrValue are invalid
     */
    public double getPriorProb(int node, int attrValue) {
	if (node >=0 && node < dimension // the node number must be valid
	    && attrValue >= 0 && attrValue < arity.values(node)) { // the attribute value must be valid
	    // return false if the node has parents which cannot have prior probabilities
	    for (int i=0; i<dimension; i++)
		if (bnAdjMatrix[i][node] == true)
		    return -1;

	    if (probTable[node] == null)
		return -1;

	    PriorProbTable priorProbTable = (PriorProbTable)probTable[node];
	    return priorProbTable.getPriorProb(attrValue);
	} else
	    return -1;
    }

    /**
     * @param node the attribute index of a node
     * @param allValue the array containing all the values [v1, v2, v3] of attributes x, y, z in P(x=v1 | y=v2, z=v3)
     * @param conProb the conditional probability of P(x=v1 | y=v2, z=v3)
     * @return true if the conditional probability is set successfully, return false if the input condition or conProb is invalid
     */
    public boolean setConProb(int node, int[] allValue, double conProb) {
	if (probTable[node] == null)
	    probTable[node] = new MapConProbTable(arity, allValue);
	ConditionalProbTable conProbTable = (ConditionalProbTable)probTable[node];
	return conProbTable.setConProb(allValue, conProb);
    }

    /**
     * @param node the attribute index of a node
     * @param curValue the value v1 of the attribute x in P(x=v1 | y=v2, z=v3)
     * @param conValue the array containing all the values [v2, v3] of attributes y, z in P(x=v1 | y=v2, z=v3)
     * @param conProb the conditional probability of P(x=v1 | y=v2, z=v3)
     * @return true if the conditional probability is set successfully, return false if the input condition or conProb is invalid
     */
    public boolean setConProb(int node, int curValue, int[] conValue, double conProb) {
	if (probTable[node] == null)
	    probTable[node] = new MapConProbTable(arity, curValue, conValue);
	ConditionalProbTable conProbTable = (ConditionalProbTable)probTable[node];
	return conProbTable.setConProb(curValue, conValue, conProb);
    }

    /**
     * @param node the attribute index of a node
     * @param allValue the array containing all the values [v1, v2, v3] of attributes x, y, z in P(x=v1 | y=v2, z=v3)
     * @return the conditional probability, return -1 if the input condition is invalid
     */
    public double getConProb(int node, int[] allValue) {
	if (probTable[node] == null)
	    return -1;
	else {
	    ConditionalProbTable conProbTable = (ConditionalProbTable)probTable[node];
	    return conProbTable.getConProb(allValue);
	}
    }

    /**
     * @param node the attribute index of a node
     * @param curValue the value v1 of the attribute x in P(x=v1 | y=v2, z=v3)
     * @param conValue the array containing all the values [v2, v3] of attributes y, z in P(x=v1 | y=v2, z=v3). The conValue only contains the values of parent nodes.
     * @return the conditional probability, return -1 if the input condition is invalid
     */
    public double getConProb(int node, int curValue, int[] conValue) {
	if (probTable[node] == null)
	    return -1;
	else {
	    ConditionalProbTable conProbTable = (ConditionalProbTable)probTable[node];
	    return conProbTable.getConProb(curValue, conValue);
	}
    }

    /**
     * @param node the attribute index of a node
     * @param attrValue the specific possible value of the node
     * @return the marginal probability: P(node = attrValue), return -1 if the node or attrValue are invalid
     */
    public double getMarginalProb(int node, int attrValue) {
	if (node >=0 && node < dimension // the node number must be valid
	    && attrValue >= 0 && attrValue < arity.values(node)) // the attribute value must be valid
	    return marginalProbTable[node][attrValue];
	else
	    return -1;
    }

    /**
     * Update the marginal probabilities of a specific node according to the prior and conditional probabilities
     * @param node the attribute index of a node
     * @return true if updates successfully, otherwise return false
     */
    public boolean updateMarginalProb(int node) {
	if (node <0 || node >= dimension)
	    return false;

	// System.out.println("update marginal prob. of node_" + node);
	// obtain the parents list of the node
	int[] parentList = getParentList(node);
	if (parentList.length == 0) {
	    // set the marginal probabilities as the prior probabilities
	    for (int i=0; i<arity.values(node); i++)
		marginalProbTable[node][i] = getPriorProb(node, i);
	    return true;
	} else {
	    // calculate the marginal probabitlies for each P(node=eachCurValue)
	    // where the eachCurValue ranges from 0 to the arity.values(node)-1
	    for (int eachCurValue=0; eachCurValue<arity.values(node); eachCurValue++) {
		marginalProbTable[node][eachCurValue] = 0.0; // reset the marginal prob.
		// calculate the cartesian product for the parents nodes
		// namely, calculate all the possible value combinations of the conditions
		CartesianProduct conditionCartProd = new CartesianProduct(arity, parentList);
		for (int[] eachConditions : conditionCartProd) {
		    // calculate the contribution for each P(node=eachCurValue | conditions)
		    // set the contribution as conditional prob. initially
		    double eachContribution = getConProb(node, eachCurValue, eachConditions);
		    // parentList[i] is the i_th parent node's attribute index
		    // eachConditions[i] is the i_th parent node's current value
		    for (int i=0; i<eachConditions.length; i++)
			eachContribution *= getMarginalProb(parentList[i], eachConditions[i]);
		    // System.out.println(eachContribution);
		    marginalProbTable[node][eachCurValue] += eachContribution;
		}
	    }
	    return true;
	}
    }

    /**
     * Update the marginal probabilities of all nodes according to the prior and conditional probabilities
     */
    public void updateAllMarginalProb() {
	topologicalSort(); // topoligical sort
	for (int eachNode : topoSequence)
	    updateMarginalProb(eachNode);
    }

    public boolean isValidAdding(int parent, int child) {
	if (parent < 0 || parent >= dimension || child < 0 || child >= dimension)
	    return false;
	if (bnAdjMatrix[parent][child] == true || bnAdjMatrix[child][parent] == true)
	    return false;
	for (Integer eachAncestor : getAncestors(child)) {
	    System.out.print(eachAncestor + ", ");
	    if (eachAncestor.intValue() == parent)
		return false;
	}
	return true;
    }

    public List<Integer> getAncestors(int node) {
	List<Integer> ancestorsList = new LinkedList<Integer>();
	List<Integer> stack = new LinkedList<Integer>();
	// push the parents of node into the stack first
	for (int eachParent : getParentList(node))
	    stack.add(new Integer(eachParent));
	// get ancestors of the node using a loop
	while (!stack.isEmpty()) {
	    Integer firstNode = stack.remove(0);
	    ancestorsList.add(firstNode);
	    for (int eachParent : getParentList(firstNode.intValue()))
		stack.add(new Integer(eachParent));
	}
	return ancestorsList;
    }

    /**
     * Conduct the topological sort of the bayesnet(DAG)
     * saves the topological sequence to the topoSequence
     * @return a copy of the sorted topological sequence
     */
    public int[] topologicalSort() {
	boolean adjMatrixCopy[][];
	adjMatrixCopy = new boolean[dimension][dimension];
	for (int i=0; i<dimension; i++)
	    for (int j=0; j<dimension; j++)
		adjMatrixCopy[i][j] = bnAdjMatrix[i][j];
	for (int i=0; i<dimension; i++) {
	    boolean isLeader;
	    for (int j=0; j<dimension; j++) {
		boolean sorted = false;
		for (int k=0; k<i; k++) {
		    if (j == topoSequence[k]) {
			sorted = true;
			break;
		    }
		}
		if (sorted)
		    continue;
		isLeader = true;
		for (int k=0; k<dimension; k++) {
		    if (adjMatrixCopy[k][j] == true) {
			isLeader = false;
			break;
		    }
		}
		if (isLeader) {
		    topoSequence[i] = j;
		    for (int k=0; k<dimension; k++)
			adjMatrixCopy[j][k] = false;
		    break;
		}
	    }
	}
	int[] topoSequenceCopy = new int[dimension];
	for (int i=0; i<dimension; i++)
	    topoSequenceCopy[i] = topoSequence[i];
	return topoSequenceCopy;
    }

    /**
     * For the joint probability of (A=T, B=T, C=T, D=T), if the topological
     * sequence is D, B, A, C, then the P(A=T, B=T, C=T, D=T) is:
     * P(D=T | A=T, B=T, C=T) * P(A=T, B=T, C=T). The first conditional prob.
     * can be obtained by the conditionalProbTab of node D. The second joint
     * probability can be obtain by another recursive calling of this funciton.
     * @param query the query like (1, 3, -1, 5) where -1 represents ANY
     * @return the joint probability of the query, return -1 if the query is invalid
     */
    public double getJointProb(int[] query) {
	topologicalSort();
	updateAllMarginalProb();
	return getJointProbRecursive(query);
    }

    /**
     * The recursive function that actually calculates the joint prob.
     * used for avoiding running topological sort and marginal prob. updating in every recursion.
     * @param query the query like (1, 3, -1, 5) where -1 represents ANY
     * @return the joint probability of the query, return -1 if the query is invalid
     */
    private double getJointProbRecursive(int[] query) {
	// sortedQuery is the query with the sequence of topoSequence
	// NOTE: The query is in the attribute index sequence.
	//       The sortedQuery is in the topological sequence.
	int sortedLength = 0;
	for (int eachValue : query)
	    if (eachValue != -1)
		sortedLength++;

	// return the marginal prob. if the query has only one node
	if (sortedLength == 1)
	    for (int i=0; i<dimension; i++)
		if (query[i] != -1)
		    return getMarginalProb(i, query[i]);

	int[] sortedQuery = new int[sortedLength];
	int[] sortedAttrList = new int [sortedLength];
	Set<Integer> queryAttrIndexSet = new HashSet<Integer>();
	for (int i=0; i<query.length; i++)
	    if (query[i] != -1)
		queryAttrIndexSet.add(i);
	int j=0;
	for (int i=dimension-1; i>=0; i--) {
	    if (queryAttrIndexSet.contains(topoSequence[i])) {
		sortedAttrList[j] = topoSequence[i];
		sortedQuery[j] = query[topoSequence[i]];
		j++;
	    }
	}

	for (int eachItem : sortedAttrList) System.out.print(eachItem + ", ");
	System.out.println();
	for (int eachItem : sortedQuery) System.out.print(eachItem + ", ");
	System.out.println();
	
	// generate the query like P(D=T | A=T, B=T, C=T),
	// and eliminate the non-parent nodes in the condition part
	int currentNode = sortedAttrList[0];
	int currentValue = sortedQuery[0];
	int[] curParentList = getParentList(currentNode);
	if (curParentList.length == 0) {
	    // return the prior probability if the current node has no parents
	    double priorProb = getPriorProb(currentNode, currentValue);
	    System.out.println("prior prob.: P(node_" + currentNode + "=" + currentValue + ") = " + priorProb);
	    return priorProb;
	}

	int[] curConList = new int[curParentList.length];
	for (int i=0; i<curConList.length; i++)
	    curConList[i] = query[curParentList[i]];
	double conditionalPart = getConProb(currentNode, currentValue, curConList);
	System.out.println("conditional part: P(node_" + currentNode + "=" + currentValue + ") = " + conditionalPart);

	// generate the query like P(A=T, B=T, C=T),
	// and fill the current value with -1 (ANY)
	int[] otherQueryList = new int[query.length];
	for (int i=0; i<query.length; i++)
	    otherQueryList[i] = (i != currentNode ? query[i] : -1);
	double otherJointPart = getJointProbRecursive(otherQueryList);
	System.out.println("other joint part: " + otherJointPart);
	return (conditionalPart*otherJointPart);
    }

    public boolean noEdges() {
	for (int i=0; i<dimension; i++)
	    for (int j=0; j<dimension; j++)
		if (bnAdjMatrix[i][j])
		    return false;
	return true;
    }

    /**
     * Print all the information of nodes in the bayesnet
     */
    public void printNodesInfo() {
	System.out.println("************************************");
        System.out.println("*     Bayesnet nodes infomation    *");
	// System.out.println("************************************");
	// System.out.println("* nodes:");
	// for (int i=0; i<dimension; i++)
	//     System.out.println("node_" + i + ": [" + arity.names(i) + "]\tarity: " + arity.values(i));
	// System.out.println();

	System.out.println("* edges:");
	for (int i=0; i<dimension; i++)
	    for (int j=0; j<dimension; j++)
		if (bnAdjMatrix[i][j])
		    System.out.println(i + " -> " + j);	

	// System.out.println("\n* probability tables:");
	// for (int i=0; i<dimension; i++) {
	//     System.out.println("* node_" + i + " marginal probability:");
	//     for (int j=0; j<arity.values(i); j++) {
	// 	String str = String.format("\tp(a_%d=%d)=%.4f", i, j, marginalProbTable[i][j]);
	// 	System.out.println(str);
	//     }

	//     if (probTable[i] != null) {
	//     	System.out.println((probTable[i].getTabType() == ProbTabType.PriorTab ? "prior" : "conditional") + " probabilities:");
	// 	System.out.println("prob. number: " + probTable[i].getProbNum());
	//     	probTable[i].printAllProb(); // prints all prob. tables, could be a lot
	//     }
	//     System.out.println();
	// }

	// System.out.println("************************************");
        System.out.println("* End of Bayesnet nodes infomation *");
	System.out.println("************************************");
    }
    
    /**
     * Information of nodes in the bayesnet
     */
    @Override
    public String toString() {
	String str = "\n";
	str += "************************************\n";
	str += "*     Bayesnet nodes infomation    *\n";
	str += "* edges:\n";
	for (int i=0; i<dimension; i++)
	    for (int j=0; j<dimension; j++)
		if (bnAdjMatrix[i][j]) {
		    str += String.valueOf(i);
		    str += " -> ";
		    str += String.valueOf(j);
		    str += "\n";
		}

	str += "Counter: ";
	str += String.valueOf(Debug.getCount());
	str += "\n";
	
	str += "* End of Bayesnet nodes infomation *\n";
	str += "************************************\n";
	return str;
    }

}
