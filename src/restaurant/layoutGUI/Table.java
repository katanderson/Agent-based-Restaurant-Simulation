package restaurant.layoutGUI;

import java.awt.*;

public class Table
{
    private int x, y, size;
    private String name, order;
    
    public Table(String name, int x, int y, int size)
    {
        this.name       =   name;
        this.x          =   x;
        this.y          =   y;
        this.size       =   size;
    }
    
    public void takeOrder(String order)
    {
        this.order      =   order;
    }
    
	public int getSize()
	{
		return size;
	}

    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public int seatX()
    {
        return x;
    }
    
    public int seatY()
    {
        return y+1;
    }
    
    public int foodX()
    {
        return x+1;
    }
    
    public int foodY()
    {
        return y+1;
    }
    
    public String getName()
    {
        return name;
    }
            
    public void relocateTable(int x, int y)
    {
        this.x  =   x;
        this.y  =   y;
    }
}
