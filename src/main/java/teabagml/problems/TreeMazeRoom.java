package teabagml.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeMazeRoom extends BasicStatusNode{

	private String name;
	
	private static Map<String, List<IStatusNode>> connectionMap = null;
	
	public TreeMazeRoom(String name) {
		this.name = name;
		
		if(connectionMap == null) {
			connectionMap = new HashMap<String, List<IStatusNode>>();
			
			List<IStatusNode> C1 = new ArrayList<IStatusNode>();
			C1.add(new TreeMazeRoom("R2"));
			C1.add(new TreeMazeRoom("R5"));
			connectionMap.put("R1", C1);
			
			List<IStatusNode> C2 = new ArrayList<IStatusNode>();
			C2.add(new TreeMazeRoom("R3"));
			connectionMap.put("R2", C2);
			
			List<IStatusNode> C3 = new ArrayList<IStatusNode>();
			C3.add(new TreeMazeRoom("R4"));
			connectionMap.put("R3", C3);
			
			List<IStatusNode> C4 = new ArrayList<IStatusNode>();
			C4.add(new TreeMazeRoom("R8"));
			connectionMap.put("R4", C4);
			
			List<IStatusNode> C5 = new ArrayList<IStatusNode>();
			C5.add(new TreeMazeRoom("R6"));
			C5.add(new TreeMazeRoom("R9"));
			connectionMap.put("R5", C5);
			
			List<IStatusNode> C6 = new ArrayList<IStatusNode>();
			C6.add(new TreeMazeRoom("R7"));
			connectionMap.put("R6", C6);
			
			List<IStatusNode> C7 = new ArrayList<IStatusNode>();
			C7.add(new TreeMazeRoom("R11"));
			connectionMap.put("R7", C7);
			
			List<IStatusNode> C8 = new ArrayList<IStatusNode>();
			C8.add(new TreeMazeRoom("R12"));
			connectionMap.put("R8", C8);
			
			List<IStatusNode> C9 = new ArrayList<IStatusNode>();
			C9.add(new TreeMazeRoom("R10"));
			C9.add(new TreeMazeRoom("R13"));
			connectionMap.put("R9", C9);
			
			List<IStatusNode> C10 = new ArrayList<IStatusNode>();
			C10.add(new TreeMazeRoom("R14"));
			connectionMap.put("R10", C10);
			
			connectionMap.put("R11", null);
			
			connectionMap.put("R12", null);
			
			connectionMap.put("R13", null);
			
			List<IStatusNode> C14 = new ArrayList<IStatusNode>();
			C14.add(new TreeMazeRoom("R15"));
			connectionMap.put("R14", C14);
			
			List<IStatusNode> C15 = new ArrayList<IStatusNode>();
			C15.add(new TreeMazeRoom("R16"));
			connectionMap.put("R15", C15);
			
			connectionMap.put("R16", null);
		}
	}

	/**
	 * @return the children of this node, whose parentNum is -1 and not initialised.
	 */
	@Override
	public List<IStatusNode> expand() {
		return connectionMap.get(name);
	}
	
	/**
	 * Be aware that this function return the weight of a directed edge.
	 * When calling N.weight(n), it returns the weight of the edge which
	 * goes from N to n.
	 */
	@Override
	public int costTo(IStatusNode otherNode) throws Exception {
		if(connectionMap.get(name).contains(otherNode))
			return 1;
		else
			throw new Exception();
	}
	
	public void displayNode() {
		System.out.print(name + '\t');
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TreeMazeRoom) {
			TreeMazeRoom node = (TreeMazeRoom)obj;
			return name.equals(node.name);
		}
		else
			return false;
	}

}
