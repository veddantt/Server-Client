import java.io.*;
import java.net.*;
import java.util.*;

public class ChildThread extends Thread {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String currentUser = null;
    private HashMap<String, String> users;
    private HashSet<String> activeUsers;
    private Map<String, String> addressBook;

    public ChildThread(Socket socket, HashMap<String, String> users, HashSet<String> activeUsers, Map<String, String> addressBook) {
        this.socket = socket;
        this.users = users;
        this.activeUsers = activeUsers;
        this.addressBook = addressBook;
    }

    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            String message;

            while ((message = input.readLine()) != null) {
                String[] command = message.split(" ");
                switch (command[0].toUpperCase()) {
                    case "LOGIN":
                        handleLogin(command);
                        break;
                    case "LOGOUT":
                        handleLogout();
                        break;
                    case "WHO":
                        handleWho();
                        break;
                    case "LOOK":
                        handleLook(command);
                        break;
                    case "UPDATE":
                        handleUpdate(command);
                        break;
                    case "ADD":
                        handleAdd(command);
                        break;
                    case "DELETE":
                        handleDelete(command);
                        break;
                    case "LIST":
                        handleList();
                        break;
                    case "SHUTDOWN":
                        handleShutdown();
                        break;
                    case "QUIT":
                        handleQuit();
                        break;
                    default:
                        output.println("400 Unknown command");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                System.out.println("Client disconnected: " + socket.getInetAddress());
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private void handleLogin(String[] command) {
        System.out.println("Processing LOGIN command");
        if (command.length != 3) {
            output.println("400 Invalid LOGIN command");
            return;
        }
        String userID = command[1];
        String password = command[2];
        if (users.containsKey(userID) && users.get(userID).equals(password)) {
            currentUser = userID;
            activeUsers.add(userID);
            output.println("200 OK");
            System.out.println("User logged in: " + userID);  // Log successful login
        } else {
            output.println("410 Wrong UserID or Password");
            System.out.println("Failed login attempt for user: " + userID);  // Log failed login
        }
    }

    private void handleLogout() {
        System.out.println("Processing LOGOUT command");
        if (currentUser != null) {
            activeUsers.remove(currentUser);
            System.out.println("User logged out: " + currentUser);  // Log logout
            currentUser = null;
            output.println("200 OK");
        } else {
            output.println("401 You are not logged in");
        }
    }

    private void handleWho() {
        System.out.println("Processing WHO command");  // Debug print
        StringBuilder response = new StringBuilder("200 OK\nThe list of active users:");
        for (String user : activeUsers) {
            response.append("\n").append(user);
        }
        output.println(response);
    }

    private void handleLook(String[] command) {
        System.out.println("Processing LOOK command");
        if (command.length < 2) {
            output.println("400 Invalid LOOK command");
            return;
        }
        
        StringBuilder keywordBuilder = new StringBuilder();
        for (int i = 1; i < command.length; i++) {
            keywordBuilder.append(command[i]).append(" ");
        }
        String keyword = keywordBuilder.toString().trim();

        
        StringBuilder response = new StringBuilder("200 OK\nSearch results:");
        boolean found = false;
        
        for (Map.Entry<String, String> entry : addressBook.entrySet()) {
            if (entry.getKey().contains(keyword) || entry.getValue().contains(keyword)) {
                response.append("\n").append(entry.getKey()).append(": ").append(entry.getValue());
                found = true;
            }
        }

        if (!found) {
            response.append("\nNo matching records found.");
        }

        output.println(response.toString());
    }

    private void handleUpdate(String[] command) {
        System.out.println("Processing UPDATE command");
        if (command.length < 4) {
            output.println("400 Invalid UPDATE command");
            return;
        }
        String recordID = command[1];
        StringBuilder newValue = new StringBuilder();
        for (int i = 2; i < command.length; i++) {
            newValue.append(command[i]).append(" ");
        }

        if (addressBook.containsKey(recordID)) {
            addressBook.put(recordID, newValue.toString().trim());
            output.println("200 OK Record updated: " + recordID);
            System.out.println("Updated record: " + recordID);  // Log update
        } else {
            output.println("403 The Record ID does not exist.");
        }
    }
    

    private void handleAdd(String[] command) {
        System.out.println("Processing ADD command");  // Debug print
        if (currentUser == null) {
            output.println("401 You are not currently logged in, login first");
            return;
        }
        if (command.length < 3) {
            output.println("400 Invalid ADD command");
            return;
        }

        StringBuilder value = new StringBuilder();
        for (int i = 1; i < command.length; i++) {
            value.append(command[i]).append(" ");
        }

        String recordID = String.valueOf(1000 + addressBook.size() + 1); // Simple numeric ID without "ID" prefix
        addressBook.put(recordID, value.toString().trim());
        output.println("200 OK Record added: " + recordID);
        System.out.println("Added record: " + recordID + " " + value.toString().trim());  // Log addition
    }


    private void handleDelete(String[] command) {
        System.out.println("Processing DELETE command");  // Debug print
        if (currentUser == null) {
            output.println("401 You are not currently logged in, login first");
            return;
        }
        if (command.length != 2) {
            output.println("400 Invalid DELETE command");
            return;
        }

        String recordID = command[1];
        if (addressBook.containsKey(recordID)) {
            addressBook.remove(recordID);
            output.println("200 OK Record deleted: " + recordID);
            System.out.println("Deleted record: " + recordID);  // Log deletion
        } else {
            output.println("404 Record not found: " + recordID);
        }
    }

    private void handleList() {
        System.out.println("Processing LIST command");  // Debug print
        StringBuilder response = new StringBuilder("200 OK\nAddress book:");
        if (addressBook.isEmpty()) {
            response.append("\nNo records found.");
        } else {
            for (Map.Entry<String, String> entry : addressBook.entrySet()) {
                response.append("\n").append(entry.getKey()).append(": ").append(entry.getValue());
            }
        }
        output.println(response.toString());
    }

    private void handleShutdown() {
        System.out.println("Processing SHUTDOWN command");  // Debug print
        if ("root".equals(currentUser)) {
            output.println("200 OK\n210 the server is about to shutdown...");
            System.exit(0); // Shutting down the server
        } else {
            output.println("402 User not allowed to execute this command");
        }
    }

    private void handleQuit() {
        System.out.println("Processing QUIT command");  // Debug print
        if (currentUser != null) {
            activeUsers.remove(currentUser);
            System.out.println("User quit: " + currentUser);  // Log user quit
            currentUser = null;
        }
        output.println("200 OK");
    }
}