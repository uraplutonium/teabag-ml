package teabagml.pack;

import java.util.Map;
import java.util.HashMap;
import java.lang.UnsupportedOperationException;

public class DataCollector {
    private static boolean finished = false;
    private static long maxIteration = 0;
    private static long scoringCalls = 0;
    private static Map<String, Long[]> eventMap = new HashMap<String, Long[]>(); // <type, [counts, totaltime, timebuffer]>
    
    public static void printInfo() {
	for (String eachKey : eventMap.keySet())
	    System.out.println(eachKey + "," + String.valueOf((eventMap.get(eachKey))[0]) + "," + String.valueOf((eventMap.get(eachKey))[1]));
    }

    public static void startEvent(String type, long startTime) throws UnsupportedOperationException {
	String key = type + String.valueOf(startTime);
	if (eventMap.containsKey(key)) {
	    // event exists, try to start a new one
	    if ((eventMap.get(key))[2] != 0) {
		// there is an unfinished event
		throw new UnsupportedOperationException();
	    } else {
		(eventMap.get(key))[2] = System.currentTimeMillis();
	    }
	} else {
	    // new event, try to create
	    Long[] newTuple = new Long[3];
	    newTuple[0] = (long)0;
	    newTuple[1] = (long)0;
	    newTuple[2] = System.currentTimeMillis();
	    eventMap.put(key, newTuple);
	}
	System.out.println("START: " + key + "," + String.valueOf((eventMap.get(key))[0]) + "," + String.valueOf((eventMap.get(key))[1]));
    }
    
    public static void endEvent(String type, long startTime) throws UnsupportedOperationException {
	String key = type + String.valueOf(startTime);
	if (eventMap.containsKey(key)) {
	    // end the event
	    if ((eventMap.get(key))[2] != 0) {
		// try to finish the event
		(eventMap.get(key))[0] += 1; // counts increased by 1
		(eventMap.get(key))[1] += (System.currentTimeMillis() - (eventMap.get(key))[2]); // total time increased by the duration
		(eventMap.get(key))[2] = (long)0; // reset the time buffer
	    } else {
		// the event has not been started
		throw new UnsupportedOperationException();
	    }
	} else {
	    // the event does not exist
	    throw new UnsupportedOperationException();
	}
	System.out.println("END:" + key + "," + String.valueOf((eventMap.get(key))[0]) + "," + String.valueOf((eventMap.get(key))[1]));
	if (type.equals("score"))
	    scoringCalls++;
    }
    
    public static void setMaxIteration(long i) {
	maxIteration = i;
    }

    public static boolean finished() {
	return (scoringCalls >= maxIteration);
    }
}
