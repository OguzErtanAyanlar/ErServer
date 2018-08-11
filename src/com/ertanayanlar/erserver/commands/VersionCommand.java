package com.ertanayanlar.erserver.commands;
import com.ertanayanlar.erserver.core.model.Command;
import com.ertanayanlar.erserver.core.model.Server;

public class VersionCommand extends Command {
    public VersionCommand() {
        super("version", "Prints out the server version", false, true, true, true);
    }

    @Override
    public void run() {
        sendMessageToCommandCaller("Server version: " + Server.VERSION);
    }
}
