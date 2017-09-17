package net.take.blip.samples;

import net.take.blip.BlipClient;
import net.take.blip.BlipClientBuilder;
import org.limeprotocol.Command;
import org.limeprotocol.LimeUri;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Sample {
    public static void main(String[] args) throws InterruptedException, TimeoutException, IOException {

        Semaphore semaphore = new Semaphore(1);
        semaphore.tryAcquire();
        semaphore.release();

        BlipClientBuilder builder = new BlipClientBuilder()
                .usingAccessKey("blipsdkdemo", "Mm10THlXd085aXdjeXlPZlBsbTM=");

        BlipClient client = builder.build();


        client.addMessageListener(envelope -> {
            try {
                client.sendMessage("Pong!", envelope.getFrom());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        client.addMessageListener(envelope -> {
            try {
                client.processCommand(new Command(UUID.randomUUID().toString()){{
                    setMethod(CommandMethod.DELETE);
                    setUri(new LimeUri("/sessions"));
                }}, 5, TimeUnit.SECONDS);
            } catch (IOException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }
        }, m -> m.getContent().toString().equals("disconnect"));

        client.start();

        System.out.println("Client started. Press any key to stop.");
        System.in.read();

        client.stop();

        System.out.println("Client stopped.");
    }
}
