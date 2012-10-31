package restaurant.layoutGUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import restaurant.layoutGUI.*;
import java.util.concurrent.*; 

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class Restaurant extends JFrame implements MouseListener
{   
    private long animDelay;
    private JLabel[][] buttons;
    private int grilX, grilY, grilSize, cntrX, cntrY, cntrSize, waitX, waitY, waitSize, waiterX, waiterY;
    private String defaultText;
    private boolean grilFull, cntrFull, waitFull;
    private int xPos, yPos;
    private int waiterNum[][], waiterCnt, defSize, tableCnt;
    private Table tables[], tableSel;
    private Semaphore[][] grid;
	public Semaphore lock = new Semaphore(1, true);
    
    private	int tune_mouse_X, tune_mouse_Y, tune_waiter_pos_X, tune_waiter_pos_Y;
    
    public Restaurant(String caption, int x, int y, Semaphore[][] grid, Table[] tables)
    {
    	tune_mouse_X		=	1; //tunes the X positioning of mouse. Possible values are -1, 0 and 1.
    	tune_mouse_Y		=	1; //tunes the Y positioning of mouse. Possible values are -1, 0 and 1.
    	tune_waiter_pos_X	=	3; //tunes the start X position of the waiter
    	tune_waiter_pos_Y	=	1; //tunes the start Y position of the waiter
    	
        this.tables		=   tables;
        this.grid		=   grid;
        this.setTitle(caption);
        this.getContentPane().setLayout(new GridLayout(x, y, 1, 1));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        defaultText 	=   "_____";
        xPos        	=   x;
        yPos        	=   y;
        defSize     	=   (int)getToolkit().getScreenSize().getHeight()/(x+2);
        buttons     	=   new JLabel[x][y];
        for (int i  = 0; i < x; i++)
        {
            for (int j = 0; j < y; j++)
            {
                buttons[i][j]  =   new JLabel(defaultText, JLabel.CENTER);
                
                Font tmpFont   =   buttons[i][j].getFont();
                buttons[i][j].setFont(new java.awt.Font(tmpFont.getName(), tmpFont.getStyle(), 9));
                buttons[i][j].setPreferredSize(new Dimension(defSize, defSize));
                buttons[i][j].setBackground(new Color(255, 255, 255));
                buttons[i][j].setForeground(new Color(255, 255, 255));
                buttons[i][j].setOpaque(true);
                this.getContentPane().add(buttons[i][j]);
            }
        }
        waiterNum   =   new int[x*y][2];
        tableCnt    =   0;
        animDelay   =   1000;
        addMouseListener(this);    
    }
    
    public void setAnimDelay(long animDelay)
    {
        this.animDelay  =   animDelay;
    }
    
    public void addTable(String tableNum, int x, int y, int size)
    {
		tableCnt ++;
        for (int i = (x - 1); i < (x - 1 + size); i++)
        {
            for (int j = (y - 1); j < (y - 1 + size); j++)
            {
                buttons[i][j].setForeground(new Color(0, 0, 0));
                buttons[i][j].setBackground(new Color(0, 0, 0));
            }
        }
        buttons[x-1][y-1].setForeground(new Color(255, 255, 255));
        buttons[x-1][y-1].setText(tableNum);
    }
    
    public void addTableAgain(String tableNum, int x, int y, int size)
    {
        for (int i = (x - 1); i < (x - 1 + size); i++)
        {
            for (int j = (y - 1); j < (y - 1 + size); j++)
            {
                buttons[i][j].setForeground(new Color(0, 0, 0));
                buttons[i][j].setBackground(new Color(0, 0, 0));
            }
        }
        buttons[x-1][y-1].setForeground(new Color(255, 255, 255));
        buttons[x-1][y-1].setText(tableNum);
    }

	public void addWaitArea(int x, int y, int size)
    {
        waitX       =   x;
        waitY       =   y;
        waitSize    =   size;
        for (int j  = (y - 1); j < (y - 1 + size); j++)
        {
            buttons[x-1][j].setForeground(new Color(0, 0, 0));
            buttons[x-1][j].setBackground(new Color(0, 0, 0));
        }
    }
        
    public void addCounter(int x, int y, int size)
    {
        cntrX       =   x;
        cntrY       =   y;
        cntrSize    =   size;
        for (int j  = (y - 1); j < (y - 1 + size); j++)
        {
            buttons[x-1][j].setForeground(new Color(0, 0, 0));
            buttons[x-1][j].setBackground(new Color(0, 0, 0));
        }
    }
        
    public void addGrill(int x, int y, int size)
    {
        grilX       =   x;
        grilY       =   y;
        grilSize    =   size;
        for (int j  = (y - 1); j < (y - 1 + size); j++)
        {
            buttons[x-1][j].setForeground(new Color(0, 0, 0));
            buttons[x-1][j].setBackground(new Color(0, 0, 0));
        }
    }
    
    public void displayRestaurant()
    {
        this.pack();
        this.setVisible(true);
    }
    
    protected int getGrilX()
    {
        if (!grilFull)
        {
            return grilX;
        }
        else
        {
            for(int i = 1; i <= xPos; i++)
            {
                for(int j = 1; j <= yPos; j++)
                {
                    if(buttons[i-1][j-1].getText().equals(defaultText))
                    {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
    protected int getGrilY()
    {
        grilFull    =   false;
        for(int i = grilY; i < grilY + grilSize; i++)
        {
            if (buttons[grilX-1][i-1].getText().equals(defaultText))
            {
                return i;
            }
        }
        grilFull    =   true;
        for(int i = 1; i <= xPos; i++)
        {
            for(int j = 1; j <= yPos; j++)
            {
                if(buttons[i-1][j-1].getText().equals(defaultText))
                {
                    return j;
                }
            }
        }
        return -1;
    }
    
    protected int getCntrX()
    {
        if (!cntrFull)
        {
            return cntrX;
        }
        else
        {
            for(int i = 1; i <= xPos; i++)
            {
                for(int j = 1; j <= yPos; j++)
                {
                    if(buttons[i-1][j-1].getText().equals(defaultText))
                    {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
    protected int getCntrY()
    {
        cntrFull    =   false;
        for(int i = cntrY; i < cntrY + cntrSize; i++)
        {
            if (buttons[cntrX-1][i-1].getText().equals(defaultText))
            {
                return i;
            }
        }
        cntrFull    =   true;
        for(int i = 1; i <= xPos; i++)
        {
            for(int j = 1; j <= yPos; j++)
            {
                if(buttons[i-1][j-1].getText().equals(defaultText))
                {
                    return j;
                }
            }
        }
        return -1;
    }

    protected int getWaitX()
    {
        if (!waitFull)
        {
            return waitX;
        }
        else
        {
            for(int i = 1; i <= xPos; i++)
            {
                for(int j = 1; j <= yPos; j++)
                {
                    if(buttons[i-1][j-1].getText().equals(defaultText))
                    {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
    protected int getWaitY()
    {
        waitFull    =   false;
        for(int i = waitY; i < waitY + waitSize; i++)
        {
            if (buttons[waitX-1][i-1].getText().equals(defaultText))
            {
                return i;
            }
        }
        waitFull    =   true;
        for(int i = 1; i <= xPos; i++)
        {
            for(int j = 1; j <= yPos; j++)
            {
                if(buttons[i-1][j-1].getText().equals(defaultText))
                {
                    return j;
                }
            }
        }
        return -1;
    }
    
    protected int getWaiterX()
    {
        for(int i = tune_waiter_pos_Y; i <= yPos; i++)
        {
            for(int j = tune_waiter_pos_X; j <= xPos; j++)
            {
                if(buttons[i-1][j-1].getText().equals(defaultText) && grid[j][i].tryAcquire())
                {
					grid[j][i].release();
                    boolean occupied = false;
                    for(int k = 0; k < waiterCnt; k++)
                    {
                        if(waiterNum[k][0] == j && waiterNum[k][1] == i)
                            occupied = true;
                    }
                    if(!occupied)
                    {
                        waiterX =   j;
                        waiterY =   i;

                        waiterNum[waiterCnt][0] = j;
                        waiterNum[waiterCnt][1] = i;
                        
                        waiterCnt++;
                        return waiterX;
                    }
                }
            }
        }
        return waiterX;
    }
    
    protected int getWaiterY()
    {
        return waiterY;
    }
    
    protected void placeWaiter(int x, int y, Color color, String name)
    {
        if (name.length() > 2)
            name = name.substring(0, 2);
        buttons[x-1][y-1].setForeground(color);
        buttons[x-1][y-1].setText(name);
    }
    
    protected void moveWaiter(int oldx, int oldy, int newx, int newy, Color color, String name)
    {
        if (name.length() > 2)
            name = name.substring(0, 2);
        buttons[oldx-1][oldy-1].setForeground(new Color(255, 255, 255));
        buttons[oldx-1][oldy-1].setText(defaultText);
        buttons[newx-1][newy-1].setForeground(color);
        buttons[newx-1][newy-1].setText(name);
        try
        {
            Thread.sleep(animDelay);
        }
        catch(Exception e) {}
    }
  
    protected void moveWaiterCustomer(int oldx, int oldy, int newx, int newy, Color color, String waiterName, String customerName)
    {
        if (waiterName.length() > 2)
            waiterName = waiterName.substring(0, 2);
        if (customerName.length() > 2)
            customerName = customerName.substring(0, 2);
        buttons[oldx-1][oldy-1].setForeground(new Color(255, 255, 255));
        buttons[oldx-1][oldy-1].setText(defaultText);
        buttons[newx-1][newy-1].setForeground(color);
        buttons[newx-1][newy-1].setText(waiterName + customerName);
        try
        {
            Thread.sleep(animDelay);
        }
        catch(Exception e) {}
    }
    
    protected void moveWaiterFood(int oldx, int oldy, int newx, int newy, Color color, String waiterName, String foodName)
    {
        if (waiterName.length() > 2)
            waiterName = waiterName.substring(0, 2);
        if (foodName.length() > 3)
            foodName = foodName.substring(0, 3);
        buttons[oldx-1][oldy-1].setForeground(new Color(255, 255, 255));
        buttons[oldx-1][oldy-1].setText(defaultText);
        buttons[newx-1][newy-1].setForeground(color);
        buttons[newx-1][newy-1].setText(waiterName + foodName);
        try
        {
            Thread.sleep(animDelay);
        }
        catch(Exception e) {}
    }

    protected void placeCustomer(int x, int y, Color color, String name)
    {
        if (name.length() > 2)
            name = name.substring(0, 2);
        buttons[x-1][y-1].setForeground(color);
        buttons[x-1][y-1].setText(name);
        try
        {
            Thread.sleep(animDelay);
        }
        catch(Exception e) {}
    }
    
    protected void removeCustomer(int x, int y)
    {
        buttons[x-1][y-1].setForeground(new Color(0, 0, 0));
        buttons[x-1][y-1].setText(defaultText);
        try
        {
            Thread.sleep(animDelay);
        }
        catch(Exception e) {}
    }
    
    public void placeFood(int x, int y, Color color, String name)
    {
        buttons[x-1][y-1].setForeground(color);
        buttons[x-1][y-1].setText(name);
        try
        {
            Thread.sleep(animDelay);
        }
        catch(Exception e) {}
    }
    
    protected void removeFood(int x, int y)
    {
        buttons[x-1][y-1].setForeground(buttons[x-1][y-1].getBackground());
        buttons[x-1][y-1].setText(defaultText);
        try
        {
            Thread.sleep(animDelay);
        }
        catch(Exception e) {}
    }   
    
    public void mousePressed(MouseEvent e) {
	   int tmpX = e.getY()/defSize + tune_mouse_X;
       int tmpY = e.getX()/defSize + tune_mouse_Y;
       tableSel = null;
       for(int i = 0; i < tableCnt; i ++) {
           if ((tables[i].getX() == tmpX && tables[i].getY() == tmpY)) {
               tableSel = tables[i];
               break;
           }
       }
	   if(tableSel == null) {
		   for(int i = 0; i < tableCnt; i ++) {
			   if ((tables[i].getX() == tmpX - 1 && tables[i].getY() == tmpY)) {
				   tableSel = tables[i];
				   break;
				}
			}
	   }
    }
    
    public void mouseReleased(MouseEvent e) {
       	if (tableSel != null)
       	{
            int oldX			=   tableSel.getX();
            int oldY			=   tableSel.getY();
            int newX			=   e.getY()/defSize + 1;
            int newY			=   e.getX()/defSize + 1;
			boolean gotPermit	=	true;

			for(int i = 0; i < tableSel.getSize(); i++)
		    {
				for(int j = 0; j < tableSel.getSize(); j++)
				{
					if (!grid[newX + i][newY + j].tryAcquire())
					{
						boolean owned	=	false;
						if((newX + i) >= oldX && ((newX + i) <= (oldX + tableSel.getSize())))
							if((newY + j) >= oldY && ((newY + j) <= (oldY + tableSel.getSize())))
								owned	=	true;
						if (!owned)
						{
							gotPermit	=	false;
							for(int k = 0; k <= i; k++)
								for(int l = 0; l < j; l++)
									grid[newX + k][newY + l].release();
							break;
						}
					}
				}
				if (!gotPermit)
					break;
			}
			
			System.out.println("Permit to move table: " + gotPermit);

			if (gotPermit)
			{
	            removeTable(tableSel.getName(), tableSel.getX(), tableSel.getY(), tableSel.getSize());
				tableSel.relocateTable(newX, newY);
				addTableAgain(tableSel.getName(), tableSel.getX(), tableSel.getY(), tableSel.getSize());
				
				for(int i = 0; i < tableSel.getSize(); i++)
					for(int j = 0; j < tableSel.getSize(); j++)
						grid[oldX + i][oldY + j].release();
				for(int i = 0; i < tableSel.getSize(); i++)
					for(int j = 0; j < tableSel.getSize(); j++)
						grid[newX + i][newY + j].tryAcquire();
			}
       }
    }

    public void removeTable(String tableNum, int x, int y, int size)
    {
        for (int i = (x - 1); i < (x - 1 + size); i++)
        {
            for (int j = (y - 1); j < (y - 1 + size); j++)
            {
                buttons[i][j].setText(defaultText);
                buttons[i][j].setForeground(new Color(255, 255, 255));
                buttons[i][j].setBackground(new Color(255, 255, 255));
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }

    public void mouseExited(MouseEvent e) {
       // Do nothing
    }

    public void mouseClicked(MouseEvent e) {
       // Do nothing
    }
}
