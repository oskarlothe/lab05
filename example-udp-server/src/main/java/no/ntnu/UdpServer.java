package no.ntnu;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Random;

/**
 * An example UDP server. The example from the lecture slides, a bit cleaned.
 * Waits for an incoming UDP datagram with a text message. Transforms that
 * message to uppercase and
 * sends it back to the client. Then waits for the next client datagram (from
 * the same or another client), etc.
 */
public class UdpServer {
    public final static int SERVER_PORT = 9876;
    private String[] sentence = new String[] { "What is your name?", "The earth is round.", "What is 5 plus 5 minus 2?",
            "I am hungry." };
    private String[] type = new String[] { "question 4", "statement 4", "question 7", "statement 3" };
    private HashMap<String, String> portAndIndex = new HashMap<String, String>();

    /**
     * Starts the UDP server, never returns (goes into a never-ending loop)
     */
    public void run() {
        System.out.println("Starting UDP server on port " + SERVER_PORT);
        byte[] receiveData = new byte[1024];
        DatagramSocket serverSocket;
        try {
            serverSocket = new DatagramSocket(SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Socket error, shutting down the server: " + e.getMessage());
            return;
        }
        while (true) {
            try {
                // Receive a datagram from a client (any client)
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                System.out.println("Received UDP packet from " + clientAddress.getHostAddress() + ", port " + port);

                byte[] sendData;
                if (sentence.equalsIgnoreCase("task")) {
                    Random ran = new Random();
                    int randomIndex = ran.nextInt(this.sentence.length);
                    String randomSentence = this.sentence[randomIndex];
                    sendData = randomSentence.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(sendData, sendData.length, clientAddress, port);
                    serverSocket.send(responsePacket);
                    this.portAndIndex.put(String.valueOf(clientAddress), String.valueOf(randomIndex));
                } else {
                    int clientIndex = Integer.parseInt(this.portAndIndex.get(String.valueOf(clientAddress)));
                    System.out.println(clientIndex);
                    this.portAndIndex.remove(String.valueOf(clientAddress));
                    if (sentence.equalsIgnoreCase(this.type[clientIndex])) {
                        sendData = ("ok").getBytes();
                    } else {
                        sendData = ("error").getBytes();
                    }
                    DatagramPacket responsePacket = new DatagramPacket(sendData, sendData.length, clientAddress,
                            port);
                    serverSocket.send(responsePacket);
                }

            } catch (IOException e) {
                System.out.println("Socket error for the client: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Returns a random element from the sentence array.
     * 
     * @return random element from the sentence array
     */
    public String getRandomSentence() {
        Random ran = new Random();
        int randomIndex = ran.nextInt(sentence.length);
        return sentence[randomIndex];
    }
}
