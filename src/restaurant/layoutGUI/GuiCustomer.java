package restaurant.layoutGUI;

import java.awt.*;

public class GuiCustomer
{
    private int x, y;
    private Color color;
    private Restaurant restaurant;
    private String name;
    
    public GuiCustomer(String name, Color color, Restaurant restaurant)
    {
        this.name       =   name;
        this.color      =   color;
        this.restaurant =   restaurant;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void appearInWaitingQueue()
    {
		try  {
			restaurant.lock.acquire();
			this.y  =   restaurant.getWaitY();
			this.x  =   restaurant.getWaitX();
			this.placeCustomer();	
			restaurant.lock.release();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    protected void placeCustomer()
    {
        restaurant.placeCustomer(x, y, color, name);
    }
    
    protected void move(int x, int y)
    {
        this.x  =   x;
        this.y  =   y;
    }
    
    public void leave()
    {
        restaurant.removeCustomer(x, y);
    }
}
