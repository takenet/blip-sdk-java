package net.take.blip;

import org.limeprotocol.*;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.network.CommandChannel;
import org.limeprotocol.network.CommandProcessor;
import org.limeprotocol.network.MessageChannel;
import org.limeprotocol.network.NotificationChannel;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Predicate;

public interface Sender extends CommandProcessor {

    default void sendMessage(final String content, final Node to) throws IOException {
        sendMessage(new PlainText(content), to);
    }

    default void sendMessage(final Document content, final Node to) throws IOException {
        if (content == null) throw new IllegalArgumentException("Content is required");

        Message message = new Message() {{
            setId(UUID.randomUUID().toString());
            setTo(to);
            setContent(content);
        }};

        sendMessage(message);
    }

    void sendMessage(Message message) throws IOException;

    void sendCommand(Command command) throws IOException;

    void sendNotification(Notification notification) throws IOException;

    Sender addMessageListener(MessageListener messageListener, Predicate<Message> messageFilter);

    default Sender addMessageListener(MessageListener messageListener) {
        return addMessageListener(messageListener, null);
    }

    Sender addCommandListener(CommandListener commandListener, Predicate<Command> commandFilter);

    default Sender addCommandListener(CommandListener commandListener) {
        return addCommandListener(commandListener, null);
    }

    Sender addNotificationListener(NotificationListener notificationListener, Predicate<Notification> commandFilter);

    default Sender addNotificationListener(NotificationListener notificationListener) {
        return addNotificationListener(notificationListener, null);
    }

    interface EnvelopeListener<T extends Envelope> {
        void onReceive(T envelope);
    }

    interface MessageListener extends EnvelopeListener<Message> { }

    interface CommandListener extends EnvelopeListener<Command> { }

    interface NotificationListener extends EnvelopeListener<Notification> { }
}
