package net.take.blip;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface BlipClient extends Sender {
    void start() throws IOException, TimeoutException, InterruptedException;

    void stop() throws IOException, TimeoutException, InterruptedException;
}
