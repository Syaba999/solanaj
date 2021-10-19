package org.p2p.solanaj.ws.listeners;

public interface NotificationEventListener {
    void onNotifiacationEvent(long subscriptionId, Object data);
}
