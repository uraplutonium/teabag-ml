package teabagml.problems;

import java.util.ArrayList;
import java.util.List;

public class TravellingSalesmanCity extends BasicStatusNode{
	
	private int number;
	
	private static int[][] map = null;
		
	public TravellingSalesmanCity(int number) {
		this.number = number;
		
		if(map == null) {
			map = new int[6][6];
			
			map[0][0] = 3;
			map[0][1] = 2;
			map[0][2] = -1;
			map[0][3] = -1;
			map[0][4] = -1;
			map[0][5] = -1;
			map[1][1] = 1;
			map[1][2] = 2;
			map[1][3] = 3;
			map[1][4] = -1;
			map[1][5] = -1;
			map[2][2] = -1;
			map[2][3] = 5;
			map[2][4] = 10;
			map[2][5] = -1;
			map[3][3] = 1;
			map[3][4] = 5;
			map[3][5] = 6;
			map[4][4] = 3;
			map[4][5] = -1;
			map[5][5] = 1;
		}
	}

	/**
	 * @return the children of this node, whose parentNum is -1 and not initialised.
	 */
	@Override
	public List<IStatusNode> expand() {
		List<IStatusNode> children = new ArrayList<IStatusNode>();

		for(int i[] : map){
			for(int j : i)
				System.out.print(j + "\t");
			System.out.println();
		}
		
		for(int row=0; row<=number-2; row++)
			if(map[row][number-2] > 0)
				children.add(new TravellingSalesmanCity(row+1));
		
		for(int column=number-1; column<=5; column++)
			if(map[number-1][column] > 0)
				children.add(new TravellingSalesmanCity(column+2));
		
		return children;				
	}

	public void displayNode() {
		System.out.print('R' + Integer.toString(number) + '\t');
	}
	
	@Override
	public int hashCode() {
		return number;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TravellingSalesmanCity) {
			TravellingSalesmanCity node = (TravellingSalesmanCity)obj;
			return number==node.number;
		}
		else
			return false;
	}

	@Override
	public int costTo(IStatusNode otherNode) throws Exception {
		if(this.equals(otherNode) || !(otherNode instanceof TravellingSalesmanCity))
			throw new Exception();
		else {
			TravellingSalesmanCity other = (TravellingSalesmanCity)otherNode;
			int row = number > other.number ? other.number-1 : number-1;
			int column = number > other.number ? number-2 : other.number-2;
			if(map[row][column] < 0)
				throw new Exception();
			else
				return map[row][column];
		}
	}

}
