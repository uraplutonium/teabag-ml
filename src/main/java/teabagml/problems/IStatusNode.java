package teabagml.problems;

import java.util.List;

public interface IStatusNode {
    /**
     * Calculates all the possible adjacent nodes of the current status node.
     * @return a list of BasicStatusNode with all possible adjacent status nodes,
     *         return an empty list if the current node is a leaf status node (note: NOT null!).
     */
    public List<IStatusNode> expand();
    
    public int costTo(IStatusNode otherNode) throws Exception;

    public String getParentId();
	
    public int getCost();
	
    public void setCost(int c);
	
    public int h(IStatusNode GOAL);
		
    public boolean isSolved(IStatusNode GOAL);
}
