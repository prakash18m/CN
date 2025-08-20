import java.io.*;
import java.net.*;

class Server {
    public static void main(String args[]) {
        try {
            ServerSocket serverSocket = new ServerSocket(3636);
            System.out.println("Server is running...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            BufferedReader din = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());

            String[] ip = {"165.165.80.80", "165.165.79.1"};
            String[] mac = {"6A:08:AA:C2", "8A:BC:E3:FA"};

            while (true) {
                String str = din.readLine();
                if (str == null) {
                    break;  // Client disconnected
                }

                boolean found = false;
                for (int i = 0; i < ip.length; i++) {
                    if (str.equals(ip[i])) {
                        dout.writeBytes(mac[i] + '\n');
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    dout.writeBytes("MAC not found\n");
                }
            }

            clientSocket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import java.io.*;
import java.net.*;

class Client {
    public static void main(String args[]) {
        try {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            Socket socket = new Socket("127.0.0.1", 3636);

            BufferedReader din = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

            System.out.print("Enter the Logical address (IP): ");
            String ipQuery = userInput.readLine();

            dout.writeBytes(ipQuery + '\n');

            String response = din.readLine();
            System.out.println("The Physical Address is: " + response);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}