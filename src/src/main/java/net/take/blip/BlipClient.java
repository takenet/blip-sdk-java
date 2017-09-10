package net.take.blip;

import java.io.IOException;

public interface BlipClient extends Sender {
    void start() throws IOException;

    void stop() throws IOException;
}
