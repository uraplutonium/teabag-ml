package teabagml.bnlearn;

import teabagml.datasets.*;
import teabagml.bayesnet.*;
import java.lang.Math;

public class AICScoreFunction implements BNScoreFunction {

    @Override
    public double getScore(Queryable dataset, BayesNet bayesNet) {
	// System.out.println("calculating AIC score for bayesnet \"" + bayesNet.getName() + "\" on the dataset with " + dataset.getDimension() + " attributes...");

	Arity arity = dataset.getArity();
	int dimension = dataset.getDimension();
	double bicScore = 0.0;
	double penalisation = 0.0;

	int mCount1 = 0;
	int mCount2 = 0;
	
	// the first level of summation for each node in the bayesnet
	// which are the same with attribute index within the dimension
	for (int eachNode=0; eachNode<dimension; eachNode++) {
	    // System.out.println("calculating for node " + eachNode);
	    int[] parentList = bayesNet.getParentList(eachNode);
	    
	    // if the current node has no parents, compute the next one
	    if (parentList.length == 0)
		continue;

	    // generate each query, such like (1, -1, 0), for the second level of summation
	    QuerySet m1Queries = new QuerySet(arity, parentList);
	    for (int[] eachm1Query : m1Queries) {
		/***********************************
		 * this is the calculation of mij*
		 **********************************/
		// System.out.print("calculating mj* query: [");
		// for (int eachNum : eachm1Query) System.out.print(eachNum + ", ");
		// System.out.println("]");

		double m1;
		m1 = dataset.count(eachm1Query);
		//m1 = 1;
		// if (++mCount1%1000==0)
		//     System.out.println("mCount1: " + mCount1);


		// System.out.print("mij* [");
		// for (int eachNum : eachm1Query) System.out.print(eachNum + ", ");
		// System.out.println("] = " + m1);

		/***********************************
		 * end of the calculation of mij*
		 **********************************/

		// INCORRECT!
		// int[] parentSelfList = new int[parentList.length+1];
		// int j=0; // indicate the index for parentList
		// for (int i=0; i<parentSelfList.length; i++) {
		//     if (j<parentList.length && parentList[j] < eachNode) {
		// 	parentSelfList[i] = parentList[j];
		// 	j++;
		//     } else {
		// 	parentSelfList[i] = eachNode;
		//     }
		// }
		// QuerySet m2Queries = new QuerySet(arity, parentSelfList);

		// the third level of summation for each value of the current node
		int[] eachm2Query = new int[eachm1Query.length];
		for (int i=0; i<eachm1Query.length; i++)
		    eachm2Query[i] = eachm1Query[i];
		for (int eachK=0; eachK<arity.values(eachNode); eachK++) {
		    eachm2Query[eachNode] = eachK;

		    /***********************************
		     * this is the calculation of mijk
		     **********************************/
		    // System.out.print("calculating mjk query: [");
		    // for (int eachNum : eachm2Query) System.out.print(eachNum + ", ");
		    // System.out.println("]");

		    double m2 = dataset.count(eachm2Query);
		    // double m2 = 1;
		    // if (++mCount2%10000000==0) {
		    // 	System.out.println("mCount2: " + mCount2);
		    // }

		    // System.out.print("mijk [");
		    // for (int eachNum : eachm2Query) System.out.print(eachNum + ", ");
		    // System.out.println("] = " + m1);
		    /***********************************
		     * end of the calculation of mijk
		     **********************************/
		    if (m2 == 0)
			continue;
		    if (m1 == 0)
			continue;
		    
		    // calculate the first part
		    double firstPart = m2 * Math.log((m2/m1));
		    bicScore += firstPart;
		}
	    }
	    // calculate the penalisation
	    // System.out.println("m1Queries.size()" + m1Queries.size());
	    // System.out.println("arity.values(eachNode)" + arity.values(eachNode));
	    penalisation = penalisation + m1Queries.size()*(arity.values(eachNode)-1);
	}
	// finish the calculation of penalisation
	// the penalisation is already calculated
	// AIC = -2l + 2k (k=Mf=penalisation)

	// System.out.println("AIC score calculated.");
	// System.out.println("mCount1(m_ij*): " + mCount1);
	// System.out.println("mCount2(m_ijk): " + mCount2);
	// System.out.println("socre: " + bicScore);
	// System.out.println("penalisation: " + penalisation);
	// System.out.println("AIC: " + (bicScore - penalisation));

	return bicScore;
	// return bicScore-penalisation;
    }

} 
