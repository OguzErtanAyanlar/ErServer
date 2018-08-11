package com.ertanayanlar.erserver.commands;

import com.ertanayanlar.erserver.core.model.Command;

public class PingCommand extends Command {
    public PingCommand() {
        super("ping", "Sends back pong ! as a message", false, true, true, true);
    }

    @Override
    public void run() {
        sendMessageToCommandCaller("PONG !");
    }
}
