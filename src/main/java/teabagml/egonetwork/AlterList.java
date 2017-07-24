package teabagml.egonetwork;

import java.io.File;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class AlterList extends ArrayList<Alter> {
    
    private int dimension;
    private int numAlter;
    private String[] featureNames;
    
    /**
     * Import alters from facebook, googleplus and twitter dataset
     * @param filePath the path of the input file
     * @return the list containing all the alters in the file.
     */
    public AlterList(String featPath, String featNamePath) throws Exception {
	boolean exc = false;
	boolean binaryFormat = true;
	Scanner featFileScanner = new Scanner("");
	Scanner featNameFileScanner = new Scanner("");
	File featFile = new File(featPath);
	File featNameFile = new File(featNamePath);
	try {
	    featFileScanner = new Scanner(featFile);
	    featNameFileScanner = new Scanner(featNameFile);
	}
	catch(FileNotFoundException e) {
	    exc = true;
	    e.printStackTrace();
	    System.out.println("open file failed!");
	}

	if(!exc) {
	    dimension = 0;
	    if(binaryFormat) {
		while(featFileScanner.hasNext()) {
		    String newLine = featFileScanner.nextLine();
		    String[] features = newLine.split(" ");
		    if(dimension!=0 && dimension!=features.length-1)
			throw new Exception("dimensions do not match in featFile");
		    else
			dimension = features.length-1;
		    Alter newAlter = new Alter(dimension);
		    newAlter.setId(features[0]);
		    for(int i=0; i<dimension; i++)
			newAlter.setFeature(i, Integer.valueOf(features[i+1]));
		    // System.out.println(newAlter);
		    add(newAlter);
		}
		numAlter = size();
	    
		int i=0;
		featureNames = new String[dimension];
		while(featNameFileScanner.hasNext()) {
		    String newLine = featNameFileScanner.nextLine();
		    String[] names = newLine.split(" ");
		    featureNames[i] = "f" + names[0];
		    // System.out.println(featureNames[i]);
		    i++;
		}
		if(i!=dimension)
		    throw new Exception("number of feature names does not match dimensions");
	    } else {
		// extract the featue segment indexes and names
		List<Integer> featSegIndex = new ArrayList<Integer>();
		List<String> featSegNames = new ArrayList<String>();
		String lastName = null;
		int i=0;
		int allDimension;
		while(featNameFileScanner.hasNext()) {
		    String newLine = featNameFileScanner.nextLine();
		    String[] names = newLine.split(" ");
		    if(lastName == null || !(lastName.equals(names[1]))) { // read a new feature name
			if(lastName != null)
			    i=0;
			lastName = names[1];
			featSegIndex.add(i);
			featSegNames.add(names[1]);
			System.out.println("ADD:" + i + " " + names[1]);
		    }
		    i++;
		}
		allDimension = i;
		
		dimension = featSegIndex.size();
		int[] segIndex = new int[dimension];
		featureNames = new String[dimension];
		for(int j=0; j<dimension; j++) {
		    segIndex[j] = featSegIndex.get(j);
		    featureNames[j] = featSegNames.get(j);
		}
		
		while(featFileScanner.hasNext()) {
		    String newLine = featFileScanner.nextLine();
		    String[] features = newLine.split(" ");
		    Alter newAlter = new Alter(dimension);
		    newAlter.setId(features[0]);
		    int j=0; // j indicates the real index of features[j]
		    for(int x=0; x<dimension; x++) {
			int index = 0;
			//System.out.println("x:" + x);
			//System.out.println("segIndex[x]" + segIndex[x]);
			//System.out.println("j" + j);
			for(int k=0; k<segIndex[x]; k++) { // there should be only one j that makes features[j+1]=="1"
			    //System.out.println("j:" + j);
			    if(Integer.valueOf(features[j+1]) == 1)
				index = j;
			    j++;
			}
			newAlter.setFeature(x, index);
		    }
		    System.out.println(newAlter);
		    add(newAlter);
		}
		numAlter = size();
	    }
	}
	System.out.println("AlterList created.");
	System.out.println("Dimension: " + dimension);
	System.out.println("number of alters: " + numAlter);
    }

    public void makeDifferenceFile(String filePath) {
	File file = new File(filePath);
	Writer fileWriter = null;
	BufferedWriter bufferedWriter = null;
	
	String newLine = "";
	try {
	    fileWriter = new FileWriter(file);
	    bufferedWriter = new BufferedWriter(fileWriter);

	    // write the feature names
	    for(int i=0; i<dimension; i++) {
		newLine += featureNames[i];
		if(i!=dimension-1)
		    newLine += ",";
	    }
	    newLine += "\n";
	    bufferedWriter.write(newLine);

	    // write feature differences
	    for(int i=0; i<numAlter; i++) {
		for(int j=i+1; j<numAlter; j++) { // for each pair of alters
		    newLine = "";
		    for(int k=0; k<dimension; k++) { // for each feature
			int f1 = get(i).getFeature(k);
			int f2 = get(j).getFeature(k);
			// newLine += (f1==f2 ? (f1?"3":"0") : (f1?"2":"1") ); // directed (f1,f2): FF=0, FT=1, TF=2, TT=3
			newLine += (f1==f2 ? "0" : "1");
			if(k!=dimension-1)
			    newLine += ",";
		    }
		    newLine += "\n";
		    bufferedWriter.write(newLine);
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (bufferedWriter != null && fileWriter != null) {
		try {
		    bufferedWriter.close();
		    fileWriter.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }
}
