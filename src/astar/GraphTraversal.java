package astar;

import java.util.*;
import java.lang.String;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public abstract class GraphTraversal
{
	protected Queue<Node> nodes = null;
	protected Object startingState, endingState;

	public GraphTraversal()	{		
	}
	public Object getStartingState() {return startingState;}
	public Object getEndingState() {return endingState;}
	public Node generalSearch(Object start, Object end) {
	    //set starting Pos 
	    startingState = start;	    
	    //set ending Pos
	    endingState = end;
	    nodes.clear();
	    nodes.add(createStartNode(startingState));
	    //System.out.println("After createStartNode, size of nodes="+nodes.size());
	    //create a loop
	    //if nodes is empty, search failed
	    //otherwise remove the first node,
	    //if it matches the destination, search succeeded return node
	    //else expand the graph on the node that was just removed
	    while(true) {
		if(nodes.size() == 0) {//failed to find a path
		    printMessage("Failed to find a path from " +
				 startingState.toString()  +
				 " to " + endingState.toString() +
				 ", sorry.", null);
		    return null;
		} else {
		    //printCurrentList();
		    //Console.pause();
		    Node first = nodes.poll(); 
		    if (first.goalTest(endingState)){
			//printMessage("Path Found.", first); 
			return first;
		    }else{
			queueFn(nodes, expandFunc(first)); 
		    }		
		}
	    }
	}

	public void printMessage(String message, Node n) {
		System.out.print("\n" + message);
		if (n != null)	System.out.print(n);
	}
	public abstract Node createStartNode(Object state);
	public abstract void queueFn(Queue<Node> old,
						      List<Node> newNodes);
	public abstract List<Node> expandFunc(Node node); 

	public abstract void printCurrentList();
}//end printCurrentList