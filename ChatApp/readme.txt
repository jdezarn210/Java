Implementation Details:

Server Implementation (ChatServer.java)

The ChatServer.java file implements a multi-threaded chat server that manages client connections, broadcasts messages, and maintains a list of active users.
Each client is handled in a separate thread using the ClientHandler class.
The server distributes messages to all connected clients.
Clients must provide a username to join the chat.
Commands:
Bye: Logs the user out and notifies all clients.
AllUsers: Sends the list of active users to the requesting client.
Steps:
1. Start the server using:
javac ChatServer.java
java ChatServer

2. The server listens for client connections on port 8989.
3. When a client connects, they must enter a username.
4. The server then manages message forwarding, broadcasting, and user disconnection.

GUI/ClientCode:
ChatClientGUI.java

To enhance the user experience, a GUI version of the client code was implemented using Java Swing. 

Messages are shown in a JTextArea for easy viewing.
A JTextField allows users to type messages.
The user can connect to the server and send messages with button clicks.
A separate thread listens for incoming messages.
 Connection Management:
 When the server disconnects, the client is notified and the interface updates accordingly.
 The connect button re-enables allowing users to reconnect.
Steps:
1. Start the GUI client using:
   javac ChatClientGUI.java
   java ChatClientGUI
2. Enter a **username** and press Connect.
3. The chat messages appear in a text area, and users can type messages in a text field.
4. Click Send to send a message.
5. If the server disconnects, the interface updates, disabling the input fields and allows the user to reconnect.
