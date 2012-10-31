package restaurant;

import agent.Agent;
import java.util.*;

import restaurant.layoutGUI.Restaurant;
import restaurant.CookAgent.Payment;

public class CashierAgent extends Agent {
	
	private int bank;
	private int change;
	private WaiterAgent waiter;
	private CustomerAgent customer;
	private CookAgent cook;
	private String name;
	private Restaurant restaurant;
	private List<Payment> cookRequests = new ArrayList<Payment>();
	
	public CashierAgent(String name, Restaurant restaurant){
		super();
		this.name = name;
		this.restaurant = restaurant;
		this.bank = 100;
		this.change = 0;
	}
	
	private class Bill{
		private WaiterAgent w;
		private CustomerAgent c;
		private int total;
		private int custMoney;
		
		public Bill(CustomerAgent c, WaiterAgent w, int total, int custMoney){
			this.w = w;
			this.c = c;
			this.total = total;
			this.custMoney = custMoney;
		}
	}
	
	
	private List<Bill> bills = new ArrayList<Bill>();
	
	
	//messages
	
	public void msgIWantToPay(CustomerAgent c, WaiterAgent w, int total, int custMoney){
		bills.add(new Bill(c, w, total, custMoney));
		stateChanged();
	}
	
	public void msgNeedMoneyForMarket(CookAgent cook, Payment payment){
		this.cook = cook;
		cookRequests.add(payment);
		stateChanged();
	}
	
	
	//scheduler
	
	protected boolean pickAndExecuteAnAction(){
		if(!bills.isEmpty()){
			synchronized(bills){
				processBill(bills.get(0));
			}
			return true;
		}
		
		if(!cookRequests.isEmpty()){
			giveCookMoney(cookRequests.get(0));
		}
		return false;
		
	}
	
	
	//actions
	
	private void processBill(Bill b){
		//DoProcessing();
		if(b.total <= b.custMoney){
			change = b.custMoney - b.total;
			this.bank+=b.total;
			print("Customer "+b.c.getName() +" paid");
			b.c.msgThankYouForPaying(change); //to customer
			b.w.msgCustomerPaid(b.c); //to waiter
		}
		else{
			this.bank -=b.total;
			print("Customer" + b.c.getName() + "did not have money to pay!");
			b.c.msgInsufficientFunds(); //to customer
			b.w.msgCustomerPaid(b.c); //to waiter
		}	
		bills.remove(b);
	}
	
	private void giveCookMoney(Payment payment){
		if(payment.total <bank){
			int cost = payment.total;
			bank -= cost;
			cook.msgHereIsMoney(payment, cost);
		}
	}

}
