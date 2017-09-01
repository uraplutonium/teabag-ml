package teabagml.egonetwork;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

import teabagml.egonetwork.*;

public class Circle {

    private int numAlter;
    private int numFeature;
    private boolean[] alterVector;
    private boolean[] featureVector;

    /**
     * @param na the number of alters in dataset (EgoNetwork.getDimension())
     * @param nf the arity of features (Dataset.getArity())
     */
    public Circle(int na, int nf) {
	numAlter = na;
	numFeature = nf;
	alterVector = new boolean[numAlter];
	featureVector = new boolean[numFeature];
	for(int i=0; i<numAlter; i++)
	    alterVector[i] = false;
	for(int i=0; i<numFeature; i++)
	    featureVector[i] = false;
    }

    /**
     * @param n add the (n+1)_th alter into this circle. 0<=n<numAlter
     * @return 0 if the alter is successfully added,
     *        -1 if the n is invalid.
     */
    public int addAlter(int n) {
	if(n<0 || n>= numAlter)
	    return -1;
	alterVector[n] = true;
	return 0;
    }

    /**
     * @param n remove the (n+1)_th alter into this circle. 0<=n<numAlter
     * @return 0 if the alter is successfully removed,
     *        -1 if the n is invalid.
     */
    public int removeAlter(int n) {
	if(n<0 || n>= numAlter)
	    return -1;
	alterVector[n] = false;
	return 0;
    }

    /**
     * @return the number of alters in this circle.
     */
    public int alterCounts() {
	int c = 0;
	for(int i=0; i<numAlter; i++)
	    c += (alterVector[i] ? 1 : 0);
	return c;
    }
    
    /**
     * @param n the (n+1)_th alter in this circle. 0<=n<numAlter
     * @return 0 if the alter IS NOT in this circle,
     *        1 if the alter IS in this circle,
     *        -1 if the n is invalid.
     */
    public int isAlterIn(int n) {
	if(n<0 || n>= numAlter)
	    return -1;
	return (alterVector[n] ? 1 : 0);
    }

    /**
     * @param n add the (n+1)_th feature into this circle. 0<=n<numFeature
     * @return 0 if the feature is successfully added,
     *        -1 if the n is invalid.
     */
    public int addFeature(int n) {
	if(n<0 || n>= numFeature)
	    return -1;
	featureVector[n] = true;
	return 0;
    }

    /**
     * @param n remove the (n+1)_th feature into this circle. 0<=n<numFeature
     * @return 0 if the feature is successfully removed,
     *        -1 if the n is invalid.
     */
    public int removeFeature(int n) {
	if(n<0 || n>= numFeature)
	    return -1;
	featureVector[n] = false;
	return 0;
    }

    /**
     * @return the number of features in this circle.
     */
    public int featureCounts() {
	int c = 0;
	for(int i=0; i<numFeature; i++)
	    c += (featureVector[i] ? 1 : 0);
	return c;
    }
    
    /**
     * @param n the (n+1)_th feature in this circle. 0<=n<numFeature
     * @return 0 if the feature IS NOT in this circle,
     *        1 if the feature IS in this circle,
     *        -1 if the n is invalid.
     */
    public int isFeatureIn(int n) {
	if(n<0 || n>= numFeature)
	    return -1;
	return (featureVector[n] ? 1 : 0);
    }

    public void copyFrom(Circle src) {
	numAlter = src.numAlter;
	numFeature = src.numFeature;
	for(int i=0; i<numAlter; i++)
	    alterVector[i] = src.alterVector[i];
	for(int i=0; i<numFeature; i++)
	    featureVector[i] = src.featureVector[i];
    }

    @Override
    public String toString() {
	String str = "Alter: ";
	for(int i=0; i<numAlter; i++)
	    if(alterVector[i]) {
		str += String.valueOf(i);
		str += ", ";
	    }
	str += "Feature: ";
	for(int i=0; i<numFeature; i++)
	    if(featureVector[i]) {
		str += String.valueOf(i);
		str += ", ";
	    }
	str += "\n";
	return str;
    }

    public static Circle[] loadFile(String filePath, int kappa, AlterList alterList, int numFeature) {
	int numAlter = alterList.size();
	Map<String, Integer> nameMap = new HashMap<String, Integer>();
	int k=0;
	for(Alter eachAlter : alterList) {
	    nameMap.put(eachAlter.getId(), k);
	    k++;
	}
	Circle[] circleSet = new Circle[kappa];
	boolean exc = false;
	Scanner fileScan = new Scanner("");
	File file = new File(filePath);
	try {
	    fileScan = new Scanner(file);
	}
	catch(FileNotFoundException e) {
	    exc = true;
	    e.printStackTrace();
	    System.out.println("open " + filePath + " failed!");
	}

	if(!exc) {
	    int i=0;
	    while(fileScan.hasNext()) {
		circleSet[i] = new Circle(numAlter, numFeature);
		String newLine = fileScan.nextLine();
		String[] circleInfo = newLine.split("\t");
		for(int j=1; j<circleInfo.length; j++)
		    circleSet[i].addAlter(nameMap.get(circleInfo[j])); // the alter number counts from 1 in file
		
		circleSet[i].addFeature(i%numFeature);
		
		System.out.println(i + " " + circleSet[i]);
		i++;
	    }

	    /*
	    circleSet[0].addFeature(0);
	    circleSet[1].addFeature(0);
	    circleSet[1].addFeature(1);
	    circleSet[2].addFeature(0);
	    circleSet[2].addFeature(1);
	    circleSet[2].addFeature(2);
	    */
	    
	}
	return circleSet;
    }

    /**
     * @param c1 a Circle
     * @param c2 another Circle
     * @return the Jaccard similarity of circle c1 and c2.
     *        Namely, the quotient of the intersection's cardinality and the union's cardinality.
     */
    public static double JaccardSimilarity(Circle c1, Circle c2) {
	int union = 0;
	int intersection = 0;
	for(int i=0; i<c1.numAlter; i++) {
	    if(c1.isAlterIn(i)==1 && c2.isAlterIn(i)==1)
		intersection++;
	    if(c1.isAlterIn(i)==1 || c2.isAlterIn(i)==1)
		union++;
	}
	return (double)intersection/(double)union;
    }

    /**
     * @param costMatrix the matrix storing the similarity of each different circles.
     *       The size of costMatrix is kappa*kappa.
     *       The value of costMatrix[i][j] represents the maximum Jaccard similarity minus that between cirlce trueCircle[i] and predCircle[j]. Namely: costMatrix[i][j] = MAX_JacSim - JacSim(ci, cj).
     * @rerturn an array representing the reflection from trueCircle to predCircle.
     *         The value of return array[i]=j represents that circle trueCircle[i] is associated with predCircle[j].
     */
    public static int[] HungarianAssignment(double[][] costMatrix) {
	return null;
    }

    /**
     * @param kappa
     * @param predCircle
     * @param trueCircle
     * @return 
     */
    public static int[][] confusionMatrix(int kappa, Circle[] predCircleSet, Circle[] trueCircleSet) {


	
	// double test = Circle.JaccardSimilarity(targetCircleSet[0], targetCircleSet[1]);
	return null;
    }

    /**
     * @param egoNet the ego-network
     * @param circle the circle whose modularity to calculate
     * @return the modularity of circle upon egoNet
     */
    public static double circleModularity(EgoNetwork egoNet, Circle circle) {
	int numAlter = egoNet.getNumAlters();
	double modularity=0;
	double twiceNumEdges = (double)(2*egoNet.getNumEdges());
	for(int i=0; i<numAlter; i++) {
	    for(int j=i+1; j<numAlter; j++) {
		// the for conditions have ensure i<j
		if(circle.isAlterIn(i)==1 && circle.isAlterIn(j)==1) {
		    double diffProb = (egoNet.isEdgeIn(i, j) ? 1 : 0);
		    double degreeProduct = (double)(egoNet.degree(i)*egoNet.degree(j))/twiceNumEdges;
		    diffProb-=degreeProduct;
		    // System.out.println("diffProb(v" + i + "-v" + j + ")=" + diffProb);
		    modularity+=diffProb;
		    if(egoNet.isDiredted()) {
			diffProb = (egoNet.isEdgeIn(j, i) ? 1 : 0);
			diffProb-=degreeProduct;
			modularity+=diffProb;
		    }
		}
	    }
	}
	return modularity/twiceNumEdges;
    }

    /**
     * @param egoNet the ego-network
     * @param circleSet the set of circles whose modularity to calculate
     * @return the modularity of circleSet upon egoNet
     */
    public static double circleModularity(EgoNetwork egoNet, Circle[] circleSet) {
	double modularity = 0;
	for(Circle eachCircle : circleSet) {
	    modularity += circleModularity(egoNet, eachCircle);
	}
	return modularity;
    }
    
    /**
     * @param egoNet the ego-network
     * @param circleSet the set of circles whose entropy to calculate
     * @param targetCircleSet the set of real circles
     * @return the entropy of circle upon egoNet
     */
    public static double circleEntropy(EgoNetwork egoNet, Circle[] circleSet, Circle[] targetCircleSet) {
	double sum = 0;
	for(Circle eachCircle : circleSet) {
	    double eachAlterCounts = (double)eachCircle.alterCounts();
	    //System.out.println("eachC: " + eachAlterCounts); ////
	    for(Circle eachTargetCircle : targetCircleSet) {
		double intersection = 0;
		for(int i=0; i<eachCircle.numAlter; i++) {
		    if(eachCircle.isAlterIn(i)==1 && eachTargetCircle.isAlterIn(i)==1)
			intersection++;
		}
		//System.out.println("intersection: " + intersection); ////
		if(intersection != 0)
		    sum += (intersection * Math.log(intersection/eachAlterCounts));
	    }
	}
	return (sum *(-1) /egoNet.getNumAlters() / Math.log(circleSet.length));
    }

    /**
     * @param egoNet the ego-network
     * @param circleSet the set of circles whose NMI to calculate
     * @param targetCircleSet the set of real circles
     * @return the NMI score of circle upon egoNet
     */
    public static double circleNMI(EgoNetwork egoNet, Circle[] circleSet, Circle[] targetCircleSet) {
	double numerator = 0, denominator = 0;
	for(int i=0; i<circleSet.length; i++) { // detected c_i
	    double eachAlterCounts = (double)circleSet[i].alterCounts();
	    for(int j=0; j<targetCircleSet.length; j++) { // real c_j
		double intersection = 0;
		for(int k=0; k<egoNet.getNumAlters(); k++) {
		    if(circleSet[i].isAlterIn(k)==1 && targetCircleSet[j].isAlterIn(k)==1)
			intersection++;
		}
		if(intersection != 0) {
		    double newNum = (intersection * Math.log((double)egoNet.getNumAlters() * intersection / eachAlterCounts / (double)targetCircleSet[j].alterCounts()));
		    numerator += newNum;
		}
	    }
	}
	numerator *= (-2);

	for(Circle eachCircle : circleSet) {
	    if(eachCircle.alterCounts() != 0)
		denominator += (eachCircle.alterCounts() * Math.log((double)eachCircle.alterCounts() / egoNet.getNumAlters()));
	}

	for(Circle eachTargetCircle : targetCircleSet) {
	    if(eachTargetCircle.alterCounts() != 0)
	    denominator += (eachTargetCircle.alterCounts() * Math.log((double)eachTargetCircle.alterCounts() / egoNet.getNumAlters()));
	}
	return (numerator/denominator);
    }

        /**
     * @param egoNet the ego-network
     * @param circleSet the set of circles whose entropy to calculate
     * @param targetCircleSet the set of real circles
     * @return the purity of circle upon egoNet
     */
    public static double circlePurity(EgoNetwork egoNet, Circle[] circleSet, Circle[] targetCircleSet) {
	double sum = 0;
	for(Circle eachCircle : circleSet) {
	    double eachAlterCounts = (double)eachCircle.alterCounts();
	    double maxIntersection = 0;
	    //System.out.println("eachC: " + eachAlterCounts); ////
	    for(Circle eachTargetCircle : targetCircleSet) {
		double intersection = 0;
		for(int i=0; i<eachCircle.numAlter; i++) {
		    if(eachCircle.isAlterIn(i)==1 && eachTargetCircle.isAlterIn(i)==1)
			intersection++;
		}
		//System.out.println("intersection: " + intersection); ////
		if (maxIntersection < intersection)
		    maxIntersection = intersection;
	    }
	    sum += maxIntersection;
	}
	return (sum /egoNet.getNumAlters());
    }
}
