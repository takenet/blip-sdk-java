package net.take.blip;

import org.junit.Test;
import org.limeprotocol.client.OnDemandClientChannel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

public class BlipClientImplTests {

    private OnDemandClientChannel onDemandClientChannel;

    private BlipClientImpl getTarget() {
        this.onDemandClientChannel = mock(OnDemandClientChannel.class);
        return new BlipClientImpl(this.onDemandClientChannel);
    }

    @Test
    public void start_notStarted_callsEstablish() throws InterruptedException, TimeoutException, IOException {
        // Arrange
        BlipClientImpl target = getTarget();

        // Act
        target.start();

        // Assert
        verify(onDemandClientChannel, times(1)).establish();
    }
}
