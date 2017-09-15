package net.take.blip;

import org.limeprotocol.network.Transport;

import java.net.URI;

public interface TransportFactory {
    Transport create(URI endpoint);
}
