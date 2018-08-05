
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

//Thread class that reads input from a client

public class ServerConnection extends Thread {

	private Socket socket;
	private ChatServer server;
	private PrintWriter out;
	private boolean running = true;
	private String userName;
	
	//Constructor method 
	
	public ServerConnection(Socket socket, ChatServer server) {
		super("ServerConnectionThread");
		this.socket = socket;
		this.server = server;
	}
	
	
	//sendStringToClient sends the latest message to the client
	
	public void sendStringToClient(String message) {
		out.println(message);
	}
	
	/*sendStringToAll calls the sendStringToClient for each client connected,
	*and also prints the latest message to the server, if it is not the shut down command
	*Synchronized to stop potential errors if objects are added to Arraylist while it is being iterated through*/
	
	public synchronized void sendStringToAll(String message) {
		if(message.indexOf("USERNAME") == 0) {
			userName = message.substring(8);
			message = userName + " has Connected";
		}
		for(int i = 0; i< server.connections.size() ;i++) {
			ServerConnection serverConnection = server.connections.get(i);
			serverConnection.sendStringToClient(message);
		}
		if(!message.equals("EXIT")) {
		System.out.println(message);
		}
	}
	
	/*run method override checks for messages from clients,
	 *then calls the sendStringToAll method on the message*/
	
	public void run(){
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			InputStreamReader clientIn = new InputStreamReader(socket.getInputStream());
			BufferedReader clientInput = new BufferedReader(clientIn);
			while(running) {
				try {
					/*Thread sleeps for 10 milliseconds so as not to drain CPU time,
					 *and is unnoticeable for the user*/
					Thread.sleep(10);
					String userInput = clientInput.readLine();
					sendStringToAll(userInput);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} 
		catch (SocketException e) {
			individualShutDown();
		}
		catch (NullPointerException e) {
			individualShutDown();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*shutDown method sends "EXIT" to all clients, allowing them to shut down cleanly,
	 *then shuts itself down cleanly*/
	
	public void shutDown() {
		try {
			socket.close();
			running = false;
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*induvidualShutDown called when Client disconnects, 
	 * removes the connection from connections array list,then cleanly shuts down
	 * Synchronized to stop potential errors if objects are added to Arraylist while it is being iterated through*/
	
	public synchronized void  individualShutDown() {
		running = false;
		/*for loop removes ClientConnection from connections,
		*so shutdown is not called on a closed connection at EXIT*/
		for(int i = 0; i< server.connections.size() ;i++) {
			if(server.connections.get(i) == this) {
				server.connections.remove(i);
				break;
			}
		}
		if(!(userName == null)) {
			sendStringToAll(userName + " has disconnected");
		}
	}
}
