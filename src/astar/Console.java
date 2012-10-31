package astar;
/**
 * Console class for keyboard input 
 * Invitation to Computer Science, Java Version, 
 * G. Michael Schneider and Judith L. Gersting
 * Brooks/Cole, Pacific Grove, CA, 2000, pp. 695-696
 * 
 * @author (G. Michael Schneider and Judith L. Gersting) 
 * @version (2000)
 */
import java.io.*; 

public class Console {

   public static void pause() { 
      System.out.println("\nHit Enter to terminate: ");
      try { 
         InputStreamReader reader = new InputStreamReader(System.in); 
         BufferedReader buffer = new BufferedReader(reader); 
         buffer.readLine(); 
      }
      catch (Exception e) {
         System.exit (0); 
      }
   }

   public static int readInt(String prompt) {
      int value = 0; 
      System.out.println(prompt); 
      try { 
         InputStreamReader reader = new InputStreamReader(System.in); 
         BufferedReader buffer = new BufferedReader(reader); 
         String s = buffer.readLine(); 
         value = Integer.parseInt(s); 
      }
      catch (Exception e) {
         System.out.println(e.toString()); 
         Console.pause(); 
         System.exit(0); 
      }
      return value; 
   }   

   public static double readDouble(String prompt) {
      double value = 0; 
      System.out.println(prompt); 
      try { 
         InputStreamReader reader = new InputStreamReader(System.in); 
         BufferedReader buffer = new BufferedReader(reader); 
         String s = buffer.readLine(); 
         value = (Double.valueOf(s)).doubleValue();  
      }
      catch (Exception e) {
         System.out.println(e.toString()); 
         Console.pause(); 
         System.exit(0); 
      }
      return value; 
   }

   public static char readChar(String prompt) {
      char value = ' '; 
      System.out.println(prompt); 
      try { 
         InputStreamReader reader = new InputStreamReader(System.in); 
         BufferedReader buffer = new BufferedReader(reader); 
         String s = buffer.readLine(); 
         if (s.length() == 0) 
            throw new Exception("No characetr entered");
         value = s.charAt(0); 
      }
      catch (Exception e) {
         System.out.println(e.toString()); 
         Console.pause(); 
         System.exit(0); 
      }
      return value; 
   }

   public static String readString(String prompt) {
      String value = ""; 
      System.out.println(prompt); 
      try { 
         InputStreamReader reader = new InputStreamReader(System.in); 
         BufferedReader buffer = new BufferedReader(reader); 
         value = buffer.readLine(); 
      }
      catch (Exception e) {
         System.out.println(e.toString()); 
         Console.pause(); 
         System.exit(0); 
      }
      return value; 
   }

}
