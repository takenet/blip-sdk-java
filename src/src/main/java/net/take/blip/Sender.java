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

    void sendMessage(Message message);

    void sendCommand(Command command);

    void sendNotification(Notification notification);

    Sender addMessageListener(MessageChannel.MessageChannelListener messageChannelListener, Predicate<Message> messageFilter);

    Sender addCommandListener(CommandChannel.CommandChannelListener commandChannelListener, Predicate<Command> commandFilter);

    Sender addNotificationListener(NotificationChannel.NotificationChannelListener notificationChannelListener, Predicate<Notification> commandFilter);
}
