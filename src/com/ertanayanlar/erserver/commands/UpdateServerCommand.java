package com.ertanayanlar.erserver.commands;

import com.ertanayanlar.erserver.core.model.Command;
import com.ertanayanlar.erserver.util.UtilityFunctions;

public class UpdateServerCommand extends Command {
    public UpdateServerCommand() {
        super("updateServer", "Updates the server", "updateServer [url]", 1, true, true, true);
    }

    @Override
    public void run() {
        String url = getCommandArgs()[0];

        getServer().sendMessageToAll("Updating the server...");

        if (UtilityFunctions.updateServer(url)) {
            getServer().sendMessageToAll("Server has been updated, restarting the server !");
            getServer().killServer();
        } else {
            getServer().sendMessage(getCommandCaller().getClientSocket(), "Server update failed !");
        }
    }
}
