package com.ertanayanlar.erserver;

import com.ertanayanlar.erserver.core.model.Server;

public class ErServerRunner {
    public static void main(String[] args) {
        Server server = new Server(7235, true);
        server.listen();
    }
}
