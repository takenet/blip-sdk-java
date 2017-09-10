package net.take.blip;


import org.limeprotocol.client.ClientChannel;

import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

public interface OnDemandClientChannel extends EstablishedChannel  {

    boolean isEstablished();

    void establish() throws IOException, InterruptedException;

    void finish() throws IOException, InterruptedException;

    //Set<Consumer<ClientChannel>> getChannelCreatedHandlers();
}

