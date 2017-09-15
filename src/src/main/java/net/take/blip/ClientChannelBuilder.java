package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Envelope;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.network.ChannelModule;

import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

public interface ClientChannelBuilder {

    URI getServerURI();

    boolean getFillEnvelopeRecipients();

    boolean getAutoReplyPings();

    boolean getAutoNotifyReceipt();

    ClientChannelBuilder withFillEnvelopeRecipients(boolean fillEnvelopeRecipients);

    ClientChannelBuilder withAutoReplyPings(boolean autoReplyPings);

    ClientChannelBuilder withAutoNotifyReceipt(boolean autoNotifyReceipt);

    ClientChannelBuilder addMessageModule(ChannelModuleFactory<Message> moduleFactory);

    ClientChannelBuilder addNotificationModule(ChannelModuleFactory<Notification> moduleFactory);

    ClientChannelBuilder addCommandModule(ChannelModuleFactory<Command> moduleFactory);

    ClientChannelBuilder addBuiltHandler(Consumer<ClientChannel> handler);

    ClientChannel build() throws IOException;

    interface ChannelModuleFactory<T extends Envelope> {
        ChannelModule<T> create(ClientChannel clientChannel);
    }
}
