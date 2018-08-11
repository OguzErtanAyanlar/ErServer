package com.ertanayanlar.erserver.commands;
import com.ertanayanlar.erserver.core.model.Command;

public class ShutdownServerCommand extends Command {
    public ShutdownServerCommand() {
        super("shutdownServer", "Shutdowns the server", false, true, true, true);
    }

    @Override
    public void run() {
        getServer().killServer();
    }
}
