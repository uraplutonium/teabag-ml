package teabagml.pack;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import teabagml.datasets.*;
import teabagml.bayesnet.*;
import teabagml.bnlearn.*;
import teabagml.problems.StatusNode;
import teabagml.bayesnet.ConditionalProbTable.Mode;
import teabagml.problems.StatusType;
import teabagml.problems.BayesNetStatus;
import teabagml.algorithms.*;
import teabagml.egonetwork.*;
import teabagml.pack.Debug;
import teabagml.pack.DataCollector;
import teabagml.adtree.*;

import org.python.util.PythonInterpreter;
import org.python.core.*;
import org.python.core.PyObject;

public class DMLA {

    public static void testDataset() {
	Dataset dataset = new Dataset("/media/uraplutonium/Workstation/Workspace/Distributed_MachineLearning_Acceleration/res/iris_labelled.csv", false, true);
	System.out.println(dataset.getInfo());

	dataset.printData();

	int[] q = {3, -1, -1, 1};
	int c = dataset.count(q);
	System.out.println(c);
    }

    public static void testBN() {
	String[] arity_name = {"first", "second", "third", "forth"};
	int[] arity_value = {2, 4, 3, 2};
	Arity arity = new Arity(4, arity_name, arity_value);

	BayesNet bn = new BayesNet("test bn", 4, arity);
	
	bn.addEdge(0, 3);
	bn.addEdge(0, 1);
	bn.addEdge(1, 3);
	bn.addEdge(3, 2);

	// set prior prob. for node 3
	bn.setPriorProb(0, 0, 0.6);
	bn.setPriorProb(0, 1, 0.4);

	// set conditional prob. for node 3
	int[] node_0_list = {3, 0, 1};
	boolean ttt = true;
	CartesianProduct cartProd_0 = new CartesianProduct(arity, node_0_list);
	for (int[] eachCon : cartProd_0) {
	    Debug.print(eachCon);
	    bn.setConProb(3, eachCon, (ttt ? 0.3 : 0.7));
	    ttt = (ttt? false : true);
	}
	
	// set conditional prob. for node 1
	int[] node_1_list = {1, 0};
	CartesianProduct cartProd_1 = new CartesianProduct(arity, node_1_list);
	Iterator<int[]> conProbIter_1 = cartProd_1.iterator();
	bn.setConProb(1, conProbIter_1.next(), 0.5);
	bn.setConProb(1, conProbIter_1.next(), 0);
	bn.setConProb(1, conProbIter_1.next(), 0.5);
	bn.setConProb(1, conProbIter_1.next(), 0);
	bn.setConProb(1, conProbIter_1.next(), 0.7);
	bn.setConProb(1, conProbIter_1.next(), 0.1);
	bn.setConProb(1, conProbIter_1.next(), 0.1);
	bn.setConProb(1, conProbIter_1.next(), 0.1);

	// set conditional prob. for node 2
	int[] node_2_list = {2, 3};
	CartesianProduct cartProd_2 = new CartesianProduct(arity, node_2_list);
	Iterator<int[]> conProbIter_2 = cartProd_2.iterator();
	bn.setConProb(2, conProbIter_2.next(), 0.5);
	bn.setConProb(2, conProbIter_2.next(), 0.2);
	bn.setConProb(2, conProbIter_2.next(), 0.3);
	bn.setConProb(2, conProbIter_2.next(), 0.2);
	bn.setConProb(2, conProbIter_2.next(), 0.5);
	bn.setConProb(2, conProbIter_2.next(), 0.3);
	

	// bn.printNodesInfo();

	bn.updateAllMarginalProb();

	bn.printNodesInfo();

	// bn.addEdge(1, 2, Mode.Evenly); /////
	bn.addEdge(1, 2, Mode.Random); /////
	// bn.addEdge(1, 2, Mode.Inherited); /////
	bn.updateAllMarginalProb();

	System.out.println("HC1: " + bn.hashCode());

	bn.printNodesInfo();

	bn.removeEdge(3, 2, Mode.Inherited);
	bn.updateAllMarginalProb();

	BayesNet bn1 = bn.makeCopy();
	System.out.println("HC2: " + bn.hashCode()); // HERE

	bn.printNodesInfo();

	bn.addEdge(3, 2, Mode.Inherited);
	bn.updateAllMarginalProb();

	BayesNet bn2 = bn.makeCopy();
	System.out.println("HC3: " + bn.hashCode());

	bn.printNodesInfo();

	bn.removeEdge(3, 2, Mode.Inherited);
	bn.updateAllMarginalProb();

	BayesNet bn3 = bn.makeCopy();
	System.out.println("HC4: " + bn.hashCode()); // HERE

	System.out.println(bn1.equals(bn2) + ", " + (bn1.hashCode()==bn2.hashCode()));
	System.out.println(bn2.equals(bn3) + ", " + (bn2.hashCode()==bn3.hashCode()));
	System.out.println(bn1.equals(bn3) + ", " + (bn1.hashCode()==bn3.hashCode()));
	System.out.println(bn.equals(bn3) + ", " + (bn.hashCode()==bn3.hashCode()));

	bn.printNodesInfo();

    }

    public static void testBNLearn() {
	Dataset fdDataset = new Dataset("/media/uraplutonium/Workstation/Workspace/Distributed_MachineLearning_Acceleration/res/fd_unlabelled.csv", true, false);
	System.out.println(fdDataset.getInfo());

	fdDataset.printData();

	BayesNet fdbn = new BayesNet("fd bn", fdDataset.getDimension(), fdDataset.getArity());
	
	fdbn.addEdge(3, 0);
	fdbn.addEdge(3, 1);
	fdbn.addEdge(1, 0);
	fdbn.addEdge(0, 2);

	// set prior prob. for node 3
	fdbn.setPriorProb(3, 0, 0.6);
	fdbn.setPriorProb(3, 1, 0.4);

	// set conditional prob. for node 0
	int[] node_0_list = {0, 1, 3};
	boolean ttt = true;
	CartesianProduct cartProd_0 = new CartesianProduct(fdDataset.getArity(), node_0_list);
	for (int[] eachCon : cartProd_0) {
	    fdbn.setConProb(0, eachCon, (ttt ? 0.3 : 0.7));
	    ttt = (ttt? false : true);
	    if (eachCon[0] == 1 && eachCon[1]==3 && eachCon[2]==0)
		ttt = (ttt? false : true);
	}

	// set conditional prob. for node 1
	int[] node_1_list = {1, 3};
	CartesianProduct cartProd_1 = new CartesianProduct(fdDataset.getArity(), node_1_list);
	Iterator<int[]> conProbIter_1 = cartProd_1.iterator();
	fdbn.setConProb(1, conProbIter_1.next(), 0.5);
	fdbn.setConProb(1, conProbIter_1.next(), 0);
	fdbn.setConProb(1, conProbIter_1.next(), 0.5);
	fdbn.setConProb(1, conProbIter_1.next(), 0);
	fdbn.setConProb(1, conProbIter_1.next(), 0.7);
	fdbn.setConProb(1, conProbIter_1.next(), 0.1);
	fdbn.setConProb(1, conProbIter_1.next(), 0.1);
	fdbn.setConProb(1, conProbIter_1.next(), 0.1);

	// set conditional prob. for node 2
	int[] node_2_list = {2, 0};
	CartesianProduct cartProd_2 = new CartesianProduct(fdDataset.getArity(), node_2_list);
	Iterator<int[]> conProbIter_2 = cartProd_2.iterator();
	fdbn.setConProb(2, conProbIter_2.next(), 0.5);
	fdbn.setConProb(2, conProbIter_2.next(), 0.2);
	fdbn.setConProb(2, conProbIter_2.next(), 0.3);
	fdbn.setConProb(2, conProbIter_2.next(), 0.2);
	fdbn.setConProb(2, conProbIter_2.next(), 0.5);
	fdbn.setConProb(2, conProbIter_2.next(), 0.3);

	// fdbn.printNodesInfo();

	fdbn.updateAllMarginalProb();

	fdbn.printNodesInfo();
	
	// BICScoreFunction bicFunc = new BICScoreFunction();
	// double bicScore = bicFunc.getScore(fdDataset, fdbn);
	
	fdDataset.printData();

	Queryable fakeds = new FakeDataset(fdbn, 10000, 4, fdDataset.getArity());
	
	BICScoreFunction bicFunc2 = new BICScoreFunction();
	double bicScore2 = bicFunc2.getScore(fakeds, fdbn);
	System.out.println("BIC 2: " + bicScore2);

    }

    public static void testBNLearnIris() {
	// 1. load the iris dataset
	Dataset irisDataset = new Dataset("/media/uraplutonium/Workstation/Workspace/datasets/iris_labelled.csv", false, false);
	System.out.println(irisDataset.getInfo());

	// 2. generate a bayesnet manually, with corret edges
	BayesNet irisbn = new BayesNet("iris bn 1", irisDataset.getDimension(), irisDataset.getArity());

	// 2.1 add edges
	irisbn.addEdge(0, 4);
	irisbn.addEdge(1, 4);
	irisbn.addEdge(2, 4);
	irisbn.addEdge(3, 4);

	// 2.2 set prior prob. for node 0, 1, 2, 3
	int[] q0_0 = {0, -1, -1, -1, -1};
	irisbn.setPriorProb(0, 0, ((double)irisDataset.count(q0_0))/150);
	int[] q0_1 = {1, -1, -1, -1, -1};
	irisbn.setPriorProb(0, 1, ((double)irisDataset.count(q0_1))/150);
	int[] q0_2 = {2, -1, -1, -1, -1};
	irisbn.setPriorProb(0, 2, ((double)irisDataset.count(q0_2))/150);
	int[] q0_3 = {3, -1, -1, -1, -1};
	irisbn.setPriorProb(0, 3, ((double)irisDataset.count(q0_3))/150);
	int[] q0_4 = {4, -1, -1, -1, -1};
	irisbn.setPriorProb(0, 4, ((double)irisDataset.count(q0_4))/150);
	int[] q0_5 = {5, -1, -1, -1, -1};
	irisbn.setPriorProb(0, 5, ((double)irisDataset.count(q0_5))/150);
	int[] q0_6 = {6, -1, -1, -1, -1};
	irisbn.setPriorProb(0, 6, ((double)irisDataset.count(q0_6))/150);
	int[] q0_7 = {7, -1, -1, -1, -1};
	irisbn.setPriorProb(0, 7, ((double)irisDataset.count(q0_7))/150);
	int[] q0_8 = {8, -1, -1, -1, -1};
	irisbn.setPriorProb(0, 8, ((double)irisDataset.count(q0_8))/150);
	int[] q0_9 = {9, -1, -1, -1, -1};
	irisbn.setPriorProb(0, 9, ((double)irisDataset.count(q0_9))/150);

	int[] q1_0 = {-1, 0, -1, -1, -1};
	irisbn.setPriorProb(1, 0, ((double)irisDataset.count(q1_0))/150);
	int[] q1_1 = {-1, 1, -1, -1, -1};
	irisbn.setPriorProb(1, 1, ((double)irisDataset.count(q1_1))/150);
	int[] q1_2 = {-1, 2, -1, -1, -1};
	irisbn.setPriorProb(1, 2, ((double)irisDataset.count(q1_2))/150);
	int[] q1_3 = {-1, 3, -1, -1, -1};
	irisbn.setPriorProb(1, 3, ((double)irisDataset.count(q1_3))/150);
	int[] q1_4 = {-1, 4, -1, -1, -1};
	irisbn.setPriorProb(1, 4, ((double)irisDataset.count(q1_4))/150);
	int[] q1_5 = {-1, 5, -1, -1, -1};
	irisbn.setPriorProb(1, 5, ((double)irisDataset.count(q1_5))/150);
	int[] q1_6 = {-1, 6, -1, -1, -1};
	irisbn.setPriorProb(1, 6, ((double)irisDataset.count(q1_6))/150);
	int[] q1_7 = {-1, 7, -1, -1, -1};
	irisbn.setPriorProb(1, 7, ((double)irisDataset.count(q1_7))/150);
	int[] q1_8 = {-1, 8, -1, -1, -1};
	irisbn.setPriorProb(1, 8, ((double)irisDataset.count(q1_8))/150);
	int[] q1_9 = {-1, 9, -1, -1, -1};
	irisbn.setPriorProb(1, 9, ((double)irisDataset.count(q1_9))/150);

	int[] q2_0 = {-1, -1, 0, -1, -1};
	irisbn.setPriorProb(2, 0, ((double)irisDataset.count(q2_0))/150);
	int[] q2_1 = {-1, -1, 1, -1, -1};
	irisbn.setPriorProb(2, 1, ((double)irisDataset.count(q2_1))/150);
	int[] q2_2 = {-1, -1, 2, -1, -1};
	irisbn.setPriorProb(2, 2, ((double)irisDataset.count(q2_2))/150);
	int[] q2_3 = {-1, -1, 3, -1, -1};
	irisbn.setPriorProb(2, 3, ((double)irisDataset.count(q2_3))/150);
	int[] q2_4 = {-1, -1, 4, -1, -1};
	irisbn.setPriorProb(2, 4, ((double)irisDataset.count(q2_4))/150);
	int[] q2_5 = {-1, -1, 5, -1, -1};
	irisbn.setPriorProb(2, 5, ((double)irisDataset.count(q2_5))/150);
	int[] q2_6 = {-1, -1, 6, -1, -1};
	irisbn.setPriorProb(2, 6, ((double)irisDataset.count(q2_6))/150);
	int[] q2_7 = {-1, -1, 7, -1, -1};
	irisbn.setPriorProb(2, 7, ((double)irisDataset.count(q2_7))/150);
	int[] q2_8 = {-1, -1, 8, -1, -1};
	irisbn.setPriorProb(2, 8, ((double)irisDataset.count(q2_8))/150);
	int[] q2_9 = {-1, -1, 9, -1, -1};
	irisbn.setPriorProb(2, 9, ((double)irisDataset.count(q2_9))/150);

	int[] q3_0 = {-1, -1, -1, 0, -1};
	irisbn.setPriorProb(3, 0, ((double)irisDataset.count(q3_0))/150);
	int[] q3_1 = {-1, -1, -1, 1, -1};
	irisbn.setPriorProb(3, 1, ((double)irisDataset.count(q3_1))/150);
	int[] q3_2 = {-1, -1, -1, 2, -1};
	irisbn.setPriorProb(3, 2, ((double)irisDataset.count(q3_2))/150);
	int[] q3_3 = {-1, -1, -1, 3, -1};
	irisbn.setPriorProb(3, 3, ((double)irisDataset.count(q3_3))/150);
	int[] q3_4 = {-1, -1, -1, 4, -1};
	irisbn.setPriorProb(3, 4, ((double)irisDataset.count(q3_4))/150);
	int[] q3_5 = {-1, -1, -1, 5, -1};
	irisbn.setPriorProb(3, 5, ((double)irisDataset.count(q3_5))/150);
	int[] q3_6 = {-1, -1, -1, 6, -1};
	irisbn.setPriorProb(3, 6, ((double)irisDataset.count(q3_6))/150);
	int[] q3_7 = {-1, -1, -1, 7, -1};
	irisbn.setPriorProb(3, 7, ((double)irisDataset.count(q3_7))/150);
	int[] q3_8 = {-1, -1, -1, 8, -1};
	irisbn.setPriorProb(3, 8, ((double)irisDataset.count(q3_8))/150);

	int[] q4_0 = {-1, -1, -1, -1, 0};
	irisbn.setPriorProb(4, 0, ((double)irisDataset.count(q4_0))/150);
	int[] q4_1 = {-1, -1, -1, -1, 1};
	irisbn.setPriorProb(4, 1, ((double)irisDataset.count(q4_1))/150);
	int[] q4_2 = {-1, -1, -1, -1, 2};
	irisbn.setPriorProb(4, 2, ((double)irisDataset.count(q4_2))/150);
	int[] q4_3 = {-1, -1, -1, -1, 3};
	irisbn.setPriorProb(4, 3, ((double)irisDataset.count(q4_3))/150);
	int[] q4_4 = {-1, -1, -1, -1, 4};
	irisbn.setPriorProb(4, 4, ((double)irisDataset.count(q4_4))/150);
	int[] q4_5 = {-1, -1, -1, -1, 5};
	irisbn.setPriorProb(4, 5, ((double)irisDataset.count(q4_5))/150);
	int[] q4_6 = {-1, -1, -1, -1, 6};
	irisbn.setPriorProb(4, 6, ((double)irisDataset.count(q4_6))/150);
	int[] q4_7 = {-1, -1, -1, -1, 7};
	irisbn.setPriorProb(4, 7, ((double)irisDataset.count(q4_7))/150);
	int[] q4_8 = {-1, -1, -1, -1, 8};
	irisbn.setPriorProb(4, 8, ((double)irisDataset.count(q4_8))/150);
	int[] q4_9 = {-1, -1, -1, -1, 9};
	irisbn.setPriorProb(4, 9, ((double)irisDataset.count(q4_9))/150);

	// 2.3 set conditional prob. for node 4
	int[] attrList = {0, 1, 2, 3};
	QuerySet qs = new QuerySet(irisDataset.getArity(), attrList);
	for (int[] eachQuery : qs) {
	    //for (int eachq : eachQuery) System.out.print(eachq + ", ");
	    //System.out.println();
	    
	    int[] condition = new int[4];
	    for (int i=0; i<4; i++)
		condition[i] = eachQuery[i];

	    double buf = (double)irisDataset.count(eachQuery);
	    if (buf != 0) {
		eachQuery[4] = 0;
		double buf2 = (double)irisDataset.count(eachQuery);
		irisbn.setConProb(4, 0, condition, buf2/buf);

		eachQuery[4] = 1;
		double buf3 = (double)irisDataset.count(eachQuery);
		irisbn.setConProb(4, 1, condition, buf3/buf);

		eachQuery[4] = 2;
		double buf4 = (double)irisDataset.count(eachQuery);
		irisbn.setConProb(4, 2, condition, buf4/buf);
	    } else {
		irisbn.setConProb(4, 0, condition, 0.33333);
		irisbn.setConProb(4, 1, condition, 0.33333);
		irisbn.setConProb(4, 2, condition, 0.33333);
	    }
	}

	// 3. update the marginal prob.
	irisbn.updateAllMarginalProb();

	//irisbn.printNodesInfo();

	// 4. make adtree
	PySparseADTree adtree = new PySparseADTree("/media/uraplutonium/Workstation/Workspace/datasets/iris_labelled.csv");

	PyDataset pyDataset = new PyDataset("/media/uraplutonium/Workstation/Workspace/datasets/iris_labelled.csv", false, false);
	
	// 5. calculate the BIC score of the current bn with the dataset
	BICScoreFunction bicFunc = new BICScoreFunction();
	// double bicScore = bicFunc.getScore(irisDataset, irisbn, null);
	double bicScore = bicFunc.getScore(pyDataset, irisbn);
	System.out.println("BIC is : " + bicScore);
	
    }

    public static void testAutoLearning() {
	// 1. load the iris dataset

	//Queryable dataset = new Dataset("/media/uraplutonium/Workstation/Workspace/datasets/iris_labelled.csv", false, false);
	//Queryable dataset = new PyADTreeDataset("/media/uraplutonium/Workstation/Workspace/datasets/iris_labelled.csv", false, false);
	//Queryable dataset = new Dataset("/media/uraplutonium/Workstation/Workspace/datasets/asia.csv", true, false);
	//Queryable dataset = new PyADTreeDataset("/media/uraplutonium/Workstation/Workspace/datasets/asia.csv", true, false);
	Queryable dataset = new Dataset("/media/uraplutonium/Workstation/Workspace/datasets/egonet/difference.csv", true, false);

	// 2. generate an empty bayesnet with no edges
	//BayesNet irisbn = new BayesNet("iris bn 1", irisDataset.getDimension(), irisDataset.getArity());
	BayesNet bn = new BayesNet("asia bn", dataset.getDimension(), dataset.getArity());

	// 3. update the marginal prob.
	// irisbn.updateAllMarginalProb();
	// irisbn.printNodesInfo();
	
	bn.updateAllMarginalProb();
	bn.printNodesInfo();

	// 4. calculate the BIC score of the current bn with the dataset
	BICScoreFunction bicFunc = new BICScoreFunction();
	double bicScore = bicFunc.getScore(dataset, bn);

	Debug.resetCounter();

	System.out.println("\n\n################\n################\n################\n################\n\n");

	//TreeSearchEngine ASearchEngine = new TreeSearchEngine(new ASearchOPENTable());
	//ASearchEngine.search(new BayesNetStatus(StatusType.BASIC, bn, dataset), new BayesNetStatus(StatusType.BASIC, bn, dataset));
	//ASearchEngine.displayPath();


	// TreeSearchEngine leastCostEngine = new TreeSearchEngine(new LeastCostOPENTable());
	// leastCostEngine.search(new BayesNetStatus(irisbn, irisDataset), new BayesNetStatus(irisbn, irisDataset));
	// leastCostEngine.search(new BayesNetStatus(asiabn, asiaDataset), new BayesNetStatus(asiabn, asiaDataset));
	// leastCostEngine.displayPath();
    }

    public static double testCategorisation(double alpha) {
	long[] checkpoint = new long[13];
	
	String filePrefix = "/media/uraplutonium/Workstation/Workspace/datasets/egonet/googleplus";
	AlterList alterList = null;
	System.out.println("#1. Creating AlterList..."); checkpoint[0] = System.currentTimeMillis();
	try {
	    alterList = new AlterList(filePrefix+".feat", filePrefix+".featnames");
	} catch(Exception e) {
	    e.printStackTrace();
	}

	int numAlter = alterList.size();
	System.out.println("#2. Creating EgoNetwork..."); checkpoint[1] = System.currentTimeMillis();
	EgoNetwork egoNet = EgoNetwork.getEgoNetwork(filePrefix+".edges", false, numAlter, alterList);

	// FSPG starts
	System.out.println("#3. Making DifferenceFile..."); checkpoint[2] = System.currentTimeMillis();
	// alterList.makeDifferenceFile("/media/uraplutonium/Workstation/Workspace/datasets/egonet/difference.csv");
	
	System.out.println("#4. Loading Dataset..."); checkpoint[3] = System.currentTimeMillis();
	Dataset egoDataset = new Dataset("/media/uraplutonium/Workstation/Workspace/datasets/egonet/difference.csv", true, false);
	System.out.println(egoDataset.getInfo());

	//System.out.println(egoDataset.getSymbolicTable());

	//BayesNet fbn = new BayesNet("F bn", egoDataset.getDimension(), egoDataset.getArity());
	// calculate the hyper parameter kappa, which represents the number of circles
	int kappa = 2;
	int numIter = 3; // 30000
	System.out.println("#5. Creating EgoBayesNet..."); checkpoint[4] = System.currentTimeMillis();
	EgoBayesNet egobn = new EgoBayesNet("ego bn", egoDataset.getDimension(), kappa, egoNet.getNumAlters(), egoDataset.getArity(), alpha);
	
	// calculate the probability of E in a spacific circumstance, which includes a set of circles, belong their associated features

	System.out.println("#6. Constructing structure of EgoBayesNet..."); checkpoint[5] = System.currentTimeMillis();
	
	TreeSearchEngine closestCostEngine = new TreeSearchEngine(new ClosestCostOPENTable());
	closestCostEngine.search(new BayesNetStatus(StatusType.BASIC, egobn.getFeatNet(), egoDataset), new BayesNetStatus(StatusType.BASIC, egobn.getFeatNet(), egoDataset));
	Stack<StatusNode> resultStack = closestCostEngine.displayPath();
	StatusNode resultStatusNode = null;
	while(!resultStack.isEmpty())
	    resultStatusNode = resultStack.pop();
	System.out.println(resultStatusNode);
	egobn.setFeatNet(((BayesNetStatus)resultStatusNode).getBayesNet());
	
	
	double targetEgoNetProb = 0;
	// calculate prob and modularity of the target circle set

	System.out.println("#7. Loading target CircleSet..."); checkpoint[6] = System.currentTimeMillis();
	Circle[] targetCircleSet = Circle.loadFile(filePrefix+".circles", kappa, alterList, egoDataset.getDimension());
	egobn.setNodeC(targetCircleSet);
	System.out.println("#8. Updating Prob. for target CircleSet..."); checkpoint[7] = System.currentTimeMillis();
	egobn.updateAllMarginalProb(egoNet, egoDataset);
	egobn.printNodesInfo();
	System.out.println("#9. Computing EgoNetProb for target CircleSet..."); checkpoint[8] = System.currentTimeMillis();
	targetEgoNetProb = egobn.getEgoNetProb(egoNet);
	System.out.println("target egoNetProb: " + targetEgoNetProb);

	System.out.println("#10. Computing modularity and entorpy for target CircleSet..."); checkpoint[9] = System.currentTimeMillis();
	System.out.println("modularity");
	double totalm = 0;
	for(Circle eachCircle : targetCircleSet) {
	    System.out.println(eachCircle);
	    double m = Circle.circleModularity(egoNet, eachCircle);
	    System.out.println(m);
	    totalm+=m;
	}
	System.out.println("total M:" + totalm);
	
	double entropy = Circle.circleEntropy(egoNet, targetCircleSet, targetCircleSet);
	System.out.println("target Entropy: " + entropy);

	double nmi = Circle.circleNMI(egoNet, targetCircleSet, targetCircleSet);
	System.out.println("target NMI: " + nmi);
	
	System.out.println("#11. Creating RandomCircleSet..."); checkpoint[10] = System.currentTimeMillis();
	// CircleSet circleSets = new CircleSet(kappa, egoNet.getNumAlters(), egoDataset.getDimension());
	RandomCircleSet circleSets = new RandomCircleSet(kappa, egoNet.getNumAlters(), egoDataset.getDimension(), numIter);
	
	int circleSetCounter=0;
	double maxProb, minProb;
	Circle[] maxCircleSet, minCircleSet;
	maxProb = Double.MIN_VALUE;
	minProb = Double.MAX_VALUE;
	maxCircleSet = new Circle[kappa];
	minCircleSet = new Circle[kappa];
	for(int i=0; i<kappa; i++) {
	    maxCircleSet[i] = new Circle(egoNet.getNumAlters(), egoDataset.getDimension());
	    minCircleSet[i] = new Circle(egoNet.getNumAlters(), egoDataset.getDimension());
	}
	

	int smaller = 0;
	int larger = 0;

	double[] probs = new double[numIter];
	double[] mods = new double[numIter];

	double minEntropy = 10000000, maxEntropy = 0, minNMI = 10000000, maxNMI = 0, minPurity = 10000000, maxPurity = 0;
	
	System.out.println("#12. Updating and computing prob. for circleSets..."); checkpoint[11] = System.currentTimeMillis();


	/*************************/
	/*
	Circle[] circleSet = new Circle[kappa];
	for(int i=0; i<115; i++) {
	    for(int j=0; j<kappa; j++) {
		circleSet[j] = new Circle(numAlter, 1);
		circleSet[j].addFeature(0);
	    }
	    circleSet[0].addAlter(i);
	}

	for(int i=0; i<115; i++) {
	    int maxIndex = 0;
	    double maxnmi = 10000000;
	    for(int j=0; j<kappa; j++) {
		circleSet[maxIndex].removeAlter(i);
		circleSet[j].addAlter(i);
		
		egobn.setNodeC(circleSet);
		egobn.updateAllMarginalProb(egoNet, egoDataset);
		double egoNetProb = egobn.getEgoNetProb(egoNet);
		
		if(egoNetProb < maxnmi) { // we find a better cirlce(index) for alter_i (0<=i<numAlter)
		    maxnmi = egoNetProb;
		    maxIndex = j;
		} else { // undo
		    circleSet[j].removeAlter(i);
		    circleSet[maxIndex].addAlter(i);
		}
	    }
	    System.out.print(maxIndex + "\t");
	}
	
	egobn.setNodeC(circleSet);
	egobn.updateAllMarginalProb(egoNet, egoDataset);
	double egoNetProb = egobn.getEgoNetProb(egoNet);
	
	System.out.println("FINAL: " + Circle.circleNMI(egoNet, circleSet, targetCircleSet));
	System.out.println("FINAL: " + Circle.circleEntropy(egoNet, circleSet, targetCircleSet));
	System.out.println("FINAL: " + Circle.circleModularity(egoNet, circleSet));
	System.out.println("FINAL: " + egoNetProb);

	*/
	/*************************/
	    
	
	for(Circle[] eachCircleSet : circleSets) {
	    circleSetCounter++;
	    System.out.print(" circleSet_" + circleSetCounter);
	    egobn.setNodeC(eachCircleSet);
	    egobn.updateAllMarginalProb(egoNet, egoDataset);
	    //egobn.printNodesInfo();
	    double egoNetProb = egobn.getEgoNetProb(egoNet);
	    // System.out.println("egoNetProb(" + circleSetCounter + "): " + egoNetProb);
	    probs[circleSetCounter-1] = egoNetProb;
	    mods[circleSetCounter-1] = Circle.circleModularity(egoNet, eachCircleSet);

	    double newEntropy = Circle.circleEntropy(egoNet, eachCircleSet, targetCircleSet);
	    double newNMI = Circle.circleNMI(egoNet, eachCircleSet, targetCircleSet);
	    double newPurity = Circle.circlePurity(egoNet, eachCircleSet, targetCircleSet);

	    minEntropy = newEntropy<minEntropy ? newEntropy : minEntropy;
	    maxEntropy = newEntropy>maxEntropy ? newEntropy : maxEntropy;

	    minNMI = newNMI<minNMI ? newNMI : minNMI;
	    maxNMI = newNMI>maxNMI ? newNMI : maxNMI;
	    
	    minPurity = newPurity<minPurity ? newPurity : minPurity;
	    maxPurity = newPurity>maxPurity ? newPurity : maxPurity;

	    if(egoNetProb < minProb) {
		minProb = egoNetProb;
		for(int i=0; i<kappa; i++)
		    minCircleSet[i].copyFrom(eachCircleSet[i]);
	    }
		
	    if(egoNetProb > maxProb) {
		maxProb = egoNetProb;
		for(int i=0; i<kappa; i++)
		    maxCircleSet[i].copyFrom(eachCircleSet[i]);
	    }

	    if(targetEgoNetProb >= egoNetProb)
		smaller++;
	    else
		larger++;
	}
    

	System.out.println("\n===================================");
	System.out.println("minEntropy: " + minEntropy);
	System.out.println("maxEntropy: " + maxEntropy);
	System.out.println("minNMI: " + minNMI);
	System.out.println("maxNMI: " + maxNMI);
	System.out.println("minPurity: " + minPurity);
	System.out.println("maxPurity: " + maxPurity);
	System.out.println("===================================");

	System.out.println("MAX_CIRCLE: " + maxProb);
	double m = 0;
	for(int i=0; i<kappa; i++) {
	    m+=Circle.circleModularity(egoNet, maxCircleSet[i]);
	    System.out.println("C" + i + " ==========\n" + maxCircleSet[i] + " mod:" + Circle.circleModularity(egoNet, maxCircleSet[i]));
	}
	System.out.println("mod_MAX: " + m);
	System.out.println("ent_MAX: " + Circle.circleEntropy(egoNet, maxCircleSet, targetCircleSet));
	System.out.println("nmi_MAX: " + Circle.circleNMI(egoNet, maxCircleSet, targetCircleSet));
	
	System.out.println("MIN_CIRCLE: " + minProb);
	m=0;
	for(int i=0; i<kappa; i++) {
	    m+=Circle.circleModularity(egoNet, minCircleSet[i]);
	    System.out.println("C" + i + " ==========\n" + minCircleSet[i] + " mod:" + Circle.circleModularity(egoNet, minCircleSet[i]));
	}
	System.out.println("mod_MIN: " + m);
	System.out.println("ent_MIN: " + Circle.circleEntropy(egoNet, minCircleSet, targetCircleSet));
	System.out.println("nmi_MIN: " + Circle.circleNMI(egoNet, minCircleSet, targetCircleSet));
	
	System.out.println("larger:" + larger);
	System.out.println("smaller:" + smaller);

	checkpoint[12] = System.currentTimeMillis();

	File f=new File("/home/uraplutonium/w.csv");
	FileWriter fw;
	try {
	    fw=new FileWriter(f);
	    String str = "";
	    for(int i=0; i<numIter; i++) {
		str += (String.valueOf(probs[i]) + "," + String.valueOf(mods[i]) + "\n");
	    }
	    fw.write(str);
	    fw.close();
	}catch(Exception e){
	    e.printStackTrace();
	}

	for (int i=0; i<13; i++) {
	    System.out.println(i + "," + checkpoint[i]);
	}


	return (double)larger/(double)(smaller+larger);
    }

    public static double testEgoNetwork(double alpha) {
	String filePrefix = "/media/uraplutonium/Workstation/Workspace/datasets/egonet/facebook";
	AlterList alterList = null;
	System.out.println("#1. Creating AlterList...");
	try {
	    alterList = new AlterList(filePrefix+".feat", filePrefix+".featnames");
	} catch(Exception e) {
	    e.printStackTrace();
	}

	int numAlter = alterList.size();
	System.out.println("#2. Creating EgoNetwork...");
	EgoNetwork egoNet = EgoNetwork.getEgoNetwork(filePrefix+".edges", false, numAlter, alterList);

	System.out.println("#3. Making DifferenceFile...");
	alterList.makeDifferenceFile("/media/uraplutonium/Workstation/Workspace/datasets/egonet/difference.csv");
	
	System.out.println("#4. Loading Dataset...");
	//Queryable egoDataset = new PyDataset("/media/uraplutonium/Workstation/Workspace/datasets/egonet/difference.csv", true, false);
	Dataset egoDataset = new Dataset("/media/uraplutonium/Workstation/Workspace/datasets/egonet/difference.csv", true, false);

	//System.out.println(egoDataset.getSymbolicTable());

	//BayesNet fbn = new BayesNet("F bn", egoDataset.getDimension(), egoDataset.getArity());
	// calculate the hyper parameter kappa, which represents the number of circles
	int kappa = 24;
	int numIter = 1; // 30000
	System.out.println("#5. Creating EgoBayesNet...");
	EgoBayesNet egobn = new EgoBayesNet("ego bn", egoDataset.getDimension(), kappa, egoNet.getNumAlters(), egoDataset.getArity(), alpha);
	
	// calculate the probability of E in a spacific circumstance, which includes a set of circles, belong their associated features

	System.out.println("#6. Constructing structure of EgoBayesNet...");
	/*
	TreeSearchEngine closestCostEngine = new TreeSearchEngine(new ClosestCostOPENTable());
	closestCostEngine.search(new BayesNetStatus(StatusType.BASIC, egobn.getFeatNet(), egoDataset), new BayesNetStatus(StatusType.BASIC, egobn.getFeatNet(), egoDataset));
	Stack<StatusNode> resultStack = closestCostEngine.displayPath();
	StatusNode resultStatusNode = null;
	while(!resultStack.isEmpty())
	    resultStatusNode = resultStack.pop();
	System.out.println(resultStatusNode);
	egobn.setFeatNet(((BayesNetStatus)resultStatusNode).getBayesNet());
	*/
	
	double targetEgoNetProb = 0;
	// calculate prob and modularity of the target circle set

	System.out.println("#7. Loading target CircleSet...");
	Circle[] targetCircleSet = Circle.loadFile(filePrefix+".circles", kappa, alterList, egoDataset.getDimension());
	egobn.setNodeC(targetCircleSet);
	System.out.println("#8. Updating Prob. for target CircleSet...");
	egobn.updateAllMarginalProb(egoNet, egoDataset);
	egobn.printNodesInfo();
	System.out.println("#9. Computing EgoNetProb for target CircleSet...");
	targetEgoNetProb = egobn.getEgoNetProb(egoNet);
	System.out.println("target egoNetProb: " + targetEgoNetProb);

	System.out.println("#10. Computing modularity and entorpy for target CircleSet...");
	System.out.println("modularity");
	double totalm = 0;
	for(Circle eachCircle : targetCircleSet) {
	    System.out.println(eachCircle);
	    double m = Circle.circleModularity(egoNet, eachCircle);
	    System.out.println(m);
	    totalm+=m;
	}
	System.out.println("total M:" + totalm);
	
	double entropy = Circle.circleEntropy(egoNet, targetCircleSet, targetCircleSet);
	System.out.println("target Entropy: " + entropy);

	double nmi = Circle.circleNMI(egoNet, targetCircleSet, targetCircleSet);
	System.out.println("target NMI: " + nmi);
	
	System.out.println("#11. Creating RandomCircleSet...");
	// CircleSet circleSets = new CircleSet(kappa, egoNet.getNumAlters(), egoDataset.getDimension());
	RandomCircleSet circleSets = new RandomCircleSet(kappa, egoNet.getNumAlters(), egoDataset.getDimension(), numIter);
	
	int circleSetCounter=0;
	double maxProb, minProb;
	Circle[] maxCircleSet, minCircleSet;
	maxProb = Double.MIN_VALUE;
	minProb = Double.MAX_VALUE;
	maxCircleSet = new Circle[kappa];
	minCircleSet = new Circle[kappa];
	for(int i=0; i<kappa; i++) {
	    maxCircleSet[i] = new Circle(egoNet.getNumAlters(), egoDataset.getDimension());
	    minCircleSet[i] = new Circle(egoNet.getNumAlters(), egoDataset.getDimension());
	}
	

	int smaller = 0;
	int larger = 0;

	double[] probs = new double[numIter];
	double[] mods = new double[numIter];

	double minEntropy = 10000000, maxEntropy = 0, minNMI = 10000000, maxNMI = 0, minPurity = 10000000, maxPurity = 0;
	
	System.out.println("#12. Updating and computing prob. for circleSets...");


	/*************************/
	/*
	Circle[] circleSet = new Circle[kappa];
	for(int i=0; i<115; i++) {
	    for(int j=0; j<kappa; j++) {
		circleSet[j] = new Circle(numAlter, 1);
		circleSet[j].addFeature(0);
	    }
	    circleSet[0].addAlter(i);
	}

	for(int i=0; i<115; i++) {
	    int maxIndex = 0;
	    double maxnmi = 10000000;
	    for(int j=0; j<kappa; j++) {
		circleSet[maxIndex].removeAlter(i);
		circleSet[j].addAlter(i);
		
		egobn.setNodeC(circleSet);
		egobn.updateAllMarginalProb(egoNet, egoDataset);
		double egoNetProb = egobn.getEgoNetProb(egoNet);
		
		if(egoNetProb < maxnmi) { // we find a better cirlce(index) for alter_i (0<=i<numAlter)
		    maxnmi = egoNetProb;
		    maxIndex = j;
		} else { // undo
		    circleSet[j].removeAlter(i);
		    circleSet[maxIndex].addAlter(i);
		}
	    }
	    System.out.print(maxIndex + "\t");
	}
	
	egobn.setNodeC(circleSet);
	egobn.updateAllMarginalProb(egoNet, egoDataset);
	double egoNetProb = egobn.getEgoNetProb(egoNet);
	
	System.out.println("FINAL: " + Circle.circleNMI(egoNet, circleSet, targetCircleSet));
	System.out.println("FINAL: " + Circle.circleEntropy(egoNet, circleSet, targetCircleSet));
	System.out.println("FINAL: " + Circle.circleModularity(egoNet, circleSet));
	System.out.println("FINAL: " + egoNetProb);

	*/
	/*************************/
	    
	
	for(Circle[] eachCircleSet : circleSets) {
	    circleSetCounter++;
	    System.out.print(" circleSet_" + circleSetCounter);
	    egobn.setNodeC(eachCircleSet);
	    egobn.updateAllMarginalProb(egoNet, egoDataset);
	    //egobn.printNodesInfo();
	    double egoNetProb = egobn.getEgoNetProb(egoNet);
	    // System.out.println("egoNetProb(" + circleSetCounter + "): " + egoNetProb);
	    probs[circleSetCounter-1] = egoNetProb;
	    mods[circleSetCounter-1] = Circle.circleModularity(egoNet, eachCircleSet);

	    double newEntropy = Circle.circleEntropy(egoNet, eachCircleSet, targetCircleSet);
	    double newNMI = Circle.circleNMI(egoNet, eachCircleSet, targetCircleSet);
	    double newPurity = Circle.circlePurity(egoNet, eachCircleSet, targetCircleSet);

	    minEntropy = newEntropy<minEntropy ? newEntropy : minEntropy;
	    maxEntropy = newEntropy>maxEntropy ? newEntropy : maxEntropy;

	    minNMI = newNMI<minNMI ? newNMI : minNMI;
	    maxNMI = newNMI>maxNMI ? newNMI : maxNMI;
	    
	    minPurity = newPurity<minPurity ? newPurity : minPurity;
	    maxPurity = newPurity>maxPurity ? newPurity : maxPurity;

	    if(egoNetProb < minProb) {
		minProb = egoNetProb;
		for(int i=0; i<kappa; i++)
		    minCircleSet[i].copyFrom(eachCircleSet[i]);
	    }
		
	    if(egoNetProb > maxProb) {
		maxProb = egoNetProb;
		for(int i=0; i<kappa; i++)
		    maxCircleSet[i].copyFrom(eachCircleSet[i]);
	    }

	    if(targetEgoNetProb >= egoNetProb)
		smaller++;
	    else
		larger++;
	}
    

	System.out.println("\n===================================");
	System.out.println("minEntropy: " + minEntropy);
	System.out.println("maxEntropy: " + maxEntropy);
	System.out.println("minNMI: " + minNMI);
	System.out.println("maxNMI: " + maxNMI);
	System.out.println("minPurity: " + minPurity);
	System.out.println("maxPurity: " + maxPurity);
	System.out.println("===================================");

	System.out.println("MAX_CIRCLE: " + maxProb);
	double m = 0;
	for(int i=0; i<kappa; i++) {
	    m+=Circle.circleModularity(egoNet, maxCircleSet[i]);
	    System.out.println("C" + i + " ==========\n" + maxCircleSet[i] + " mod:" + Circle.circleModularity(egoNet, maxCircleSet[i]));
	}
	System.out.println("mod_MAX: " + m);
	System.out.println("ent_MAX: " + Circle.circleEntropy(egoNet, maxCircleSet, targetCircleSet));
	System.out.println("nmi_MAX: " + Circle.circleNMI(egoNet, maxCircleSet, targetCircleSet));
	
	System.out.println("MIN_CIRCLE: " + minProb);
	m=0;
	for(int i=0; i<kappa; i++) {
	    m+=Circle.circleModularity(egoNet, minCircleSet[i]);
	    System.out.println("C" + i + " ==========\n" + minCircleSet[i] + " mod:" + Circle.circleModularity(egoNet, minCircleSet[i]));
	}
	System.out.println("mod_MIN: " + m);
	System.out.println("ent_MIN: " + Circle.circleEntropy(egoNet, minCircleSet, targetCircleSet));
	System.out.println("nmi_MIN: " + Circle.circleNMI(egoNet, minCircleSet, targetCircleSet));
	
	System.out.println("larger:" + larger);
	System.out.println("smaller:" + smaller);

	File f=new File("/home/uraplutonium/w.csv");
	FileWriter fw;
	try {
	    fw=new FileWriter(f);
	    String str = "";
	    for(int i=0; i<numIter; i++) {
		str += (String.valueOf(probs[i]) + "," + String.valueOf(mods[i]) + "\n");
	    }
	    fw.write(str);
	    fw.close();
	}catch(Exception e){
	    e.printStackTrace();
	}

	return (double)larger/(double)(smaller+larger);
    }

    public static void testCountTime() {
	int counts = 100;
	String filePrefix = "/media/uraplutonium/Workstation/Workspace/datasets/egonet/facebook";
	AlterList alterList = null;
	System.out.println("#1. Creating AlterList...");
	try {
	    alterList = new AlterList(filePrefix+".feat", filePrefix+".featnames");
	} catch(Exception e) {
	    e.printStackTrace();
	}

	int numAlter = alterList.size();
	System.out.println("#2. Creating EgoNetwork...");
	EgoNetwork egoNet = EgoNetwork.getEgoNetwork(filePrefix+".edges", false, numAlter, alterList);

	System.out.println("#3. Making DifferenceFile...");
	alterList.makeDifferenceFile("/media/uraplutonium/Workstation/Workspace/datasets/egonet/difference.csv");
	
	System.out.println("#4. Loading Dataset...");
	Queryable dataset = new PyDataset("/media/uraplutonium/Workstation/Workspace/datasets/egonet/difference.csv", true, false);

	System.out.println("#5. Making ADTree...");
	long tADTree = System.currentTimeMillis();
	PySparseADTree adtree = new PySparseADTree("/media/uraplutonium/Workstation/Workspace/datasets/egonet/difference.csv");
	tADTree = (System.currentTimeMillis() - tADTree);

	// ramdomly generate a
	System.out.println("checkpoint 1");
	Random rand = new Random();
	boolean[] bAttrList = new boolean[dataset.getDimension()];
	int numAttr = 0;
	for (int i=0; i<dataset.getDimension(); i++) {
	    bAttrList[i] = rand.nextBoolean();
	    if (bAttrList[i])
		numAttr++;
	}
	int[] attrList = new int[numAttr];
	int j=0;
	for (int i=0; i<dataset.getDimension(); i++)
	    if (bAttrList[i]) {
		attrList[j] = i;
		j++;
	    }

	System.out.print("attrList: [");
	for (int eachn : attrList) {
	    System.out.print(eachn + ",");
	}
	System.out.println("]");

	long tContab = System.currentTimeMillis();
	PyContingencyTable contab = new PyContingencyTable(adtree, attrList);
	tContab = System.currentTimeMillis() - tContab;
	
	long tCountD = 0;
	long tCountC = 0;
	for (int[] eachQuery : new RandomQuery(dataset.getArity(), attrList, 10)) {
	    long tBuf;
	    System.out.print("Q: ");
	    for (int e : eachQuery) {
		System.out.print(e + ",");
	    }System.out.println();

	    tBuf = System.currentTimeMillis();
	    int count1 = dataset.count(eachQuery);
	    tCountD += (System.currentTimeMillis() - tBuf);

	    tBuf = System.currentTimeMillis();
	    int count2 = (int)(contab.getCount(eachQuery));
	    tCountC += (System.currentTimeMillis() - tBuf);

	    if (count1 == count2)
		System.out.println("C: " + count1);
	    else
		System.out.println("ERROR!");
	}
	
	System.out.println("count time from dataset: " + tCountD);
	System.out.println("contab building time: " + tContab);
	System.out.println("adtree building time: " + tADTree);
	System.out.println("count time from adtree: " + tCountC);

    }
    
    public static void main(String[] args) {
	// testDataset();

	// System.out.println("==============================");

	// testBN();
	
	// System.out.println("==============================");

	// testBNLearnIris();

	testAutoLearning();

	System.out.println(new DataCollector());
	/*
	double[] r = new double[11];
	int i=0;
	for(double alpha=0; alpha>=-1; alpha-=0.1) {
	    r[i] = testEgoNetwork(alpha); // the closer to 1, the better
	    i++;
	}

	for(double eachR : r)
	    System.out.println(eachR);
	*/
	
	
	//double rr = testEgoNetwork(-0.5);
	//System.out.println(rr);
	
	//testCountTime();
    }
}
