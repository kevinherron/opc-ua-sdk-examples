package com.digitalpetri.opcua.sdk.examples.client;

import java.util.concurrent.CompletableFuture;

import com.digitalpetri.opcua.sdk.client.OpcUaClient;
import com.digitalpetri.opcua.sdk.client.api.nodes.attached.UaVariableNode;
import com.digitalpetri.opcua.stack.core.Identifiers;
import com.digitalpetri.opcua.stack.core.security.SecurityPolicy;
import com.digitalpetri.opcua.stack.core.types.builtin.DataValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadNodeExample implements ClientExample {

    public static void main(String[] args) throws Exception {
        String endpointUrl = "opc.tcp://localhost:12685/digitalpetri";
        SecurityPolicy securityPolicy = SecurityPolicy.None;

        ReadNodeExample example = new ReadNodeExample();

        new ClientExampleRunner(endpointUrl, securityPolicy, example).run();
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        // synchronous connect
        client.connect().get();

        // read the value of the current time node
        UaVariableNode currentTimeNode = client.getAddressSpace()
                .getVariableNode(Identifiers.Server_ServerStatus_CurrentTime);

        DataValue value = currentTimeNode.readValue().get();

        logger.info("currentTime value={}", value);

        future.complete(client);
    }

}
