import java.io.*;
import java.net.*;
import java.util.Scanner;

public class EchoApp {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  To start server: java EchoApp server <port>");
            System.out.println("  To start client: java EchoApp client <server-ip> <port>");
            return;
        }

        String mode = args[0];

        if ("server".equalsIgnoreCase(mode)) {
            if (args.length != 2) {
                System.out.println("Usage: java EchoApp server <port>");
                return;
            }
            int port = Integer.parseInt(args[1]);
            startServer(port);

        } else if ("client".equalsIgnoreCase(mode)) {
            if (args.length != 3) {
                System.out.println("Usage: java EchoApp client <server-ip> <port>");
                return;
            }
            String serverIp = args[1];
            int port = Integer.parseInt(args[2]);
            startClient(serverIp, port);

        } else {
            System.out.println("Unknown mode: " + mode);
            System.out.println("Use 'server' or 'client'");
        }
    }

    private static void startServer(int port) {
        System.out.println("Starting Echo Server on port " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle client in a separate thread to allow multiple clients
                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);
                out.println("Echo: " + inputLine);
                if ("bye".equalsIgnoreCase(inputLine.trim())) {
                    System.out.println("Client requested disconnect.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Client connection error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Closed connection with client.");
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private static void startClient(String serverIp, int port) {
        System.out.println("Connecting to Echo Server at " + serverIp + ":" + port);
        try (
            Socket socket = new Socket(serverIp, port);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected. Type messages ('bye' to quit):");
            while (true) {
                System.out.print("> ");
                String message = scanner.nextLine();
                out.println(message);

                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server closed the connection.");
                    break;
                }
                System.out.println("Server replied: " + response);

                if ("bye".equalsIgnoreCase(message.trim())) {
                    System.out.println("Closing client.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
