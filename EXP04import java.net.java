import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNSServer {

    public static void main(String[] args) {
        int port = 5353; 

        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            System.out.println("DNS Server started on port " + port);

            byte[] receiveBuffer = new byte[512];
            byte[] sendBuffer;

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);

                String domainName = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                System.out.println("Received query for domain: " + domainName);

      
                String ipAddress = resolveDomain(domainName);

                sendBuffer = ipAddress.getBytes();

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);

                System.out.println("Sent IP " + ipAddress + " to " + clientAddress + ":" + clientPort);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    private static String resolveDomain(String domain) {
        switch (domain.toLowerCase()) {
            case "example.com":
                return "93.184.216.34";
            case "google.com":
                return "142.250.64.78";
            case "openai.com":
                return "104.20.190.5";
            default:
                return "0.0.0.0"; 
        }
    }
}
 
 