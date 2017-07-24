package teabagml.problems;

import java.util.List;

public abstract class BasicStatusNode implements IStatusNode {
    protected int cost = 0;
    protected int parentNumber = -1;

    /*
     * no implementation of List<IStatusNode> expand()
     * no implementation of int costTo(IStatusNode otherNode)
     */
    
    @Override
    public final String getParentId() {
	return Integer.toString(parentNumber);
    }
		
    @Override
    public final int getCost() {
	return cost;
    }
		
    @Override
    public void setCost(int c) {
	cost = c;
    }
    
    @Override
    public int h(IStatusNode GOAL) {
	return 0;
    }
		
    @Override
    public boolean isSolved(IStatusNode GOAL) {
	return equals(GOAL);
    }

    /**
     * Override Object::toString()
     */
    @Override
    public String toString() {
	return "Undefined BasicStatusNode toString()";
    }
	
    public final void setParentId(String pid) {
	parentNumber = Integer.valueOf(pid);
    }

}
