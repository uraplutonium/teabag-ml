package teabagml.algorithms;

import java.util.LinkedList;
import java.util.Queue;
import teabagml.problems.StatusNode;

public class WideSearchOPENTable extends LinkedList<StatusNode> implements OPENTable, Queue<StatusNode> {

	private static final long serialVersionUID = 1L;

	@Override
	public StatusNode getNextNode() {
		return poll();
	}

}
