package com.digitalpetri.opcua.sdk.examples.client;

import com.digitalpetri.opcua.sdk.client.OpcUaClient;
import com.digitalpetri.opcua.sdk.client.api.nodes.attached.UaVariableNode;
import com.digitalpetri.opcua.stack.core.Identifiers;
import com.digitalpetri.opcua.stack.core.security.SecurityPolicy;
import com.digitalpetri.opcua.stack.core.types.builtin.DataValue;

public class ReadNodeExample extends AbstractClientExample {

    public static void main(String[] args) throws Exception {
        String endpointUrl = "opc.tcp://localhost:12685/digitalpetri";
        SecurityPolicy securityPolicy = SecurityPolicy.None;

        ReadNodeExample example = new ReadNodeExample(endpointUrl, securityPolicy);

        example.shutdownFuture(example.client).get();
    }

    private final OpcUaClient client;

    public ReadNodeExample(String endpointUrl, SecurityPolicy securityPolicy) throws Exception {
        client = createClient(endpointUrl, securityPolicy);

        // synchronous connect
        client.connect().get();

        // read the value of the current time node
        UaVariableNode currentTimeNode = client.getAddressSpace()
                .getVariableNode(Identifiers.Server_ServerStatus_CurrentTime);

        DataValue value = currentTimeNode.readValue().get();

        logger.info("currentTime value={}", value);
    }

}
