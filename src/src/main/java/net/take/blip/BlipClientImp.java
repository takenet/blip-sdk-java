package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.messaging.Registrator;
import org.limeprotocol.network.CommandChannel;
import org.limeprotocol.network.MessageChannel;
import org.limeprotocol.network.NotificationChannel;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

public class BlipClientImp implements BlipClient {
    private final OnDemandClientChannel onDemandClientChannel;

    private final Set<CommandChannel.CommandChannelListener> commandListeners;
    private final Set<MessageChannel.MessageChannelListener> messageListeners;
    private final Set<NotificationChannel.NotificationChannelListener> notificationListeners;

    static {
        Registrator.registerDocuments();
    }

    public BlipClientImp(OnDemandClientChannel onDemandClientChannel) {
        Objects.requireNonNull(onDemandClientChannel, "The onDemandClientChannel cannot be null");

        this.onDemandClientChannel = onDemandClientChannel;
        this.commandListeners = new HashSet<>();
        this.messageListeners = new HashSet<>();
        this.notificationListeners = new HashSet<>();
    }

    @Override
    public void start() throws IOException, TimeoutException, InterruptedException {
        this.onDemandClientChannel.establish();
    }

    @Override
    public void stop() throws IOException, TimeoutException, InterruptedException {
        this.onDemandClientChannel.finish();
    }

    @Override
    public Command processCommand(Command requestCommand) {
        return null;
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        this.onDemandClientChannel.sendMessage(message);
    }

    @Override
    public void sendCommand(Command command) throws IOException {
        this.onDemandClientChannel.sendCommand(command);
    }

    @Override
    public void sendNotification(Notification notification) throws IOException {
        this.onDemandClientChannel.sendNotification(notification);
    }

    @Override
    public Sender addMessageListener(MessageListener messageListener, Predicate<Message> messageFilter) {
        Objects.requireNonNull(messageListener);
        this.onDemandClientChannel.addMessageListener(message -> {
            if (messageFilter == null || messageFilter.test(message)) {
                messageListener.onReceive(message);
            }
        },false);

        return this;
    }

    @Override
    public Sender addCommandListener(CommandListener commandListener, Predicate<Command> commandFilter) {
        Objects.requireNonNull(commandListener);
        this.onDemandClientChannel.addCommandListener(command -> {
            if (commandFilter == null || commandFilter.test(command)) {
                commandListener.onReceive(command);
            }
        },false);

        return this;
    }

    @Override
    public Sender addNotificationListener(NotificationListener notificationListener, Predicate<Notification> notificationFilter) {
        Objects.requireNonNull(notificationListener);
        this.onDemandClientChannel.addNotificationListener(notification -> {
            if (notificationFilter == null || notificationFilter.test(notification)) {
                notificationListener.onReceive(notification);
            }
        },false);

        return this;
    }
}
