package teabagml.datasets;

import java.util.List;
import java.util.ArrayList;

import org.python.util.PythonInterpreter;
import org.python.core.*;

public class PyDataset implements Queryable {
    PyTuple fileTuple;
    private Arity arity;
    private int dimension;
    private int dataSize;
    private boolean symbolic;
    private boolean labelled;
    
    public PyDataset(String filePath, boolean symbolic, boolean labelled) {
	// BEWEAR: symbolic and labelled are NOT used
	this.symbolic = symbolic;
	this.labelled = labelled;
	PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile("/media/uraplutonium/Workstation/Workspace/teabag-ml/src/main/java/teabagml/adtree/PySparseADTree.py");
	PyFunction loadFileFunc = (PyFunction)interpreter.get("loadFile", PyFunction.class);
	fileTuple = (PyTuple)(loadFileFunc.__call__(new PyString(filePath)));
	// todo extract dimension, dataSize, arity from fileTuple
	PyInteger pyArityLength = (PyInteger)(fileTuple.pyget(1));
	PyList pyArityList = (PyList)(fileTuple.pyget(2));
	PyInteger pyRecordsLength = (PyInteger)(fileTuple.pyget(3));
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
	System.out.println("file loaded.");
    }

    public int count(int[] query) {
	// convert the int[] query to PyList<PyInteger> pyQuery
	// the input java query is like [0, 2, 1, -1], the converted python query is like [1, 3, 2, *]
	List<PyObject> pyQueryList = new ArrayList<PyObject>();
	for (int eachValue : query)
	    if (eachValue != -1)
		pyQueryList.add(new PyInteger(eachValue+1));
	    else
		pyQueryList.add(new PyString("*"));

	PyList pyQuery = new PyList(pyQueryList);
	
	PythonInterpreter interpreter = new PythonInterpreter();
	interpreter.execfile("/media/uraplutonium/Workstation/Workspace/teabag-ml/src/main/java/teabagml/adtree/PySparseADTree.py");
	PyFunction getCountFunc = (PyFunction)interpreter.get("countInFile", PyFunction.class);
	PyInteger pyCount = (PyInteger)(getCountFunc.__call__(fileTuple, pyQuery));
	int count = pyCount.getValue();

	/*
	  System.out.print("Query: ");
	  for (int eachValue : query) {
	  System.out.print(eachValue + ' ');
	  }
	  System.out.println("\t Count: " + count);
	*/
	return count;
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
