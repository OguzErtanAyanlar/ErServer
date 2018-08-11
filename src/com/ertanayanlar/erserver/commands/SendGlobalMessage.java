package com.ertanayanlar.erserver.commands;

import com.ertanayanlar.erserver.core.model.Command;

public class SendGlobalMessage extends Command {
    public SendGlobalMessage() {
        super("sendGlobalMessage", "Sends message to all active clients", "sendGlobalMessage [message]", 1, true, true, false);
    }

    @Override
    public void run() {
        String message = getCommandArgs()[0];

        getServer().sendMessageToAll(message);
    }
}
