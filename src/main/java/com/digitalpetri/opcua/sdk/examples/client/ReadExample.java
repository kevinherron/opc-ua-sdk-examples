package com.digitalpetri.opcua.sdk.examples.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.digitalpetri.opcua.sdk.client.OpcUaClient;
import com.digitalpetri.opcua.stack.core.Identifiers;
import com.digitalpetri.opcua.stack.core.security.SecurityPolicy;
import com.digitalpetri.opcua.stack.core.types.builtin.DataValue;
import com.digitalpetri.opcua.stack.core.types.builtin.NodeId;
import com.digitalpetri.opcua.stack.core.types.enumerated.ServerState;
import com.digitalpetri.opcua.stack.core.types.enumerated.TimestampsToReturn;

import static com.google.common.collect.Lists.newArrayList;

public class ReadExample extends AbstractClientExample {

    public static void main(String[] args) throws Exception {
        String endpointUrl = "opc.tcp://localhost:12685/digitalpetri";
        SecurityPolicy securityPolicy = SecurityPolicy.None;

        ReadExample example = new ReadExample(endpointUrl, securityPolicy);

        example.shutdownFuture(example.client).get();
    }

    private final OpcUaClient client;

    public ReadExample(String endpointUrl, SecurityPolicy securityPolicy) throws Exception {
        client = createClient(endpointUrl, securityPolicy);

        // synchronous connect
        client.connect().get();

        // asynchronous read request
        readServerStateAndTime().thenAccept(values -> {
            DataValue v0 = values.get(0);
            DataValue v1 = values.get(1);

            logger.info("State={}", ServerState.from((Integer) v0.getValue().getValue()));
            logger.info("CurrentTime={}", v1.getValue().getValue());
        });
    }

    private CompletableFuture<List<DataValue>> readServerStateAndTime() {
        List<NodeId> nodeIds = newArrayList(
                Identifiers.Server_ServerStatus_State,
                Identifiers.Server_ServerStatus_CurrentTime);

        return client.readValues(0.0, TimestampsToReturn.Both, nodeIds);
    }

}
