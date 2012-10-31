package restaurant;

import agent.Agent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
import restaurant.layoutGUI.*;
import java.awt.Color;


/** Cook agent for restaurant.
 *  Keeps a list of orders for waiters
 *  and simulates cooking them.
 *  Interacts with waiters only.
 */
public class CookAgent extends Agent {

    //List of all the orders
    private List<Order> orders = new ArrayList<Order>();
    public Map<String,FoodData> inventory = new HashMap<String,FoodData>();
    public enum Status {pending, ingredientsConfirmed, toBeCancelled, cooking, done}; // order status
    private List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
    private List<Delivery> marketDeliveries = new ArrayList<Delivery>();
    private List<Payment> payments = new ArrayList<Payment>();
    private enum MarketOrderState {nothing, waitingToBePlaced, pending, beingFilled, received, rejectedByAllMarkets};
    private enum MarketDeliveryState {none, waitingToBeProcessed, processed};
    private enum PaymentState {waitingToSendToCashier, cashierProcessing, readyToPay, paid};
    private enum MarketAgentState {stockedMarket, insufficientMarket};
    boolean fullKitchen = true;
   
    private CashierAgent cashier;
    private HostAgent host;
    
    private List<MyMarket> markets = Collections.synchronizedList(new ArrayList<MyMarket>());
    
    private class MyMarket{
    	MarketAgent mrk;
    	MarketAgentState state;
    	
    	MyMarket(MarketAgent market){
    		mrk = market;
    		state = MarketAgentState.stockedMarket;
    	}
    
    }
    
    public class MarketOrder{
    	public int number;
    	public int totalCost;
    	public List<Item> items;
    	private MarketOrderState state;
    	
    	MarketOrder(){
    		this.items = new ArrayList<Item>();
    		this.totalCost = 0;
    		this.state = MarketOrderState.nothing;
    	}
    	
    	public MarketOrder(int num, int cost, MarketOrderState state){
    		this.number = num;
    		this.totalCost = cost;
    		this.state = state;
    	}
    }
   
    public class Delivery{
    	public List<Item> items;
    	public int cost;
    	public MarketDeliveryState state;
    	
    	Delivery(){
    		items = new ArrayList<Item>();
    		cost = 0;
    		state = MarketDeliveryState.none;
    	}
    }
    
    public class Item{
    	public String name;
    	public int number;
    	
    	Item(String name, int num){
    		this.name = name;
    		this.number = num;
    	}
    }
    
    public class Payment{
    	
		private MyMarket market;
		public int number;
    	public int total;
    	private int restaurantMoney;
    	public PaymentState state;
    	
    	public Payment(MarketAgent market, int bill, PaymentState state, int num) {
    		this.market = new MyMarket(market);
    		this.total = bill;
    		this.state = state;
    		this.number = num;
    		this.restaurantMoney = 0;
		}
    }

    //Name of the cook
    private String name;

    //Timer for simulation
    Timer timer = new Timer();
    Restaurant restaurant; //Gui layout

    /** Constructor for CookAgent class
     * @param name name of the cook
     */
    public CookAgent(String name, Restaurant restaurant) {
	super();

	this.name = name;
	this.restaurant = restaurant;
	//Create the restaurant's inventory.
	inventory.put("Steak",new FoodData("Steak", 5, 5, 5));
	inventory.put("Chicken",new FoodData("Chicken", 4, 5, 5));
	inventory.put("Pizza",new FoodData("Pizza", 3, 5, 5));
	inventory.put("Salad",new FoodData("Salad", 2, 5, 5));
	
	
	markets.add(new MyMarket(new MarketAgent("Market1")));
	markets.add(new MyMarket(new MarketAgent("Market2")));
	markets.add(new MyMarket(new MarketAgent("Market3")));
	
    }
    /** Private class to store information about food.
     *  Contains the food type, its cooking time, and ...
     */
    public class FoodData {
    	public int currentNumber;
    	int maxCapacity;
    	String type; //kind of food
    	double cookTime;
	
	
	public FoodData(String type, double cookTime){
	    this.type = type;
	    this.cookTime = cookTime;
	}
	
	public FoodData(String type, double cookTime, int currentNumber, int maxCap){
		this.currentNumber = currentNumber;
		this.maxCapacity = maxCap;
		this.type = type;
		this.cookTime = cookTime;
	}
	
    }
    /** Private class to store order information.
     *  Contains the waiter, table number, food item,
     *  cooktime and status.
     */
    private class Order {
	public WaiterAgent waiter;
	public int tableNum;
	public String choice;
	public Status status;
	public Food food; //a gui variable

	/** Constructor for Order class 
	 * @param waiter waiter that this order belongs to
	 * @param tableNum identification number for the table
	 * @param choice type of food to be cooked 
	 */
	public Order(WaiterAgent waiter, int tableNum, String choice){
	    this.waiter = waiter;
	    this.choice = choice;
	    this.tableNum = tableNum;
	    this.status = Status.pending;
	}

	/** Represents the object as a string */
	public String toString(){
	    return choice + " for " + waiter ;
	}
    }


    


    // *** MESSAGES ***

    /** Message from a waiter giving the cook a new order.
     * @param waiter waiter that the order belongs to
     * @param tableNum identification number for the table
     * @param choice type of food to be cooked
     */
    public void msgHereIsAnOrder(WaiterAgent waiter, int tableNum, String choice){  //from waiter
	orders.add(new Order(waiter, tableNum, choice));
	stateChanged();
    }
    
    //added to v3 below
    public void msgOrderFulfilled(MarketAgent market, List<Item> items, int bill, int orderNum){ //from market
    	Delivery delivery = new Delivery();
    	System.out.println("Delivery received: ");
    	for(Item food: items){
    		System.out.print(food.name + " ");
    		System.out.println(food.number);
    		delivery.items.add(food);
    	}
    	
    	for(MarketOrder order: marketOrders){
    		if(order.number == orderNum){
    			order.state = MarketOrderState.received;
    			marketOrders.remove(order);
    			break;
    		}
    	}
    
    	marketDeliveries.add(delivery);
    	delivery.state = MarketDeliveryState.waitingToBeProcessed;
    	payments.add(new Payment(market, bill, PaymentState.waitingToSendToCashier, payments.size()+1));
    	stateChanged();
    }
    
    public void msgHereIsMoney(Payment payment, int money){  //from cashier
    	for(Payment p: payments){
    		if(p.number == payment.number){
    			p.restaurantMoney = money;
    			p.state = PaymentState.readyToPay;
    		}
    	}
    	stateChanged();
    }
    
    public void msgCanFillOrder(MarketAgent market, MarketOrder order){   //from market saying it can fill the order
    	for(MarketOrder o: marketOrders){
    		if(o.number == order.number){
    			o.state = MarketOrderState.beingFilled;
    		}
    	}
    	stateChanged();
    }
    
    public void msgCannotFillOrder(MarketAgent market, MarketOrder order){  //from market
    	
    	for(MyMarket m: markets){
    		if(m.mrk == market){
    			m.state = MarketAgentState.insufficientMarket;
    		}
    	}
    
    	for(MarketOrder o: marketOrders){
    		if(o.number == order.number){
    			o.state = MarketOrderState.waitingToBePlaced;
    		}
    	}
    	stateChanged();
    }
    
    public void msgCancelOrder(int tableNum){
    	for(Order order: orders){
    		if(order.tableNum == tableNum){               //identify the correct order to cancel and cancel it
    			order.status = Status.toBeCancelled;
    			break;
    		}
    	}
    	stateChanged();
    }
    
    
    
    //done adding to v3


    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	
    	if(!orders.isEmpty()){
	//If there exists an order o whose status is done, place o.
    		
    		for(Order o:orders){
				if(o.status == Status.toBeCancelled){
					cancelOrder(o.tableNum);
					return true;
				}
			}
    		
			for(Order o:orders){
			    if(o.status == Status.done){
				placeOrder(o);
				return true;
			    }
			}
			
			for(Order o: orders){
				if(o.status ==  Status.pending){
					checkIngredients(o);
					return true;
				}
			}
			//If there exists an order o whose status is pending, cook o.
			for(Order o:orders){
			    if(o.status == Status.ingredientsConfirmed){
				cookOrder(o);
				return true;
			    }
			}
		}
    
    //if there are payments to process
   	if(!payments.isEmpty()){
	
		for(Payment p:payments){
			if(p.state == PaymentState.waitingToSendToCashier){
				requestFunds(p);
				return true;
			}
		}
		
		//if there exists a payment that is ready to be paid, pay the market
		for(Payment p: payments){
			if(p.state == PaymentState.readyToPay){
				payMarket(p.market.mrk, p);
				return true;
				}
			}
    }
    
   	//if there are deliveries waiting to be processed
   	if(!marketDeliveries.isEmpty()){
   		for(Delivery delivery: marketDeliveries){
   			if(delivery.state == MarketDeliveryState.waitingToBeProcessed){
   				processDelivery(delivery);
   				return true;
   			}
   		}
   	}
   	
   	//if there are orders that are waiting to be placed/sent to market
   	if(!marketOrders.isEmpty()){
   		for(MarketOrder o: marketOrders){
   			if(o.state == MarketOrderState.waitingToBePlaced){
   				o.state = MarketOrderState.pending;
   				
   				for(MyMarket market: markets){
   					if(market.state == MarketAgentState.stockedMarket){  //place market in market that is fully stocked
   						market.mrk.msgHereIsOrder(this, o);
   						return true;
   					}
   				}
   				
   				//will reach this point if there are no markets that can fill the order
   				o.state = MarketOrderState.rejectedByAllMarkets;
   				return true;
   			}
   		}
   	}
    
   	 // if there are no orders to cook and no customers waiting and there are no current marketorders, then you can try to place a new order
	if(orders.isEmpty() && host.waitList.isEmpty() && marketOrders.isEmpty()){ 
		
		for(Delivery d: marketDeliveries){
			if(d.state != MarketDeliveryState.processed){   //don't restock if there is still a delivery that needs to be processed
				return true;
			}
		}
		
		int count = 0;
		for(String key: inventory.keySet()){
			if(inventory.get(key).currentNumber<(inventory.get(key).maxCapacity)/2){
				count++;
			}
			
		}
		if(count>=3){  //if more than three items in inventory need restocking
			count = 0;
			print("I need to restock");
			for(MyMarket market: markets){
				if(market.state == MarketAgentState.stockedMarket){  //place order in market that is stocked
					print("Restocking");
					restock(market.mrk);			
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
    
    /** Starts a timer for the order that needs to be cooked. 
     * @param order
     */
    private void cookOrder(Order order){
	DoCooking(order);
	order.status = Status.cooking;
	inventory.get(order.choice).currentNumber--;
    }

    private void placeOrder(Order order){
	DoPlacement(order);
	order.waiter.msgOrderIsReady(order.tableNum, order.food);
	orders.remove(order);
	
    }
    
    private void payMarket(MarketAgent market, Payment p){
    	//gui
    	print("Paying market");
    	p.state = PaymentState.paid;
    	market.msgHereIsPayment(this, p.restaurantMoney);
    }
    
    private void placeMarketOrder(Order o){
    	print("Placing market order");
    	MarketOrder order = new MarketOrder();
    	order.items.add(new Item(o.choice, inventory.get(o.choice).maxCapacity-inventory.get(o.choice).currentNumber));
    	order.state = MarketOrderState.waitingToBePlaced;
    	marketOrders.add(order);
    }

    private void restock(MarketAgent market){
    	print("No orders, so going to restock kitchen");
    	MarketOrder marketOrder = new MarketOrder();
    	int current = 0;
    	int max = 0;
    	for(String key: inventory.keySet()){
    		current = inventory.get(key).currentNumber;
    		max = inventory.get(key).maxCapacity;
    		if(current!=max){
    			marketOrder.items.add(new Item(key,max-current));
    		}
    	}
    	if(!marketOrder.items.isEmpty()){
    		marketOrder.state = MarketOrderState.pending;
    		marketOrders.add(marketOrder);
    		market.msgHereIsOrder(this, marketOrder);
    		
    	}
    }
    
    private void requestFunds(Payment p){
    	print("Requesting money from cashier to pay market");
    	p.state = PaymentState.cashierProcessing;
    	cashier.msgNeedMoneyForMarket(this, p);
    }
    
    private void processDelivery(Delivery d){
    	print("Processing Delivery");
    	for(Item item: d.items){
    		inventory.get(item.name).currentNumber+=item.number;
    	}
    	d.state = MarketDeliveryState.processed;
    }
    
    private void cancelOrder(int tableNum){
    	print("Cancelling order");
    	for(Order o: orders){
    		if(o.tableNum == tableNum){
    			orders.remove(o);
    			break;
    		}
    	}
    }
    
    private void checkIngredients(Order o){
    	print("Checking ingredients");
    	if(inventory.get(o.choice).currentNumber==0){
    		print("Telling waiter we are out of " + o.choice);
    		o.waiter.msgInsufficientIngredients(o.tableNum);
    		orders.remove(o);
    		placeMarketOrder(o);
    	}
    	
    	else{
    		o.status = Status.ingredientsConfirmed;     //do have the ingredients to cook order
    	}
    	
    }

    // *** EXTRA -- all the simulation routines***
    
    public void setCashier(CashierAgent cashier){
    	this.cashier = cashier;
    }
    
    public void setHost(HostAgent host){
    	this.host = host;
    }

    /** Returns the name of the cook */
    public String getName(){
        return name;
    }
    
    public void checkToRestock(){
    	stateChanged();
    }
    
    public boolean foodAlreadyCooking(int tableNum){
    	for(Order o: orders){
    		if(o.tableNum == tableNum){
    			if(o.status == Status.cooking){
    				return true;
    			}
    			else return false;
    		}
    	}
    	return false;
    }

    private void DoCooking(final Order order){
	print("Cooking:" + order + " for table:" + (order.tableNum+1));
	//put it on the grill. gui stuff
	order.food = new Food(order.choice.substring(0,2),new Color(0,255,255), restaurant);
	order.food.cookFood();

	timer.schedule(new TimerTask(){
	    public void run(){//this routine is like a message reception    
		order.status = Status.done;
		stateChanged();
	    }
	}, (int)(inventory.get(order.choice).cookTime*1000));
    }
    public void DoPlacement(Order order){
	print("Order finished: " + order + " for table:" + (order.tableNum+1));
	order.food.placeOnCounter();
    }
}


    
