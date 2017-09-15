package net.take.blip;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class BlipClientImplTests {

    private OnDemandClientChannel onDemandClientChannel;


    private BlipClientImpl getTarget() {
        this.onDemandClientChannel = mock(OnDemandClientChannel.class);
        return new BlipClientImpl(this.onDemandClientChannel);
    }

    @Test
    public void start_nonStarted_callsEstablish() {

    }
}
