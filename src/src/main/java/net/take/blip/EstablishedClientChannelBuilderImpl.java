package net.take.blip;

import org.limeprotocol.Identity;
import org.limeprotocol.Session;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.security.Authentication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.System.out;

public class EstablishedClientChannelBuilderImpl implements EstablishedClientChannelBuilder {

    private final ClientChannelBuilder clientChannelBuilder;
    private Identity identity;
    private String instance;
    private SessionCompression sessionCompression;
    private SessionEncryption sessionEncryption;
    private Authentication authentication;
    private long establishmentTimeout;

    public EstablishedClientChannelBuilderImpl(ClientChannelBuilder clientChannelBuilder) {
        Objects.requireNonNull(clientChannelBuilder, "clientChannelBuilder cannot be null");
        this.clientChannelBuilder = clientChannelBuilder;
        this.withGuestAuthentication()
            .withIdentity(new Identity(UUID.randomUUID().toString(), clientChannelBuilder.getServerURI().getHost()))
            .withSessionEncryption(SessionEncryption.TLS)
            .withEstablishmentTimeout(30);

        try {
            withInstance(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) { withInstance("default"); }
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
    public EstablishedClientChannelBuilder withEstablishmentTimeout(long establishmentTimeout) {
        this.establishmentTimeout = establishmentTimeout;
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
        clientChannel.establishSession(
                getSessionCompression(),
                getSessionEncryption(),
                getIdentity(),
                getAuthentication(),
                getInstance(),
                new ClientChannel.EstablishSessionListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        exception.printStackTrace();
                        semaphore.release();
                    }

                    @Override
                    public void onReceiveSession(Session session) {
                        semaphore.release();
                    }
                });

        if (semaphore.tryAcquire(1, this.establishmentTimeout, TimeUnit.SECONDS)) {

        } else {
            throw new TimeoutException("Could not establish the session in the configured timeout");
        }


        return null;
    }
}
