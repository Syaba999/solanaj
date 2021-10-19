package org.p2p.solanaj.ws;

public class SignatureNotification {
    private final Object error;

    public SignatureNotification(Object error) {
        this.error = error;
    }

    public Object getError() {
        return this.error;
    }
}
