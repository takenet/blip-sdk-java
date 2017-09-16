package net.take.blip;

import org.limeprotocol.*;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.messaging.resources.Presence;
import org.limeprotocol.messaging.resources.Receipt;
import org.limeprotocol.messaging.resources.UriTemplates;
import org.limeprotocol.network.ChannelExtensions;
import org.limeprotocol.network.modules.ReplyPingChannelModule;
import org.limeprotocol.network.modules.ThroughputControlChannelModule;
import org.limeprotocol.security.Authentication;
import org.limeprotocol.security.GuestAuthentication;
import org.limeprotocol.security.KeyAuthentication;
import org.limeprotocol.security.PlainAuthentication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class BlipClientBuilder {

    private final TransportFactory transportFactory;

    private String domain;
    private String scheme;
    private String hostName;
    private int port;
    private long sendTimeout;
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
    private String instance;
    private int throughputInSeconds;
    private String accessKey;

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
        this.receiptEvents = new Notification.Event[]{Notification.Event.ACCEPTED, Notification.Event.DISPATCHED, Notification.Event.RECEIVED, Notification.Event.CONSUMED, Notification.Event.FAILED};
    }

    public BlipClientBuilder usingPassword(String identifier, String password) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(password);

        this.identifier = identifier;
        this.password = password;
        return this;
    }

    public BlipClientBuilder usingGuest() {
        this.identifier = UUID.randomUUID().toString();
        return this;
    }

    public BlipClientBuilder usingAccessKey(String identifier, String accessKey) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(accessKey);

        this.identifier = identifier;
        this.accessKey = accessKey;
        return this;
    }

    public BlipClientBuilder usingInstance(String instance) {
        this.instance = instance;
        return this;
    }

    public BlipClientBuilder usingRoutingRule(Presence.RoutingRule routingRule) {
        this.routingRule = routingRule;
        return this;
    }

    public BlipClientBuilder usingRoundRobin(boolean roundRobin) {
        this.routingRule = routingRule;
        return this;
    }

    public BlipClientBuilder usingScheme(String scheme) {
        Objects.requireNonNull(scheme);
        this.scheme = scheme;
        return this;
    }


    public BlipClientBuilder usingPort(int port) {
        if (port <= 0) throw new IllegalArgumentException("Port should be a positive value");
        this.port = port;
        return this;
    }

    public BlipClientBuilder usingDomain(String domain) {
        Objects.requireNonNull(domain);
        this.domain = domain;
        return this;
    }

    public BlipClientBuilder usingEncryption(SessionEncryption sessionEncryption) {
        this.encryption = sessionEncryption;
        return this;
    }

    public BlipClientBuilder usingCompression(SessionCompression sessionCompression) {
        this.compression = sessionCompression;
        return this;
    }

    public BlipClientBuilder usingSendTimeout(long sendTimeout, TimeUnit sendTimeoutTimeUnit) {
        this.sendTimeout = sendTimeout;
        this.sendTimeoutTimeUnit = sendTimeoutTimeUnit;
        return this;
    }

    public BlipClientBuilder usingAutoNotify(boolean autoNotify) {
        this.autoNotify = autoNotify;
        return this;
    }

    public BlipClientBuilder usingReceiptEvents(Notification.Event[] events) {
        Objects.requireNonNull(events);
        this.receiptEvents = events;
        return this;
    }

    public BlipClientBuilder usingMaxConnectionRetries(int maxConnectionRetries) {
        this.maxConnectionRetries = maxConnectionRetries;
        return this;
    }

    public BlipClientBuilder usingThroughput(int throughputInSeconds) {
        this.throughputInSeconds = throughputInSeconds;
        return this;
    }

    public URI getEndpoint() {
        try {
            return new URI(scheme, null, hostName, port, null, null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Identity getIdentity() {
        return new Identity(identifier, domain);
    }

    public Authentication getAuthentication() {
        Authentication authentication;
        if (password != null) {
            authentication = new PlainAuthentication() {{
                setPassword(password);
            }};
        } else if (accessKey != null) {
            authentication = new KeyAuthentication() {{
                setKey(accessKey);
            }};
        } else {
            authentication = new GuestAuthentication();
        }

        return authentication;
    }

    public BlipClient build() {
        ClientChannelBuilder channelBuilder = ClientChannelBuilderImpl
                .create(() -> transportFactory.create(getEndpoint()), getEndpoint())
                .withAutoNotifyReceipt(autoNotify)
                .addCommandModule(c -> new ReplyPingChannelModule(c))
                .addBuiltHandler(c -> {
                    if (throughputInSeconds > 0) ThroughputControlChannelModule.createAndRegister(c, throughputInSeconds);
                });
        EstablishedClientChannelBuilder establishedClientChannelBuilder = new EstablishedClientChannelBuilderImpl(channelBuilder)
                .withIdentity(getIdentity())
                .withAuthentication(getAuthentication())
                .withSessionCompression(compression)
                .withSessionEncryption(encryption)
                .addEstablishedHandler(this::setPresence)
                .addEstablishedHandler(this::setReceipt);

        if (instance != null) {
            establishedClientChannelBuilder = establishedClientChannelBuilder.withInstance(instance);
        }
        OnDemandClientChannel onDemandClientChannel = new OnDemandClientChannelImpl(establishedClientChannelBuilder);
        return new BlipClientImpl(onDemandClientChannel);
    }

    private void setPresence(ClientChannel clientChannel) {
        try {

            if (getAuthentication() instanceof GuestAuthentication) return;

            clientChannel.setResource(
                    new LimeUri(UriTemplates.PRESENCE),
                    new Presence() {{
                        setStatus(PresenceStatus.AVAILABLE);
                        setRoutingRule(routingRule);
                        setRoundRobin(roundRobin);
                    }});

        } catch (IOException | TimeoutException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void setReceipt(ClientChannel clientChannel) {
        try {

            if (getAuthentication() instanceof GuestAuthentication) return;

            clientChannel.setResource(
                    new LimeUri(UriTemplates.RECEIPT),
                    new Receipt() {{
                        setEvents(receiptEvents);
                    }});

        } catch (IOException | TimeoutException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
