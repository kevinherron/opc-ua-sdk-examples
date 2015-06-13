package com.digitalpetri.opcua.sdk.examples.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.digitalpetri.opcua.sdk.client.OpcUaClient;
import com.digitalpetri.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import com.digitalpetri.opcua.sdk.client.api.subscriptions.UaSubscription;
import com.digitalpetri.opcua.stack.core.AttributeId;
import com.digitalpetri.opcua.stack.core.Identifiers;
import com.digitalpetri.opcua.stack.core.security.SecurityPolicy;
import com.digitalpetri.opcua.stack.core.types.builtin.QualifiedName;
import com.digitalpetri.opcua.stack.core.types.enumerated.MonitoringMode;
import com.digitalpetri.opcua.stack.core.types.enumerated.TimestampsToReturn;
import com.digitalpetri.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import com.digitalpetri.opcua.stack.core.types.structured.MonitoringParameters;
import com.digitalpetri.opcua.stack.core.types.structured.ReadValueId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.digitalpetri.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static com.google.common.collect.Lists.newArrayList;

public class SubscriptionExample implements ClientExample {

    public static void main(String[] args) throws Exception {
        String endpointUrl = "opc.tcp://localhost:12685/digitalpetri";
        SecurityPolicy securityPolicy = SecurityPolicy.None;

        SubscriptionExample example = new SubscriptionExample();

        new ClientExampleRunner(endpointUrl, securityPolicy, example).run();
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        // synchronous connect
        client.connect().get();

        // create a subscription and a monitored item
        UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000.0).get();

        ReadValueId readValueId = new ReadValueId(
                Identifiers.Server_ServerStatus_CurrentTime,
                AttributeId.VALUE.uid(), null, QualifiedName.NULL_VALUE);

        MonitoringParameters parameters = new MonitoringParameters(
                uint(1),    // client handle
                1000.0,     // sampling interval
                null,       // no (default) filter
                uint(10),   // queue size
                true);      // discard oldest

        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(
                readValueId, MonitoringMode.Reporting, parameters);

        List<UaMonitoredItem> items = subscription
                .createMonitoredItems(TimestampsToReturn.Both, newArrayList(request)).get();

        // do something with the value updates
        UaMonitoredItem item = items.get(0);

        item.setValueConsumer(v -> {
            logger.info("value received: {}", v.getValue());

            future.complete(client);
        });
    }

}
