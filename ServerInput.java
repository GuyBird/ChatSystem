
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*Thread class that allows allows 
 *input from the user to be read by the server */

public class ServerInput extends Thread{
	
	private ChatServer server;
	private boolean running = true;

	//Constructor method
	
	public ServerInput(ChatServer server) {
		super("ServerInputThread");
		this.server = server;
	}
	
	/*run method override calls the server shutDown method if "EXIT" is inputed by the user,
	 *to allow the server to shut down cleanly.
	 *Also shuts down on null pointer exception to avoid errors*/
	
	public void run() {
		String serverInput;
		while(running) {
			try {
				/*Thread sleeps for 10 milliseconds so as not to drain CPU time,
				 *and is unnoticeable for the user*/
				Thread.sleep(10);
				BufferedReader serverIn = new BufferedReader(new InputStreamReader(System.in));
				serverInput = serverIn.readLine();
				if(serverInput.equals("EXIT")) {
					server.shutDown();
					running = false;
				}
				else {
					System.out.println("Invalid input");
				}
			} 
			catch(NullPointerException e) {
				server.shutDown();   
				running = false;
			}
			catch (IOException e) {
				System.out.println("Invalid input");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
