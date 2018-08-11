package com.ertanayanlar.erserver.commands;

import com.ertanayanlar.erserver.core.model.Command;

public class DisableCommandCommand extends Command {
    public DisableCommandCommand() {
        super("disableCommand", "Disables the given command.", "disableCommand [commandTrigger]", 1, true, true, true);
    }

    @Override
    public void run() {
        String commandTrigger = getCommandArgs()[0];

        Command commandToDisable = getCommandByTrigger(commandTrigger);

        if (commandToDisable != null) {
            commandToDisable.setActive(false);
            sendMessageToCommandCaller("Command " + commandTrigger + " has been disabled.");
        } else {
            sendMessageToCommandCaller("Command " + commandTrigger + " is not exists.");
        }
    }
}
