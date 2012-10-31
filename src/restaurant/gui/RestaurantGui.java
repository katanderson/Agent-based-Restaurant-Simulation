package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JSlider;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.File;


/** Main GUI class.
 * Contains the main frame and subsequent panels */
public class RestaurantGui extends JFrame implements ActionListener, ChangeListener{
   
    private final int WINDOWX = 450;
    private final int WINDOWY = 700;

    private RestaurantPanel restPanel = new RestaurantPanel(this);
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel(
    "<html><pre><i>(Click on a customer/waiter)</i></pre></html>");
    private JCheckBox stateCB = new JCheckBox();
	private JButton addTable = new JButton("Add Table");
	private JPanel inventorySetup = new JPanel();
	private JPanel inventoryInput = new JPanel();
	private JSlider chicken = new JSlider(0,5);
	private JSlider steak = new JSlider(0,5);
	private JSlider salad = new JSlider(0,5);
	private JSlider pizza = new JSlider(0,5);
	private JLabel inventoryLabel = new JLabel("Set Inventory");
	private JLabel pizzaLabel = new JLabel("Pizza inventory");
	private JLabel saladLabel = new JLabel("Salad inventory");
	private JLabel chickenLabel = new JLabel("Chicken inventory");
	private JLabel steakLabel = new JLabel("Steak inventory:");
	public JTextField currentChicken = new JTextField();
	public JTextField currentSteak = new JTextField();
	public JTextField currentPizza = new JTextField();
	public JTextField currentSalad = new JTextField();
	
	public int custMoney = 50;
	private JPanel moneyPanel = new JPanel();
	private JLabel moneyLabel = new JLabel();
	private JButton custMoneyInput = new JButton();
	public JTextField customerMoney = new JTextField("50");
	
	private Timer t = new Timer(0,null);
	
	private JButton changeOrder = new JButton("Attempt to Change Order");
	private JButton requestBreak = new JButton("Request Break");
	
	
	private JLabel hungerLabel = new JLabel("Customer Hunger Level");
	private JSlider custHungerLevel = new JSlider(0,5);
	public int hunger = 3;
	
    private Object currentPerson;

    /** Constructor for RestaurantGui class.
     * Sets up all the gui components. */
    public RestaurantGui(){

	super("Restaurant Application");

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(50,50, WINDOWX, WINDOWY);

	getContentPane().setLayout(new BoxLayout((Container)getContentPane(),BoxLayout.Y_AXIS));

	Dimension rest = new Dimension(WINDOWX, (int)(WINDOWY*.4));
	Dimension info = new Dimension(WINDOWX, (int)(WINDOWY*.1));
	restPanel.setPreferredSize(rest);
	restPanel.setMinimumSize(rest);
	restPanel.setMaximumSize(rest);
	infoPanel.setPreferredSize(info);
	infoPanel.setMinimumSize(info);
	infoPanel.setMaximumSize(info);
	infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
	
	
	
	inventorySetup.setLayout(new BorderLayout());
	inventorySetup.add(inventoryLabel, BorderLayout.NORTH);
	chicken.setMajorTickSpacing(1);
	chicken.setPaintLabels(true);
	chicken.setPaintTicks(true);
	steak.setMajorTickSpacing(1);
	steak.setPaintLabels(true);
	steak.setPaintTicks(true);
	pizza.setMajorTickSpacing(1);
	pizza.setPaintLabels(true);
	pizza.setPaintTicks(true);
	salad.setMajorTickSpacing(1);
	salad.setPaintLabels(true);
	salad.setPaintTicks(true);
	chicken.addChangeListener(this);
	steak.addChangeListener(this);
	pizza.addChangeListener(this);
	salad.addChangeListener(this);
	custHungerLevel.addChangeListener(this);
	currentChicken.setEditable(false);
	currentSteak.setEditable(false);
	currentSalad.setEditable(false);
	currentPizza.setEditable(false);
	
	moneyLabel.setText("New Customer Money");
	customerMoney.setEditable(true);
	custMoneyInput.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			custMoney = Integer.parseInt(customerMoney.getText());
		}
	});
	custMoneyInput.setText("submit");
	
	moneyPanel.setLayout(new GridLayout(1,3));
	moneyPanel.add(moneyLabel);
	moneyPanel.add(customerMoney);
	moneyPanel.add(custMoneyInput);
	/*currentChicken.setText(Integer.toString(restPanel.cook.inventory.get("Chicken").currentNumber));
	currentSteak.setText(Integer.toString(restPanel.cook.inventory.get("Steak").currentNumber));
	currentPizza.setText(Integer.toString(restPanel.cook.inventory.get("Pizza").currentNumber));
	currentSalad.setText(Integer.toString(restPanel.cook.inventory.get("Salad").currentNumber));*/

	
	
	inventoryInput.setLayout(new GridLayout(5,3));
	inventoryInput.add(chickenLabel);
	inventoryInput.add(currentChicken);
	inventoryInput.add(chicken);
	inventoryInput.add(saladLabel);
	inventoryInput.add(currentSalad);
	inventoryInput.add(salad);
	inventoryInput.add(pizzaLabel);
	inventoryInput.add(currentPizza);
	inventoryInput.add(pizza);
	inventoryInput.add(steakLabel);
	inventoryInput.add(currentSteak);
	inventoryInput.add(steak);
	inventoryInput.add(hungerLabel);
	inventoryInput.add(custHungerLevel);
	
	inventorySetup.add(inventoryInput, BorderLayout.CENTER);
	
	requestBreak.addActionListener(this);
	requestBreak.setVisible(false);

	stateCB.setVisible(false);
	stateCB.addActionListener(this);
	changeOrder.setVisible(false);
	changeOrder.addActionListener(this);

	infoPanel.setLayout(new GridLayout(1,2, 30,0));
	infoPanel.add(infoLabel);
	infoPanel.add(stateCB);
	infoPanel.add(requestBreak);
	infoPanel.add(changeOrder);
	
	getContentPane().add(restPanel);
	getContentPane().add(addTable);
	getContentPane().add(infoPanel);
	getContentPane().add(moneyPanel);
	getContentPane().add(inventorySetup);
	
	addTable.addActionListener(this);
	
	 t.setDelay(40);
	    t.addActionListener(new ActionListener(){
	     	public void actionPerformed(ActionEvent e){
	     		updateInventory();
	     	}
	     });
    

	  
    }
    
  
	  public void updateInventory(){
	    	currentChicken.setText(Integer.toString(restPanel.cook.inventory.get("Chicken").currentNumber));
	 		currentSteak.setText(Integer.toString(restPanel.cook.inventory.get("Steak").currentNumber));
	 		currentPizza.setText(Integer.toString(restPanel.cook.inventory.get("Pizza").currentNumber));
	 		currentSalad.setText(Integer.toString(restPanel.cook.inventory.get("Salad").currentNumber));
	    }

    /** This function takes the given customer or waiter object and 
     * changes the information panel to hold that person's info.
     * @param person customer or waiter object */
    public void updateInfoPanel(Object person){
	stateCB.setVisible(true);
	changeOrder.setVisible(false);
	currentPerson = person;

	
	if(person instanceof CustomerAgent){
	    CustomerAgent customer = (CustomerAgent) person;
	    stateCB.setText("Hungry?");
	    changeOrder.setVisible(true);
	    //changeOrder.setText("Change Order?");
	   // changeOrder.setSelected(customer.waiter.requestingChange(customer));
	    //changeOrder.setEnabled(!customer.waiter.requestingChange(customer));
	    stateCB.setSelected(customer.isHungry());
	    stateCB.setEnabled(!customer.isHungry());
	    infoLabel.setText(
	    "<html><pre>     Name: " + customer.getName() + " </pre></html>");

	}else if(person instanceof WaiterAgent){
	    WaiterAgent waiter = (WaiterAgent) person;
	    //stateCB.setText("On Break?");
	    //stateCB.setSelected(waiter.isOnBreak());
	    //stateCB.setEnabled(true);
	    requestBreak.setVisible(true);
	    stateCB.setVisible(false);
	    changeOrder.setVisible(false);
	    infoLabel.setText(
	    "<html><pre>     Name: " + waiter.getName() + " </html>");
	}	   

	infoPanel.validate();
    }

    /** Action listener method that reacts to the checkbox being clicked or button pushed*/
    public void actionPerformed(ActionEvent e){

	if(e.getSource() == stateCB){
	    if(currentPerson instanceof CustomerAgent){
		CustomerAgent c = (CustomerAgent) currentPerson;
		c.setHungry(hunger);
		stateCB.setEnabled(false);

	    }
	}
	    
   else if(e.getSource() == changeOrder){
	   if(currentPerson instanceof CustomerAgent){
		   CustomerAgent c = (CustomerAgent) currentPerson;
		   c.waiter.msgIWantToChangeOrder(c);
		   //changeOrder.setEnabled(false);
	   }
   }
	    
	   else if(e.getSource() == requestBreak){
		   if(currentPerson instanceof WaiterAgent){
			   WaiterAgent w = (WaiterAgent) currentPerson;
			   w.requestBreak();
		   }
	   }
	
	else if (e.getSource() == addTable)
	{
		try {
			System.out.println("[Gautam] Add Table!");
			//String XPos = JOptionPane.showInputDialog("Please enter X Position: ");
			//String YPos = JOptionPane.showInputDialog("Please enter Y Position: ");
			//String size = JOptionPane.showInputDialog("Please enter Size: ");
			//restPanel.addTable(10, 5, 1);
			//restPanel.addTable(Integer.valueOf(YPos).intValue(), Integer.valueOf(XPos).intValue(), Integer.valueOf(size).intValue());
			restPanel.addTable();
		}
		catch(Exception ex) {
			System.out.println("Unexpected exception caught in during setup:"+ ex);
		}
	}
	    
    }
    
    
    
    public void stateChanged(ChangeEvent e){
    	JSlider source = (JSlider)e.getSource();
    	if (!source.getValueIsAdjusting()) {
			if(source == chicken){
				System.out.println("chicken inventory: "+chicken.getValue());
				restPanel.cook.inventory.get("Chicken").currentNumber = (int)chicken.getValue();
				restPanel.cook.checkToRestock();  //calls cook stateChanged
			}
			else if(source == pizza){
				System.out.println("pizza inventory: "+pizza.getValue());
				restPanel.cook.inventory.get("Pizza").currentNumber = (int)pizza.getValue();
				restPanel.cook.checkToRestock();
			}
			else if(source == salad){
				System.out.println("salad inventory: "+salad.getValue());
				restPanel.cook.inventory.get("Salad").currentNumber = (int)salad.getValue();
				restPanel.cook.checkToRestock();
			}
			else if(source == steak){
				System.out.println("steak inventory: "+steak.getValue());
				restPanel.cook.inventory.get("Steak").currentNumber = (int)steak.getValue();
				restPanel.cook.checkToRestock();
			}
			else if(source == custHungerLevel){
				System.out.println("customer Hunger level set");
				hunger = source.getValue();
			}
		}
    }
    
    

    /** Message sent from a customer agent to enable that customer's 
     * "I'm hungery" checkbox.
     * @param c reference to the customer */
    public void setCustomerEnabled(CustomerAgent c){
	if(currentPerson instanceof CustomerAgent){
	    CustomerAgent cust = (CustomerAgent) currentPerson;
	    if(c.equals(cust)){
		stateCB.setEnabled(true);
		stateCB.setSelected(false);
		changeOrder.setEnabled(true);
		changeOrder.setSelected(false);
	    }
	}
    }
	
	
    /** Main routine to get gui started */
    public static void main(String[] args){
	RestaurantGui gui = new RestaurantGui();
	gui.setVisible(true);
	gui.setResizable(true); //should be false
	 gui.t.start();
    }
}
