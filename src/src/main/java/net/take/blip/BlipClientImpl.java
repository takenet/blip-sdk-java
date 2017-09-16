package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Envelope;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BlipClientImpl implements BlipClient {
    private final OnDemandClientChannel onDemandClientChannel;

    private final Set<CommandChannel.CommandChannelListener> commandListeners;
    private final Set<MessageChannel.MessageChannelListener> messageListeners;
    private final Set<NotificationChannel.NotificationChannelListener> notificationListeners;
    private final Object syncRoot = new Object();
    private ExecutorService executorService;

    static {
        Registrator.registerDocuments();
    }

    public BlipClientImpl(OnDemandClientChannel onDemandClientChannel) {
        Objects.requireNonNull(onDemandClientChannel, "The onDemandClientChannel cannot be null");

        this.onDemandClientChannel = onDemandClientChannel;
        commandListeners = new HashSet<>();
        messageListeners = new HashSet<>();
        notificationListeners = new HashSet<>();
    }

    @Override
    public synchronized void start(long timeout, TimeUnit timeoutTimeUnit) throws IOException, TimeoutException, InterruptedException {
        synchronized (syncRoot) {
            if (executorService != null) {
                throw new IllegalStateException("The client is already started");
            }
            executorService = Executors.newWorkStealingPool();
            onDemandClientChannel.establish(timeout, timeoutTimeUnit);
        }
    }

    @Override
    public void stop(long timeout, TimeUnit timeoutTimeUnit) throws IOException, TimeoutException, InterruptedException {
        synchronized (syncRoot) {
            if (executorService == null) {
                throw new IllegalStateException("The client is not started");
            }
            onDemandClientChannel.finish(timeout, timeoutTimeUnit);
            executorService.shutdown();
            executorService.awaitTermination(timeout, timeoutTimeUnit);
            executorService = null;
        }
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        onDemandClientChannel.sendMessage(message);
    }

    @Override
    public void sendCommand(Command command) throws IOException {
        onDemandClientChannel.sendCommand(command);
    }

    /**
     * Processes a command request, awaiting for the response.
     *
     * @param requestCommand
     * @param timeout
     * @param timeoutTimeUnit @return
     */
    @Override
    public Command processCommand(Command requestCommand, long timeout, TimeUnit timeoutTimeUnit) throws IOException, TimeoutException, InterruptedException {
        return onDemandClientChannel.processCommand(requestCommand, timeout, timeoutTimeUnit);
    }

    @Override
    public void sendNotification(Notification notification) throws IOException {
        onDemandClientChannel.sendNotification(notification);
    }

    @Override
    public Sender addMessageListener(MessageListener messageListener, Predicate<Message> messageFilter) {
        Objects.requireNonNull(messageListener);
        onDemandClientChannel.addMessageListener(
                message -> receiveEnvelope(message, messageListener::onReceive, messageFilter),false);
        return this;
    }

    @Override
    public Sender addCommandListener(CommandListener commandListener, Predicate<Command> commandFilter) {
        Objects.requireNonNull(commandListener);
        onDemandClientChannel.addCommandListener(
                command -> receiveEnvelope(command, commandListener::onReceive, commandFilter),false);
        return this;
    }

    @Override
    public Sender addNotificationListener(NotificationListener notificationListener, Predicate<Notification> notificationFilter) {
        Objects.requireNonNull(notificationListener);
        onDemandClientChannel.addNotificationListener(
                notification -> receiveEnvelope(notification, notificationListener::onReceive, notificationFilter),false);
        return this;
    }

    private <T extends Envelope> void receiveEnvelope(T envelope, Consumer<T> envelopeReceiver, Predicate<T> envelopeFilter) {
        executorService.submit(() -> {
            if (envelopeFilter == null || envelopeFilter.test(envelope)) {
                envelopeReceiver.accept(envelope);
            }
        });
    }


}
