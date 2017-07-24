package teabagml.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GraphMazeRoom2 extends BasicStatusNode{
	
	private String name;
	private static Map<String, List<IStatusNode>> connectionMap = null;
	
	public GraphMazeRoom2(String name) {
		this.name = name;
		
		if(connectionMap == null) {
			connectionMap = new HashMap<String, List<IStatusNode>>();
			
			List<IStatusNode> C1 = new ArrayList<IStatusNode>();
			C1.add(new GraphMazeRoom2("R2"));
			C1.add(new GraphMazeRoom2("R5"));
			connectionMap.put("R1", C1);
			
			List<IStatusNode> C2 = new ArrayList<IStatusNode>();
			C2.add(new GraphMazeRoom2("R1"));
			C2.add(new GraphMazeRoom2("R3"));
			connectionMap.put("R2", C2);
			
			List<IStatusNode> C3 = new ArrayList<IStatusNode>();
			C3.add(new GraphMazeRoom2("R7"));
			connectionMap.put("R3", C3);
			
			List<IStatusNode> C4 = new ArrayList<IStatusNode>();
			C4.add(new GraphMazeRoom2("R3"));
			connectionMap.put("R4", C4);
			
			List<IStatusNode> C5 = new ArrayList<IStatusNode>();
			C5.add(new GraphMazeRoom2("R6"));
			connectionMap.put("R5", C5);
			
			List<IStatusNode> C6 = new ArrayList<IStatusNode>();
			C6.add(new GraphMazeRoom2("R2"));
			connectionMap.put("R6", C6);
			
			List<IStatusNode> C7 = new ArrayList<IStatusNode>();
			C7.add(new GraphMazeRoom2("R8"));
			C7.add(new GraphMazeRoom2("R11"));
			connectionMap.put("R7", C7);
			
			List<IStatusNode> C8 = new ArrayList<IStatusNode>();
			C8.add(new GraphMazeRoom2("R4"));
			C8.add(new GraphMazeRoom2("R12"));
			connectionMap.put("R8", C8);
			
			List<IStatusNode> C9 = new ArrayList<IStatusNode>();
			C9.add(new GraphMazeRoom2("R5"));
			C9.add(new GraphMazeRoom2("R13"));
			connectionMap.put("R9", C9);
			
			List<IStatusNode> C10 = new ArrayList<IStatusNode>();
			C10.add(new GraphMazeRoom2("R6"));
			C10.add(new GraphMazeRoom2("R9"));
			C10.add(new GraphMazeRoom2("R11"));
			connectionMap.put("R10", C10);
			
			List<IStatusNode> C11 = new ArrayList<IStatusNode>();
			C11.add(new GraphMazeRoom2("R10"));
			C11.add(new GraphMazeRoom2("R12"));
			connectionMap.put("R11", C11);
			
			List<IStatusNode> C12 = new ArrayList<IStatusNode>();
			C12.add(new GraphMazeRoom2("R8"));
			connectionMap.put("R12", C12);
			
			List<IStatusNode> C13 = new ArrayList<IStatusNode>();
			C13.add(new GraphMazeRoom2("R14"));
			connectionMap.put("R13", C13);
			
			List<IStatusNode> C14 = new ArrayList<IStatusNode>();
			C14.add(new GraphMazeRoom2("R15"));
			connectionMap.put("R14", C14);
			
			List<IStatusNode> C15 = new ArrayList<IStatusNode>();
			C15.add(new GraphMazeRoom2("R11"));
			C15.add(new GraphMazeRoom2("R16"));
			connectionMap.put("R15", C15);
			
			List<IStatusNode> C16 = new ArrayList<IStatusNode>();
			C16.add(new GraphMazeRoom2("R12"));
			connectionMap.put("R16", C16);
		}
	}

	/**
	 * @return the children of this node, whose parentNum is -1 and not initialised.
	 */
	@Override
	public List<IStatusNode> expand() {
		return connectionMap.get(name);
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
		if(obj instanceof GraphMazeRoom2) {
			GraphMazeRoom2 node = (GraphMazeRoom2)obj;
			return name.equals(node.name);
		}
		else
			return false;
	}

	@Override
	public int costTo(IStatusNode otherNode) throws Exception {
		if(connectionMap.get(name).contains(otherNode))
			return 1;
		else
			throw new Exception();
	}

}
