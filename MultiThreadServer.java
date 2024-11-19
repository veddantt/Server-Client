import java.io.*;
import java.net.*;
import java.util.*;

public class MultiThreadServer {
    private static final int PORT = 4652;
    private static final HashMap<String, String> users = new HashMap<>();
    private static final HashSet<String> activeUsers = new HashSet<>();
    private static final Map<String, String> addressBook = new HashMap<>();

    private static final String ADDRESS_BOOK_FILE = "C:\\Users\\patel\\Desktop\\patel_v_singh_m_p2\\addressbook.txt";
    private static volatile boolean isRunning = true;  // Flag to control server shutdown
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        // Initialize users
        users.put("john", "john01");
        users.put("root", "rootpass");
        users.put("doe", "doe123");
        users.put("jane", "jane456");
        

        localAddressBook(); // Load address book from file

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (isRunning) {
                // Accept new client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new ChildThread(clientSocket, users, activeUsers, addressBook).start();
            }
        } catch (IOException e) {
            if (isRunning) {
                e.printStackTrace();  // Print exception only if server is supposed to be running
            }
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Server shutting down...");
            saveAddressBook();  // Save address book before shutting down
        }
    }
    
    // Load address book from a file at server startup
    private static void localAddressBook() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ADDRESS_BOOK_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
               String[] entry = line.split(",", 2);
               if (entry.length == 2) {
                   addressBook.put(entry[0], entry[1]);
               }
            }
            System.out.println("Address book loaded from file.");
        } catch (IOException e) {
            System.out.println("No address book found. Starting with an empty address book.");
        }
    }
    // Save address book to a file when the server shuts down
    private static void saveAddressBook() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ADDRESS_BOOK_FILE))) {
            for (Map.Entry<String, String> entry : addressBook.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
            System.out.println("Address book saved to file.");
        } catch (IOException e) {
            System.out.println("Error saving address book to file.");
        }
    }
    
    // This method will be called when a SHUTDOWN command is received
    public static void stopServer() {
        isRunning = false;  // Set flag to false to stop accepting new connections
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();  // Close the server socket to stop accepting new connections
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
