package teabagml.adtree;

import java.util.List;
import java.util.ArrayList;
import org.python.util.PythonInterpreter;
import org.python.core.*;

public class PyContingencyTable {
    private PyObject contab;
    
    public PyContingencyTable(PySparseADTree pyADTree, int[] attrList) {
	// convert the int[] attrList to PyList<PyInteger> pyAttrList
	PyInteger[] pyAttrArray = new PyInteger[attrList.length];
	for (int i=0; i<attrList.length; i++)
	    pyAttrArray[i] = new PyInteger(attrList[i]);
	PyList pyAttrList = new PyList(pyAttrArray);
	
	PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile("/media/uraplutonium/Workstation/Workspace/teabag-ml/src/main/java/teabagml/adtree/PySparseADTree.py");
	PyFunction makeContabFunc = (PyFunction)interpreter.get("makeContab", PyFunction.class);
	contab = makeContabFunc.__call__(pyADTree.getPyADTree(), pyAttrList);
	System.out.println("contingency table constructed.");
    }

    public double getCount(int[] query) {
	// convert the int[] query to PyList<PyInteger> pyQuery
	// the input java query is like [0, 2, 1, -1], the converted python query is like [1, 3, 2]
	List<PyInteger> pyQueryList = new ArrayList<PyInteger>();
	for (int eachValue : query)
	    if (eachValue != -1)
		pyQueryList.add(new PyInteger(eachValue+1));
	PyList pyQuery = new PyList(pyQueryList);
	
	PythonInterpreter interpreter = new PythonInterpreter();
	interpreter.execfile("/media/uraplutonium/Workstation/Workspace/teabag-ml/src/main/java/teabagml/adtree/PySparseADTree.py");
	PyFunction makeContabFunc = (PyFunction)interpreter.get("getCount", PyFunction.class);
	PyInteger pyCount = (PyInteger)(makeContabFunc.__call__(contab, pyQuery));
	double count = (double)(pyCount.getValue());

	/*
	System.out.print("Query: ");
	for (int eachValue : query) {
	    System.out.print(eachValue + ' ');
	}
	System.out.println("\t Count: " + count);
	*/
	return count;
    }
}
