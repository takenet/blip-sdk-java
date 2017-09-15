package net.take.blip.samples;

import net.take.blip.BlipClient;
import net.take.blip.BlipClientBuilder;

import java.io.IOException;
import java.util.concurrent.Semaphore;
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
            System.out.println(envelope.getContent().toString());
            try {
                client.sendMessage("Pong!", envelope.getFrom());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, m -> true);

        client.start();

        System.in.read();
    }
}
