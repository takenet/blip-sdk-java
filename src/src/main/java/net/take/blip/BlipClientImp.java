package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.network.CommandChannel;
import org.limeprotocol.network.MessageChannel;
import org.limeprotocol.network.NotificationChannel;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class BlipClientImp implements BlipClient {
    private final OnDemandClientChannel onDemandClientChannel;

    private final Set<CommandChannel.CommandChannelListener> commandListeners;
    private final Set<MessageChannel.MessageChannelListener> messageListeners;
    private final Set<NotificationChannel.NotificationChannelListener> notificationListeners;

    public BlipClientImp(OnDemandClientChannel onDemandClientChannel) {
        Objects.requireNonNull(onDemandClientChannel, "The onDemandClientChannel cannot be null");

        this.onDemandClientChannel = onDemandClientChannel;
        this.commandListeners = new HashSet<>();
        this.messageListeners = new HashSet<>();
        this.notificationListeners = new HashSet<>();
    }

    @Override
    public void start() throws IOException {
        //ClientChannel clientChannel = this.clientChannelFactory.create();
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
    public Sender addMessageListener(MessageChannel.MessageChannelListener messageChannelListener, Predicate<Message> messageFilter) {
        return null;
    }

    @Override
    public Sender addCommandListener(CommandChannel.CommandChannelListener commandChannelListener, Predicate<Command> commandFilter) {
        return null;
    }

    @Override
    public Sender addNotificationListener(NotificationChannel.NotificationChannelListener notificationChannelListener, Predicate<Notification> commandFilter) {
        return null;
    }
}
