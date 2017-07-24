package teabagml.problems;

import java.util.List;

public abstract class StatusNode implements IStatusNode {
    
    // for both BASIC status node and STORM status node
    protected StatusType statusType = StatusType.BASIC;
    protected int cost = 0;

    // for BASIC status node
    protected int parentNumber = -1;
    
    // for STORM status node
    protected String parentId = null;
    protected int pBoltId = -1; // the first part of parentId, indicates which bolt the parent node is stored in.
    protected int pListIndex = -1; // the second part of parentId, indicates the index of the parent node int he resultList

    /*
     * no implementation of List<IStatusNode> expand()
     * no implementation of int costTo(IStatusNode otherNode)
     */

    public StatusNode(StatusType type) {
	statusType = type;
    }
    
    @Override
    public final String getParentId() {
	if(statusType == StatusType.BASIC)
	    return Integer.toString(parentNumber);
	else if(statusType == StatusType.STORM)
	    return parentId;
	else
	    return null;
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
	return "Undefined StatusNode toString()";
    }

    /**
     * set parentId as an interger for BASIC status nodes,
     * and set parentId as a string for STORM status nodes.
     */
    public final void setParentId(String pid) {
	if(statusType == StatusType.BASIC)
	    parentNumber = Integer.valueOf(pid);
	else
	    parentId = pid;
    }

    /**
     * set parentBoltId as a string for STORM status nodes
     */
    public boolean setParentBoltId(int boltId) {
     	if(boltId >= 0) {
     	    pBoltId = boltId;
     	    return true;
     	} else {
    	    return false;
     	}
    }

    /**
     * set parentIndex as an interger for STORM status nodes
     */
    public boolean setParentIndex(int index) {
     	if(index >= 0) {
     	    pListIndex = index;
     	    return true;
     	} else {
    	    return false;
     	}
    }

}
