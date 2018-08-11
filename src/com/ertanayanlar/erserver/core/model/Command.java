package com.ertanayanlar.erserver.core.model;

import java.util.Collection;
import java.util.HashMap;

public class Command {
    private static HashMap<String, Command> registeredCommands = new HashMap<>();

    private Server server;

    private String commandTrigger;
    private Runnable commandTask;
    private boolean commandWithArgs;
    private boolean isActive;
    private String description;
    private String usage;
    private boolean isLocalCommand;
    private boolean isServerCommand;
    private int numberOfArgs;

    // Will be given by server
    private ClientTask commandCaller;
    private String[] commandArgs;

    // No args
    public Command(String commandTrigger, String description, boolean commandWithArgs, boolean isActive, boolean isLocalCommand, boolean isServerCommand) {
        this(commandTrigger, description, commandTrigger, null, commandWithArgs, 0, isActive, isLocalCommand, isServerCommand, null);
    }

    // Args
    public Command(String commandTrigger, String description, String usage, int numberOfArgs, boolean isActive, boolean isLocalCommand, boolean isServerCommand) {
        this(commandTrigger, description, usage, null, numberOfArgs > 0, numberOfArgs, isActive, isLocalCommand, isServerCommand, null);
    }

    public Command(String commandTrigger, String description, String usage, Runnable commandTask, boolean commandWithArgs, int numberOfArgs, boolean isActive, boolean isLocalCommand, boolean isServerCommand, Server server) {
        this.commandTask = commandTask;
        this.description = description;
        this.commandTrigger = commandTrigger;
        this.commandWithArgs = commandWithArgs;
        this.server = server;
        this.isActive = isActive;
        this.isLocalCommand = isLocalCommand;
        this.isServerCommand = isServerCommand;
        this.usage = usage;
        this.numberOfArgs = numberOfArgs;

        registerCommand(this);
    }

    public static Collection<Command> getRegisteredCommands() {
        return registeredCommands.values();
    }

    public static Command getCommandByTrigger(String commandTrigger) {
        return Command.registeredCommands.get(commandTrigger);
    }

    public static boolean isACommand(String command) {
        return registeredCommands.containsKey(command);
    }

    public static boolean isValidServerCommand(String command) {
        return registeredCommands.containsKey(command) && getCommandByTrigger(command).isServerCommand() && getCommandByTrigger(command).isActive();
    }

    public static boolean isValidLocalCommand(String command) {
        return registeredCommands.containsKey(command) && getCommandByTrigger(command).isLocalCommand() && getCommandByTrigger(command).isActive();
    }

    public static void registerCommand(Command command) {
        registeredCommands.put(command.getCommandTrigger(), command);
    }

    public static void removeCommand(String commandTrigger) {
        if (isACommand(commandTrigger)) {
            registeredCommands.remove(commandTrigger);
        }
    }

    public void sendMessageToCommandCaller(String message) {
        ClientTask currentCommandCaller = getCommandCaller();

        if (currentCommandCaller == null) { // Command called by server
            System.out.println(message);
        } else {
            getServer().sendMessage(currentCommandCaller.getClientSocket(), message);
        }
    }

    public String[] getCommandArgs() {
        return commandArgs;
    }

    public void setCommandArgs(String[] commandArgs) {
        this.commandArgs = commandArgs;
    }

    public Runnable getCommandTask() {
        return commandTask;
    }

    public void setCommandTask(Runnable commandFunction) {
        this.commandTask = commandFunction;
    }

    public void run() {
        commandTask.run();
        setCommandCaller(null);
        setCommandArgs(null);
    }

    public ClientTask getCommandCaller() {
        return commandCaller;
    }

    public void setCommandCaller(ClientTask commandCaller) {
        this.commandCaller = commandCaller;
    }

    public String getCommandTrigger() { // may not affect changes on the registeredCommands hashmap
        return commandTrigger;
    }

    public void setCommandTrigger(String commandTrigger) {
        this.commandTrigger = commandTrigger;
    }

    public boolean isCommandWithArgs() {
        return commandWithArgs;
    }

    public void setCommandWithArgs(boolean commandWithArgs) {
        this.commandWithArgs = commandWithArgs;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public boolean isLocalCommand() {
        return isLocalCommand;
    }

    public void setLocalCommand(boolean localCommand) {
        isLocalCommand = localCommand;
    }

    public boolean isServerCommand() {
        return isServerCommand;
    }

    public void setServerCommand(boolean serverCommand) {
        isServerCommand = serverCommand;
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }

    public void setNumberOfArgs(int numberOfArgs) {
        this.numberOfArgs = numberOfArgs;
    }
}
