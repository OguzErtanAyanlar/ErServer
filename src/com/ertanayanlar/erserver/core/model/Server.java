package com.ertanayanlar.erserver.core.model;

import com.ertanayanlar.erserver.core.ServerCallback;
import com.ertanayanlar.erserver.util.AutoCommandInitializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    public static final String VERSION = "1.0";
    public static final int CLIENT_THREADS = 10;
    private int port;
    private ServerSocket serverSocket;
    private Map<Integer, ClientTask> activeClients;
    private ServerCallback serverCallback;

    private Class<?> clientModal;

    public Server(int port) {
        this(port, false, null);
    }

    public Server(int port, boolean autoInitializeCommands) {
        this(port, autoInitializeCommands, null);
    }

    public Server(int port, boolean autoInitializeCommands, ServerCallback serverCallback) {
        this(port, autoInitializeCommands, serverCallback, null);
    }

    public Server(int port, boolean autoInitializeCommands, ServerCallback serverCallback, Class<?> clientModal) {
        this.port = port;
        this.serverCallback = serverCallback;
        activeClients = new HashMap<>();

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("[FATAL ERROR] Server socket creation failed ! Port may be in use !");
        }

        System.out.println("[System.OUT] Server socket created !");

        if (serverCallback != null)
            serverCallback.onServerSocketCreated(this);

        if (clientModal != null && ClientTask.class.isAssignableFrom(clientModal)) {
            this.clientModal = clientModal;
        } else {
            System.out.println("[WARNING] Custom client modal is null or does not extend to ClientTask so custom client modal class is swapped with default !");
            this.clientModal = ClientTask.class;
        }

        if (autoInitializeCommands)
            AutoCommandInitializer.initializeCommands("com.ertanayanlar.erserver.commands");

        parseLocalResponse();
    }

    private static List<String> splitResponse(String localResponse) {
        List<String> splittedResponse = new ArrayList<>();

        // Group 1: Double Quote | Group 2: Single Quote | Group 3: No Quotes (Space)
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(localResponse);

        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                splittedResponse.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                splittedResponse.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                splittedResponse.add(regexMatcher.group());
            }
        }

        return splittedResponse;
    }

    public Class<?> getClientModal() {
        return clientModal;
    }

    public void setClientModal(Class<?> clientModal) {
        this.clientModal = clientModal;
    }

    public void parseLocalResponse() {
        final Runnable listenLocalResponse = () -> {
            try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
                String localResponse;

                while ((localResponse = console.readLine()) != null) {
                    if (localResponse.isEmpty()) {
                        continue;
                    }

                    final List<String> splittedResponse = splitResponse(localResponse);
                    final String commandTrigger = splittedResponse.get(0);

                    if (Command.isValidLocalCommand(commandTrigger)) {
                        final Command command = Command.getCommandByTrigger(commandTrigger);

                        if (splittedResponse.size() == 1) { // No argument commands
                            if (command.isCommandWithArgs()) {
                                System.out.println("Wrong command usage. Command " + command.getCommandTrigger() + " requires arguments !");
                            } else {
                                command.setServer(this);
                                command.setCommandCaller(null);
                                command.setCommandArgs(null);
                                command.run();
                            }
                        } else { // Commands with arguments
                            if (command.isCommandWithArgs()) {
                                final String[] commandArgs = splittedResponse.subList(1, splittedResponse.size()).toArray(new String[0]);

                                if (commandArgs.length == command.getNumberOfArgs()) {
                                    command.setServer(this);
                                    command.setCommandCaller(null);
                                    command.setCommandArgs(commandArgs);
                                    command.run();
                                } else {
                                    System.out.println("Wrong command usage. Command " + command.getCommandTrigger() + " requires " + command.getNumberOfArgs() + " arguments !");
                                }
                            } else {
                                System.out.println("Wrong command usage. Command " + command.getCommandTrigger() + " is a non argument command !");
                            }
                        }
                    } else {
                        System.out.println("Invalid command: " + commandTrigger);
                    }
                }
            } catch (IOException e) {
                System.out.println("[System.OUT] IOException while parsing local response !");
            }
        };

        final Thread listenLocalResponseThread = new Thread(listenLocalResponse);
        listenLocalResponseThread.start();
    }

    public ClientTask getActiveClientById(int id) {
        return activeClients.get(id);
    }

    public void listen() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(CLIENT_THREADS);

        final Runnable listenClients = () -> {
            System.out.println("[System.OUT] Server started listening for clients");

            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept(); // This method blocks execution until a new connection comes

                    if (clientSocket.isConnected()) {
                        clientSocket.setTcpNoDelay(true);

                        ClientTask clientModelInstance;

                        try {
                            clientModelInstance = (ClientTask) clientModal.getDeclaredConstructor(Server.class, Socket.class).newInstance(this, clientSocket);
                        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                            clientModelInstance = new ClientTask(this, clientSocket);
                            System.out.println("[System.OUT] Exception while initializing custom client modal instance. Default client modal initialized!");
                        }

                        System.out.println("[System.OUT] Connection from " + clientSocket.getInetAddress().getHostAddress() + " with ID: " + clientModelInstance.getId());

                        activeClients.put(clientModelInstance.getId(), clientModelInstance);

                        if (serverCallback != null) {
                            serverCallback.onClientConnected(clientModelInstance);
                        }

                        clientProcessingPool.submit(clientModelInstance); // Creates a new thread for client's tasks that belongs to that client
                    }
                } catch (IOException e) {
                    System.out.println("[System.OUT] Exception while accepting connection !");
                }
            }
        };

        final Thread listenThread = new Thread(listenClients);
        listenThread.start();
    }

    public void parseResponse(ClientTask clientTask) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientTask.getClientSocket().getInputStream()))) {
            String response;

            while (activeClients.containsKey(clientTask.getId()) && (response = in.readLine()) != null) { // contains value ?
                if (response.isEmpty())
                    continue;

                if (serverCallback != null) {
                    try {
                        serverCallback.onResponseReceived(clientTask, response);
                    } catch (AbortParsingResponseException e) {
                        e.printStackTrace();
                        continue;
                    }
                }

                System.out.println("[System.OUT] Message from " + clientTask.getClientSocket().getInetAddress().getHostAddress() + ": " + response);

                final List<String> splittedResponse = splitResponse(response);
                final String commandTrigger = splittedResponse.get(0);

                if (!response.isEmpty() && Command.isValidServerCommand(commandTrigger)) {
                    final Command command = Command.getCommandByTrigger(commandTrigger);

                    if (splittedResponse.size() == 1) { // Command has args
                        if (command.isCommandWithArgs()) {
                            sendMessage(clientTask.getClientSocket(), "Wrong command usage. Command " + command.getCommandTrigger() + " requires arguments !");
                        } else {
                            command.setServer(this);
                            command.setCommandCaller(clientTask);
                            command.setCommandArgs(null);
                            command.run();
                        }
                    } else { // Non-arg command
                        if (command.isCommandWithArgs()) {
                            final String[] commandArgs = splittedResponse.subList(1, splittedResponse.size()).toArray(new String[0]);

                            command.setServer(this);
                            command.setCommandCaller(clientTask);
                            command.setCommandArgs(commandArgs);
                            command.run();
                        } else {
                            sendMessage(clientTask.getClientSocket(), "Wrong command usage. Command " + command.getCommandTrigger() + " is a non argument command !");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[System.OUT] Client crashed unexpectedly, so the connection lost !");
            // killClient(clientTask);
            activeClients.remove(clientTask.getId());
        }
    }

    // Sends EOF, so killConnection() method in ClientTask runs.
    public void killClient(ClientTask clientTask) throws IOException {
        serverCallback.onKillClient(clientTask);
        activeClients.remove(clientTask.getId());
        clientTask.getClientSocket().shutdownOutput(); // EOF
    }

    public void sendMessage(Socket clientSocket, String message) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.print("[System.OUT] Sending message: '" + message + "' to " + clientSocket.getInetAddress().getHostAddress() + "\n");
            out.write(message + "\r\n");
            out.flush();
        } catch (IOException e) {
            System.out.println("[System.OUT] Exception while sending message to the client !");
        }
    }

    public void sendMessageToAll(String message) {
        for (ClientTask client : getActiveClients().values()) {
            sendMessage(client.getClientSocket(), message);
        }
    }

    public void killServer() {
        try {
            for (ClientTask clientTask : activeClients.values()) {
                killClient(clientTask);
                sendMessage(clientTask.getClientSocket(), "Shutting down the server !");
            }

            serverSocket.close();
            System.exit(0); // ?
        } catch (IOException e) {
            System.out.println("[System.OUT] Exception while closing the clientSocket for output !");
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Map<Integer, ClientTask> getActiveClients() {
        return activeClients;
    }

    public void setServerCallback(ServerCallback serverCallback) {
        this.serverCallback = serverCallback;
    }
}
