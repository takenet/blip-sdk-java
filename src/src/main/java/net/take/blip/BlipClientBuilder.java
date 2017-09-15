package net.take.blip;

import org.limeprotocol.Notification;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.messaging.resources.Presence;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class BlipClientBuilder {

    private final TransportFactory transportFactory;

    private String domain;
    private String scheme;
    private String hostName;
    private int port;
    private int sendTimeout;
    private TimeUnit sendTimeoutTimeUnit;
    private int maxConnectionRetries;
    private SessionCompression compression;
    private SessionEncryption encryption;
    private Presence.RoutingRule routingRule;
    private boolean roundRobin;
    private boolean autoNotify;
    private Notification.Event[] receiptEvents;
    private String identifier;
    private String password;

    public BlipClientBuilder() {
        this(new TcpTransportFactory());
    }

    public BlipClientBuilder(TransportFactory transportFactory) {
        Objects.requireNonNull(transportFactory);

        this.transportFactory = transportFactory;
        this.domain = Constants.DEFAULT_DOMAIN;
        this.scheme = Constants.DEFAULT_SCHEME;
        this.hostName = Constants.DEFAULT_HOST_NAME;
        this.port = Constants.DEFAULT_PORT;
        this.sendTimeout = 60;
        this.sendTimeoutTimeUnit = TimeUnit.SECONDS;
        this.maxConnectionRetries = 3;
        this.compression = SessionCompression.NONE;
        this.encryption = SessionEncryption.TLS;
        this.routingRule = Presence.RoutingRule.IDENTITY;
        this.roundRobin = true;
        this.autoNotify = true;
        this.receiptEvents = new Notification.Event[] { Notification.Event.ACCEPTED, Notification.Event.DISPATCHED, Notification.Event.RECEIVED, Notification.Event.CONSUMED, Notification.Event.FAILED };
    }

    public BlipClientBuilder usingPassword(String identifier, String password) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(password);

        this.identifier = identifier;
        this.password = password;
        return this;
    }

}
