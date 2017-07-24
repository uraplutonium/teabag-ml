package teabagml.problems;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;
import java.io.FileWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

public class TraveseGraph extends BasicStatusNode{

    private static int dimension;
    private static int[][] adjMatrix = null;
    private static boolean[] V = null;
    public static int startNode;
    public static int endNode;
    public static Map<String,String> edges;

    public static void readFile(String graphFilePath, String demandFilePath) {
	File graphFile = new File(graphFilePath);
	File demandFile = new File(demandFilePath);
	Scanner graphScanner = new Scanner("");
	Scanner demandScanner = new Scanner("");
	try {
	    graphScanner = new Scanner(graphFile);
	    demandScanner = new Scanner(demandFile);
	} catch(Exception e) {
	    System.out.println("shitfire");
	}
	String newLine;

	// init the adjMatrix
	TraveseGraph.adjMatrix = new int[650][650];
	for(int i=0; i<650; i++)
	    for(int j=0; j<650; j++)
		TraveseGraph.adjMatrix[i][j] = Integer.MAX_VALUE;
	dimension = -1;
	edges = new HashMap<String,String>();

	// read the graph file
	while(graphScanner.hasNext()) {
	    newLine = graphScanner.nextLine();
	    String[] numbers = newLine.split(",");
	    int i, j;
	    i = Integer.parseInt(numbers[1]);
	    j = Integer.parseInt(numbers[2]);
	    edges.put((numbers[1]+"-"+numbers[2]), numbers[0]);
	    TraveseGraph.adjMatrix[i][j] = Integer.parseInt(numbers[3]);
	    dimension = (dimension > i) ? ((dimension > j) ? dimension : j) : ((i > j) ? i : j);
	}
	dimension++;

	// read the demand file
	newLine = demandScanner.nextLine();
	String[] demands = newLine.split(",");
	startNode = Integer.parseInt(demands[0]);
	endNode = Integer.parseInt(demands[1]);
	String[] vs = demands[2].split("\\|");

	TraveseGraph.V = new boolean[dimension];
	for(int i=0; i<dimension; i++)
	    TraveseGraph.V[i] = false;
	for(String v : vs)
	    TraveseGraph.V[Integer.parseInt(v)] = true;
    }
    
    public static void writeFile(Stack<BasicStatusNode> nodeStack) {
	File outFile = new File("/home/uraplutonium/test-case/case1/result.csv");
	FileWriter writer = null;
	try {
	    writer = new FileWriter(outFile);
	} catch(Exception e) {
	    System.out.println("motherfucker");
	}
	
	BasicStatusNode node = nodeStack.pop();
	int i, j;
	j = ((TraveseGraph)node).number;
	boolean first = true;
	while(!nodeStack.isEmpty()) {
	    node = nodeStack.pop();
	    i=j;
	    j=((TraveseGraph)node).number;
	    String str = String.valueOf(i) + "-" + String.valueOf(j);
	    String edge = edges.get(str);
	    if(first) {
		first = false;
	    } else {
		try {
		    writer.write("|");
		} catch(Exception e) {

		}
	    }
	    try {
		writer.write(edge);
	    } catch(Exception e) {

	    }
	}
	try {
	    writer.close();
	} catch(Exception e) {

	}
    }
		
    private int number;
    private boolean[][] path = null;
    private boolean[] visited = null;
    
    public TraveseGraph(int number) {
	this.number = number;
	
	path = new boolean[dimension][dimension];
	for(int i=0; i<dimension; i++)
	    for(int j=0; j<dimension; j++)
		path[i][j] = false;

	visited = new boolean[dimension];
	for(int i=0; i<dimension; i++)
	    visited[i] = false;
	visited[number] = true;
    }

    /**
     * @return the children of this node, whose parentNum is -1 and not initialised.
     */
    @Override
    public List<IStatusNode> expand() {
	List<IStatusNode> children = new ArrayList<IStatusNode>();
	System.out.println("visited[]:");
	for(int i=0; i<dimension; i++)
	    System.out.print(visited[i] + ", ");
	System.out.println();
	for(int i=0; i<dimension; i++) {
	    boolean toExpand = (i!=number &&
				visited[i] != true &&
				adjMatrix[number][i] < Integer.MAX_VALUE);
	    if(i == 1) {
		for(int j=0; j<dimension; j++)
		    if(V[j])
			if(visited[j] == false) {
			    toExpand = false;
			    break;
			}
	    }
	    
	    if(toExpand) {
		TraveseGraph newNode = new TraveseGraph(i);
		for(int j=0; j<dimension; j++)
		    for(int k=0; k<dimension; k++)
			newNode.path[j][k] = path[j][k];
		newNode.path[number][i] = true;
		for(int j=0; j<dimension; j++)
		    newNode.visited[j] = visited[j];
		newNode.visited[i] = true;

		children.add(newNode);
		System.out.println("new node " + i + " added to children list of node " + number);
	    }
	}
	return children;
    }

    @Override
    public String toString() {
	String str = "N";
	str += Integer.toString(number);
	/*
	str += "\n";
	for(int i=0; i<dimension; i++) {
	    for(int j=0; j<dimension; j++)
		str += (path[i][j]?"1\t":"0\t");
	    str += "\n";
	}
	*/
	return str;
    }
    
    @Override
    public int hashCode() {
	int hash = 0;
	for(int i=0; i<dimension; i++)
	    for(int j=0; j<dimension; j++)
		if(path[i][j])
		    hash += 1;
	hash += (number*dimension*dimension);
	return hash;
    }
	
    @Override
    public boolean equals(Object obj) {
	if(obj instanceof TraveseGraph) {
	    TraveseGraph node = (TraveseGraph)obj;
	    if(number != node.number)
		return false;
	    else {
		for(int i=0; i<dimension; i++)
		    for(int j=0; j<dimension; j++)
			if(path[i][j] != node.path[i][j])
			    return false;
	    }
	    return true;
	}
	else
	    return false;
    }

    @Override
    public int costTo(IStatusNode otherNode) throws Exception {
	if(this.equals(otherNode) || !(otherNode instanceof TraveseGraph))
	    throw new Exception();
	else {
	    TraveseGraph other = (TraveseGraph)otherNode;
	    return adjMatrix[number][other.number];
	}
    }

    @Override
    public int h(IStatusNode GOAL) {
	int s = 1;
	for(int i=0; i<dimension; i++)
	    if(V[i])
		if(visited[i] == false)
		    s++;
	return s;
    }

    @Override
    public boolean isSolved(IStatusNode GOAL) {
	boolean solved = true;
	TraveseGraph goal = (TraveseGraph)GOAL;
	if(number != goal.number)
	    solved = false;
	for(int i=0; i<dimension; i++)
	    if(V[i])
		if(visited[i] == false) {
		    solved = false;
		    break;
		}
	System.out.println("N" + number + (solved?" == ":" != ") + "N" + goal.number);
	return solved;
    }

}
