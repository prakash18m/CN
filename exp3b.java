import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatApp {

    private static Set<ClientHandler> clientHandlers = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  To start server: java ChatApp server <port>");
            System.out.println("  To start client: java ChatApp client <server-ip> <port>");
            return;
        }

        String mode = args[0];
        if ("server".equalsIgnoreCase(mode)) {
            if (args.length != 2) {
                System.out.println("Usage: java ChatApp server <port>");
                return;
            }
            int port = Integer.parseInt(args[1]);
            startServer(port);
        } else if ("client".equalsIgnoreCase(mode)) {
            if (args.length != 3) {
                System.out.println("Usage: java ChatApp client <server-ip> <port>");
                return;
            }
            String serverIp = args[1];
            int port = Integer.parseInt(args[2]);
            startClient(serverIp, port);
        } else {
            System.out.println("Unknown mode. Use 'server' or 'client'.");
        }
    }

    // Server code
    private static void startServer(int port) throws IOException {
        System.out.println("Chat server started on port " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        }
    }

    private static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    private static void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
        System.out.println("Client disconnected: " + client.name);
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String name = "Unknown";

        ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void sendMessage(String message) {
            out.println(message);
        }

        public void run() {
            try {
                out.println("Enter your name:");
                name = in.readLine();
                System.out.println(name + " joined the chat.");
                broadcast(name + " has joined the chat.", this);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(name + ": " + message);
                    broadcast(name + ": " + message, this);
                    if ("bye".equalsIgnoreCase(message.trim())) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection error with client " + name);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                removeClient(this);
                broadcast(name + " has left the chat.", this);
            }
        }
    }

    // Client code
    private static void startClient(String serverIp, int port) {
        try (
            Socket socket = new Socket(serverIp, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in)
        ) {
            Thread listener = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            listener.start();

            while (true) {
                String userInput = scanner.nextLine();
                out.println(userInput);
                if ("bye".equalsIgnoreCase(userInput.trim())) {
                    break;
                }
            }
            listener.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
