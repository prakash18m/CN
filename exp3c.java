import java.io.*;
import java.net.*;

public class FileTransferApp {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  To start server: java FileTransferApp server <port>");
            System.out.println("  To start client: java FileTransferApp client <server-ip> <port> <file-path>");
            return;
        }

        String mode = args[0];

        try {
            if ("server".equalsIgnoreCase(mode)) {
                if (args.length != 2) {
                    System.out.println("Usage: java FileTransferApp server <port>");
                    return;
                }
                int port = Integer.parseInt(args[1]);
                startServer(port);

            } else if ("client".equalsIgnoreCase(mode)) {
                if (args.length != 4) {
                    System.out.println("Usage: java FileTransferApp client <server-ip> <port> <file-path>");
                    return;
                }
                String serverIp = args[1];
                int port = Integer.parseInt(args[2]);
                String filePath = args[3];
                startClient(serverIp, port, filePath);

            } else {
                System.out.println("Unknown mode. Use 'server' or 'client'.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Port must be a number.");
        }
    }

    private static void startServer(int port) {
        System.out.println("File Server started on port " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("Waiting for client...");
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Client connected: " + socket.getInetAddress());

                    DataInputStream dis = new DataInputStream(socket.getInputStream());

                    // Read filename length and filename
                    int fileNameLength = dis.readInt();
                    if (fileNameLength > 0) {
                        byte[] fileNameBytes = new byte[fileNameLength];
                        dis.readFully(fileNameBytes, 0, fileNameLength);
                        String fileName = new String(fileNameBytes);

                        // Read file size
                        long fileSize = dis.readLong();
                        System.out.println("Receiving file: " + fileName + " (" + fileSize + " bytes)");

                        // Save file
                        try (FileOutputStream fos = new FileOutputStream("received_" + fileName)) {
                            byte[] buffer = new byte[4096];
                            long totalRead = 0;
                            int read;
                            while (totalRead < fileSize && (read = dis.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalRead))) != -1) {
                                fos.write(buffer, 0, read);
                                totalRead += read;
                            }
                            System.out.println("File received successfully.");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startClient(String serverIp, int port, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        try (Socket socket = new Socket(serverIp, port);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream(file)) {

            // Send filename length and filename bytes
            byte[] fileNameBytes = file.getName().getBytes();
            dos.writeInt(fileNameBytes.length);
            dos.write(fileNameBytes);

            // Send file size
            dos.writeLong(file.length());

            // Send file content
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, read);
            }
            dos.flush();
            System.out.println("File sent successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

