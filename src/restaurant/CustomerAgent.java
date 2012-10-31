package restaurant;

import restaurant.gui.RestaurantGui;
import restaurant.layoutGUI.*;
import agent.Agent;
import java.util.*;
import java.awt.Color;

/** Restaurant customer agent. 
 * Comes to the restaurant when he/she becomes hungry.
 * Randomly chooses a menu item and simulates eating 
 * when the food arrives. 
 * Interacts with a waiter only */
public class CustomerAgent extends Agent {
    private String name;
    private int hungerLevel = 5;  // Determines length of meal
    private RestaurantGui gui;
    private int myMoney;
    private int total;
    private boolean enoughMoney;
    public boolean waitingForTable;
    
    
    // ** Agent connections **
    private HostAgent host;
    public WaiterAgent waiter;
    private CashierAgent cashier;
    Restaurant restaurant;
    private Menu menu;
    Timer timer = new Timer();
    GuiCustomer guiCustomer; //for gui
   // ** Agent state **
    private boolean isHungry = false; //hack for gui
    public enum AgentState
	    {DoingNothing, WaitingInRestaurant, SeatedWithMenu, WaiterCalled, WaitingForFood, Eating, WaitingForBill, PayingBill, MakingNewChoice};
	//{NO_ACTION,NEED_SEATED,NEED_DECIDE,NEED_ORDER,NEED_EAT,NEED_LEAVE};
    private AgentState state = AgentState.DoingNothing;//The start state
    public enum AgentEvent 
	    {gotHungry, waitForOpenTable, beingSeated, decidedChoice, waiterToTakeOrder, foodDelivered, doneEating, billReceived, donePaying, notEnoughMoney, decidedToLeave, tablesFull, makeNewChoice, cantMakeNewChoice};
    List<AgentEvent> events = new ArrayList<AgentEvent>();
    
    /** Constructor for CustomerAgent class 
     * @param name name of the customer
     * @param gui reference to the gui so the customer can send it messages
     */
    public CustomerAgent(String name, RestaurantGui gui, Restaurant restaurant) {
	super();
	this.gui = gui;
	this.name = name;
	this.restaurant = restaurant;
	guiCustomer = new GuiCustomer(name.substring(0,2), new Color(0,255,0), restaurant);
	this.enoughMoney = true;
	this.myMoney = 50;
	this.waitingForTable = false;
	
    }
    public CustomerAgent(String name, Restaurant restaurant) {
	super();
	this.gui = null;
	this.name = name;
	this.restaurant = restaurant;
	guiCustomer = new GuiCustomer(name.substring(0,1), new Color(0,255,0), restaurant);
	this.enoughMoney = true;
	this.myMoney = 50;
	this.waitingForTable = false;
    }
    // *** MESSAGES ***
    /** Sent from GUI to set the customer as hungry */
    public void setHungry(int hungerLevel) {
    	this.hungerLevel = hungerLevel;
	events.add(AgentEvent.gotHungry);
	isHungry = true;
	print("I'm hungry");
	print("I have this much money " + myMoney);
	stateChanged();
    }
    /** Waiter sends this message so the customer knows to sit down 
     * @param waiter the waiter that sent the message
     * @param menu a reference to a menu */
    public void msgFollowMeToTable(WaiterAgent waiter, Menu menu) {
    	this.waitingForTable = false;
    	if(myMoney>5){
    		enoughMoney = true;
    	}
    	else{
    		enoughMoney = false;
    	}
    	
			this.menu = menu;
			this.waiter = waiter;
			print("Received msgFollowMeToTable from" + waiter);
			events.add(AgentEvent.beingSeated);
			stateChanged();
   	}
    
    /** Waiter sends this message to take the customer's order */
    public void msgDecided(){
	events.add(AgentEvent.decidedChoice);
	stateChanged(); 
    }
    /** Waiter sends this message to take the customer's order */
    public void msgWhatWouldYouLike(){
	events.add(AgentEvent.waiterToTakeOrder);
	stateChanged(); 
    }

    /** Waiter sends this when the food is ready 
     * @param choice the food that is done cooking for the customer to eat */
    public void msgHereIsYourFood(String choice) {
	events.add(AgentEvent.foodDelivered);
	stateChanged();
    }
    /** Timer sends this when the customer has finished eating */
    public void msgDoneEating() {
	events.add(AgentEvent.doneEating);
	stateChanged(); 
    }
    
    ///added to V3 below
    public void msgHereIsYourBill(WaiterAgent w, int total){
    	this.total = total;
    	events.add(AgentEvent.billReceived);
    	stateChanged();
    }
    
    public void msgThankYouForPaying(int change){
    	myMoney=change;
    	events.add(AgentEvent.donePaying);
    	stateChanged();
    }
    
    public void msgInsufficientFunds(){
    	events.add(AgentEvent.donePaying);
    	stateChanged();
    }
    
    public void msgNotEnoughMoney(){
    	events.add(AgentEvent.notEnoughMoney);
    	stateChanged();
    }
    
    
    public void msgAllTablesFull(){
    	if(hungerLevel<=3){    
    		events.add(AgentEvent.waitForOpenTable);
    	}
    	else{
    		events.add(AgentEvent.decidedToLeave);
    	}
    	stateChanged();
    }
    
    public void msgOkToChangeOrder(){
    	events.add(AgentEvent.makeNewChoice);
    	stateChanged();
    }
    
    public void msgTooLateToChangeOrder(){
    	events.add(AgentEvent.cantMakeNewChoice);
    	stateChanged();
    }
    
    public void msgPleaseReorder(){
    	events.add(AgentEvent.makeNewChoice);
    	stateChanged();
    }
    //done adding to v3

    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	if (events.isEmpty()) return false;
	AgentEvent event = events.remove(0); //pop first element
	
	//Simple finite state machine
	
	if(event == AgentEvent.makeNewChoice){  //regardless of where the customer is, if he can make a new choice, then go back and make a new choice
		state = AgentState.SeatedWithMenu;
		makeMenuChoice();
		return true;
	}
	
	if (state == AgentState.DoingNothing){
	    if (event == AgentEvent.gotHungry)	{
		goingToRestaurant();
		state = AgentState.WaitingInRestaurant;
		return true;
	    }
	    
	}
	if (state == AgentState.WaitingInRestaurant) {
	    if (event == AgentEvent.beingSeated)	{
		makeMenuChoice();
		state = AgentState.SeatedWithMenu;
		return true;
	    }
	    
	    else if(event == AgentEvent.decidedToLeave){
	    	notWaitingForTable();
	    	furiouslyLeaveRestaurant();
	    	state = AgentState.DoingNothing;
	    	return true;
	    }
	    
	    else if(event == AgentEvent.waitForOpenTable){
	    	waitForTable();
	    	state = AgentState.WaitingInRestaurant;
	    	return true;
	    }
	}
	
	if (state == AgentState.SeatedWithMenu) {
		if(event == AgentEvent.notEnoughMoney){
			leaveBecauseNoFunds();
			state = AgentState.DoingNothing;
			stateChanged();
		}
		else if (event == AgentEvent.decidedChoice)	{
			callWaiter();
			state = AgentState.WaiterCalled;
			return true;
	    }
		else if(event == AgentEvent.makeNewChoice){
			makeMenuChoice();
			state = AgentState.SeatedWithMenu;
			stateChanged();
		}
	}
	if (state == AgentState.WaiterCalled) {
	    if (event == AgentEvent.waiterToTakeOrder)	{
		orderFood();
		state = AgentState.WaitingForFood;
		return true;
	    }
	}
	
	if(state == AgentState.MakingNewChoice){
		if(event == AgentEvent.makeNewChoice){
			makeMenuChoice();
			state = AgentState.SeatedWithMenu;
			return true;
		}
		else if(event == AgentEvent.cantMakeNewChoice){
			state = AgentState.WaitingForFood;
			return true;
		}
	}
	if (state == AgentState.WaitingForFood) {
		
		if(event == AgentEvent.makeNewChoice){
			makeMenuChoice();
			state = AgentState.SeatedWithMenu;
			return true;
		}
		
		else if (event == AgentEvent.foodDelivered)	{
			eatFood();
			state = AgentState.Eating;
			return true;
	    }
	}
	if (state == AgentState.Eating) {
	    if (event == AgentEvent.doneEating)	{
		//leaveRestaurant();
	    requestBill();
		state = AgentState.WaitingForBill;
		return true;
	    }
	}
	
	
	if(state == AgentState.WaitingForBill){
		if(event == AgentEvent.billReceived){
			payBill();
			state = AgentState.PayingBill;
			return true;
		}
	}
	
	if(state == AgentState.PayingBill){
		if(event == AgentEvent.donePaying){
			leaveRestaurant();
			state = AgentState.DoingNothing;
			return true;
		}
	}

	print("No scheduler rule fired, should not happen in FSM, event="+event+" state="+state);
	return false;
    }
    
    // *** ACTIONS ***
    
    //added to v3 below
    
    public void requestBill(){
    	print("Requesting bill from waiter");
    	//gui stuff
    	waiter.msgIWantTheBill(this);
    	stateChanged();
    }
    
    public void payBill(){
    	print("Going to pay bill now");
    	//gui stuff
    	cashier.msgIWantToPay(this, waiter, total, myMoney);
    	stateChanged();
    }
    
    public void notWaitingForTable(){
    	print("I'm NOT going to wait for a table!");
    	//gui
    	host.msgIWillNotWaitForTable(this);
    	stateChanged();
    }
    
    public void waitForTable(){
    	print("I'm going to wait for a table");
    	//gui
    	this.waitingForTable = true;
    	host.msgIWillWait(this);
    	stateChanged();
    }
    
    
    /// done adding to v3
    
    /** Goes to the restaurant when the customer becomes hungry */
    private void goingToRestaurant() {
		print("Going to restaurant");
		guiCustomer.appearInWaitingQueue();
		host.msgIWantToEat(this);//send him our instance, so he can respond to us
		stateChanged();
    }
    
    /** Starts a timer to simulate the customer thinking about the menu */
    private void makeMenuChoice(){
    	
    	if(enoughMoney == false){
    		if((int)(Math.random()*10) <= 5){     //50/50 chance the customer will lie about not having money
    			msgNotEnoughMoney();             //if the customer is honest
    			stateChanged();
    		}
    		else{
    			print("Deciding menu choice...(3000 milliseconds)");     //if the customer was dishonest
    			timer.schedule(new TimerTask() {
    			    public void run() {  
    				msgDecided();	    
    			    }},
    			    3000);//how long to wait before running task
    			stateChanged();
    		}
    	}
    	
    	else{           //if the customer actually has enough money
    	
		print("Deciding menu choice...(3000 milliseconds)");
		timer.schedule(new TimerTask() {
		    public void run() {  
			msgDecided();	    
		    }},
		    3000);//how long to wait before running task
		stateChanged();
    	}
    }
    	
    private void callWaiter(){
	print("I decided!");
	waiter.msgImReadyToOrder(this);
	stateChanged();
    }

    /** Picks a random choice from the menu and sends it to the waiter */
    private void orderFood(){
    	List<String> options = new ArrayList<String>();          //stores choices the customer can pay for
    	for(int i = 0; i<menu.choices.length; i++){            
    		if(myMoney>=menu.prices[i]){
    			options.add(menu.choices[i]);                //adds choices to list
    		}
    	}
    	if(!options.isEmpty()){             //if the customer can pay for one of the choices
			String choice = options.get((int)(Math.random()*options.size()));
			print("Ordering the " + choice);
			waiter.msgHereIsMyChoice(this, choice);
			stateChanged();
    	}
    	else{ //the customer doesnt have money for anything, but will make a random selection
    		String choice = menu.choices[(int)(Math.random()*menu.choices.length-1)];
			print("Ordering the " + choice);
			waiter.msgHereIsMyChoice(this, choice);
			stateChanged();
    	}
    }

    /** Starts a timer to simulate eating */
    private void eatFood() {
	print("Eating for " + hungerLevel*1000 + " milliseconds.");
	timer.schedule(new TimerTask() {
	    public void run() {
		msgDoneEating();    
	    }},
	    getHungerLevel() * 1000);//how long to wait before running task
	stateChanged();
    }
    

    /** When the customer is done eating, he leaves the restaurant */
    private void leaveRestaurant() {
	print("Leaving the restaurant");
	print("Money left:" + myMoney);
	guiCustomer.leave(); //for the animation
	waiter.msgDoneEatingAndLeaving(this);                     
	isHungry = false;
	stateChanged();
	gui.setCustomerEnabled(this); //Message to gui to enable hunger button

	//hack to keep customer getting hungry. Only for non-gui customers
	if (gui==null) becomeHungryInAWhile();//set a timer to make us hungry.
    }
    
    //this happens if the customer leaves because he doesn't have enough money
    private void leaveBecauseNoFunds(){
    	print("I dont have money, I am going to leave");
    	guiCustomer.leave();
    	waiter.msgNoMoneySoLeaving(this);
    	isHungry = false;
    	stateChanged();
    	gui.setCustomerEnabled(this);
    	if(gui == null) becomeHungryInAWhile();
    }
    
    private void furiouslyLeaveRestaurant(){  //if a customer decided to not stay at the restaurant because hunger level was too high
    	print("Leaving the restaurant because I am too hungry");
    	print("Money left:" +myMoney);
    	guiCustomer.leave();
    	isHungry = false;
    	stateChanged();
    	gui.setCustomerEnabled(this);
    	if(gui == null) becomeHungryInAWhile();
    }
    
    /** This starts a timer so the customer will become hungry again.
     * This is a hack that is used when the GUI is not being used */
    private void becomeHungryInAWhile() {
	timer.schedule(new TimerTask() {
	    public void run() {  
		setHungry(hungerLevel);		    
	    }},
	    15000);//how long to wait before running task
    }

    // *** EXTRA ***

    /** establish connection to host agent. 
     * @param host reference to the host */
    public void setHost(HostAgent host) {
		this.host = host;
    }
    
    public void setCashier(CashierAgent cashier){
    	this.cashier = cashier;
    }
    
    /** Returns the customer's name
     *@return name of customer */
    public String getName() {
	return name;
    }

    /** @return true if the customer is hungry, false otherwise.
     ** Customer is hungry from time he is created (or button is
     ** pushed, until he eats and leaves.*/
    public boolean isHungry() {
	return isHungry;
    }

    /** @return the hungerlevel of the customer */
    public int getHungerLevel() {
	return hungerLevel;
    }
    
    public void setMoney(int money){
    	this.myMoney = money;
    }
    
    /** Sets the customer's hungerlevel to a new value
     * @param hungerLevel the new hungerlevel for the customer */
    public void setHungerLevel(int hungerLevel) {
	this.hungerLevel = hungerLevel; 
    }
    public GuiCustomer getGuiCustomer(){
	return guiCustomer;
    }
    
    /** @return the string representation of the class */
    public String toString() {
	return "customer " + getName();
    }

    
}

