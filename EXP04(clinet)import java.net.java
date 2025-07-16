import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNSClient {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java DNSClient <server-ip> <domain-name>");
            return;
        }

        String serverIp = args[0];
        String domainName = args[1];
        int serverPort = 5353;

        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(serverIp);

            byte[] sendBuffer = domainName.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);

            byte[] receiveBuffer = new byte[512];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            clientSocket.receive(receivePacket);

            String ipAddress = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Resolved IP for " + domainName + ": " + ipAddress);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
