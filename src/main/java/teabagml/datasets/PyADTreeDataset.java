package teabagml.datasets;

//import teabagml.pack.DataCollector;

import org.python.util.PythonInterpreter;
import org.python.core.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * PyADTreeDataset is a dataset implementing Queryable interface,
 * that utilises the sparse ADTree acceleration.
 * It can be used as a dataset initialised in initial task
 * and passed to searching and scoring tasks as a parameter.
 * The process of constructing a sparse ADTree is only executed in the first call.
 * The contingency tables coressponding to a particular query is built from the
 * sparse adtree in scoring tasks for the first time it is required to query.
 */
public class PyADTreeDataset implements Queryable {
    private static PyObject pyADTree = null;
    private PyTuple adtreeTuple;
    private Arity arity;
    private int dimension;
    private int dataSize;
    private boolean symbolic;
    private boolean labelled;
    private Map<String, PyObject> contabMap;
    
    public PyADTreeDataset(String filePath, boolean symbolic, boolean labelled) {
	// BEWEAR: symbolic and labelled are NOT used
	if (pyADTree == null) {
	    long startTime = System.currentTimeMillis();
	    //DataCollector.startEvent("adtree", startTime);
	    
	    this.symbolic = symbolic;
	    this.labelled = labelled;
	    PythonInterpreter interpreter = new PythonInterpreter();
	    interpreter.execfile("/media/uraplutonium/Workstation/Workspace/teabag-ml/src/main/java/teabagml/adtree/PySparseADTree.py");
	    PyFunction makeSparseADTreeFunc = (PyFunction)interpreter.get("makeSparseADTree", PyFunction.class);
	    adtreeTuple = (PyTuple)(makeSparseADTreeFunc.__call__(new PyString(filePath)));
	
	    // todo extract dimension, dataSize, arity from adtreeTuple
	    PyADTreeDataset.pyADTree = (PyObject)(adtreeTuple.pyget(0));
	    PyInteger pyArityLength = (PyInteger)(adtreeTuple.pyget(1));
	    PyList pyArityList = (PyList)(adtreeTuple.pyget(2));
	    PyInteger pyRecordsLength = (PyInteger)(adtreeTuple.pyget(3));
	    this.dimension = pyArityLength.getValue();
	    this.dataSize = pyRecordsLength.getValue();
	    PyObject[] pyArityArray = pyArityList.getArray();
	    String[] arityNames = new String[dimension];
	    int[] arityArray = new int[dimension];
	    for (int i=0; i<dimension; i++) {
		arityArray[i] = ((PyInteger)(pyArityArray[i])).getValue();
		arityNames[i] = Integer.toString(arityArray[i]);
	    }
	    arity = new Arity(dimension, arityNames, arityArray);
	    System.out.println("sparse adtree constructed.");
	    
	    //DataCollector.endEvent("adtree", startTime);
	} else {
	    System.out.println("sparse adtree already built.");
	}

	// initialise the contabMap
	contabMap = new HashMap<String, PyObject>();
    }

    /**
     * 
     */
    private PyObject getContab(int[] attrList) {
	String attrStr = "";
	for (int eachAttr : attrList) {
	    attrStr = attrStr + Integer.toString(eachAttr) + ",";
	}

	if (contabMap.containsKey(attrStr)) {
	    // contab already built
	    System.out.println("contingency table already exists.");
	    return contabMap.get(attrStr);
	} else {
	    // build new contab and add it to contabMap
	    // convert the int[] attrList to PyList<PyInteger> pyAttrList
	    PyInteger[] pyAttrArray = new PyInteger[attrList.length];
	    for (int i=0; i<attrList.length; i++)
		pyAttrArray[i] = new PyInteger(attrList[i]);
	    PyList pyAttrList = new PyList(pyAttrArray);
	
	    PythonInterpreter interpreter = new PythonInterpreter();
	    interpreter.execfile("/media/uraplutonium/Workstation/Workspace/teabag-ml/src/main/java/teabagml/adtree/PySparseADTree.py");
	    PyFunction makeContabFunc = (PyFunction)interpreter.get("makeContab", PyFunction.class);
	    PyObject contab = makeContabFunc.__call__(PyADTreeDataset.pyADTree, pyAttrList);
	    contabMap.put(attrStr, contab);
	    System.out.println("contingency table constructed.");
	    return contab;
	}
    }

    public int count(int[] query) {
	// convert the int[] query to PyList<PyInteger> pyQuery, as well as the attrList
	// the input java query is like [0, 2, 1, -1], the converted python query is like [1, 3, 2], the attrList is like [0, 1, 2]
	List<PyInteger> pyQueryList = new ArrayList<PyInteger>();
	List<Integer> attrArrayList = new ArrayList<Integer>();
	for (int i=0; i<query.length; i++) {
	    if (query[i] != -1) {
		pyQueryList.add(new PyInteger(query[i]+1));
		attrArrayList.add(new Integer(i));
	    }
	}
	PyList pyQuery = new PyList(pyQueryList);
	int[] attrList = new int[attrArrayList.size()];
	int i=0;
	for (Integer eachAttr : attrArrayList) {
	    attrList[i] = (int)eachAttr;
	    i++;
	}
	PyObject contab = getContab(attrList); // create or find the contab
	
	PythonInterpreter interpreter = new PythonInterpreter();
	interpreter.execfile("/media/uraplutonium/Workstation/Workspace/teabag-ml/src/main/java/teabagml/adtree/PySparseADTree.py");
	PyFunction getCountFunc = (PyFunction)interpreter.get("getCount", PyFunction.class);
	PyInteger pyCount = (PyInteger)(getCountFunc.__call__(contab, pyQuery));
	double count = (double)(pyCount.getValue());

	/*
	System.out.print("Query: ");
	for (int eachValue : query) {
	    System.out.print(eachValue + ' ');
	}
	System.out.println("\t Count: " + count);
	*/
	return (int)count;
    }

    /**
     * @return the arity of the dataset
     */
    public Arity getArity() {
	return arity;
    }

    /**
     * @return the dimension of the dataset
     */
    public int getDimension() {
	return dimension;
    }

    /**
     * @return the size of the dataset
     */
    public int getDataSize() {
	return dataSize;
    }

    /**
     * @return true if the dataset is symbolic, otherwise fasle
     */
    public boolean isSymbolic() {
	return symbolic;
    }

    /**
     * @return true if the dataset is labelled, otherwise fasle
     */
    public boolean isLabelled() {
	return labelled;
    }
}
