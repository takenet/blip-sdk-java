package net.take.blip;

import org.limeprotocol.Command;
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

    ClientChannelBuilder addMessageModule(ChannelModule<Message> module);

    ClientChannelBuilder addNotificationModule(ChannelModule<Notification> module);

    ClientChannelBuilder addCommandModule(ChannelModule<Command> module);

    ClientChannelBuilder addBuiltHandler(Consumer<ClientChannel> builtHandler);

    ClientChannel build() throws IOException;

}
