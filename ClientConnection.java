
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/*Thread class that prints messages from the server to a clients
 */

public class ClientConnection extends Thread{

	private Socket server;
	private ChatClient client;
	private PrintWriter out;
	private BufferedReader serverIn;
	private boolean running = true;
	
	//Constructor method
	
	public ClientConnection(Socket socket, ChatClient client) {
		this.server = socket;
		this.client = client;
	}
	
	//sendToServer method sends a message from a client to the server
	
	public void sendToServer(String clientMessage) {
		out.println(clientMessage);
	}
	
	
	/*run method override listens for messages from the server and prints them for the user.
	 *Also listens for the EXIT command from the server, and calls the shutDown method if it is received,
	 *so it shuts down cleanly*/
	
	public void run() {
		try {
			serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
			out = new PrintWriter(server.getOutputStream(), true);
			while(running) {
				try {
					/*Thread sleeps for 10 milliseconds so as not to drain CPU time,
					 *and is unnoticeable for the user*/
					Thread.sleep(10);
					String serverResponse = serverIn.readLine();
					if(serverResponse.equals("EXIT")) {
						shutDown();
					}
					else {
					System.out.println(serverResponse);
					}
				} 
				catch (SocketException e) {
					System.out.println("Server forced shutdown");
					shutDown();
				}
				catch (NullPointerException e) {
					System.out.println("Server forced shutdown");
					shutDown();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				} 
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//shutDown message shuts down the thread and socket cleanly
	
	public void shutDown() {
		try {
			running = false;
			System.out.println("Server shutting down");
			System.out.println("You have been disconnected from the server");
			client.shutDown();
			server.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
