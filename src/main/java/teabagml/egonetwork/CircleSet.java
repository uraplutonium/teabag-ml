package teabagml.egonetwork;

import teabagml.datasets.*;

import java.util.Iterator;
import java.lang.Math;

public class CircleSet implements Iterable<Circle[]> {
    protected final int size;
    int kappa, numAlter, numFeature;
    BCPCombination alterCombinationiter;
    BCPPermutation featPermunationiter;
    Circle[] circleSet;
    
    public class CircleSetiter implements Iterator<Circle[]> {
	
	@Override
	public boolean hasNext() {
	    return (alterCombinationiter.iterator().hasNext() || featPermunationiter.iterator().hasNext());
	}

	/**
	 * Note that the addresses of return value Circle[] are always the same one.
	 * But the value of circleSet is different each time the next() is called.
	 */
	@Override
	public Circle[] next() {
	    if(featPermunationiter.iterator().hasNext()) {
		// alters for each circle do not change
		int[][] newFeatPermunation = featPermunationiter.iterator().next();
		for(int i=0; i<kappa; i++) {
		    // for each circle
		    for(int j=0; j<numFeature; j++) {
			if(newFeatPermunation[i][j] == 1)
			    circleSet[i].addFeature(j);
			else
			    circleSet[i].removeFeature(j);
		    }
		}
	    } else {
		// reset the featPermunationiter and get the next alterCombinationiter
		featPermunationiter.reset();
		int[][] newAlterCombination = alterCombinationiter.iterator().next();
		int[][] newFeatPermunation = featPermunationiter.iterator().next();
		for(int i=0; i<kappa; i++) {
		    for(int j=0; j<numFeature; j++) {
			if(newFeatPermunation[i][j] == 1)
			    circleSet[i].addFeature(j);
			else
			    circleSet[i].removeFeature(j);
		    }
		    for(int j=0; j<numAlter; j++) {
			if(newAlterCombination[i][j] == 1)
			    circleSet[i].addAlter(j);
			else
			    circleSet[i].removeAlter(j);
		    }
		}
	    }
	    return circleSet;
	}
	
	@Override
	public void remove() {
	    System.out.println("Function remove() does not make any sence for making CircleSet, nothing is done here.");
	}
    }

    public CircleSet(int kappa, int numAlter, int numFeature) {
	this.kappa = kappa;
	this.numAlter = numAlter;
	this.numFeature = numFeature;
	
	int a, f;
	a = (int)Math.pow(2, numAlter);
	f = (int)Math.pow(2, numFeature);
	int s = 1;
	for(int i=1; i<=kappa; i++) {
	    s *= (a-i);
	    s *= (f-i);
	    s /= i;
	}
	size = s;
	
	System.out.println("!!!!!!!! THERE ARE " + size + " POSSIBILITIES !!!!!!!!");
	
	circleSet = new Circle[kappa];
	for(int i=0; i<kappa; i++)
	    circleSet[i] = new Circle(numAlter, numFeature);

	featPermunationiter = new BCPPermutation(kappa, numFeature);
	for(int i=0; i<kappa; i++) {
	    for(int j=0; j<numFeature; j++) {
		circleSet[i].removeFeature(j);
	    }
	}
	    
	alterCombinationiter = new BCPCombination(kappa, numAlter);
	System.out.println("alterCombinationiter.size():" + alterCombinationiter.size());
	int[][] firstAlterCombination = alterCombinationiter.iterator().next();
	for(int i=0; i<kappa; i++) {
	    for(int j=0; j<numAlter; j++) {
		System.out.println("i:" + i + " j:" + j);
		if(firstAlterCombination[i][j] == 1)
		    circleSet[i].addAlter(j);
		else
		    circleSet[i].removeAlter(j);
	    }
	}
    }
	
    @Override
    public Iterator<Circle[]> iterator() {
	Iterator<Circle[]> circleSetiter = new CircleSetiter();
	return circleSetiter;
    }

    public int size() {
	return size;
    }
}
