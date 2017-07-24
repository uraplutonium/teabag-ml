package teabagml.datasets;

public class Arity {

    private int dimension;
    private String[] arityNames;
    private int[] arityValues;

    public Arity(int d) {
	dimension = d;
	arityNames = new String[dimension];
	arityValues = new int[dimension];
	for (int i=0; i<dimension; i++) {
	    arityNames[i] = "";
	    arityValues[i] = 0;
	}
    }

    public Arity(int d, String[] names, int[] values) {
	dimension = d;
	arityNames = names;
	arityValues = values;
    }

    int getDimension() {
	return dimension;
    }

    boolean setName(int i, String name) {
	if (i<dimension && i>=0) {
	    arityNames[i] = name;
	    return true;
	} else {
	    return false;
	}
    }

    boolean incValue(int i) {
	if (i<dimension && i>=0) {
	    arityValues[i] += 1;
	    return true;
	} else {
	    return false;
	}
    }

    public String names(int i) {
	return (i<dimension && i>=0) ? arityNames[i] : null;
    }

    public int values(int i) {
	return (i<dimension && i>=0) ? arityValues[i] : 0;
    }

    @Override
    public String toString() {
	String str="";
	for (int i=0; i<dimension ; i++)
	    str = str + "[" + arityNames[i] + "]\t" + arityValues[i] + "\n";
	return str;
    }
}
