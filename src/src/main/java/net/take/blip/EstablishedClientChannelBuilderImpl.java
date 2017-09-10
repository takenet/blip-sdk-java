package net.take.blip;

import org.limeprotocol.*;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.network.LimeException;
import org.limeprotocol.security.Authentication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static java.lang.System.out;

public class EstablishedClientChannelBuilderImpl implements EstablishedClientChannelBuilder {

    private final ClientChannelBuilder clientChannelBuilder;
    private final Set<Consumer<ClientChannel>> establishedHandlers;
    private Identity identity;
    private String instance;
    private SessionCompression sessionCompression;
    private SessionEncryption sessionEncryption;
    private Authentication authentication;
    private long establishmentTimeout;

    public EstablishedClientChannelBuilderImpl(ClientChannelBuilder clientChannelBuilder) {
        Objects.requireNonNull(clientChannelBuilder, "clientChannelBuilder cannot be null");
        this.clientChannelBuilder = clientChannelBuilder;
        this.establishedHandlers = new HashSet<>();
        this.withGuestAuthentication()
            .withIdentity(new Identity(UUID.randomUUID().toString(), clientChannelBuilder.getServerURI().getHost()))
            .withSessionEncryption(SessionEncryption.TLS)
            .withEstablishmentTimeout(30000);

        try {
            withInstance(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            withInstance("default");
        }
    }

    @Override
    public ClientChannelBuilder getClientChannelBuilder() {
        return clientChannelBuilder;
    }

    @Override
    public Identity getIdentity() {
        return identity;
    }

    @Override
    public String getInstance() {
        return instance;
    }

    @Override
    public SessionCompression getSessionCompression() {
        return sessionCompression;
    }

    @Override
    public SessionEncryption getSessionEncryption() {
        return sessionEncryption;
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public long getEstablishmentTimeout() {
        return establishmentTimeout;
    }

    @Override
    public EstablishedClientChannelBuilder withIdentity(Identity identity) {
        Objects.requireNonNull(identity);
        this.identity = identity;
        return this;
    }

    @Override
    public EstablishedClientChannelBuilder withInstance(String instance) {
        this.instance = instance;
        return this;
    }

    @Override
    public EstablishedClientChannelBuilder withAuthentication(Authentication authentication) {
        Objects.requireNonNull(authentication);
        this.authentication = authentication;
        return this;
    }

    @Override
    public EstablishedClientChannelBuilder withEstablishmentTimeout(long establishmentTimeoutInMilliseconds) {
        this.establishmentTimeout = establishmentTimeoutInMilliseconds;
        return this;
    }

    @Override
    public EstablishedClientChannelBuilder addEstablishedHandler(Consumer<ClientChannel> handler) {
        this.establishedHandlers.add(handler);
        return this;
    }

    @Override
    public EstablishedClientChannelBuilder withSessionCompression(SessionCompression sessionCompression) {
        Objects.requireNonNull(sessionCompression);
        this.sessionCompression = sessionCompression;
        return this;
    }

    @Override
    public EstablishedClientChannelBuilder withSessionEncryption(SessionEncryption sessionEncryption) {
        Objects.requireNonNull(sessionEncryption);
        this.sessionEncryption = sessionEncryption;
        return this;
    }

    @Override
    public ClientChannel buildAndEstablish() throws IOException, InterruptedException, TimeoutException {
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();

        ClientChannel clientChannel = clientChannelBuilder.build();

        final Session[] receivedSessions = new Session[1];
        final Exception[] receivedExceptions = new Exception[1];

        clientChannel.establishSession(
                getSessionCompression(),
                getSessionEncryption(),
                getIdentity(),
                getAuthentication(),
                getInstance(),
                new ClientChannel.EstablishSessionListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        receivedExceptions[1] = exception;
                        semaphore.release();
                    }

                    @Override
                    public void onReceiveSession(Session session) {
                        receivedSessions[0] = session;
                        semaphore.release();
                    }
                });

        if (semaphore.tryAcquire(1, this.establishmentTimeout, TimeUnit.MILLISECONDS)) {

            if (receivedExceptions[1] != null) {
                throw new RuntimeException(receivedExceptions[1]);
            }

            if (clientChannel.getState() != Session.SessionState.ESTABLISHED) {
                Reason reason = null;
                if (receivedSessions[1] != null) {
                    reason = receivedSessions[1].getReason();
                }
                if (reason == null) {
                    reason = new Reason(ReasonCodes.GENERAL_ERROR, "receivedExceptions");
                }
                throw new LimeException(reason);
            }

            for (Consumer<ClientChannel> handler : establishedHandlers) {
                handler.accept(clientChannel);
            }

            return clientChannel;

        } else {
            throw new TimeoutException("Could not establish the session in the configured timeout");
        }
    }
}
