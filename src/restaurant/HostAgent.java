package restaurant;

import agent.Agent;
import java.util.*;

import restaurant.WaiterAgent.breakState;


/** Host agent for restaurant.
 *  Keeps a list of all the waiters and tables.
 *  Assigns new customers to waiters for seating and 
 *  keeps a list of waiting customers.
 *  Interacts with customers and waiters.
 */
public class HostAgent extends Agent {

    /** Private class storing all the information for each table,
     * including table number and state. */
    private class Table {
		public int tableNum;
		public boolean occupied;
	
		/** Constructor for table class.
		 * @param num identification number
		 */
		public Table(int num){
		    tableNum = num;
		    occupied = false;
		}	
    }

    /** Private class to hold waiter information and state */
    private class MyWaiter {
	public WaiterAgent wtr;
	public boolean working = true;

	/** Constructor for MyWaiter class
	 * @param waiter
	 */
	public MyWaiter(WaiterAgent waiter){
	    wtr = waiter;
	}
    }

    //List of all the customers that need a table
    public List<CustomerAgent> waitList =
		Collections.synchronizedList(new ArrayList<CustomerAgent>());
    
    private List<CustomerAgent> waitingCust = 
    	Collections.synchronizedList(new ArrayList<CustomerAgent>());

    //List of all waiter that exist.
    private List<MyWaiter> waiters =
		Collections.synchronizedList(new ArrayList<MyWaiter>());
    private int nextWaiter =0; //The next waiter that needs a customer
    
    //List of all the tables
    int nTables;
    private Table tables[];
    private boolean allTablesFull;

    //Name of the host
    private String name;
    
    private int count;

    /** Constructor for HostAgent class 
     * @param name name of the host */
    public HostAgent(String name, int ntables) {
	super();
	this.nTables = ntables;
	tables = new Table[nTables];
	this.allTablesFull = false;

	for(int i=0; i < nTables; i++){
	    tables[i] = new Table(i);
	}
	this.name = name;
    }

    // *** MESSAGES ***

    /** Customer sends this message to be added to the wait list 
     * @param customer customer that wants to be added */
    public void msgIWantToEat(CustomerAgent customer){
    	int count = 0;
    	for(Table t: tables){
    		if(t.occupied == true){
    			count++;
    		}
    	}
    	
    	if(count == nTables){     //all tables are full
    		customer.msgAllTablesFull();
    	}
    	else{
    		waitList.add(customer);
    		
    	}
    	stateChanged();
    }

    /** Waiter sends this message after the customer has left the table 
     * @param tableNum table identification number */
    public void msgTableIsFree(int tableNum){
	tables[tableNum].occupied = false;
	stateChanged();
    }
    
    public void msgIWantBreak(WaiterAgent waiter){
    	print("I have recieved word that a waiter is requesting a break");
    	stateChanged();
    }
    
    public void msgIWillWait(CustomerAgent c){
    	waitingCust.add(c);
    	stateChanged();
    }
    
    public void msgIWillNotWaitForTable(CustomerAgent c){
    	waitList.remove(c);
    	stateChanged();
    }

    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
    	
    	

    	// first seat customers who are waiting for a table when all tables are full
    	if(!waitingCust.isEmpty() && !waiters.isEmpty()){
    		
    		    synchronized(waiters){
    			//Finds the next waiter that is working and not requesting a break
    			while(!waiters.get(nextWaiter).working || waiters.get(nextWaiter).wtr.WaiterBreakState==breakState.requestingBreak){
    			    nextWaiter = (nextWaiter+1)%waiters.size();
    			}
    		    }
    		    //print("picking waiter number a,jsdhk:"+nextWaiter);
    		    //Then runs through the tables and finds the first unoccupied 
    		    //table and tells the waiter to sit the first customer at that table
    		    for(int i=0; i < nTables; i++){

    			if(!tables[i].occupied){
    			    synchronized(waitingCust){
    				tellWaiterToSitCustomerAtTable(waiters.get(nextWaiter),
    				    waitingCust.get(0), i);
    			    }
    			    return true;
    			}
    		    }
    		
    	}
    	
	// then seat customers who were not waiting for a table
	if(!waitList.isEmpty() && !waiters.isEmpty()){
	    synchronized(waiters){
		//Finds the next waiter that is working
		//while(!waiters.get(nextWaiter).working || waiters.get(nextWaiter).wtr.WaiterBreakState==breakState.requestingBreak){
		   while(!waiters.get(nextWaiter).working){
			   if(waiters.get(nextWaiter).wtr.WaiterBreakState==breakState.requestingBreak){
				   nextWaiter = (nextWaiter+1)%waiters.size();
			   }
			   else break;
	    }
	    print("picking waiter number:"+nextWaiter);
	    //Then runs through the tables and finds the first unoccupied 
	    //table and tells the waiter to sit the first customer at that table
	    for(int i=0; i < nTables; i++){

		if(!tables[i].occupied){
		    synchronized(waitList){
			tellWaiterToSitCustomerAtTable(waiters.get(nextWaiter),
			    waitList.get(0), i);
		    }
		    return true;
		}
	    }
	}
	}
	
	for(MyWaiter w: waiters){
		if(w.wtr.WaiterBreakState == WaiterAgent.breakState.requestingBreak){
			if(waitList.isEmpty() && !waiters.isEmpty()){
				//print("num of waitiers "+waiters.size());
	    			if(waiters.size()>1){
	    				print("Allowing break for waiter: " + w.wtr);
	    				AllowBreak(w.wtr, true);
	    				return true;
	    			}
	    			else{
	    				//print("cant allow break");
	    				AllowBreak(w.wtr, false);
	    				return true;
	    			}
    			}
		return true;
    	}
	}
	
    	
    

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
    }
    
    // *** ACTIONS ***
    
    
    //adding to v3 now
    public void AllowBreak(WaiterAgent w, boolean flag){
    	//DoCheckBreak(); animation
    	w.msgOkToBreak(flag);
    	//okBreak = false;
    }
    
    //done adding
    
    /** Assigns a customer to a specified waiter and 
     * tells that waiter which table to sit them at.
     * @param waiter
     * @param customer
     * @param tableNum */
    private void tellWaiterToSitCustomerAtTable(MyWaiter waiter, CustomerAgent customer, int tableNum){
	print("Telling " + waiter.wtr + " to sit " + customer +" at table "+(tableNum+1));
	waiter.wtr.msgSitCustomerAtTable(customer, tableNum);
	tables[tableNum].occupied = true;
	if(customer.waitingForTable == false)  //determine which list the customer is on
	{
		waitList.remove(customer);
	}
	else
	{
		waitingCust.remove(customer);
	}
	nextWaiter = (nextWaiter+1)%waiters.size();
	stateChanged();
    }
	
    

    // *** EXTRA ***

    /** Returns the name of the host 
     * @return name of host */
    public String getName(){
        return name;
    }    

    /** Hack to enable the host to know of all possible waiters 
     * @param waiter new waiter to be added to list
     */
    public void setWaiter(WaiterAgent waiter){
	waiters.add(new MyWaiter(waiter));
	stateChanged();
    }
    
    //Gautam Nayak - Gui calls this when table is created in animation
    public void addTable() {
	nTables++;
	Table[] tempTables = new Table[nTables];
	for(int i=0; i < nTables - 1; i++){
	    tempTables[i] = tables[i];
	}  		  			
	tempTables[nTables - 1] = new Table(nTables - 1);
	tables = tempTables;
    }
}
