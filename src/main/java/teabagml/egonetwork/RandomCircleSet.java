package teabagml.egonetwork;

import teabagml.datasets.*;

import java.util.Iterator;
import java.util.Random;
import java.lang.Math;
import java.util.Set;
import java.util.HashSet;

public class RandomCircleSet implements Iterable<Circle[]> {
    protected final int size; // the number of Circle[] to return
    private int kappa, mu, numAlter, numFeature, counter; // the value of counter is the Circle[] that already return
    
    public class RandomCircleSetiter implements Iterator<Circle[]> {
	
	@Override
	public boolean hasNext() {
	    return counter<size;
	}

	/**
	 * Note that the addresses of return value Circle[] are always the same one.
	 * But the value of circleSet is different each time the next() is called.
	 */
	@Override
	public Circle[] next() {
	    Random rand = new Random(System.currentTimeMillis());
	    Circle[] randomCircleSet = new Circle[kappa];

	    for(int i=0; i<kappa; i++) {
		randomCircleSet[i] = new Circle(numAlter, numFeature);
		for(int j=0; j<numAlter; j++) {
		    if(rand.nextBoolean())
			randomCircleSet[i].addAlter(j);
		    else
			randomCircleSet[i].removeAlter(j);
		}
		Set<Integer> featSet = new HashSet<Integer>();
		while(featSet.size()<mu) {
		    int newFeat = rand.nextInt(numFeature);
		    if(!featSet.contains(new Integer(newFeat)))
			featSet.add(new Integer(newFeat));
		    randomCircleSet[i].addFeature(newFeat);
		}
	    }
	    counter++;
	    
	    return randomCircleSet;
	}
	
	@Override
	public void remove() {
	    System.out.println("Function remove() does not make any sence for making RandomCircleSet, nothing is done here.");
	}
    }

    public RandomCircleSet(int kappa, int numAlter, int numFeature, int size) {
	this.kappa = kappa;
	this.numAlter = numAlter;
	this.numFeature = numFeature;
	mu = (int)Math.ceil((double)numFeature/(double)kappa);
	this.size = size;
	counter = 0;

	long a, f;
	a = (long)Math.pow(2, numAlter);
	f = (long)Math.pow(2, numFeature);
	long s = 1;
	for(long i=1; i<=kappa; i++) {
	    s *= (a-i);
	    s *= (f-i);
	    s /= i;
	}
	System.out.println("The RandomCircleSet iterator generate " + size + " out of " + s + " possibilities.");
    }
	
    @Override
    public Iterator<Circle[]> iterator() {
	Iterator<Circle[]> randomCircleSetiter = new RandomCircleSetiter();
	return randomCircleSetiter;
    }

    public int size() {
	return size;
    }
}
