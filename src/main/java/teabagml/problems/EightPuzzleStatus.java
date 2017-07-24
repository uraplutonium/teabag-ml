package teabagml.problems;

import java.util.ArrayList;
import java.util.List;

public class EightPuzzleStatus extends BasicStatusNode {
	
    private int status[] = new int[9];
    private List<IStatusNode> children = new ArrayList<IStatusNode>();
	
    public EightPuzzleStatus(int n0, int n1, int n2
			     ,int n3, int n4, int n5
			     ,int n6, int n7, int n8) {
	status[0] = n0;
	status[1] = n1;
	status[2] = n2;
	status[3] = n3;
	status[4] = n4;
	status[5] = n5;
	status[6] = n6;
	status[7] = n7;
	status[8] = n8;
    }

    @Override
    public List<IStatusNode> expand() {
	int index;
	for(index  = 0; status[index] != 0; index++)
	    ;
	int row = index/3;
	int column = index%3;
		
	switch(row) {
	case 0:
	    int s1[] = exchange(0, column, 1, column);
	    children.add(new EightPuzzleStatus(s1[0], s1[1], s1[2], s1[3], s1[4], s1[5], s1[6], s1[7], s1[8]));
	    break;
	case 1:
	    int s2[] = exchange(1, column, 0, column);
	    children.add(new EightPuzzleStatus(s2[0], s2[1], s2[2], s2[3], s2[4], s2[5], s2[6], s2[7], s2[8]));
	    int s3[] = exchange(1, column, 2, column);
	    children.add(new EightPuzzleStatus(s3[0], s3[1], s3[2], s3[3], s3[4], s3[5], s3[6], s3[7], s3[8]));
	    break;
	case 2:
	    int s4[] = exchange(2, column, 1, column);
	    children.add(new EightPuzzleStatus(s4[0], s4[1], s4[2], s4[3], s4[4], s4[5], s4[6], s4[7], s4[8]));
	    break;
	}
		
	switch(column) {
	case 0:
	    int s1[] = exchange(row, 0, row, 1);
	    children.add(new EightPuzzleStatus(s1[0], s1[1], s1[2], s1[3], s1[4], s1[5], s1[6], s1[7], s1[8]));
	    break;
	case 1:
	    int s2[] = exchange(row, 1, row, 0);
	    children.add(new EightPuzzleStatus(s2[0], s2[1], s2[2], s2[3], s2[4], s2[5], s2[6], s2[7], s2[8]));
	    int s3[] = exchange(row, 1, row, 2);
	    children.add(new EightPuzzleStatus(s3[0], s3[1], s3[2], s3[3], s3[4], s3[5], s3[6], s3[7], s3[8]));
	    break;
	case 2:
	    int s4[] = exchange(row, 2, row, 1);
	    children.add(new EightPuzzleStatus(s4[0], s4[1], s4[2], s4[3], s4[4], s4[5], s4[6], s4[7], s4[8]));
	    break;
	}
			
	return children;
    }
	
    @Override
    public int h(IStatusNode GOAL) {
	EightPuzzleStatus goal = (EightPuzzleStatus)GOAL;
	int d_status = 0;
	for(int index1=0; index1<9; index1++) {
	    int index2;
	    for(index2=0; status[index1] != goal.status[index2]; index2++)
		;
	    int row1 = index1/3;
	    int col1 = index1%3;
	    int row2 = index2/3;
	    int col2 = index2%3;
			
	    int d_row = row1>row2 ? row1-row2 : row2-row1;
	    int d_col = col1>col2 ? col1-col2 : col2-col1;
			
	    d_status = d_status + d_row + d_col;
	}
	return d_status;
    }

    @Override
    public int hashCode() {
	int hashcode = 0;
	for(int n : status)
	    hashcode = hashcode*10+n;
	return hashcode;
    }

    @Override
    public boolean equals(Object obj) {
	if(obj instanceof EightPuzzleStatus) {
	    EightPuzzleStatus s = (EightPuzzleStatus)obj;
	    for(int i=0; i<9; i++)
		if(s.status[i] != status[i])
		    return false;
	    return true;
	}
	else
	    return false;
    }

    public void displayNode() {
	System.out.println();
	for(int row=0; row<3; row++) {
	    System.out.print('|');
	    for(int column=0; column<3; column++) {
		System.out.print(status[row*3+column]);
		if(column != 2)
		    System.out.print("  ");
	    }
	    System.out.println('|');
	}
	System.out.println();
    }
	
    private int[] exchange(int row1, int col1, int row2, int col2) {
	int s[] = new int[9];
	for(int i=0; i<9; i++)
	    s[i] = status[i];
				
	int index1 = row1*3+col1;
	int index2 = row2*3+col2;
	int temp = s[index1];
	s[index1] = s[index2];
	s[index2] = temp;
	return s;
    }

    @Override
    public int costTo(IStatusNode otherNode) throws Exception {
	if(children.contains(otherNode))
	    return 1;
	else
	    throw new Exception();
    }

}
