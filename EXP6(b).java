import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try {
            // Create a server socket on port 3000
            ServerSocket serverSocket = new ServerSocket(3000);
            System.out.println("Server started, waiting for client...");

            // Accept the incoming client connection
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            // BufferedReader to read from client input stream
            BufferedReader din = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // DataOutputStream to write to client output stream
            DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());

            // Define IP and MAC address pairs
            String[] ip = {"165.165.80.80", "165.165.79.1"};
            String[] mac = {"6A:08:AA:C2", "8A:BC:E3:FA"};

            while (true) {
                // Read the MAC address from the client
                String str = din.readLine();
                if (str == null || str.equalsIgnoreCase("exit")) {
                    break;  // Exit the loop if input is null or "exit"
                }

                boolean found = false;

                // Look for the corresponding IP address for the given MAC
                for (int i = 0; i < mac.length; i++) {
                    if (str.equalsIgnoreCase(mac[i])) {
                        dout.writeBytes(ip[i] + "\n");
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    dout.writeBytes("IP not found\n");
                }
            }

            // Close the connection
            clientSocket.close();
            serverSocket.close();

        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            // BufferedReader for user input
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Create a socket to connect to the server
            Socket socket = new Socket("127.0.0.1", 3000);

            // BufferedReader to read from the server's input stream
            BufferedReader din = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // DataOutputStream to write to the server's output stream
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

            // Prompt user to enter the MAC address
            System.out.print("Enter the Physical Address (MAC): ");
            String macAddress = userInput.readLine();

            // Send the MAC address to the server
            dout.writeBytes(macAddress + "\n");

            // Read the server's response
            String response = din.readLine();
            System.out.println("The Logical Address (IP) is: " + response);

            // Close the connection
            socket.close();

        } catch (Exception e) {
            System.out.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}