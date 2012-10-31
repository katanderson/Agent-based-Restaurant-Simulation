package restaurant.layoutGUI;

import java.awt.*;

public class GuiWaiter
{
    private int x, y;
    private Color color;
    private Restaurant restaurant;
    private GuiCustomer customer;
    private Food food;
    private boolean customerPresent, foodPresent;
    private String name;
    
    public GuiWaiter(String name, Color color, Restaurant restaurant)
    {
		this.name       =   name;
		System.out.println("GuiWaiter name="+name);
        this.color      =   color;
        this.restaurant =   restaurant;
        customerPresent =   false;
        foodPresent     =   false;
        this.x          =   restaurant.getWaiterX();
        this.y          =   restaurant.getWaiterY();
        this.placeWaiter();
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
    
    protected void placeWaiter()
    {
        restaurant.placeWaiter(x, y, color, name);
    }
    
    public void move(int x, int y)
    {
        if (customerPresent)
        {
            restaurant.moveWaiterCustomer(this.x, this.y, x, y, color, name, customer.getName());
            this.x          =   x;
            this.y          =   y;
            customer.move(x, y);            
        }
        else if (foodPresent)
        {
            restaurant.moveWaiterFood(this.x, this.y, x, y, color, name, food.getName());
            this.x          =   x;
            this.y          =   y;
            food.move(x, y);
        }
        else
        {
            restaurant.moveWaiter(this.x, this.y, x, y, color, name);
            this.x          =   x;
            this.y          =   y;
        }
    }
    
    public void pickUpCustomer(GuiCustomer customer)
    {
        customerPresent     =   true;
        this.customer       =   customer;
        customer.leave();
        customer.move(x, y);
        this.move(x, y);
    }
    
    public void seatCustomer(Table table)
    {
        customerPresent     =   false;
        customer.move(table.seatX(), table.seatY());
        customer.placeCustomer();
        this.move(this.x, this.y);
    }
    
    public void pickUpFood(Food food)
    {
        foodPresent         =   true;
        this.food           =   food;
        food.remove();
        food.move(x, y);
        this.move(x, y);
    }
    
    public void serveFood(Table table)
    {
        foodPresent         =   false;
        food.move(table.foodX(), table.foodY());
        food.placeFood();
        this.move(this.x, this.y);
    }
}
