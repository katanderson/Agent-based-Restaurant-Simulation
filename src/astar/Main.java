package astar;
import java.util.concurrent.*;
public class Main
{
	public static void main(String [] args){
	    System.out.print("\n\n\nA* Traversal from 0,0 to 3,3\n");
	    int xdim = Console.readInt("Type x dimension of grid:");
	    int ydim = Console.readInt("Type y dimension of grid:");
	    Semaphore[][] grid = new Semaphore[xdim][ydim];
	    //grid[0][1] =1; //test not found    
	    //grid[1][1] =1; //test not found    
	    //grid[1][0] =1; //test not found    
	    AStarTraversal aStarTraversal =
		new AStarTraversal(grid);
	    AStarNode a = (AStarNode)aStarTraversal.generalSearch(new Position(0,0),new Position(3,3));
	}

}