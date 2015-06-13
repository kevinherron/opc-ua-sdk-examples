package com.digitalpetri.opcua.sdk.examples.client;

import java.util.concurrent.CompletableFuture;

import com.digitalpetri.opcua.sdk.client.OpcUaClient;

public interface ClientExample {

    void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception;

}
