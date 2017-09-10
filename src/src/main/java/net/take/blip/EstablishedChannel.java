package net.take.blip;

import org.limeprotocol.network.CommandChannel;
import org.limeprotocol.network.MessageChannel;
import org.limeprotocol.network.NotificationChannel;

public interface EstablishedChannel extends MessageChannel, NotificationChannel, CommandChannel {

}
