package com.ertanayanlar.erserver.core;

import com.ertanayanlar.erserver.core.model.AbortParsingResponseException;
import com.ertanayanlar.erserver.core.model.ClientTask;
import com.ertanayanlar.erserver.core.model.Server;

public interface ServerCallback {
    void onServerSocketCreated(Server server);
    void onClientConnected(ClientTask clientTask);
    void onResponseReceived(ClientTask clientTask, String response) throws AbortParsingResponseException;
    void onKillClient(ClientTask clientTask);
}
