package net.take.blip;

import org.limeprotocol.Node;
import org.limeprotocol.Session;

public class FailedChannelInformation extends ChannelInformation {

    private final boolean isConnected;
    private final Exception exception;
    private final String operationName;

    public FailedChannelInformation(String sessionId, Session.SessionState sessionState, Node localNode, Node remoteNode, boolean isConnected, Exception exception, String operationName) {
        super(sessionId, sessionState, localNode, remoteNode);
        this.isConnected = isConnected;
        this.exception = exception;
        this.operationName = operationName;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public Exception getException() {
        return exception;
    }

    public String getOperationName() {
        return operationName;
    }
}
