package org.p2p.solanaj.ws;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.java_websocket.AbstractWebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.p2p.solanaj.rpc.types.RpcNotificationResult;
import org.p2p.solanaj.rpc.types.RpcRequest;
import org.p2p.solanaj.rpc.types.RpcResponse;
import org.p2p.solanaj.ws.listeners.NotificationEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionWebSocketClient extends WebSocketClient {
    private final Logger log = LoggerFactory.getLogger(SubscriptionWebSocketClient.class);
    private class SubscriptionParams {
        RpcRequest request;
        NotificationEventListener listener;

        SubscriptionParams(RpcRequest request, NotificationEventListener listener) {
            this.request = request;
            this.listener = listener;
        }
    }

    private static SubscriptionWebSocketClient instance;

    private Map<String, SubscriptionParams> subscriptions = new ConcurrentHashMap<>();
    private Map<String, Long> subscriptionIds = new HashMap<>();
    private Map<Long, NotificationEventListener> subscriptionLinsteners = new ConcurrentHashMap<>();

    public static SubscriptionWebSocketClient getInstance(String endpoint, int connectionTimout) {
        URI endpointURI;
        URI serverURI;

        try {
            endpointURI = new URI(endpoint);
            serverURI = new URI(endpointURI.getScheme() == "https" ? "wss" : "ws" + "://" + endpointURI.getHost());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        if (instance == null) {
            if (connectionTimout == 0)
                instance = new SubscriptionWebSocketClient(serverURI);
            else
                instance = new SubscriptionWebSocketClient(serverURI, connectionTimout);
        }

        if (!instance.isOpen()) {
            instance.connect();
        }

        return instance;

    }

    public SubscriptionWebSocketClient(URI serverURI) {
        super(serverURI);
    }

    public SubscriptionWebSocketClient(URI serverURI, int connectionTimeout) {
        super(serverURI, new Draft_6455(), null, connectionTimeout);
    }

    public void accountSubscribe(String accountPKey, NotificationEventListener listener) {
        List<Object> params = new ArrayList<Object>();
        params.add(accountPKey);

        RpcRequest rpcRequest = new RpcRequest("accountSubscribe", params);

        subscriptions.put(rpcRequest.getId(), new SubscriptionParams(rpcRequest, listener));
        subscriptionIds.put(rpcRequest.getId(), null);

        updateSubscriptions();
    }

    public void programSubscribe(String programId, NotificationEventListener listener) {
        List<Object> params = new ArrayList<Object>();
        params.add(programId);

        RpcRequest rpcRequest = new RpcRequest("programSubscribe", params);

        subscriptions.put(rpcRequest.getId(), new SubscriptionParams(rpcRequest, listener));
        subscriptionIds.put(rpcRequest.getId(), null);

        updateSubscriptions();
    }

    public void signatureSubscribe(String signature, NotificationEventListener listener) {
        List<Object> params = new ArrayList<Object>();
        params.add(signature);

        RpcRequest rpcRequest = new RpcRequest("signatureSubscribe", params);

        subscriptions.put(rpcRequest.getId(), new SubscriptionParams(rpcRequest, listener));
        subscriptionIds.put(rpcRequest.getId(), null);

        updateSubscriptions();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        updateSubscriptions();
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public void onMessage(String message) {
        log.debug("Websockets. Got Message: " + message);
        JsonAdapter<RpcResponse<Long>> resultAdapter = new Moshi.Builder().build()
                .adapter(Types.newParameterizedType(RpcResponse.class, Long.class));

        try {
            RpcResponse<Long> rpcResult = resultAdapter.fromJson(message);
            String rpcResultId = rpcResult.getId();
            if (rpcResultId != null) {
                if (subscriptions.containsKey(rpcResultId)) {
                    subscriptionIds.put(rpcResultId, rpcResult.getResult());
                    subscriptionLinsteners.put(rpcResult.getResult(), subscriptions.get(rpcResultId).listener);
                    subscriptions.remove(rpcResultId);
                }
            } else {
                JsonAdapter<RpcNotificationResult> notificationResultAdapter = new Moshi.Builder().build()
                        .adapter(RpcNotificationResult.class);
                RpcNotificationResult result = notificationResultAdapter.fromJson(message);
                long subscriptionId = result.getParams().getSubscription();
                NotificationEventListener listener = subscriptionLinsteners.get(subscriptionId);

                Map value = (Map) result.getParams().getResult().getValue();

                switch (result.getMethod()) {
                    case "signatureNotification":
                        listener.onNotifiacationEvent(subscriptionId, new SignatureNotification(value.get("err")));
                        break;
                    case "accountNotification":
                        listener.onNotifiacationEvent(subscriptionId, value);
                        break;
                }
            }
        } catch (Exception ex) {
            log.error("Error processing message [" + message + "]", ex);
        }
    }

    @Override
    public void onCloseInitiated(int code, String reason) {
        log.trace("onCloseInitiated called. Reconnecting");
        reconnect();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.trace("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        log.error("Websocket error", ex);
        ex.printStackTrace();
    }

    private void updateSubscriptions() {
        if (isOpen() && subscriptions.size() > 0) {
            JsonAdapter<RpcRequest> rpcRequestJsonAdapter = new Moshi.Builder().build().adapter(RpcRequest.class);

            for (SubscriptionParams sub : subscriptions.values()) {
                send(rpcRequestJsonAdapter.toJson(sub.request));
            }
        }
    }

}
