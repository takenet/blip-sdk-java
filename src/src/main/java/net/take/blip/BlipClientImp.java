package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.network.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class BlipClientImp implements BlipClient {
    private final ClientChannelFactory clientChannelFactory;

    private final Set<CommandChannel.CommandChannelListener> commandListeners;
    private final Set<MessageChannel.MessageChannelListener> messageListeners;
    private final Set<NotificationChannel.NotificationChannelListener> notificationListeners;

    public BlipClientImp(ClientChannelFactory clientChannelFactory) {

        if (clientChannelFactory == null) {
            throw new IllegalArgumentException("The clientChannelFactory cannot be null");
        }
        this.clientChannelFactory = clientChannelFactory;
        this.commandListeners = new HashSet<>();
        this.messageListeners = new HashSet<>();
        this.notificationListeners = new HashSet<>();
    }

    @Override
    public void start() throws IOException {
        ClientChannel clientChannel = this.clientChannelFactory.create();

        //clientChannel.establishSession(SessionCompression.NONE, );
    }

    @Override
    public void stop() throws IOException {

    }

    @Override
    public Command processCommand(Command requestCommand) {
        return null;
    }

    @Override
    public void sendMessage(Message message) {

    }

    @Override
    public void sendCommand(Command command) {

    }

    @Override
    public void sendNotification(Notification notification) {

    }

    @Override
    public Sender addMessageListener(MessageChannel.MessageChannelListener messageChannelListener) {
        this.messageListeners.add(messageChannelListener);
        return this;
    }

    @Override
    public Sender addCommandListener(CommandChannel.CommandChannelListener commandChannelListener) {
        this.commandListeners.add(commandChannelListener);
        return this;
    }

    @Override
    public Sender addNotificationListener(NotificationChannel.NotificationChannelListener notificationChannelListener) {
        this.notificationListeners.add(notificationChannelListener);
        return this;
    }
}
