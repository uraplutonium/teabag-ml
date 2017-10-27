package teabagml.datasets;
import java.util.Random;

import java.util.Iterator;

public class RandomQuery implements Iterable<int[]> {
    protected Arity arity;
    protected int[] attributeList;
    protected int MAX_COUNT;

    public class RandomQueryiter implements Iterator<int[]> {
	protected int count = 0;
	Random rand = new Random();
	
	@Override
	public boolean hasNext() {
	    return (count < MAX_COUNT);
	}
	
	@Override
	public int[] next() {
	    int[] newQuery = new int[arity.getDimension()];
	    int i = 0;
	    for (int j=0; j<arity.getDimension(); j++) {
		if (i<attributeList.length && attributeList[i] == j) {
		    newQuery[j] = rand.nextInt(arity.values(attributeList[i]));
		    i++;
		} else {
		    newQuery[j] = -1;
		}
	    }
	    count++;
	    return newQuery;
	}
	
	@Override
	public void remove() {
	    System.out.println("Function remove() does not make any sence for making a new query, nothing is done here.");
	}
    }
    
    public RandomQuery(Arity a, int[] attrList, int c) {
	arity = a;
	attributeList = attrList;
	MAX_COUNT = c;
    }
	
    @Override
    public Iterator<int[]> iterator() {
	Iterator<int[]> randomQueryiter = new RandomQueryiter();
	return randomQueryiter;
    }
}
