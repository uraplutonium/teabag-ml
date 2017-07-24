package teabagml.algorithms;

import java.util.Stack;
import teabagml.problems.StatusNode;

public class DeepSearchOPENTable extends Stack<StatusNode> implements OPENTable {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(StatusNode node) {
		push(node);
		return true;
	}

	@Override
	public StatusNode getNextNode() {
		return pop();
	}

}
