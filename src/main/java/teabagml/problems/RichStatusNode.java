package teabagml.problems;

import java.util.List;

public abstract class RichStatusNode implements IStatusNode {
    protected int cost = 0;
    protected String parentId = null;
    protected int pBoltId = -1; // the first part of parentId, indicates which bolt the parent node is stored in.
    protected int pListIndex = -1; // the second part of parentId, indicates the index of the parent node int he resultList

    /*
     * no implementation of List<IStatusNode> expand()
     * no implementation of int costTo(IStatusNode otherNode)
     */
	
    @Override
    public final String getParentId() {
	return parentId;
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
	return initH();
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
	return "Undefined RichStatusNode toString()";
    }
	
    public final void setParentId(String pid) {
	parentId = pid;
    }
    
    // Why do I used this function?
    // I don't know whether it is called in the stormml package
    public int initH() {
	return 0;
    }

    public boolean setParentBoltId(int boltId) {
     	if(boltId >= 0) {
     	    pBoltId = boltId;
     	    return true;
     	} else {
    	    return false;
     	}
    }

    public boolean setParentIndex(int index) {
     	if(index >= 0) {
     	    pListIndex = index;
     	    return true;
     	} else {
    	    return false;
     	}
    }

}
