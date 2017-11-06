package net.take.blip;

import org.limeprotocol.*;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.network.CommandProcessor;

import java.io.IOException;
import java.util.UUID;

/**
 * Defines a service for sending messages, notifications and commands through an active connection.
 */
public interface Sender extends CommandProcessor {

    /**
     * Sends a text message through the current session.
     * @param content
     * @param to
     * @throws IOException
     */
    default void sendMessage(final String content, final Node to) throws IOException {
        sendMessage(new PlainText(content), to);
    }

    /**
     * Sends a document message thought the current session.
     * @param content
     * @param to
     * @throws IOException
     */
    default void sendMessage(final Document content, final Node to) throws IOException {
        if (content == null) throw new IllegalArgumentException("Content is required");

        Message message = new Message() {{
            setId(UUID.randomUUID().toString());
            setTo(to);
            setContent(content);
        }};

        sendMessage(message);
    }

    /**
     * Sends a message through the current session.
     * @param message
     * @throws IOException
     */
    void sendMessage(Message message) throws IOException;

    /**
     * Sends a commands though the current session.
     * This method is asynchronous and if the command is a request and it is required to wait for the response, use the 'processCommand' method instead.
     * @param command
     * @throws IOException
     */
    void sendCommand(Command command) throws IOException;

    /**
     * Sends a notification though the current session.
     * @param notification
     * @throws IOException
     */
    void sendNotification(Notification notification) throws IOException;
}
