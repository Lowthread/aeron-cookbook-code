package com.aeroncookbook.aeron.mdc;

import org.agrona.CloseHelper;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiDestinationPublisher
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiDestinationPublisher.class);

    public static void main(String[] args)
    {
        final String mdcHost = System.getenv().get("MDCHOST");
        final String controlPort = System.getenv().get("CONTROLPORT");

        if (mdcHost == null || controlPort == null)
        {
            LOGGER.error("requires 2 env vars: MDCHOST, CONTROLPORT");
        } else
        {
            final int controlChannelPort = Integer.parseInt(controlPort);
            final ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
            final MultiDestinationPublisherAgent hostAgent = 
                new MultiDestinationPublisherAgent(mdcHost, controlChannelPort);
            final AgentRunner runner =
                new AgentRunner(new SleepingMillisIdleStrategy(), MultiDestinationPublisher::errorHandler,
                    null, hostAgent);

            AgentRunner.startOnThread(runner);

            barrier.await();

            CloseHelper.quietClose(runner);
        }
    }

    private static void errorHandler(Throwable throwable)
    {
        LOGGER.error("agent failure {}", throwable.getMessage(), throwable);
    }
}
