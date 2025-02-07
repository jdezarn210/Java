import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ChatClientGUI {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String username;

    private JFrame frame;
    private JTextArea chatArea; // displays chat messages recieved from the server
    private JTextField messageField; // allows the user to type messages before sending them
    private JButton sendButton; // sends entered message when clicked
    private JButton connectButton; // establishes connection to the chat
    private JTextField usernameField;

    public ChatClientGUI() {
        createGUI();
    }
    // initilizes GUI components
    private void createGUI() {
        frame = new JFrame("Chat Client");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new FlowLayout());
        usernameField = new JTextField(10);
        connectButton = new JButton("Connect");
        
        topPanel.add(new JLabel("Username: "));
        topPanel.add(usernameField);
        topPanel.add(connectButton);
        frame.add(topPanel, BorderLayout.NORTH);

        frame.setVisible(true);

        connectButton.addActionListener(e -> connectToServer());
        sendButton.addActionListener(e -> sendMessage());
    }
    // establishes connection to the server and initializes the input and output streams
    private void connectToServer() {
        try {
            String serverIP = JOptionPane.showInputDialog(frame, "Enter Server IP:", "Connect", JOptionPane.QUESTION_MESSAGE);
            int serverPort = 8989;
            socket = new Socket(serverIP, serverPort);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            
            username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            output.println(username);
            chatArea.append("Connected as: " + username + "\n"); //shows a message confirming the user has connected to the server

            sendButton.setEnabled(true);
            messageField.setEnabled(true);
            connectButton.setEnabled(false);
            usernameField.setEnabled(false);

            new Thread(new IncomingMessageHandler()).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Could not connect to server!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // sends messages to the server and disables input if the user exits
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            output.println(message);
            messageField.setText("");
            if (message.equalsIgnoreCase("Bye")) {
                sendButton.setEnabled(false);
                messageField.setEnabled(false);
                connectButton.setEnabled(true);
                usernameField.setEnabled(true);
            }
        }
    }
    //handles incoming messages from the server and detects disconnections
    private class IncomingMessageHandler implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = input.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                chatArea.append("\n[System] Server has disconnected.\n"); //shown when server disconnects
            } finally {
                try {
                    if (socket != null) socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                chatArea.append("\n[System] Connection closed.\n");
                sendButton.setEnabled(false);
                messageField.setEnabled(false);
                connectButton.setEnabled(true);
                usernameField.setEnabled(true);
            }
        }
    }
    //initializes and starts the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClientGUI::new);
    }
}
