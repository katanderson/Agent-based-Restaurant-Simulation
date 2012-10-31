package restaurant;

import agent.Agent;
import java.util.*;

import restaurant.CookAgent.MarketOrder;
import restaurant.CookAgent.Item;


public class MarketAgent extends Agent{
	
	private Map<String, FoodData> inventory = Collections.synchronizedMap(new HashMap<String, FoodData>());
	private CookAgent cook; 
	private int bank;
	private String name;
	
	public MarketAgent(String name){
		super();
		this.name = name;
		this.bank = 100;
		inventory.put("Steak", new FoodData("Steak", 10, 10, 5));
		inventory.put("Chicken", new FoodData("Chicken", 10,10, 5));
		inventory.put("Salad", new FoodData("Salad", 10, 10, 5));
		inventory.put("Pizza", new FoodData("Pizza", 10, 10, 5));
		this.startThread();
	}
	
	public class FoodData{
		String name;
		int currentNumber;
		int maxCapacity;
		int pricePerItem;
		
		FoodData(String name, int currentNum, int maxCapacity, int price){
			this.name = name;
			this.currentNumber = currentNum;
			this.maxCapacity = maxCapacity;
			this.pricePerItem = price;
		}
	}

	private List<MarketOrder> orders = Collections.synchronizedList(new ArrayList<MarketOrder>());
	
	
	//Messages
	
	public void msgHereIsOrder(CookAgent cook, MarketOrder order){   //from cook
		this.cook = cook;
		orders.add(order);
		stateChanged();
	}
	
	public void msgHereIsPayment(CookAgent cook, int money){    //from cook
		this.bank+=money;
		stateChanged();
	}
	
	
	
	//Scheduler
	protected boolean pickAndExecuteAnAction(){
		if(!orders.isEmpty()){
			fillOrder(orders.get(0));
			return true;
		}
		return false;
	}
	

	//Actions

	public void fillOrder(MarketOrder o){
		orders.remove(o);
		
		synchronized(o){
		for(Item item: o.items){ //check to see if have the required inventory to fill the order
			if(inventory.get(item.name).currentNumber<item.number){
				print(name +":Can't fill order");
				cook.msgCannotFillOrder(this, o);
				return;
			}
			}
		}
		print(name+ ":Filling order");
		
		List<Item> cookOrder = new ArrayList<Item>();
		int totalCost = 0;
		
		for(Item item: o.items){   //place the items in a package to send to cook, subtract from inventory
			cookOrder.add(item);
			inventory.get(item.name).currentNumber-= item.number;
			totalCost+=(item.number*inventory.get(item.name).pricePerItem);
		}
		print(name+":Order filled");
		cook.msgOrderFulfilled(this, cookOrder, totalCost, o.number);
		
		
	}
	
}
