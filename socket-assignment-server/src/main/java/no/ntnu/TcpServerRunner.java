package no.ntnu;

import no.ntnu.tcp.TcpTaskServer;

/**
 * Starts the TCP task-server
 */
public class TcpServerRunner {
    public static void main(String[] args) {
        TcpTaskServer server = new TcpTaskServer();
        server.runIndefinitely();
    }
}