package teabagml.algorithms;

import teabagml.problems.StatusNode;

public interface OPENTable extends Iterable<StatusNode> {
	
    public boolean add(StatusNode node);
	
    public StatusNode getNextNode();
	
    public boolean isEmpty();
	
    public void clear();
	
    public boolean contains(Object o);

}
