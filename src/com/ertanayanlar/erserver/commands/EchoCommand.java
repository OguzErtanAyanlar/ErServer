package com.ertanayanlar.erserver.commands;

import com.ertanayanlar.erserver.core.model.Command;

public class EchoCommand extends Command {
    public EchoCommand() {
        super("echo", "Sends back your message", "echo [message]", 1, true, true, true);
    }

    @Override
    public void run() {
        StringBuilder result = new StringBuilder();

        for (String commandArgument : getCommandArgs()) {
            result.append(commandArgument).append(" ");
        }

        sendMessageToCommandCaller(result.toString());
    }
}
