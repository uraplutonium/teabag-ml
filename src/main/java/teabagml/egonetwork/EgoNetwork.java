package teabagml.egonetwork;

import teabagml.egonetwork.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;

public class EgoNetwork {
    private boolean[][] net;
    private int dimension;
    private boolean directed;
    private HashMap<String, Integer> nameMap;
	
    public EgoNetwork(boolean dir, int d, AlterList alterList) {
	directed = dir;
	dimension = d;
	net = new boolean[dimension][dimension];
	for(int i=0; i<dimension; i++)
	    for(int j=0; j<dimension; j++)
		net[i][j] = false;
	nameMap = new HashMap<String, Integer>();
	int i=0;
	for(Alter eachAlter : alterList) {
	    nameMap.put(eachAlter.getId(), i);
	    i++;
	}
    }

    /**
     * @param alter the index of an alter
     * @return the degree if the graph is undirected
     *        the sum of in-degree and out-degree if the graph is directed
     */
    public int degree(int alter) {
	int d=0;
	if(!directed) {
	    for(int i=0; i<dimension; i++)
		if(i!=alter && isEdgeIn(i, alter))
		    d++;
	}else{
	    for(int i=0; i<dimension; i++)
		if(i!=alter) {
		    if(isEdgeIn(i, alter))
			d++;
		    if(isEdgeIn(alter, i))
			d++;
		}
	}
	return d;
    }

    public boolean isEdgeIn(int srcAlter, int dstAlter) {
	if(!directed && srcAlter > dstAlter) {
	    int buf;
	    buf = srcAlter;
	    srcAlter = dstAlter;
	    dstAlter = buf;
	}
	return net[srcAlter][dstAlter];
    }

    public void setEdge(int srcAlter, int dstAlter, boolean value) {
	if(!directed && srcAlter > dstAlter) {
	    int buf;
	    buf = srcAlter;
	    srcAlter = dstAlter;
	    dstAlter = buf;
	}
	net[srcAlter][dstAlter] = value;
    }
    
    public void setEdge(String srcAlterName, String dstAlterName, boolean value) {
	int srcAlter, dstAlter;
	srcAlter = nameMap.get(srcAlterName);
	dstAlter = nameMap.get(dstAlterName);
	setEdge(srcAlter, dstAlter, value);
    }

    public int getNumEdges() {
	int numEdge=0;
	for(int i=0; i<dimension; i++) {
	    for(int j=i+1; j<dimension; j++) {
		if(net[i][j])
		    numEdge++;
		if(directed && net[j][i])
		    numEdge++;
	    }
	}
	return numEdge;
    }

    public int getNumAlters() {
	return dimension;
    }

    public boolean isDiredted() {
	return directed;
    }
    
    @Override
    public String toString() {
	String str = "";
	for(int i=0; i<dimension; i++) {
	    for(int j=0; j<dimension; j++) {
		str += (net[i][j] ? "1 " : "0 ");
	    }
	    str += "\n";
	}
	return str;
    }

    /**
     *
     */
    public static EgoNetwork getEgoNetwork(String filePath, boolean directed, int d, AlterList alterList) {
	boolean exc = false;
	Scanner fileScan = new Scanner("");
	File file = new File(filePath);
	try {
	    fileScan = new Scanner(file);
	}
	catch(FileNotFoundException e) {
	    exc = true;
	    e.printStackTrace();
	    System.out.println("open " + filePath + " failed!");
	}

	EgoNetwork newEgo = new EgoNetwork(directed, d, alterList);
	if(!exc) {
	    while(fileScan.hasNext()) {
		String newLine = fileScan.nextLine();
		String[] alters = newLine.split(" ");
		newEgo.setEdge(alters[0], alters[1], true);
	    }
	}
	return newEgo;
    }

    
}
