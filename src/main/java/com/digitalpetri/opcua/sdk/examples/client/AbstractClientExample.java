package com.digitalpetri.opcua.sdk.examples.client;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import com.digitalpetri.opcua.sdk.client.OpcUaClient;
import com.digitalpetri.opcua.sdk.client.api.config.OpcUaClientConfig;
import com.digitalpetri.opcua.sdk.examples.util.KeyStoreLoader;
import com.digitalpetri.opcua.stack.client.UaTcpStackClient;
import com.digitalpetri.opcua.stack.core.Stack;
import com.digitalpetri.opcua.stack.core.security.SecurityPolicy;
import com.digitalpetri.opcua.stack.core.types.builtin.LocalizedText;
import com.digitalpetri.opcua.stack.core.types.structured.EndpointDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.digitalpetri.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public abstract class AbstractClientExample {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final KeyStoreLoader loader = new KeyStoreLoader();

    protected OpcUaClient createClient(String endpointUrl, SecurityPolicy securityPolicy) throws Exception {
        EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints(endpointUrl).get();

        EndpointDescription endpoint = Arrays.stream(endpoints)
                .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getSecurityPolicyUri()))
                .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

        logger.info("Using endpoint: {} [{}]", endpoint.getEndpointUrl(), securityPolicy);

        loader.load();

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setApplicationName(LocalizedText.english("digitalpetri opc-ua client"))
                .setApplicationUri("urn:digitalpetri:opcua:client")
                .setCertificate(loader.getClientCertificate())
                .setKeyPair(loader.getClientKeyPair())
                .setEndpoint(endpoint)
                .setRequestTimeout(uint(60000))
                .build();

        return new OpcUaClient(config);
    }

    protected CompletableFuture<Void> shutdownFuture(OpcUaClient client) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.disconnect();
            Stack.releaseSharedResources();
            future.complete(null);
        }));

        return future;
    }

}
