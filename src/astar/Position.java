package astar;
import java.util.*;
import java.lang.Math;
import java.util.concurrent.*;

public class Position {
    int x;
    int y;

    public Position (int x, int y){
	this.x = x;
	this.y = y;
    }
    public Position (Position p, int x, int y){
	this.x = p.getX() + x;
	this.y = p.getY() + y;
    }

    public int getX() {return x;}
    public int getY() {return y;}
    
    public double distance(Position destination){
	return Math.sqrt(
		Math.pow(destination.getX()-x,2) +
		Math.pow(destination.getY()-y,2));
    }
    public String toString(){
	return "{"+x+" "+y+"}";
    }
    public boolean equals(Position p){
	return (p.getX()==x && p.getY()==y);
    }
    public boolean open(Semaphore[][] grid){
	//right now this just tests if grid is available. I
	//suspect this is where the locking done. What's in the grid is
	//supposed to be taken care of by the gui.
	return grid[x][y].availablePermits()>0;
    }
    public boolean moveInto(Semaphore[][] grid){
	//System.out.println("moveInto"+this+ " permits="+grid[x][y].availablePermits());
	return grid[x][y].tryAcquire();
    }
    public void release(Semaphore[][] grid){
	grid[x][y].release();
    }
}