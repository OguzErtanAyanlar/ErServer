package com.ertanayanlar.erserver.commands;

import com.ertanayanlar.erserver.core.model.Command;

import java.io.IOException;

public class DisconnectCommand extends Command {
    public DisconnectCommand() {
        super("disconnect","Disconnects the client from the server", false, true, false, true);
    }

    @Override
    public void run() {
        try {
            getServer().killClient(getCommandCaller());
        } catch (IOException e) {
            System.out.println("[System.OUT] Exception on disconnect command !");
        }
    }
}
