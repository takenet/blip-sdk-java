package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Envelope;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

/**
 * Defines a BLiP client to manage the connection with the service and to send and receive envelopes.
 */
public interface BlipClient extends Sender {

    /**
     * Starts the client connecting to the BLiP service and begin listening for envelopes.
     * In case of disconnection, the client will try to reconnect automatically and will only stop if the 'stop' method is called.
     * @param timeout
     * @param timeoutTimeUnit
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    void start(long timeout, TimeUnit timeoutTimeUnit) throws IOException, TimeoutException, InterruptedException;

    /**
     * Starts the client connecting to the BLiP service and begin listening for envelopes.
     * In case of disconnection, the client will try to reconnect automatically and will only stop if the 'stop' method is called.
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws IOException
     */
    default void start() throws InterruptedException, TimeoutException, IOException {
        start(30, TimeUnit.SECONDS);
    }

    /**
     * Stops the client connection and listeners.
     * @param timeout
     * @param timeoutTimeUnit
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    void stop(long timeout, TimeUnit timeoutTimeUnit) throws IOException, TimeoutException, InterruptedException;

    /**
     * Stops the client connection and listeners.
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws IOException
     */
    default void stop() throws InterruptedException, TimeoutException, IOException {
        stop(30, TimeUnit.SECONDS);
    }

    /**
     * Adds a service for listening for messages.
     * The provided listener will be called when a received message matches the filter predicate.
     * @param messageListener
     * @param messageFilter
     * @return
     */
    BlipClient addMessageListener(MessageListener messageListener, Predicate<Message> messageFilter);

    /**
     * Adds a service for listening for messages.
     * The provided listener will be called when any message is received.
     * @param messageListener
     * @return
     */
    default BlipClient addMessageListener(MessageListener messageListener) {
        return addMessageListener(messageListener, null);
    }

    /**
     * Adds a service for listening for commands.
     * The provided listener will be called when a received command matches the filter predicate.
     * @param commandListener
     * @param commandFilter
     * @return
     */
    BlipClient addCommandListener(CommandListener commandListener, Predicate<Command> commandFilter);

    /**
     * Adds a service for listening for commands.
     * The provided listener will be called when any command is received.
     * @param commandListener
     * @return
     */
    default BlipClient addCommandListener(CommandListener commandListener) {
        return addCommandListener(commandListener, null);
    }

    /**
     * Adds a service for listening for notifications.
     * The provided listener will be called when a received notification matches the filter predicate.
     * @param notificationListener
     * @param commandFilter
     * @return
     */
    BlipClient addNotificationListener(NotificationListener notificationListener, Predicate<Notification> commandFilter);

    /**
     * Adds a service for listening for notifications.
     * The provided listener will be called when any notification is received.
     * @param notificationListener
     * @return
     */
    default BlipClient addNotificationListener(NotificationListener notificationListener) {
        return addNotificationListener(notificationListener, null);
    }

    interface EnvelopeListener<T extends Envelope> {
        /**
         * Handles the received envelope.
         * @param envelope
         */
        void onReceive(T envelope);
    }

    /**
     * Represents a service for handling received messages.
     */
    interface MessageListener extends EnvelopeListener<Message> { }

    /**
     * Represents a service for handling received commands.
     */
    interface CommandListener extends EnvelopeListener<Command> { }

    /**
     * Represents a service for handling received notifications.
     */
    interface NotificationListener extends EnvelopeListener<Notification> { }
}
