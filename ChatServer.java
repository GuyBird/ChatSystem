
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*Main server class that starts the serverInput method,
 *and starts a new ServerConnection thread every time 
 *a new user connects*/

public class ChatServer {
	
	private ServerSocket in;
	protected ArrayList<ServerConnection> connections = new ArrayList<ServerConnection>();
	private boolean running = true;
	private Socket socket;

	/*Constructor method starts input thread so server can take input from terminal,
	 *sets up a serverSocket, then starts the run method of the program*/
	
	public ChatServer(int port) {
		try {
			ServerInput serverInput = new ServerInput(this);
			serverInput.start();
			in = new ServerSocket(port);
			listenForClient();
		} 
		catch (BindException e) {
			System.out.println("Address already in Use");
			System.exit(0);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*listenForClient method constantly listens for new Clients trying to connect, 
	 *starts a new ServerConnection thread for each client, and gives them a number*/
	
	public void listenForClient() {

		while(running) {
			try {
				socket = in.accept();
				ServerConnection serverConnection= new ServerConnection(socket, this);
				connections.add(serverConnection);
				serverConnection.start();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*shutDown method calls the shutdown method for all ServerConnection threads,
	 *then shuts itself down cleanly*/
	
	public void shutDown() {	
		if(!connections.isEmpty()) {
			connections.get(0).sendStringToAll("EXIT");
			for(int i = 0; i< connections.size() ;i++) {
				connections.get(i).shutDown();
			}
		}
		running = false;
		System.out.println("Server shutting down");
		System.exit(0);
	}
	
	/*main method searches for a valid optional parameter,
	 *then creates a ChatServer based on the optional parameters */
	
	public static void main(String[] args ) {
		String inputPort = null;
		boolean gotPort = false;
		if (args.length == 0) {
			new ChatServer(14001);	
		}
		else {
			for(int i = 0; i < args.length; i++) {
				if(args[i].equals("-csp") && i != args.length -1 && !gotPort) {
					inputPort = args[i+1];
					gotPort = true;
				}
			}
			//Valid port range is 0 to 65535
			if(gotPort && inputPort.matches("\\d+") && Integer.parseInt(inputPort) < 65536) {
				System.out.println("Port " + inputPort + " was selected");
				new ChatServer(Integer.parseInt(inputPort));
			}
			else {
				System.out.println("Additional parameters not recognised, resetting to default");
				new ChatServer(14001);
			}
		}
	}
}
