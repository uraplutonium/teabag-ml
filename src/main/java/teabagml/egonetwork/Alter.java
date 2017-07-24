package teabagml.egonetwork;

public class Alter {
    private int dimension;
    private String id;
    private int[] features;

    public Alter(int d) {
	dimension = d;
	id = "";
	features = new int[dimension];
	for(int i=0; i<dimension; i++)
	    features[i] = -1;
    }

    public void setId(String newId) {
	id = newId;
    }

    public String getId() {
	return id;
    }

    public boolean setFeature(int n, int value) {
	if(n>=0 && n<dimension) {
	    features[n] = value;
	    return true;
	} else {
	    return false;
	}
    }

    public int getFeature(int n) {
	if(n>=0 && n<dimension) {
	    return features[n];
	} else {
	    return -1;
	}
    }

    @Override
    public String toString() {
	String str = id;
	for(int eachFeature : features) {
	    str += " ";
	    str += String.valueOf(eachFeature);
	}
	return str;
    }
}
