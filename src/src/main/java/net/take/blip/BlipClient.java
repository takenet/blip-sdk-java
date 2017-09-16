package net.take.blip;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface BlipClient extends Sender {
    void start(long timeout, TimeUnit timeoutTimeUnit) throws IOException, TimeoutException, InterruptedException;

    default void start() throws InterruptedException, TimeoutException, IOException {
        start(30, TimeUnit.SECONDS);
    }

    void stop(long timeout, TimeUnit timeoutTimeUnit) throws IOException, TimeoutException, InterruptedException;

    default void stop() throws InterruptedException, TimeoutException, IOException {
        stop(30, TimeUnit.SECONDS);
    }
}
