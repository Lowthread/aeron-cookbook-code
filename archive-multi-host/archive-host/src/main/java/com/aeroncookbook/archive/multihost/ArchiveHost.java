package com.aeroncookbook.archive.multihost;

import org.agrona.CloseHelper;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.NoOpIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveHost
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveHost.class);

    public static void main(String[] args)
    {
        final String archiveHost = System.getenv().get("ARCHIVEHOST");
        final String controlPort = System.getenv().get("CONTROLPORT");
        final String eventsPort = System.getenv().get("EVENTSPORT");

        if (archiveHost == null || controlPort == null || eventsPort == null)
        {
            LOGGER.error("requires 3 env vars: ARCHIVEHOST, CONTROLPORT, EVENTSPORT");
        } else
        {
            final int controlChannelPort = Integer.parseInt(controlPort);
            final int recEventsChannelPort = Integer.parseInt(eventsPort);
            final ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
            final ArchiveHostAgent hostAgent = 
                new ArchiveHostAgent(archiveHost, controlChannelPort, recEventsChannelPort);
            final AgentRunner runner =
                new AgentRunner(new NoOpIdleStrategy(), ArchiveHost::errorHandler, null, hostAgent);

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
