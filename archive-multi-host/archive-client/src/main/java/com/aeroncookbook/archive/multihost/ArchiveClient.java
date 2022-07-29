package com.aeroncookbook.archive.multihost;

import org.agrona.CloseHelper;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.NoOpIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveClient.class);

    public static void main(String[] args)
    {
        final String thisHost = System.getenv().get("THISHOST");
        final String archiveHost = System.getenv().get("ARCHIVEHOST");
        final String controlPort = System.getenv().get("CONTROLPORT");
        final String eventPort = System.getenv().get("EVENTSPORT");

        if (archiveHost == null || controlPort == null || thisHost == null || eventPort == null)
        {
            LOGGER.error("env vars required: THISHOST, ARCHIVEHOST, CONTROLPORT, EVENTSPORT");
        } else
        {
            final int controlChannelPort = Integer.parseInt(controlPort);
            final int eventChannelPort = Integer.parseInt(eventPort);
            final ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
            final ArchiveClientFragmentHandler fragmentHandler = new ArchiveClientFragmentHandler();
            final ArchiveClientAgent hostAgent =
                new ArchiveClientAgent(archiveHost, thisHost, controlChannelPort, eventChannelPort, fragmentHandler);
            final AgentRunner runner =
                new AgentRunner(new NoOpIdleStrategy(), ArchiveClient::errorHandler, null, hostAgent);
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
