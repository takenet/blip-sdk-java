package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.network.CommandChannel;
import org.limeprotocol.network.MessageChannel;
import org.limeprotocol.network.NotificationChannel;

import java.util.function.Consumer;

public class BlipClientImp implements BlipClient {
    private final Consumer<ClientChannel> clientChannelFactory;

    public BlipClientImp(Consumer<ClientChannel> clientChannelFactory) {

        this.clientChannelFactory = clientChannelFactory;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Command processCommand(Command requestCommand) {
        return null;
    }

    @Override
    public void sendMessage(Message message) {

    }

    @Override
    public void sendCommand(Command command) {

    }

    @Override
    public void sendNotification(Notification notification) {

    }

    @Override
    public void addMessageListener(MessageChannel.MessageChannelListener messageChannelListener) {

    }

    @Override
    public void addCommandListener(CommandChannel.CommandChannelListener commandChannelListener) {

    }

    @Override
    public void addNotificationListener(NotificationChannel.NotificationChannelListener notificationChannelListener) {

    }
}
