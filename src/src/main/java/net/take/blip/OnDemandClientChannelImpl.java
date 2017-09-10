package net.take.blip;

import org.limeprotocol.Command;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.network.CommandChannel;
import org.limeprotocol.network.MessageChannel;
import org.limeprotocol.network.NotificationChannel;

import java.io.IOException;
import java.util.Objects;

public class OnDemandClientChannelImpl implements OnDemandClientChannel {

    private final EstablishedClientChannelBuilder establishedClientChannelBuilder;

    public OnDemandClientChannelImpl(EstablishedClientChannelBuilder establishedClientChannelBuilder) {
        this.establishedClientChannelBuilder = establishedClientChannelBuilder;
        Objects.requireNonNull(establishedClientChannelBuilder);
    }

    @Override
    public boolean isEstablished() {
        return false;
    }

    @Override
    public void establish() throws IOException, InterruptedException {

    }

    @Override
    public void finish() throws IOException, InterruptedException {

    }

    @Override
    public void sendCommand(Command command) throws IOException {

    }

    @Override
    public void addCommandListener(CommandChannelListener listener, boolean removeAfterReceive) {

    }

    @Override
    public void removeCommandListener(CommandChannelListener listener) {

    }

    @Override
    public void sendMessage(Message message) throws IOException {

    }

    @Override
    public void addMessageListener(MessageChannelListener listener, boolean removeAfterReceive) {

    }

    @Override
    public void removeMessageListener(MessageChannelListener listener) {

    }

    @Override
    public void sendNotification(Notification notification) throws IOException {

    }

    @Override
    public void addNotificationListener(NotificationChannelListener listener, boolean removeAfterReceive) {

    }

    @Override
    public void removeNotificationListener(NotificationChannelListener listener) {

    }
}
