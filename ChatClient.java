
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

/*Main client class that starts a new ClientConnection thread for every
 *new client that connects. Also takes clients user name*/

public class ChatClient {
	
	private Socket server;
	private ClientConnection clientConnection;
	private BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
	private boolean running = true;

	/*Constructor class catches clients trying to connect to where
	 *there is no server, and exits cleanly*/
	
	public ChatClient(String address, int port) {
		try {
			server = new Socket(address, port);
			clientConnection = new ClientConnection(server, this);
			clientConnection.start();
		}  
		catch (ConnectException e) {
			System.out.println("No server connection at specified location");
			System.exit(0);
		}
		catch (UnknownHostException e) {
			System.out.println("No server connection at specified location");
			System.exit(0);
		}
		catch (IOException e) {
			System.out.println("No server connection at specified location");
			System.exit(0);
		}
	}
	
	/*handleUserInput method gets the users' username, then takes their input,
	*and sends it to the server using the sendToServer method.
	*Also allows Clients to exit upon a null entry*/
	
	public void handleUserInput() {
		try {
			System.out.println("Connected to server");
			String userName = getUserName();
			System.out.println("Welcome " + userName + ", type to chat!");
			clientConnection.sendToServer("USERNAME" + userName); 
			while(running) {
				String userInput = userIn.readLine();
				if(userInput == null) {
					System.out.println("Exiting Server");
					System.exit(0);
				}
				clientConnection.sendToServer(userName + ": " +userInput); 
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*getUserName gets the users user name, making sure it is not empty
	 * Also allows Clients to exit upon a null entry*/
	
	public String getUserName() {
		String userName = "";
		try {
			System.out.println("Enter UserName");
			userName = userIn.readLine();
			while (userName.replaceAll("\\s+","").isEmpty() || userName.startsWith("USERNAME")) {
				System.out.println("Invalid Username");
				System.out.println("Enter Different UserName");
				userName = userIn.readLine();
			}
		} 
		catch (NullPointerException e) {
			System.out.println("Exiting Server");
			System.exit(0);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return userName;
	}
	
	//shutDown method allows client to shut down cleanly
	
	public void shutDown() {
			running = false;
			System.exit(0);
	}
	
	/*main method searches for a valid optional parameter,
	 *then creates a ChatClient based on the optional parameters */
	
	public static void main(String[] args ) {
		String inputPort = null;
		String inputAddress = null;
		boolean gotPort = false;
		boolean gotAddress = false;
		if (args.length == 0) {
			new ChatClient("localhost",14001).handleUserInput();		
		}
		else {
			for(int i = 0; i < args.length; i++) {
				if(args[i].equals("-ccp") && i != args.length -1 && !gotPort) {
					inputPort = args[i+1];
					gotPort = true;
				}
				if(args[i].equals("-cca") && i != args.length -1 && !gotAddress) {
					inputAddress = args[i+1];
					gotAddress = true;
				}
			}
			//Valid port range is 0 to 65535
			if(gotPort&& !gotAddress && inputPort.matches("\\d+") && Integer.parseInt(inputPort) < 65536) {
				System.out.println("Port " + inputPort + " was selected");
				new ChatClient("localhost",Integer.parseInt(inputPort)).handleUserInput();
			}
			else if(!gotPort && gotAddress) {
				System.out.println("Address " + inputAddress + " was selected");
				new ChatClient(inputAddress, 14001).handleUserInput();
			}
			else if(gotPort && gotAddress && inputPort.matches("\\d+") && Integer.parseInt(inputPort) < 65536) {
				System.out.println("Port " + inputPort + " was selected");
				System.out.println("Address " + inputAddress + " was selected");
				new ChatClient(inputAddress,Integer.parseInt(inputPort)).handleUserInput();
			}
			else {
				System.out.println("Additional parameters not recognised, resetting to default");
				new ChatClient("localhost", 14001).handleUserInput();
			}
		}
	}
}
