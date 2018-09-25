#Simple Chatting system that allows multiple clients to connect to a server a chat to each other through it

##To run:

##The server needs to be started first: 
Compile the ChatServer code with: `javac ChatServer.java`
Run the ChatServer code with: java `ChatServer`, which will run on port 14001
OR
Use the optional argument -csp to use another port:
    `java ChatServer -csp [PORT NUMBER]`

##Multiple clients can connect:
Compile the ChatClient code with: `javac ChatClient.java`
Run the ChatClient code with `java ChatClient`, which will run on port 14001, and address 'localhost'
OR 
Use the optional argumenys -ccp and -cca to use another port or address: 
    `java ChatClient -ccp [PORT NUMBER] -cca [ADDRESS]`

##To use:
Clients can see what others type, and the server can see what all clients have typed

When launching a Client, they will have to submit a username before they can talk to others

The server can shutdown all Clients and itself either:
    -by closing
    -by typing in 'EXIT'