package no.ntnu;

import java.io.*;
import java.net.*;

/**
 * An example UDP client - shown in the lecture (a bit cleaned code).
 * Sends a given string message to the server, then waits for the server's
 * response and prints the response to the
 * console. Then exits.
 */
class UdpClient {
    // The address of the server. It can be either a domain name, or an IP address
    // `localhost` is a special domain name meaning "this same machine"
    private static final String SERVER_ADDRESS = "localhost";

    /**
     * Starts the client, according to the protocol described above.
     * 
     * @param messageToSend The message to send to the server
     */
    public void run(String messageToSend) {
        String exampleSentence = this.getDataFromServer(messageToSend);
        String identifiedSentence = this.identify(exampleSentence);
        System.out.println(getDataFromServer(identifiedSentence));
    }

    public String getDataFromServer(String messageToSend) {
        try {
            // Send a datagram with the message to the server
            byte[] dataToSend = messageToSend.getBytes();
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket sendPacket = new DatagramPacket(dataToSend, dataToSend.length, serverAddress,
                    UdpServer.SERVER_PORT);
            clientSocket.send(sendPacket);

            // Wait for a response from the server
            byte[] responseDataBuffer = new byte[1024]; // Reserve a bit more space than one would normally need
            DatagramPacket receivePacket = new DatagramPacket(responseDataBuffer, responseDataBuffer.length);
            clientSocket.receive(receivePacket);
            String outputData = new String(receivePacket.getData(), 0, receivePacket.getLength());

            // Release all the resources allocated for the socket - the conversation is done
            // Note: there is no "real closing" of the socket at the networking level,
            // because no connection was
            // ever established. The .close() is more for releasing the memory which is not
            // needed anymore
            clientSocket.close();

            return outputData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String identify(String sentence) {
        return findType(sentence) + " " + countWords(sentence);
    }

    public String findType(String sentence) {
        if (sentence.substring(sentence.length() - 1).equals(".")) {
            return "statement";
        }
        if (sentence.substring(sentence.length() - 1).equals("?")) {
            return "question";
        }
        throw new IllegalArgumentException("Could not identify the type of sentence.");
    }

    public String countWords(String sentence) {
        String trim = sentence.trim();
        if (trim.isEmpty()) {
            return "0";
        }
        return String.valueOf(trim.split("\\s+").length);
    }
}
