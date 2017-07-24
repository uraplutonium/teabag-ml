package teabagml.datasets;

import java.util.Iterator;

/**
 * BCPPermutation is an iterable class that returns all the permutation of several BinaryCartesianProduct.
 * The number of BCPs is numBCP. The number of elements in each BCP is numElement.
 * The number of all possible permutation of BCPs is C(numBCP, (2^numElement-1)).
 * In the case of egoNetwork learning, an BinaryCartesianProduct can be an array of integers that
 * represents whether all alters/features are in one particular circle.
 * Then the BCPPermutation represents one particualr possible permutation of that for several circles.
 */
public class BCPPermutation implements Iterable<int[][]> {
    protected final int size;
    protected int counter;
    protected int numBCP;
    protected int numElement;
    protected BinaryCartesianProduct[] BCPiterVector; // BinaryCartesianProduct[numBCP]
    protected int[][] BCPVector; // int[numBCP][numElement], int[i][] is the i_th BinaryCartesianProduct

    public class BCPPermutationiter implements Iterator<int[][]> {
	@Override
	public boolean hasNext() {
	    return (counter < size);
	}

	public boolean hasNextPermutation() {
	    boolean hasNext = false;
	    for(BinaryCartesianProduct eachBinaryCartesianProduct : BCPiterVector)
		hasNext = hasNext || (eachBinaryCartesianProduct.iterator().hasNext());
	    return hasNext;
	}
	
	@Override
	public int[][] next() {
	    boolean valid = true;
	    do {
		if(!hasNextPermutation())
		    return null;
		nextGroup();
		// validate the BCPPermutation
		// BCPVector[i+1][] must be different with BCPVector[i][]
		valid = true;
		for(int i=0; i<numBCP-1; i++) {
		    if(equals(BCPVector[i+1], BCPVector[i])) {
			valid = false;
			break;
		    }
		}
	    } while(!valid);
	    counter++;
	    return BCPVector;
	}
	
	@Override
	public void remove() {
	    System.out.println("Function remove() does not make any sence for making a new BCPVector, nothing is done here.");
	}

	private void nextGroup() {
	    int i=0;
	    while(!(BCPiterVector[i].iterator().hasNext())) {
		// reset the first several BCP and BCPVector if they do not have next
		BCPiterVector[i].reset();
		BCPVector[i][0] = 1;
		for(int j=1; j<numElement; j++)
		    BCPVector[i][j] = 0;
		i++;
	    }
	    // until we find the first CGGenerator that has next
	    int[] nonZeroBCP = BCPiterVector[i].iterator().next();
	    // copy the non-zero BCP to BCPVector[i]
	    for(int j=0; j<numElement; j++)
		BCPVector[i][j] = nonZeroBCP[j];
	}

	private boolean equals(int[] c1, int[] c2) {
	    for(int i=0; i<numElement; i++) {
		if(c1[i] == c2[i])
		    continue;
		else
		    return false;
	    }
	    return true;
	}
    }

    public BCPPermutation(int numBCP, int numElement) {
	counter = 0;
	this.numBCP = numBCP;
	this.numElement = numElement;
	BCPiterVector = new BinaryCartesianProduct[numBCP];
	BCPVector = new int[numBCP][numElement];
	for(int i=0; i<numBCP; i++) {
	    BCPiterVector[i] = new BinaryCartesianProduct(numElement);
	    if(i!=0)
		BCPiterVector[i].iterator().next();
	    BCPVector[i][0] = 1;
	    for(int j=1; j<numElement; j++)
		BCPVector[i][j] = 0;
	}

	// calculate the size
	int numPossiblePermutation = BCPiterVector[0].size();
	int s = 1;
	for(int i=numPossiblePermutation-numBCP+1; i<=numPossiblePermutation; i++)
	    s *= i;
	size = s;
    }
    
    @Override
    public Iterator<int[][]> iterator() {
	Iterator<int[][]> BCPVectoriter = new BCPPermutationiter();
	return BCPVectoriter;
    }

    @Override
    public String toString() {
	String str = "";
	for(int i=0; i<numBCP; i++) {
	    for(int j=0; j<numElement; j++) {
		str += BCPVector[i][j];
		str += " ";
	    }
	    str += "\n";
	}
	return str;
    }

    public int size() {
	return size;
    }

    public void reset() {
	counter = 0;
	for(int i=0; i<numBCP; i++) {
	    BCPiterVector[i].reset();
	    BCPVector[i][0] = 1;
	    for(int j=1; j<numElement; j++)
		BCPVector[i][j] = 0;
	}
    }
}

