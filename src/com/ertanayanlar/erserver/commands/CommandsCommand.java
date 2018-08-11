package com.ertanayanlar.erserver.commands;

import com.ertanayanlar.erserver.core.model.Command;

public class CommandsCommand extends Command {
    public CommandsCommand() {
        super("commands", "Prints the available commands", false, true, true, true);
    }

    @Override
    public void run() {
        StringBuilder registeredCommands = new StringBuilder();

        for (Command command : getRegisteredCommands()) {
            registeredCommands.append(command.getUsage()).append(" - ").append(command.getDescription()).append(" - Local Command: ").append(command.isLocalCommand()).append(" - Server Command: ").append(command.isServerCommand()).append(" - ").append("Active: ").append(command.isActive()).append("\n");
        }

        sendMessageToCommandCaller("Registered commands: ");
        sendMessageToCommandCaller(registeredCommands.toString());
    }
}
