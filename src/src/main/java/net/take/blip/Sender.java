package net.take.blip;

import org.limeprotocol.*;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.network.CommandChannel;
import org.limeprotocol.network.MessageChannel;
import org.limeprotocol.network.NotificationChannel;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Predicate;

public interface Sender {
    Command processCommand(Command requestCommand);

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

    Sender addCommandListener(CommandListener commandListener, Predicate<Command> commandFilter);

    Sender addNotificationListener(NotificationListener notificationListener, Predicate<Notification> commandFilter);

    interface EnvelopeListener<T extends Envelope> {
        void onReceive(T envelope);
    }

    interface MessageListener extends EnvelopeListener<Message> { }

    interface CommandListener extends EnvelopeListener<Command> { }

    interface NotificationListener extends EnvelopeListener<Notification> { }
}
