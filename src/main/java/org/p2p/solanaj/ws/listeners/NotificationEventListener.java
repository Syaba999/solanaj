package org.p2p.solanaj.ws.listeners;

public interface NotificationEventListener {
    void onNotificationEvent(long subscriptionId, Object data);
}
