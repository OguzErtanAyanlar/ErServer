package com.ertanayanlar.erserver.commands;

import com.ertanayanlar.erserver.core.model.ClientTask;
import com.ertanayanlar.erserver.core.model.Command;

import java.io.IOException;

public class KickClientCommand extends Command {
    public KickClientCommand() {
        super("kickClient", "Kicks connected client with the id", "kickClient [id]", 1, true, true, false);
    }

    @Override
    public void run() {
        int clientId;

        try {
            clientId = Integer.parseInt(getCommandArgs()[0]);
        } catch (NumberFormatException e) {
            System.out.println("[System.OUT] Non-integer input on kickClient command !");
            return;
        }

        ClientTask clientToBeKicked = getServer().getActiveClientById(clientId);

        if (clientToBeKicked != null) {
            try {
                getServer().sendMessage(clientToBeKicked.getClientSocket(), "\nYou have been kicked from the server !");
                getServer().killClient(clientToBeKicked);
            } catch (IOException e) {
                System.out.println("[System.OUT] Exception while killing the client on kickClient command !");
                return;
            }

            System.out.println("Client with id: " + clientId + " has been kicked from the server !");
        } else {
            System.out.println("Client with id: " + clientId + " does not exists !");
        }
    }
}

