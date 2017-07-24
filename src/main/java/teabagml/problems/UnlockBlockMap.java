package teabagml.problems;

import java.util.ArrayList;
import java.util.List;

public class UnlockBlockMap extends BasicStatusNode {
	
    private int blockNum;
    private int map[][] = new int[6][6];
    private List<IStatusNode> children = new ArrayList<IStatusNode>();
	
    public UnlockBlockMap(int START[]) {
	blockNum = -1;
	for(int i=0; i<36; i++) {
	    map[i/6][i%6] = START[i];
	    if(START[i]>blockNum)
		blockNum = START[i];
	}
	children.clear();
    }

    @Override
    public int hashCode() {
	int hash = 0;
	for(int i=0; i<6; i++) {
	    for(int j=0; j<6; j++)
		hash += map[i][j];
	    hash *= 50;
	}
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if(obj instanceof UnlockBlockMap) {
	    UnlockBlockMap m = (UnlockBlockMap)obj;
	    for(int i=0; i<6; i++)
		for(int j=0; j<6; j++)
		    if(map[i][j] != m.map[i][j])
			return false;
	    return true;
	} else
	    return false;
    }

    @Override
    public List<IStatusNode> expand() {
	children.clear();
	for(int block=0; block<=blockNum; block++) {
	    // search for the block
	    int row=0, column=0;
	    boolean found = false;
	    for(row=0; !found && row<6; row++)
		for(column=0; !found && column<6; column++)
		    if(map[row][column] == block)
			found = true;
	    row--;
	    column--;
			
	    //			System.out.println("====" + "block:" + block + "====");
	    //			System.out.println("row:" + row + "column:" + column);
			
	    // confirm the direction
	    boolean direction;	// true represents landscape, false represents portrait
	    if(row+1<=5 && map[row+1][column]==map[row][column])
		direction = false;
	    else
		direction = true;
	    //			System.out.println("direction:" + direction + (direction ? "\tlandscape" : "\tportrait"));
			
	    int length=1;
	    for(int i=(direction ? column+1 : row+1); i<6 && map[row][column]==(direction ? map[row][i] : map[i][column]); i++)
		length++;
			
	    //			System.out.println("row:" + row + " column:" + column + " direction:" + direction + " length:" + length);
			
	    // expand for moving up/left
	    for(int i=(direction ? column-1 : row-1); i>=0 && -1==(direction ? map[row][i] : map[i][column]); i--) {
		int newMap[] = new int[36];
		// copy the map
		for(int j=0; j<36; j++)
		    newMap[j] = map[j/6][j%6];
				
		// move the block
		for(int j=(direction ? column-1 : row-1); j>=i; j--) {
		    if(direction) {	// moving left
			newMap[row*6+j] = block;
			newMap[row*6+j+length] = -1;
		    } else {	// moving up
			newMap[(j*6)+column] = block;
			newMap[(j+length)*6+column] = -1;
		    }
		}
				
		// create a new block and add to the children
		children.add(new UnlockBlockMap(newMap));
	    }
				
	    // expand for moving down/right
	    for(int i=(direction ? column+length : row+length); i<6 && -1==(direction ? map[row][i] : map[i][column]); i++) {
		int newMap[] = new int[36];
		// copy the map
		for(int j=0; j<36; j++)
		    newMap[j] = map[j/6][j%6];
				
		// move the block
		for(int j=(direction ? column+length : row+length); j<=i; j++) {
		    if(direction) {	// moving right
			newMap[row*6+j] = block;
			newMap[row*6+j-length] = -1;
		    } else {	// moving down
			newMap[(j*6)+column] = block;
			newMap[(j-length)*6+column] = -1;
		    }
		}

		// create a new block and add to the children
		children.add(new UnlockBlockMap(newMap));
	    }
	}
	return children;
    }

    @Override
    public String toString() {
	String str = "";
	for(int i=0; i<6; i++) {
	    for(int j=0; j<6; j++) {
		if(map[i][j] == -1)
		    str += ".\t";
		else {
		    str += map[i][j];
		    str += "\t";
		}
	    }
	    str += "\n";
	}
	str += "\n";
	return str;
    }
	
    @Override
    public int costTo(IStatusNode otherNode) throws Exception {
	if(children.contains(otherNode))
	    return 1;
	else
	    throw new Exception();
    }
	
    @Override
    public int h(IStatusNode GOAL) {
	UnlockBlockMap g = (UnlockBlockMap)GOAL;
	int row=0, column=0;
	boolean found = false;
	for(row=0; !found && row<6; row++)
	    for(column=0; !found && column<6; column++)
		if(map[row][column] == 0)
		    found = true;
	row--;
	column--;
		
	// move to the right of No.0 block
	for( ; column<6 && map[row][column]==0; column++)
	    ;
		
	// calculate h
	int h=0;
	for( ; column<6 && g.map[row][column]!=0; column++) {
	    h++;
	    if(map[row][column]!=-1)
		h++;			
	}
	for( ; column<6 && g.map[row][column]==0; column++) {
	    h++;
	    if(map[row][column]!=-1)
		h++;
	}
	return h;
    }
	
    @Override
    public boolean isSolved(IStatusNode GOAL) {
	UnlockBlockMap g = (UnlockBlockMap)GOAL;
	for(int i=0; i<6; i++)
	    for(int j=0; j<6; j++)
		if((map[i][j]==0 && g.map[i][j]!=0) || (map[i][j]!=0 && g.map[i][j]==0))
		    return false;
	return true;
    }

}
