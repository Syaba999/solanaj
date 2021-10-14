package org.p2p.solanaj.ws;

public class SignatureNotification {
    private final Object error;
    private final long subscriptionId;

    public SignatureNotification(Object error, long subscriptionId) {
        this.error = error;
        this.subscriptionId = subscriptionId;
    }

    public Object getError() {
        return this.error;
    }

    public long getSubscriptionId() {
        return subscriptionId;
    }
}
