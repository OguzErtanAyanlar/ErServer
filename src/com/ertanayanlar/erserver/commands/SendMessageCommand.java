package com.ertanayanlar.erserver.commands;

import com.ertanayanlar.erserver.core.model.Command;

public class SendMessageCommand extends Command {
    public SendMessageCommand() {
        super("sendMessage", "Sends message to client with the given id", "sendMessage [id] [message]", 2, true, true, false);
    }

    @Override
    public void run() {
        int clientId;
        String message = getCommandArgs()[1];

        try {
            clientId = Integer.parseInt(getCommandArgs()[0]);
        } catch (NumberFormatException e) {
            System.out.println("[System.OUT] Non-integer input on sendMessage command !");
            return;
        }

        if (getServer().getActiveClientById(clientId) != null) {
            getServer().sendMessage(getServer().getActiveClients().get(clientId).getClientSocket(), message);
        } else {
            System.out.println("Client with id: " + clientId + " does not exists !");
        }
    }
}
