package net.take.blip;

import org.limeprotocol.Identity;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.security.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface EstablishedClientChannelBuilder {

    ClientChannelBuilder getClientChannelBuilder();

    Identity getIdentity();

    String getInstance();

    SessionCompression getSessionCompression();

    SessionEncryption getSessionEncryption();

    Authentication getAuthentication();

    long getEstablishmentTimeout();

    EstablishedClientChannelBuilder withIdentity(Identity identity);

    EstablishedClientChannelBuilder withInstance(String instance);

    EstablishedClientChannelBuilder withSessionCompression(SessionCompression sessionCompression);

    EstablishedClientChannelBuilder withSessionEncryption(SessionEncryption sessionEncryption);

    EstablishedClientChannelBuilder withAuthentication(Authentication authentication);

    EstablishedClientChannelBuilder withEstablishmentTimeout(long establishmentTimeout);

    ClientChannel buildAndEstablish() throws IOException, InterruptedException, TimeoutException;

    default EstablishedClientChannelBuilder withKeyAuthentication(String key) {
        KeyAuthentication authentication = new KeyAuthentication() {{
            setToBase64Password(key);
        }};

        return withAuthentication(authentication);
    }

    default EstablishedClientChannelBuilder withPlainAuthentication(String password) {
        PlainAuthentication authentication = new PlainAuthentication() {{
            setToBase64Password(password);
        }};

        return withAuthentication(authentication);
    }

    default EstablishedClientChannelBuilder withGuestAuthentication() {
        GuestAuthentication authentication = new GuestAuthentication();
        return withAuthentication(authentication);
    }

    default EstablishedClientChannelBuilder withTransportAuthentication() {
        TransportAuthentication authentication = new TransportAuthentication();
        return withAuthentication(authentication);
    }
}
