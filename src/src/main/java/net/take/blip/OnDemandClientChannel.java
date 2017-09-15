package net.take.blip;


import org.limeprotocol.client.ClientChannel;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public interface OnDemandClientChannel extends EstablishedChannel  {

    boolean isEstablished();


    void establish(long timeout, TimeUnit timeoutTimeUnit) throws IOException, InterruptedException, TimeoutException;

    default void establish() throws IOException, InterruptedException, TimeoutException {
        establish(0, TimeUnit.MILLISECONDS);
    }

    void finish(long timeout, TimeUnit timeoutTimeUnit) throws IOException, InterruptedException, TimeoutException;

    default void finish() throws IOException, InterruptedException, TimeoutException {
        finish(0, TimeUnit.MILLISECONDS);
    }

    Set<Consumer<ChannelInformation>> getChannelCreatedHandlers();

    Set<Consumer<ChannelInformation>> getChannelDiscardedHandlers();

    Set<FailedChannelInformationHandler> getChannelCreationFailedHandlers();

    Set<FailedChannelInformationHandler> getChannelOperationFailedHandlers();

    interface FailedChannelInformationHandler {
        boolean shouldContinue(FailedChannelInformation failedChannelInformation);
    }
}


