package teabagml.datasets;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileDataset implements Queryable {

    public class SymbolicTable {
	private boolean isSymbolic; // indicate whether the dataset file is symbolic
	private List<String>[] symTable; // an array of List

	private SymbolicTable(boolean isSym) {
	    isSymbolic = isSym;
	    symTable = new List[tabWidth];
	    for (int i=0; i<tabWidth; i++)
		symTable[i] = new ArrayList<String>();
	}

	/**
	 * Add a string s to the i^th dimension if not exists.
	 * The index of a string in the list represents its symbolic value.
	 * @param i the attribute index/number
	 * @param s the unsymbolic string
	 * @return the new assigned value of the string, return the value already assigned if the string already exists, the return value equals to the value of s when the dataset file is symbolic.
	 */
	private int addString(int i, String s) {
	    if (i<tabWidth && i>=0) {
		if (isSymbolic) {
		    // init each symbolic list and arity as the max number of the given s
		    int attr = Integer.valueOf(s).intValue(); // attr count from 0
		    if (attr+1 > symTable[i].size()) {
			// add new symbolic strings
			for (int j=symTable[i].size(); j<=attr; j++) {
			    symTable[i].add(String.valueOf(j));
			    arity.incValue(i);
			    if (labelled && i==tabWidth-1)
				labelNum += 1;
			}
		    }
		    return attr;
		} else {
		    // dynamicly generate the symbolic lists and the arity
		    if (symTable[i].contains(s)) {
			return symTable[i].indexOf(s);
		    } else {
			symTable[i].add(s);
			arity.incValue(i); // increases the arity value by 1
			if (labelled && i==tabWidth-1)
			    labelNum += 1;
			return symTable[i].size()-1;
		    }
		}
	    } else {
		return -1;
	    }
	}

	/**
	 * @param i the attribute index/number
	 * @param s the unsymbolic string
	 * @return the symbolic value of a string in the i^th dimension, return -1 if the string is not found.
	 */
	public int getValue(int i, String s) {
	    if (i<tabWidth && i>=0)
		return symTable[i].indexOf(s);
	    else
		return -1;
	}

	/**
	 * @param i the attribute index/number
	 * @param value the symbolic number
	 * @return the corresponding string to the symbolic value, return null if the value is invalid.
	 */
	public String getString(int i, int value) {
	    if (i<tabWidth && i>=0)
		return symTable[i].get(value);
	    else
		return null;
	}

	@Override
	public String toString() {
	    String str="";
	    for (int i=0; i<tabWidth ; i++) {
		if (labelled && i == tabWidth-1)
		    str = str + "categories: ";
		else
		    str = str + "a_" + i + ": ";

		for (int j=0; j<symTable[i].size(); j++) {
		    str = str + "(" + j + ")[" + symTable[i].get(j) + "] ";
		}
		str += "\n";
	    }
	    return str;
	}
    }

    private int dimension;
    // tabWidth equals dimension if not labelled, otherwise equals dimension+1
    private int tabWidth;
    private int dataSize;
    private boolean symbolic;
    private boolean labelled;
    private String labelName;  //  available only when labelled
    private int labelNum;  // available only when labelled
    private Arity arity;
    private SymbolicTable sTable;
    private String filePath;

    public FileDataset(String filePath, boolean symbolic, boolean labelled) {
	this.symbolic = symbolic;
	this.labelled = labelled;
	this.filePath = filePath;
	
	File file = new File(filePath);
	Scanner fileScanner = new Scanner("");
	boolean exception = false;

	try {
	    fileScanner = new Scanner(file);
	} catch (FileNotFoundException e) {
	    exception = true;
	    e.printStackTrace();
	    System.out.println("open csv file failed!");
	}

	if (!exception) {
	    String strBuf;
	    String[] splitBuf;

	    // read the first line to initialise the dimension and arity names
	    strBuf = fileScanner.nextLine();
	    splitBuf = strBuf.split(",");

	    tabWidth = splitBuf.length;
	    dimension = labelled ? splitBuf.length-1 : splitBuf.length;

	    arity = new Arity(dimension);
	    sTable = new SymbolicTable(symbolic);
	    for (int i=0; i<dimension; i++)
		arity.setName(i, splitBuf[i]);
	    labelName = labelled ? splitBuf[dimension] : "no_label";
	    labelNum = 0;

	    // read the rest of the file to initialise the data, arity values and symbolic table, but does not save the data in to memory
	    dataSize = 0;
	    int rowLength = (labelled ? dimension+1 : dimension);
	    while (fileScanner.hasNext()) {
		strBuf = fileScanner.nextLine();
		splitBuf = strBuf.split(",");
		int[] newRow = new int[rowLength];
		for (int i=0; i<rowLength; i++) {
		    // the current operating item is splitBuf[i] (String) for a_i
		    // the symbolic table either add the unsymbolic string,
		    // or add the symbolic string, according to "symbolic".
		    int sValue = sTable.addString(i, splitBuf[i]);
		    newRow[i] = sValue;
		}
		dataSize += 1;
	    }
	}
    }

    @Override
    public Arity getArity() {
	return arity;
    }

    @Override
    public int getDimension() {
	return dimension;
    }

    @Override
    public int getDataSize() {
	return dataSize;
    }

    @Override
    public boolean isSymbolic() {
	return symbolic;
    }

    @Override
    public boolean isLabelled() {
	return labelled;
    }

    public String getLabelName() {
	return labelName;
    }

    public int getLabelNum() {
	return labelNum;
    }

    public SymbolicTable getSymbolicTable() {
	return sTable;
    }

    public boolean isQueryValid(int[] query) {
	if (query.length != dimension)
	    return false;

	boolean qValid = true;
	for (int i=0; i<dimension; i++)
	    if (query[i] < -1 || query[i] >= arity.values(i)) {
		qValid = false;
		break;
	    }
	return qValid;
    }

    @Override
    public int count(int[] query) {
	if (!isQueryValid(query))
	    return -1;
	int counter = 0;
	File file = new File(filePath);
	Scanner fileScanner = new Scanner("");
	boolean exception = false;

	try {
	    fileScanner = new Scanner(file);
	} catch (FileNotFoundException e) {
	    exception = true;
	    e.printStackTrace();
	    System.out.println("open csv file failed!");
	}

	if (!exception) {
	    String strBuf;
	    String[] splitBuf;

	    // skip the first line
	    strBuf = fileScanner.nextLine();
	   
	    // read the rest of the file to obtain the count of the query
	    while (fileScanner.hasNext()) {
		boolean queryMatch = true;
		strBuf = fileScanner.nextLine();
		splitBuf = strBuf.split(",");
		for (int i=0; i<dimension; i++) {
		    // we do not use the i<rowlength because we compare only the
		    // first "dimension" values in a row.
		    // the current operating item is splitBuf[i] (String) for a_i
		    // the symbolic table either add the unsymbolic string,
		    // or add the symbolic string, according to "symbolic".
		    int sValue = sTable.addString(i, splitBuf[i]);
		    if(query[i] != -1 && query[i] != sValue) {
			queryMatch = false;
			break;
		    }
		}
		if (queryMatch)
		    counter += 1;
	    }
	    
	}
	return counter;
    }

    public String getInfo() {
	String info = "****************************\n";
        info += "*    FileDataset infomation    *\n";
	info += "****************************\n";

	info = info + "dataset size: " + dataSize + "\n";
	info = info + "dimensions: " + dimension + "\n";
	info = info + "symbolic data: " + (symbolic ? "true\n" : "false\n");
	info = info + "labelled data: " + (labelled ? "true\n" : "false\n");
	if (labelled) {
	    info = info + "label name: " + labelName + "\n";
	    info = info + "categories: " + labelNum + "\n";
	}
	info = info + "\narity list:\nname\tarity\n" + arity;
	if (!symbolic) {
	    info = info + "\nsymbolic table:\n" + sTable;
	}
	return info;
    }

}
