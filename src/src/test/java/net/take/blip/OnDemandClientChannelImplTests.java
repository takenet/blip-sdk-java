package net.take.blip;

import org.limeprotocol.client.EstablishedClientChannelBuilder;
import org.limeprotocol.client.OnDemandClientChannelImpl;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

public class OnDemandClientChannelImplTests {

    private EstablishedClientChannelBuilder establishedClientChannelBuilder;
    private long defaultTimeout;
    private TimeUnit defaultTimeoutTimeUnit;

    private OnDemandClientChannelImpl getTarget() {
        establishedClientChannelBuilder = mock(EstablishedClientChannelBuilder.class);
        defaultTimeout = 30;
        defaultTimeoutTimeUnit = TimeUnit.SECONDS;
        return new OnDemandClientChannelImpl(establishedClientChannelBuilder, defaultTimeout, defaultTimeoutTimeUnit);
    }
}
