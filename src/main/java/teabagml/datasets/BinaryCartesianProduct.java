package teabagml.datasets;

import java.util.Iterator;
import java.lang.Math;

/**
 * BinaryCartesianProduct is an iterable class that returns all the possible combination
 * of binary elements as an array of integers. The number of elements in sets is numElement.
 * The number of all possible cartesian product is (2^numElement)-1.
 * In the case of egoNetwork learning, an element can represent whether an alter or a feature is in a circle.
 * A BinaryCartesianProduct of alters/features is a circle/featureSet.
 */
public class BinaryCartesianProduct extends CartesianProduct implements Iterable<int[]> {
    protected int numElement;
    protected int[] elementVector;

    public class BinaryCartesianProductiter extends CartesianProductiter implements Iterator<int[]> {
	@Override
	public boolean hasNext() {
	    boolean end = true;
	    for(int eachElement : elementVector)
		end = end && (eachElement==1);
	    return !end;
	}
	
	@Override
	public int[] next() {
	    if(hasNext()) {
		int i=0;
		while(elementVector[i] == 1) {
		    elementVector[i] = 0;
		    i++;
		}
		elementVector[i] = 1;
		return elementVector;
	    } else
		return null;
	}
	
	@Override
	public void remove() {
	    System.out.println("Function remove() does not make any sence for making a new combination, nothing is done here.");
	}
    }

    public BinaryCartesianProduct(int numElement) {
	this.numElement = numElement;
	String[] names = new String[numElement];
	int[] values = new int[numElement];
	int[] attrList = new int[numElement];
	elementVector = new int[numElement];
	for(int i=0; i<numElement; i++) {
	    names[i] = String.valueOf(i);
	    values[i] = 2;
	    attrList[i] = i;
	    elementVector[i] = 0;
	}
	Arity a = new Arity(numElement, names, values);
	Initialise(a, attrList);
    }
    
    @Override
    public Iterator<int[]> iterator() {
	Iterator<int[]> combinationiter = new BinaryCartesianProductiter();
	return combinationiter;
    }

    @Override
    public int size() {
	return (int)Math.pow(2, numElement)-1;
    }

    /**
     * Reset the combination. Note that the initial combination is not all "0", because all zero-value combination is not allowed.
     */
    public void reset() {
	elementVector[0] = 1;
	for(int i=1; i<numElement; i++)
	    elementVector[i] = 0;
    }
}
