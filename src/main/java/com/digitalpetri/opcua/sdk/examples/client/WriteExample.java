package com.digitalpetri.opcua.sdk.examples.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.digitalpetri.opcua.sdk.client.OpcUaClient;
import com.digitalpetri.opcua.stack.core.security.SecurityPolicy;
import com.digitalpetri.opcua.stack.core.types.builtin.DataValue;
import com.digitalpetri.opcua.stack.core.types.builtin.NodeId;
import com.digitalpetri.opcua.stack.core.types.builtin.StatusCode;
import com.digitalpetri.opcua.stack.core.types.builtin.Variant;

import static com.google.common.collect.Lists.newArrayList;

public class WriteExample extends AbstractClientExample {

    public static void main(String[] args) throws Exception {
        String endpointUrl = "opc.tcp://localhost:12685/digitalpetri";
        SecurityPolicy securityPolicy = SecurityPolicy.None;

        WriteExample example = new WriteExample(endpointUrl, securityPolicy);

        example.shutdownFuture(example.client).get();
    }

    private final OpcUaClient client;

    public WriteExample(String endpointUrl, SecurityPolicy securityPolicy) throws Exception {
        client = createClient(endpointUrl, securityPolicy);

        // synchronous connect
        client.connect().get();

        List<NodeId> nodeIds = newArrayList(
                new NodeId(2, "/Static/AllProfiles/Scalar/Int32"));

        for (int i = 0; i < 10; i++) {
            Variant v = new Variant(i);

            // write asynchronously....
            CompletableFuture<List<StatusCode>> future =
                    client.writeValues(nodeIds, newArrayList(new DataValue(v)));

            // ...but block for the results so we write in order
            List<StatusCode> statusCodes = future.get();
            StatusCode status = statusCodes.get(0);

            if (status.isGood()) {
                logger.info("Wrote '{}' to nodeId={}", v, nodeIds.get(0));
            }
        }
    }

}
