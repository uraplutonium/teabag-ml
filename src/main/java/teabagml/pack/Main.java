package teabagml.pack;

import teabagml.algorithms.ASearchOPENTable;
import teabagml.algorithms.ClosestCostOPENTable;
import teabagml.algorithms.DeepSearchOPENTable;
import teabagml.algorithms.GlobalHeuristicOPENTable;
import teabagml.algorithms.LeastCostOPENTable;
import teabagml.algorithms.LocalHeuristicOPENTable;
import teabagml.algorithms.TreeSearchEngine;
import teabagml.algorithms.WideSearchOPENTable;
import teabagml.problems.EightPuzzleStatus;
import teabagml.problems.GraphMazeRoom;
import teabagml.problems.GraphMazeRoom2;
import teabagml.problems.TravellingSalesmanCity;
import teabagml.problems.TreeMazeRoom;
import teabagml.problems.UnlockBlockMap;
import teabagml.problems.TraveseGraph;

import teabagml.datasets.*;

import java.util.Stack;

public class Main {

    public static void main(String[] args) {
	TreeSearchEngine deepSearchEngine = new TreeSearchEngine(new DeepSearchOPENTable());
	TreeSearchEngine wideSearchEngine = new TreeSearchEngine(new WideSearchOPENTable());
	TreeSearchEngine globalHeuristicEngine = new TreeSearchEngine(new GlobalHeuristicOPENTable());
	TreeSearchEngine localHeuristicEngine = new TreeSearchEngine(new LocalHeuristicOPENTable());
	TreeSearchEngine leastCostEngine = new TreeSearchEngine(new LeastCostOPENTable());
	TreeSearchEngine closestCostEngine = new TreeSearchEngine(new ClosestCostOPENTable());
	TreeSearchEngine ASearchEngine = new TreeSearchEngine(new ASearchOPENTable());
		
	// Maze Problem
	//		deepSearchEngine.search(new TreeMazeRoom("R1"), new TreeMazeRoom("R16"));
	//		deepSearchEngine.displayPath();
	//		System.out.println("\n************************");
		
	//		wideSearchEngine.search(new TreeMazeRoom("R1"), new TreeMazeRoom("R16"));
	//		wideSearchEngine.displayPath();
	//		System.out.println("\n************************");
		
	//		wideSearchEngine.search(new GraphMazeRoom2("R1"), new GraphMazeRoom2("R16"));
	//		wideSearchEngine.displayPath();
		
	//		ASearchEngine.search(new GraphMazeRoom2("R1"), new GraphMazeRoom2("R16"));
	//		ASearchEngine.displayPath();
		
	//		wideSearchEngine.search(new TravellingSalesmanCity(1), new TravellingSalesmanCity(7));
	//		wideSearchEngine.displayPath();
		
	// Eight Puzzle
	//		deepSearchEngine.search(new EightPuzzleStatus(2, 8, 3, 1, 0, 4, 7, 6, 5),
	//								new EightPuzzleStatus(8, 1, 3, 2, 0, 4, 7, 6, 5));
	//		deepSearchEngine.displayPath();
	//		System.out.println("\n************************");
		
	//		wideSearchEngine.search(new EightPuzzleStatus(2, 8, 3, 1, 0, 4, 7, 6, 5),
	//								new EightPuzzleStatus(8, 1, 3, 2, 0, 4, 7, 6, 5));
	//		wideSearchEngine.displayPath();
	//		System.out.println("\n************************");
		
		
	//		wideSearchEngine.search(new EightPuzzleStatus(4, 1, 3, 2, 8, 5, 0, 7, 6),
	//				new EightPuzzleStatus(1, 2, 3, 4, 5, 6, 7, 8, 0));
	//		wideSearchEngine.displayPath();
	//		System.out.println("\n************************");

	//		globalHeuristicEngine.search(new EightPuzzleStatus(4, 1, 3, 2, 8, 5, 0, 7, 6),
	//									new EightPuzzleStatus(1, 2, 3, 4, 5, 6, 7, 8, 0));
	//		globalHeuristicEngine.displayPath();
	//		System.out.println("\n************************");
		
	//		localHeuristicEngine.search(new EightPuzzleStatus(4, 1, 3, 2, 8, 5, 0, 7, 6),
	//									new EightPuzzleStatus(1, 2, 3, 4, 5, 6, 7, 8, 0));
	//		localHeuristicEngine.displayPath();
	//		System.out.println("\n************************");
		
	//		leastCostEngine.search(new EightPuzzleStatus(4, 1, 3, 2, 8, 5, 0, 7, 6),
	//								new EightPuzzleStatus(1, 2, 3, 4, 5, 6, 7, 8, 0));
	//		leastCostEngine.displayPath();
	//		System.out.println("\n************************");
		
	// when solving eight puzzle, the closest cost approach is the same with deep search approach
	//		closestCostEngine.search(new EightPuzzleStatus(4, 1, 3, 2, 8, 5, 0, 7, 6),
	//								new EightPuzzleStatus(1, 2, 3, 4, 5, 6, 7, 8, 0));
	//		closestCostEngine.displayPath();
	//		System.out.println("\n************************");
		
	//		ASearchEngine.search(new EightPuzzleStatus(4, 1, 3, 2, 8, 5, 0, 7, 6),
	//							new EightPuzzleStatus(1, 2, 3, 4, 5, 6, 7, 8, 0));
	//		ASearchEngine.displayPath();
	//		System.out.println("\n************************");

	//		deepSearchEngine.search(new GraphMazeRoom2("R1"), new GraphMazeRoom2("R16"));
	//		deepSearchEngine.displayPath();
		
	//		deepSearchEngine.search(new TravellingSalesmanCity(1), new TravellingSalesmanCity(7));
	//		deepSearchEngine.displayPath();
		
	//		globalHeuristicEngine.search(new TravellingSalesmanCity(1), new TravellingSalesmanCity(7));
	//		globalHeuristicEngine.displayPath();
		
	//		ASearchEngine.search(new TravellingSalesmanCity(1), new TravellingSalesmanCity(7));
	//		ASearchEngine.displayPath();

	/*
	  int startMap[] = {	1,	1,	1,	-1,	-1,	7,
	  -1,	-1,	2,	-1,	-1,	7,
	  0,	0,	2,	-1,	-1,	7,
	  3,	-1,	2,	-1,	5,	5,
	  3,	-1,	-1,	-1,	6,	-1,
	  4,	4,	4,	-1,	6,	-1};
		
	  int startMap1[] = {	1,	-1,	2,	3,	3,	3,
	  1,	-1,	2,	4,	-1,	-1,
	  1,	0,	0,	4,	-1,	7,
	  5,	5,	6,	6,	-1,	7,
	  8,	8,	9,	9,	-1,	7,
	  -1,	-1,	-1,	-1,	-1,	-1};
		
	  int startMap23[] = {	1,	2,	2,	-1,	3,	3,
	  1,	-1,	4,	5,	5,	5,
	  -1,	-1,	4,	0,	0,	6,
	  7,	7,	4,	-1,	8,	6,
	  9,	10,	11,	11,	8,	12,
	  9,	10,	13,	13,	13,	12};
		
	  int startMap100[] = {	1,	2,	2,	3,	3,	4,
	  1,	-1,	-1,	5,	-1,	4,
	  0,	0,	-1,	5,	-1,	-1,
	  -1,	6,	6,	5,	7,	7,
	  8,	8,	9,	9,	10,	-1,
	  -1,	-1,	-1,	-1,	10,	-1};
		
	  int goalMap[] = {	-1,	-1,	-1,	-1,	-1,	-1,
	  -1,	-1,	-1,	-1,	-1,	-1,
	  -1,	-1,	-1,	-1,	0,	0,
	  -1,	-1,	-1,	-1,	-1,	-1,
	  -1,	-1,	-1,	-1,	-1,	-1,
	  -1,	-1,	-1,	-1,	-1,	-1};

	*/
		
	//ASearchEngine.search(new UnlockBlockMap(startMap), new UnlockBlockMap(goalMap));
	//ASearchEngine.displayPath();

		
	//TraveseGraph.readFile("/home/uraplutonium/test-case/case1/topo.csv", "/home/uraplutonium/test-case/case1/demand.csv");
		
	//ASearchEngine.search(new TraveseGraph(TraveseGraph.startNode), new TraveseGraph(TraveseGraph.endNode));

	//TraveseGraph.writeFile(ASearchEngine.displayPath());


	// Dataset dataset = new Dataset("/media/uraplutonium/Workstation/Workspace/datasets/kddcup/kddcup.100k.csv", false, false);
	// Dataset dataset = new Dataset("/media/uraplutonium/Workstation/Workspace/datasets/asia.100k.csv", true, false);
	// Dataset dataset = new Dataset("/media/uraplutonium/Workstation/Workspace/datasets/iris.100k.csv", false, false);
	// Dataset dataset = new Dataset("/media/uraplutonium/Workstation/Workspace/datasets/stumbleupon.100k.csv", false, false);
	
	Dataset dataset = new Dataset("/media/uraplutonium/Workstation/Workspace/datasets/asia.csv", true, false);
	
	System.out.println(dataset.getInfo());
		
    }

}
