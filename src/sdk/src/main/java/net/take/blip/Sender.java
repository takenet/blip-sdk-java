package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Document;
import org.limeprotocol.Message;
import org.limeprotocol.Node;
import org.limeprotocol.network.CommandChannel;
import org.limeprotocol.network.MessageChannel;
import org.limeprotocol.network.NotificationChannel;
import org.limeprotocol.messaging.contents;

import java.io.IOException;
import java.util.UUID;

public interface Sender extends MessageChannel, NotificationChannel, CommandChannel {

    Command processCommand(Command requestCommand);

    default void sendMessage(final String content, final Node to) {


        PlainText plainTextContent = new PlainText();


    }

    default void sendMessage(final Document content, final Node to) throws IOException {
        if (content == null) throw new IllegalArgumentException("Content cannot be null");

        Message message = new Message(UUID.randomUUID().toString()) {{
            setTo(to);
            setContent(content);
        }};

        sendMessage(message);
    }

}
