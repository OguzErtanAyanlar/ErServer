package com.ertanayanlar.erserver.commands;

import com.ertanayanlar.erserver.core.model.Command;

public class RemoveCommandCommand extends Command {
    public RemoveCommandCommand() {
        super("removeCommand", "Removes the given command from registered commands.", "removeCommand [commandTrigger]", 1, true, true, true);
    }

    @Override
    public void run() {
        String commandTrigger = getCommandArgs()[0];

        removeCommand(commandTrigger);

        sendMessageToCommandCaller("Command " + commandTrigger + " has been removed from the registered commands.");
    }
}
