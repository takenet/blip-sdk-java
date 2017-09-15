package net.take.blip;

import org.limeprotocol.network.Transport;
import org.limeprotocol.network.tcp.TcpTransport;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;

import java.net.URI;

public class TcpTransportFactory implements TransportFactory {
    @Override
    public Transport create(URI endpoint) {
        return new TcpTransport(new JacksonEnvelopeSerializer());
    }
}
