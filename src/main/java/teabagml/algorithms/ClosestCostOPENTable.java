package teabagml.algorithms;

import java.util.LinkedList;
import teabagml.problems.StatusNode;

public class ClosestCostOPENTable extends LinkedList<StatusNode> implements OPENTable {

	private static final long serialVersionUID = 1L;
	private static int lastNodeParentNum = -1;

	@Override
	public boolean add(StatusNode node) {
		if(isEmpty()) {
			super.add(node);
		}
		else if(Integer.valueOf(node.getParentId()) != lastNodeParentNum) {
			addFirst(node);
		}
		else {
			LinkedList<Integer> cList = new LinkedList<Integer>();
			for(StatusNode n : this) {
			    if(Integer.valueOf(node.getParentId()) == Integer.valueOf(n.getParentId()))
					cList.add(n.getCost());
				else
					break;
			}
			int cCurrent = node.getCost();
			int i;
			for(i=0; i<cList.size() && cCurrent > cList.get(i); i++)
				;
			add(i, node);
		}
		lastNodeParentNum = Integer.valueOf(node.getParentId());
		return true;
	}

	@Override
	public StatusNode getNextNode() {
		return poll();
	}

}
