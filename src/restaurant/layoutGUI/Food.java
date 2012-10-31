package restaurant.layoutGUI;

import java.awt.*;

public class Food
{
    private int x, y;
    private Color color;
    private Restaurant restaurant;
    private String name;
    
    public Food(String name, Color color, Restaurant restaurant)
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
    
    public void cookFood()
    {
        this.y  =   restaurant.getGrilY();
        this.x  =   restaurant.getGrilX();
        this.placeFood();
    }
    
    public void placeOnCounter()
    {
        this.remove();
        y       =   restaurant.getCntrY();
        x       =   restaurant.getCntrX();
        this.move(x, y);
        this.placeFood();
    }
    
    protected void placeFood()
    {
        restaurant.placeFood(x, y, color, name);
    }
    
    protected void move(int x, int y)
    {
        this.x          =   x;
        this.y          =   y;
    }
    
    public void remove()
    {
        restaurant.removeFood(x, y);
    }
}
