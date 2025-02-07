import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 8989; // Port number where server listens
    private static Set<ClientHandler> clientHandlers = new HashSet<>(); // Set to store active clients

    public static void main(String[] args) {
        System.out.println("Server started on port " + PORT);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Create server socket
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept incoming client connections
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start(); // Start a new thread for each client
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    // Method to broadcast messages to all connected clients
    public static synchronized void broadcast(String message) {
        for (ClientHandler client : clientHandlers) {
            client.sendMessage(message); // Send the message to all clients, including the sender
        }
    }

    // Method to remove a client from the active clients list
    public static synchronized void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
    }

    // Method to retrieve a list of active users
    public static synchronized String getActiveUsers() {
        StringBuilder userList = new StringBuilder("Active Users: ");
        for (ClientHandler client : clientHandlers) {
            userList.append(client.getUsername()).append(", ");
        }
        return userList.substring(0, userList.length() - 2); // Remove trailing comma
    }

    // Inner class to handle each client in a separate thread
    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Read username directly from client input
                username = in.readLine();
                if (username == null || username.trim().isEmpty()) {
                    out.println("Username cannot be empty. Disconnecting...");
                    socket.close();
                    return;
                }

                synchronized (clientHandlers) {
                    clientHandlers.add(this); // Add client to active clients list
                }

                System.out.println(username + " has joined the chat.");
                broadcast("Server: Welcome " + username);

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("Bye")) {
                        broadcast("Server: Goodbye " + username);
                        break; // Exit loop to disconnect client
                    } else if (message.equalsIgnoreCase("AllUsers")) {
                        out.println(ChatServer.getActiveUsers()); // Send active users list to client
                    } else {
                        broadcast(username + ": " + message); // Broadcast message to all users including the sender
                    }
                }

            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close(); // Close client connection
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientHandlers) {
                    clientHandlers.remove(this); // Remove client from active list
                }
                System.out.println(username + " has left the chat.");
            }
        }

        // Method to send a message to this client
        public void sendMessage(String message) {
            out.println(message);
        }

        // Getter method for retrieving the username
        public String getUsername() {
            return username;
        }
    }
}
