package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.client.ClientChannelImpl;
import org.limeprotocol.network.ChannelModule;
import org.limeprotocol.network.Transport;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class ClientChannelBuilderImpl implements ClientChannelBuilder {

    private final TransportFactory transportFactory;
    private final URI serverURI;
    private boolean fillEnvelopeRecipients;
    private boolean autoReplyPings;
    private boolean autoNotifyReceipt;
    private final Set<ChannelModuleFactory<Message>> messageModules;
    private final Set<ChannelModuleFactory<Notification>> notificationModules;
    private final Set<ChannelModuleFactory<Command>> commandModules;
    private final Set<Consumer<ClientChannel>> builtHandlers;

    private ClientChannelBuilderImpl(TransportFactory transportFactory, URI serverURI) {
        Objects.requireNonNull(transportFactory, "transportFactory cannot be null");
        Objects.requireNonNull(serverURI, "serverURI cannot be null");

        this.transportFactory = transportFactory;
        this.serverURI = serverURI;
        this.messageModules = new HashSet<>();
        this.notificationModules = new HashSet<>();
        this.commandModules = new HashSet<>();
        this.builtHandlers = new HashSet<>();
        withAutoReplyPings(true).withFillEnvelopeRecipients(true);
    }

    @Override
    public URI getServerURI() {
        return serverURI;
    }

    @Override
    public boolean getFillEnvelopeRecipients() {
        return fillEnvelopeRecipients;
    }

    @Override
    public boolean getAutoReplyPings() {
        return autoReplyPings;
    }

    @Override
    public boolean getAutoNotifyReceipt() {
        return autoNotifyReceipt;
    }

    @Override
    public ClientChannelBuilder withFillEnvelopeRecipients(boolean fillEnvelopeRecipients) {
        this.fillEnvelopeRecipients = fillEnvelopeRecipients;
        return this;
    }

    @Override
    public ClientChannelBuilder withAutoReplyPings(boolean autoReplyPings) {
        this.autoReplyPings = autoReplyPings;
        return this;
    }

    @Override
    public ClientChannelBuilder withAutoNotifyReceipt(boolean autoNotifyReceipt) {
        this.autoNotifyReceipt = autoNotifyReceipt;
        return this;
    }

    @Override
    public ClientChannelBuilder addMessageModule(ChannelModuleFactory<Message> moduleFactory) {
        Objects.requireNonNull(moduleFactory);
        messageModules.add(moduleFactory);
        return this;
    }

    @Override
    public ClientChannelBuilder addNotificationModule(ChannelModuleFactory<Notification> moduleFactory) {
        Objects.requireNonNull(moduleFactory);
        notificationModules.add(moduleFactory);
        return this;
    }

    @Override
    public ClientChannelBuilder addCommandModule(ChannelModuleFactory<Command> moduleFactory) {
        Objects.requireNonNull(moduleFactory);
        commandModules.add(moduleFactory);
        return this;
    }

    @Override
    public ClientChannelBuilder addBuiltHandler(Consumer<ClientChannel> handler) {
        Objects.requireNonNull(handler);
        builtHandlers.add(handler);
        return this;
    }

    @Override
    public ClientChannel build() throws IOException {

        Transport transport = transportFactory.create();
        if (!transport.isConnected()) {
            transport.open(getServerURI());
        }
        try {
            ClientChannel clientChannel = new ClientChannelImpl(transport, getFillEnvelopeRecipients(), getAutoReplyPings(), getAutoNotifyReceipt());

            for (ChannelModuleFactory<Message> moduleFactory : this.messageModules) {
                clientChannel.getMessageModules().add(moduleFactory.create(clientChannel));
            }

            for (ChannelModuleFactory<Notification> moduleFactory : this.notificationModules) {
                clientChannel.getNotificationModules().add(moduleFactory.create(clientChannel));
            }

            for (ChannelModuleFactory<Command> moduleFactory : this.commandModules) {
                clientChannel.getCommandModules().add(moduleFactory.create(clientChannel));
            }

            for (Consumer<ClientChannel> builtHandler : this.builtHandlers) {
                builtHandler.accept(clientChannel);
            }
            return clientChannel;
        }
        catch (Exception e) {
            transport.close();
            throw e;
        }
    }

    public static ClientChannelBuilder create(TransportFactory transportFactory, URI serverURI) {
        return new ClientChannelBuilderImpl(transportFactory, serverURI);
    }

    interface TransportFactory {
        Transport create() throws IOException;
    }

}
