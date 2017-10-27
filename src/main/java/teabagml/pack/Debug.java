package teabagml.pack;

public class Debug {

    private static int counter = 0;
    private static int maxIteration = 50;

    public static void print(int[] array) {
	String prefix = "";
	for (int eachNum : array) {
	    System.out.print(prefix + eachNum);
	    prefix = ", ";
	}
	System.out.println();
    }

    public static void resetCounter() {
	counter = 0;
    }
    
    public static void setMaxIteration(int i) {
	maxIteration = i;
    }

    public static boolean isSolved() {
	if (counter < maxIteration) {
	    //System.out.println("counter false unsolved: " + counter);
	    counter ++;
	    return false;
	} else {
	    System.out.println("counter true solved: " + counter);
	    return true;
	}
    }

    public static int getCount() {
	return counter;
    }
}
