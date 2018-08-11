package com.ertanayanlar.erserver.commands;

import com.ertanayanlar.erserver.core.model.ClientTask;
import com.ertanayanlar.erserver.core.model.Command;

public class ClientsCommand extends Command {
    public ClientsCommand() {
        super("clients", "Gets the connected clients from the client", false, true, true, true);
    }

    @Override
    public void run() {
        StringBuilder activeClientInfo = new StringBuilder();

        for (ClientTask client : getServer().getActiveClients().values()) {
            activeClientInfo.append("ID: ").append(client.getId()).append(" - Client IP: ").append(client.getClientSocket().getInetAddress().getHostAddress()).append("\n");
        }

        sendMessageToCommandCaller(activeClientInfo.toString());
    }
}
