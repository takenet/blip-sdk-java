package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.Session;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.network.Channel;
import org.limeprotocol.network.SessionChannel;
import org.limeprotocol.network.Transport;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class OnDemandClientChannelImpl implements OnDemandClientChannel {

    private final EstablishedClientChannelBuilder establishedClientChannelBuilder;
    private final long defaultTimeoutInMilliseconds;
    private final Semaphore semaphore;
    private final Set<Consumer<ChannelInformation>> channelCreatedHandlers;
    private final Set<Consumer<ChannelInformation>> channelDiscardedHandlers;
    private final Set<FailedChannelInformationHandler> channelCreationFailedHandlers;
    private final Set<FailedChannelInformationHandler> channelOperationFailedHandlers;
    private final Set<MessageChannelListener> messageChannelListeners;
    private final Set<NotificationChannelListener> notificationChannelListeners;
    private final Set<CommandChannelListener> commandChannelListeners;
    private final ChannelStateMonitor channelStateMonitor;

    private Semaphore finishedSessionChannelListenerSemaphore;
    private ClientChannel clientChannel;
    private boolean isFinishing;

    public OnDemandClientChannelImpl(EstablishedClientChannelBuilder establishedClientChannelBuilder) {
        this(establishedClientChannelBuilder, 30000);
    }

    public OnDemandClientChannelImpl(EstablishedClientChannelBuilder establishedClientChannelBuilder, long defaultTimeoutInMilliseconds) {
        this.defaultTimeoutInMilliseconds = defaultTimeoutInMilliseconds;
        Objects.requireNonNull(establishedClientChannelBuilder, "establishedClientChannelBuilder cannot be null");
        this.establishedClientChannelBuilder = establishedClientChannelBuilder;
        this.semaphore = new Semaphore(1);
        this.channelCreatedHandlers = new HashSet<>();
        this.channelDiscardedHandlers = new HashSet<>();
        this.channelCreationFailedHandlers = new HashSet<>();
        this.channelOperationFailedHandlers = new HashSet<>();
        this.messageChannelListeners = new HashSet<>();
        this.notificationChannelListeners = new HashSet<>();
        this.commandChannelListeners = new HashSet<>();
        this.channelStateMonitor = new ChannelStateMonitor();
    }

    @Override
    public boolean isEstablished() {
        return channelIsEstablished(clientChannel);
    }

    @Override
    public void establish(long timeoutInMilliseconds) throws IOException, InterruptedException, TimeoutException {
        isFinishing = false;
        getChannel("establish", timeoutInMilliseconds);
    }

    @Override
    public void finish(long timeoutInMilliseconds) throws IOException, InterruptedException, TimeoutException {
        semaphore.acquire();
        isFinishing = true;
        try {
            if (isEstablished()) {
                clientChannel.sendFinishingSession();

                if (finishedSessionChannelListenerSemaphore != null
                        && !finishedSessionChannelListenerSemaphore.tryAcquire(timeoutInMilliseconds, TimeUnit.MILLISECONDS)) {
                    throw new TimeoutException("Could not finish the active session in the configured timeout");
                }
            }

        } finally {
            semaphore.release();
        }
    }

    @Override
    public Set<Consumer<ChannelInformation>> getChannelCreatedHandlers() {
        return channelCreatedHandlers;
    }

    @Override
    public Set<Consumer<ChannelInformation>> getChannelDiscardedHandlers() {
        return channelDiscardedHandlers;
    }

    @Override
    public Set<FailedChannelInformationHandler> getChannelCreationFailedHandlers() {
        return channelCreationFailedHandlers;
    }

    @Override
    public Set<FailedChannelInformationHandler> getChannelOperationFailedHandlers() {
        return channelOperationFailedHandlers;
    }

    @Override
    public void sendCommand(Command command) throws IOException {

    }

    @Override
    public void addCommandListener(CommandChannelListener listener, boolean removeAfterReceive) {
        Objects.requireNonNull(listener);
        if (removeAfterReceive) throw new IllegalArgumentException("removeAfterReceive is not supported by OnDemandClientChannel");
        commandChannelListeners.add(listener);
    }

    @Override
    public void removeCommandListener(CommandChannelListener listener) {
        Objects.requireNonNull(listener);
        commandChannelListeners.remove(listener);
    }

    @Override
    public void sendMessage(Message message) throws IOException {

    }

    @Override
    public void addMessageListener(MessageChannelListener listener, boolean removeAfterReceive) {
        Objects.requireNonNull(listener);
        if (removeAfterReceive) throw new IllegalArgumentException("removeAfterReceive is not supported by OnDemandClientChannel");
        messageChannelListeners.add(listener);
    }

    @Override
    public void removeMessageListener(MessageChannelListener listener) {
        Objects.requireNonNull(listener);
        messageChannelListeners.remove(listener);
    }

    @Override
    public void sendNotification(Notification notification) throws IOException {

    }

    @Override
    public void addNotificationListener(NotificationChannelListener listener, boolean removeAfterReceive) {
        Objects.requireNonNull(listener);
        if (removeAfterReceive) throw new IllegalArgumentException("removeAfterReceive is not supported by OnDemandClientChannel");
        notificationChannelListeners.add(listener);
    }

    @Override
    public void removeNotificationListener(NotificationChannelListener listener) {
        Objects.requireNonNull(listener);
        notificationChannelListeners.remove(listener);
    }

    private ClientChannel getChannel(String operationName, long timeoutInMilliseconds) {
        boolean channelCreated = false;
        ClientChannel clientChannel = this.clientChannel;

        long startTime = System.currentTimeMillis();

        while (!isFinishing && shouldCreateChannel(clientChannel)) {
            try {
                if (timeoutInMilliseconds > 0) {
                    if (System.currentTimeMillis() - startTime >= timeoutInMilliseconds ||
                            !this.semaphore.tryAcquire(1, timeoutInMilliseconds, TimeUnit.MILLISECONDS)) {
                        throw new RuntimeException("The channel creation operation has timed out");
                    }
                } else {
                    this.semaphore.acquire();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                clientChannel = this.clientChannel;
                if (shouldCreateChannel(clientChannel)) {
                    this.clientChannel = clientChannel = this.establishedClientChannelBuilder.buildAndEstablish();
                    this.finishedSessionChannelListenerSemaphore = new Semaphore(0);

                    // Add listeners to monitor the channel state
                    clientChannel.getTransport().setStateListener(channelStateMonitor);
                    clientChannel.enqueueSessionListener(channelStateMonitor);

                    // Register the external listeners
                    for (MessageChannelListener listener : messageChannelListeners) {
                        clientChannel.addMessageListener(listener, false);
                    }
                    for (NotificationChannelListener listener : notificationChannelListeners) {
                        clientChannel.addNotificationListener(listener, false);
                    }
                    for (CommandChannelListener listener : commandChannelListeners) {
                        clientChannel.addCommandListener(listener, false);
                    }

                    channelCreated = true;
                }
            } catch (Exception e) {
                FailedChannelInformation failedChannelInformation = new FailedChannelInformation(
                    null, null, null, null, false, e, operationName);
                if (!invokeHandlers(channelCreationFailedHandlers, failedChannelInformation)) {
                    throw new RuntimeException(e);
                }
            } finally {
                this.semaphore.release();
            }
        }

        if (channelCreated && clientChannel != null) {
            ChannelInformation channelInformation = new ChannelInformation(
                clientChannel.getSessionId(), clientChannel.getState(), clientChannel.getLocalNode(), clientChannel.getRemoteNode());
            invokeHandlers(channelCreatedHandlers, channelInformation);
        }

        return clientChannel;
    }

    private static boolean shouldCreateChannel(Channel channel) {
        return channel == null || !channelIsEstablished(channel);
    }

    private static boolean channelIsEstablished(Channel channel) {
        return channel != null
                && channel.getState() == Session.SessionState.ESTABLISHED
                && channel.getTransport().isConnected();
    }

    private static void invokeHandlers(Set<Consumer<ChannelInformation>> handlers, ChannelInformation channelInformation) {
        Set<Exception> exceptions = new HashSet<>();
        for (Consumer<ChannelInformation> handler : new ArrayList<>(handlers)) {
            try {
                handler.accept(channelInformation);
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        throwIfAny(exceptions);
    }

    private static boolean invokeHandlers(Set<FailedChannelInformationHandler> handlers, FailedChannelInformation failedChannelInformation) {
        Set<Exception> exceptions = new HashSet<>();
        boolean handled = true;

        for (FailedChannelInformationHandler handler : new ArrayList<>(handlers)) {
            try {
                if (!handler.shouldContinue(failedChannelInformation)) {
                    handled = false;
                }

            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        throwIfAny(exceptions);
        return handled;
    }

    private static void throwIfAny(Set<Exception> exceptions) {
        if (!exceptions.isEmpty()) {
            throw new RuntimeException(exceptions.iterator().next());
        }
    }

    private final class ChannelStateMonitor implements ClientChannel.EstablishSessionListener, Transport.TransportStateListener {
        /**
         * Occurs when the session establishment fails.
         *
         * @param exception
         */
        @Override
        public void onFailure(Exception exception) {
            if (finishedSessionChannelListenerSemaphore != null) {
                finishedSessionChannelListenerSemaphore.release();
            }
        }

        /**
         * Occurs when a session envelope is received (usually when the session state is finished or failed).
         *
         * @param session
         */
        @Override
        public void onReceiveSession(Session session) {
            if (finishedSessionChannelListenerSemaphore != null) {
                finishedSessionChannelListenerSemaphore.release();
            }
        }

        @Override
        public void onClosing() {

        }

        /**
         * Occurs when the transport is closed.
         */
        @Override
        public void onClosed() {
            while (!isFinishing) {
                try {
                    // Try recreate the channel
                    establish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Occurs when the transport listener thread throws an exception.
         *
         * @param exception
         */
        @Override
        public void onException(Exception exception) {
            exception.printStackTrace();
            while (!isFinishing) {
                try {
                    // Try recreate the channel
                    establish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
