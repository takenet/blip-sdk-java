package net.take.blip;

import org.limeprotocol.Node;
import org.limeprotocol.Session;

public class ChannelInformation {

    private final String sessionId;
    private final Session.SessionState sessionState;
    private final Node localNode;
    private final Node remoteNode;

    public ChannelInformation(String sessionId, Session.SessionState sessionState, Node localNode, Node remoteNode) {
        this.sessionId = sessionId;
        this.sessionState = sessionState;
        this.localNode = localNode;
        this.remoteNode = remoteNode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Session.SessionState getSessionState() {
        return sessionState;
    }

    public Node getLocalNode() {
        return localNode;
    }

    public Node getRemoteNode() {
        return remoteNode;
    }
}
