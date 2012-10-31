package astar;
import java.util.*;
import java.util.concurrent.*;

public class AStarTraversal extends GraphTraversal
{
    private Semaphore[][] grid;

    public AStarTraversal(Semaphore[][] grid){
	super();
	this.grid = grid; 
	//grid = new Object[1000][2000];
	System.out.println("grid rows="+grid.length+",grid cols="+grid[0].length);
	nodes = new PriorityQueue<Node>(6, new Comparator<Node>()
	{
	    public int compare(Node a, Node b)
	    {
		double distanceA = ((AStarNode)a).getApproxTotalDist();
		double distanceB = ((AStarNode)b).getApproxTotalDist();
		//System.out.println("Comparing Nodes" +distanceA+ "  "+distanceB);
		if (distanceA>distanceB)
		    return 1;
		else if (distanceA<distanceB)
		    return -1;
		else return 0;     }
	}
	);
    }
    public AStarNode createStartNode(Object state){
	Position p = (Position) state;
	AStarNode n = new AStarNode(p);
	n.setDistTravelled(0);
	n.setApproxTotalDist(p.distance((Position)getEndingState()));
	List<Position> path = new ArrayList<Position>();
	path.add(p);
	n.setPath(path);
	//System.out.print("createStartNode"); n.printNode();
	return n;
    }
    public List<Node> expandFunc(Node n) {
	AStarNode node = (AStarNode) n;
	//loop computes the positions you can get to from node
	List<Node> expandedNodes = new ArrayList<Node>();
	List<Position> path = node.getPath();
	Position pos = node.getPosition();
	int x = pos.getX();
	int y = pos.getY();
	//this next pair of loops will create all the possible moves
	//from pos.
	for(int i = -1; i <= 1; i++) {//increment for x direction
	    for (int j = -1; j <= 1; j++) {//increment for y direction
		//create the potential next position
		int nextX=x+i;
		int nextY=y+j;
		//make sure next point is on the grid
		if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
		      (nextX<0 || nextY<0)) continue;
		Position next = new Position(nextX,nextY);
		//System.out.println("considering"+next);
		if (inPath(next,path) || !next.open(grid) ) continue;
		//printCurrentList();
		//System.out.println("available"+next);
		AStarNode nodeTemp = new AStarNode(next);

		//update distance travelled
		nodeTemp.setDistTravelled(
                        node.getDistTravelled()+pos.distance(next));
		//update approximate total distance to destination
		//note that we are computing the straight-line
		//heuristic on the fly right here from next to endingState
		nodeTemp.setApproxTotalDist(
			nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
		//update internal path
		nodeTemp.updatePath(path);
		expandedNodes.add(nodeTemp);//could have just added
					    //them directly to nodelist 
	    }
	}
	return expandedNodes;
    }//end expandFunc
    private boolean inPath (Position pos, List<Position> path){
	for (Position n:path) {if (pos.equals(n)) return true;};
	return false;
    }
    public void printCurrentList() {
	PriorityQueue<Node> pq = new PriorityQueue<Node>(nodes);
	AStarNode p;
	System.out.print("\n[");
	while ((p = (AStarNode)pq.poll()) != null) {
	    System.out.print("\n");
	    p.printNode();
	}
	System.out.println("]");
    }
    public void queueFn(Queue<Node> old, List<Node> newNodes){
	for (Node m:newNodes) {
	    old.offer((AStarNode)m);
	}
    }
    public Semaphore[][] getGrid(){return grid;}
}