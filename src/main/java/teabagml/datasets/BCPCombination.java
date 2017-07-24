package teabagml.datasets;

import java.util.Iterator;

/**
 * BCPCombination is an iterable class that returns all the combination of several BinaryCartesianProduct.
 * The number of BCPs is numBCP. The number of elements in each BCP is numElement.
 * The number of all possible combination of BCPs is C(numBCP, (2^numElement-1)).
 * In the case of egoNetwork learning, an BinaryCartesianProduct can be an array of integers that
 * represents whether all alters/features are in one particular circle.
 * Then the BCPCombination represents one particualr possible combination of that for several circles.
 */
public class BCPCombination implements Iterable<int[][]> {
    protected final int size;
    protected int counter;
    protected int numBCP;
    protected int numElement;
    protected BinaryCartesianProduct[] BCPiterVector; // BinaryCartesianProduct[numBCP]
    protected int[][] BCPVector; // int[numBCP][numElement], int[i][] is the i_th BinaryCartesianProduct

    public class BCPCombinationiter implements Iterator<int[][]> {
	@Override
	public boolean hasNext() {
	    return (counter < size);
	}

	public boolean hasNextCombination() {
	    boolean hasNext = false;
	    for(BinaryCartesianProduct eachBinaryCartesianProduct : BCPiterVector)
		hasNext = hasNext || (eachBinaryCartesianProduct.iterator().hasNext());
	    return hasNext;
	}
	
	@Override
	public int[][] next() {
	    boolean valid = true;
	    int c = 0;
	    do {
		if(!hasNextCombination())
		    return null;
		nextGroup();
		// validate the BCPCombination
		// BCPVector[i+1][] must be greater than BCPVector[i][]
		valid = true;

		for(int i=0; i<numBCP-1; i++) {
		    if(!greaterThan(BCPVector[i+1], BCPVector[i])) {
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

	private boolean greaterThan(int[] c1, int[] c2) {
	    for(int i=0; i<numElement; i++) {
		if(c1[i] == c2[i])
		    continue;
		else if(c1[i] > c2[i])
		    return true; // c1>c2
		else
		    return false; // c1<c2
	    }
	    return false; // c1==c2
	}
    }

    public BCPCombination(int numBCP, int numElement) {
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
	int numPossibleCombination = BCPiterVector[0].size();
	int numerator = 1;
	int denominator = 1;
	for(int i=numPossibleCombination-numBCP+1; i<=numPossibleCombination; i++)
	    numerator *= i;
	for(int i=1; i<=numBCP; i++)
	    denominator *= i;
	size = numerator/denominator;
    }
    
    @Override
    public Iterator<int[][]> iterator() {
	Iterator<int[][]> BCPVectoriter = new BCPCombinationiter();
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

