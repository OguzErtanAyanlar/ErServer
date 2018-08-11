package com.ertanayanlar.erserver.core.model;

import java.net.Socket;

// Connected client model
public class ClientTask implements Runnable {
    private final Server connectedServer;
    private final Socket clientSocket;
    private static int numberOfClients = 0;
    private int id;

    public ClientTask(Server connectedServer, Socket clientSocket) {
        this.connectedServer = connectedServer;
        this.clientSocket = clientSocket;
        this.id = numberOfClients++;
    }

    public ClientTask(Server connectedServer, Socket clientSocket, int id) {
        this.connectedServer = connectedServer;
        this.clientSocket = clientSocket;
        this.id = id;
        numberOfClients++;
    }

    @Override
    public void run() {
        connectedServer.parseResponse(this);

        // If we got here, we got EOF from server
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
