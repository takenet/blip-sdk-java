package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Document;
import org.limeprotocol.Message;
import org.limeprotocol.Node;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.network.CommandChannel;
import org.limeprotocol.network.MessageChannel;
import org.limeprotocol.network.NotificationChannel;

import java.io.IOException;
import java.util.UUID;

public interface Sender extends MessageChannel, NotificationChannel, CommandChannel {
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
}
