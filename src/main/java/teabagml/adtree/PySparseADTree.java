package teabagml.adtree;

import org.python.util.PythonInterpreter;
import org.python.core.*;

public class PySparseADTree {
    private PyObject adtree = null;
    
    public PySparseADTree(String dataPath) {
	PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile("/media/uraplutonium/Workstation/Workspace/teabag-ml/src/main/java/teabagml/adtree/PySparseADTree.py");
	PyFunction makeSparseADTreeFunc = (PyFunction)interpreter.get("makeSparseADTree", PyFunction.class);
	adtree = makeSparseADTreeFunc.__call__(new PyString(dataPath));
	System.out.println("sparse adtree constructed.");
    }

    public PyObject getPyADTree() {
	return adtree;
    }
}
