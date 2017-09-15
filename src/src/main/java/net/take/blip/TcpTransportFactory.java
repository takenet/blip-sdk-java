package net.take.blip;

import org.limeprotocol.network.TraceWriter;
import org.limeprotocol.network.Transport;
import org.limeprotocol.network.tcp.SocketTcpClientFactory;
import org.limeprotocol.network.tcp.TcpTransport;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;

import java.net.URI;

public class TcpTransportFactory implements TransportFactory {
    @Override
    public Transport create(URI endpoint) {
        return new TcpTransport(
                new JacksonEnvelopeSerializer(),
                new SocketTcpClientFactory(),
                new TraceWriter() {
                    @Override
                    public void trace(String data, DataOperation operation) {
                        System.out.printf("%s: %s", operation.toString(), data);
                        System.out.println();
                    }

                    @Override
                    public boolean isEnabled() {
                        return true;
                    }
                },
                TcpTransport.DEFAULT_BUFFER_SIZE);
    }
}
