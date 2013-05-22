package queue;

public class SharedInteger {

	private int value;
	
	public synchronized int get() {
	     return value;
   }
   public synchronized void set( int aNewId ) {
	  value = aNewId;
   }
   
   public synchronized void increment() {
	   value++;
   }
   
   public synchronized void decrement() {
	   value--;
   }
}
