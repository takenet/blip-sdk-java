package net.take.blip;

import org.limeprotocol.Identity;
import org.limeprotocol.Session;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.security.Authentication;
import org.limeprotocol.security.KeyAuthentication;

public interface EstablishedClientChannelFactory {

    ClientChannelBuilder getClientChannelBuilder();

    Identity getIdentity();

    String getInstance();

    SessionCompression getSessionCompression();

    SessionEncryption getSessionEncryption();

    Authentication getAuthentication();

    EstablishedClientChannelFactory withIdentity(Identity identity);

    EstablishedClientChannelFactory withInstance(String instance);

    EstablishedClientChannelFactory withAuthentication(Authentication authentication);

    EstablishedClientChannelFactory withSessionCompression(SessionCompression sessionCompression);

    EstablishedClientChannelFactory withSessionEncryption(SessionEncryption sessionEncryption);

    ClientChannel buildAndEstablish(ClientChannel.EstablishSessionListener establishSessionListener);

    default EstablishedClientChannelFactory withKeyAuthentication(String key) {
        KeyAuthentication keyAuthentication = new KeyAuthentication() {{
            setToBase64Password(key);
        }};

        return withAuthentication(keyAuthentication);
    }
}
