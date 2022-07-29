package com.aeroncookbook.aeron.mdc;

import org.agrona.CloseHelper;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiDestinationSubscriber
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiDestinationSubscriber.class);

    public static void main(String[] args)
    {
        final String thisHost = System.getenv().get("THISHOST");
        final String mdcHost = System.getenv().get("MDCHOST");
        final String controlPort = System.getenv().get("CONTROLPORT");

        if (mdcHost == null || controlPort == null || thisHost == null)
        {
            LOGGER.error("env vars required: THISHOST, MDCHOST, CONTROLPORT");
        } else
        {
            final int controlChannelPort = Integer.parseInt(controlPort);
            final ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
            final MultiDestinationSubscriberAgent hostAgent =
                new MultiDestinationSubscriberAgent(mdcHost, thisHost, controlChannelPort);
            final AgentRunner runner =
                new AgentRunner(new SleepingMillisIdleStrategy(), MultiDestinationSubscriber::errorHandler,
                    null, hostAgent);
            AgentRunner.startOnThread(runner);

            barrier.await();

            CloseHelper.quietClose(runner);
        }
    }

    private static void errorHandler(Throwable throwable)
    {
        LOGGER.error("agent error {}", throwable.getMessage(), throwable);
    }
}
