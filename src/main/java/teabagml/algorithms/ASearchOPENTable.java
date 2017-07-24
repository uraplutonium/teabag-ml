package teabagml.algorithms;

import java.util.LinkedList;
import teabagml.problems.StatusNode;

public class ASearchOPENTable extends LinkedList<StatusNode> implements OPENTable {

    private static final long serialVersionUID = 1L;

    @Override
	public boolean add(StatusNode node) {
	if(isEmpty()) {
	    super.add(node);
	}
	else {
	    LinkedList<Integer> fList = new LinkedList<Integer>();
	    for(StatusNode n : this)
		fList.add(n.h(TreeSearchEngine.GOAL) + n.getCost());
	    int fCurrent = node.h(TreeSearchEngine.GOAL) + node.getCost();
	    int i;
	    for(i=0; i<size() && fCurrent > fList.get(i); i++)
		;
	    add(i, node);
	}
	return true;
    }

    @Override
	public StatusNode getNextNode() {
	return poll();
    }

}
