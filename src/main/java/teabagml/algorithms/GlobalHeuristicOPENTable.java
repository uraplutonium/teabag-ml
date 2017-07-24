package teabagml.algorithms;

import java.util.LinkedList;
import teabagml.problems.StatusNode;

public class GlobalHeuristicOPENTable extends LinkedList<StatusNode> implements OPENTable {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(StatusNode node) {
		if(isEmpty()) {
			super.add(node);
		}
		else {
			LinkedList<Integer> hList = new LinkedList<Integer>();
			for(StatusNode n : this)
				hList.add(n.h(TreeSearchEngine.GOAL));
			int hCurrent = node.h(TreeSearchEngine.GOAL);
			int i;
			for(i=0; i<size() && hCurrent > hList.get(i); i++)
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
