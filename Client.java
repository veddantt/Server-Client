import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 4652;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the server.");

            // Start a thread to listen for server responses
            startServerListener(serverInput);

            // Read commands from the terminal and send them to the server
            String command;
            while ((command = consoleInput.readLine()) != null) {
                serverOutput.println(command.trim()); 
            }

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    private static void startServerListener(BufferedReader serverInput) {
        new Thread(() -> {
            String response;
            try {
                while ((response = serverInput.readLine()) != null) {
                    System.out.println("Server: " + response);
                    if (response.contains("210 The server is shutting down.")) {
                        System.out.println("Disconnected from server.");
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Connection to server lost.");
            }
        }).start();
    }
}
