package teabagml.algorithms;

import java.util.LinkedList;
import teabagml.problems.StatusNode;

public class LeastCostOPENTable extends LinkedList<StatusNode> implements OPENTable {

    private static final long serialVersionUID = 1L;

    @Override
	public boolean add(StatusNode node) {
	if(isEmpty()) {
	    super.add(node);
	}
	else {
	    LinkedList<Integer> cList = new LinkedList<Integer>();
	    for(StatusNode n : this)
		cList.add(n.getCost());
	    int cCurrent = node.getCost();
	    int i;
	    for(i=0; i<size() && cCurrent > cList.get(i); i++)
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
