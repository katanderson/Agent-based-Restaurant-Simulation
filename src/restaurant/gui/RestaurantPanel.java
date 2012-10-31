package restaurant.gui;

import restaurant.*;
import astar.*;
import restaurant.layoutGUI.*;
import java.util.concurrent.*; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/** Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers. */
public class RestaurantPanel extends JPanel {
    //create animation
    static int gridX = 20;
    static int gridY = 15;

    //**Decide how many tables to have
    private int nTables = 4;

    //I'm going to address the grid using real coordinates, not 0-based
    //ones. i.e. grid(1,1) has x=1, y=1 and is the "first" square on
    //the layout. I'll waste a row and column
    Semaphore[][] grid = new Semaphore[gridX+1][gridY+1]; 
    //Table[] tables = new Table[nTables];
    Table[] tables = new Table[gridX * gridY];

    Restaurant restaurant =  new Restaurant("Welcome to csci201's Restaurant",
					    gridX, gridY, grid, tables);
    
    //Host, cook, waiters and customers
    private HostAgent host = new HostAgent("Prof. W.", nTables);
    public CookAgent cook = new CookAgent("W. Puck", restaurant);
    private CashierAgent cashier = new CashierAgent("Freddy", restaurant);
    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    private JPanel group = new JPanel();
    

    private RestaurantGui gui; //reference to main gui

    public RestaurantPanel( RestaurantGui gui){
    
    	   
	this.gui = gui;

	//intialize the semaphore grid
	for (int i=0; i<gridX+1 ; i++)
	    for (int j = 0; j<gridY+1; j++)
		grid[i][j]=new Semaphore(1,true);
	//build the animation areas
	try {
	    //make the 0-th row and column unavailable
	    System.out.println("making row 0 and col 0 unavailable.");
	    for (int i=0; i<gridY+1; i++) grid[0][0+i].acquire();
	    for (int i=1; i<gridX+1; i++) grid[0+i][0].acquire();
	    System.out.println("adding wait area");
	    restaurant.addWaitArea(2, 2, 13);
	    for (int i=0; i<13; i++) grid[2][2+i].acquire();
	    System.out.println("adding counter area");
	    restaurant.addCounter(17, 2, 13);
	    for (int i=0; i<13; i++) grid[17][2+i].acquire();
	    System.out.println("adding grill area");
	    restaurant.addGrill(19, 3, 10);
	    for (int i=0; i<10; i++) grid[19][3+i].acquire();
	    //Let's just put the four static tables in for now
	    System.out.println("adding table 1");
	    tables[0] = new Table("T1", 5, 3, 3);//, restaurant);
		restaurant.addTable("T1", 5, 3, 3);
	    for (int i=0; i<3; i++)
		for (int j=0; j<3; j++)
		    grid[5+i][3+j].acquire();// because grid is 0-based
	    System.out.println("adding table 2");
	    tables[1] = new Table("T2", 5, 8, 3);//, restaurant);
		restaurant.addTable("T2", 5, 8, 3);
	    for (int i=0; i<3; i++)
		for (int j=0; j<3; j++)
		    grid[5+i][8+j].acquire();// because grid is 0-based
	    System.out.println("adding table 3");
	    tables[2] = new Table("T3", 10, 3, 3);//,restaurant);
		restaurant.addTable("T3", 10, 3, 3);
	    for (int i=0; i<3; i++)
		for (int j=0; j<3; j++)
		    grid[10+i][3+j].acquire();// because grid is 0-based
	    System.out.println("adding table 4");
	    tables[3] = new Table ("T4", 10, 8, 3);//,restaurant);
		restaurant.addTable("T4", 10, 8, 3);
	    for (int i=0; i<3; i++)
		for (int j=0; j<3; j++)
		    grid[10+i][8+j].acquire();// because grid is 0-based
	}catch (Exception e) {
	    System.out.println("Unexpected exception caught in during setup:"+ e);
	}
	restaurant.setAnimDelay(500);
	restaurant.displayRestaurant();
	
	cook.setCashier(cashier); //allow communication between cook and cashier
	cook.setHost(host);

	host.startThread();
	cook.startThread();
	cashier.startThread();


	setLayout(new GridLayout(1,2, 20,20));
	group.setLayout(new GridLayout(1,2, 10,10));
	
	group.add(waiterPanel);
	group.add(customerPanel);
	
	initRestLabel();
	add(restLabel);
	add(group);
    
    }

    /** Sets up the restaurant label that includes the menu, 
     * and host and cook information */
    private void initRestLabel(){
    	
      
	JLabel label = new JLabel();
	//restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
	restLabel.setLayout(new BorderLayout());
	label.setText(
	 "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>Host:</td><td>"+host.getName()+"</td></tr><tr><td width=50>Cook:</td><td>"+cook.getName()+"</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$20.00</td></tr><tr><td>Chicken</td><td>$15.00</td></tr><tr><td>Salad</td><td>$10.00</td></tr><tr><td>Pizza</td><td>$5.00</td></tr></table><br>></html>");

	restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
	restLabel.add(label, BorderLayout.CENTER);
	restLabel.add(new JLabel("               "), BorderLayout.EAST );
	restLabel.add(new JLabel("               "), BorderLayout.WEST );
    }

    /** When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information 
     * will be shown
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person*/
    public void showInfo(String type, String name){
	
	if(type.equals("Customers")){

	    for(int i=0; i < customers.size(); i++){
		CustomerAgent temp = customers.get(i);
		if(temp.getName() == name)
		    gui.updateInfoPanel(temp);
	    }
	}else if(type.equals("Waiters")){
	    for(int i=0; i < waiters.size(); i++){
		WaiterAgent temp = waiters.get(i);
		if(temp.getName() == name)
		    gui.updateInfoPanel(temp);
	    }
	}
	
    }
    
    
    /** Adds a customer or waiter to the appropriate list
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person */
    public void addPerson(String type, String name){
	
	if(type.equals("Customers")){
	    CustomerAgent c = new CustomerAgent(name, gui, restaurant);
	    c.setHost(host);
	    c.setMoney(gui.custMoney);
	    c.setCashier(cashier);
	    customers.add(c);
	    c.startThread(); //Customer is fsm.
	    c.setHungry(gui.hunger);
	} else if(type.equals("Waiters")){
	    AStarTraversal aStarTraversal = new AStarTraversal(grid);
	    WaiterAgent w = new WaiterAgent(name, aStarTraversal, restaurant, tables);
	    w.setHost(host);
	    w.setCook(cook);
	    host.setWaiter(w);
	    waiters.add(w);
	    w.startThread();
	}
    }	

	public void addTable() {
		int size = 3;
		System.out.println("adding table " + (nTables + 1));
		for(int i = 1; i <= gridX - size; i++) {
			for(int j = 1; j <= gridY - size; j++) {
				if(addTable(i, j, size)) {
					System.out.println("Added table " + nTables);
					return;
				}
			}
		}
		System.out.println("Cannot add table " + (nTables + 1));
	}
	
	public boolean addTable(int x, int y, int size)
	{
		try
		{
			int acqCnt = -1;
			int[][] acqList = new int[9][2];
			for (int i=0; i<size; i++) {
				for (int j=0; j<size; j++) {
					boolean acquired = grid[x+i][y+j].tryAcquire();
					if(acquired) {
						acqCnt++;
						acqList[acqCnt][0] = x+i;
						acqList[acqCnt][1] = y+j;
					}
				    if(!acquired) {
						for(int k=0; k<=acqCnt; k++) {
							grid[acqList[k][0]][acqList[k][1]].release();
						}
						return false;
					}
				}
			}
		    tables[nTables] = new Table ("T" + (nTables+1), x, y, size);//,restaurant);
			restaurant.addTable("T" + (nTables+1), x, y, size);
			nTables++;
			host.addTable();
		}
		catch (Exception e)
		{
		    System.out.println("Unexpected exception caught in during setup:"+ e);
		}
		return true;
	}
}
