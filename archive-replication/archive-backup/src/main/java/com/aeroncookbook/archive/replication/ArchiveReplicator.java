package com.aeroncookbook.archive.replication;

import org.agrona.CloseHelper;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveReplicator
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveReplicator.class);

    public static void main(String[] args)
    {
        final String archiveHost = System.getenv().get("ARCHIVEHOST");
        final String thisHost = System.getenv().get("THISHOST");
        final String controlPort = System.getenv().get("CONTROLPORT");
        final String eventsPort = System.getenv().get("EVENTSPORT");
        final String replayPort = System.getenv().get("REPLAYPORT");

        if (archiveHost == null || controlPort == null || eventsPort == null)
        {
            LOGGER.error("requires 5 env vars: ARCHIVEHOST, THISHOST, CONTROLPORT, EVENTSPORT, REPLAYPORT");
        } else
        {
            final int controlChannelPort = Integer.parseInt(controlPort);
            final int recEventsChannelPort = Integer.parseInt(eventsPort);
            final int replayChannelPort = Integer.parseInt(replayPort);
            final ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
            final ArchiveReplicatorAgent hostAgent = 
                new ArchiveReplicatorAgent(thisHost, archiveHost, controlChannelPort,
                recEventsChannelPort, replayChannelPort);
            final AgentRunner runner =
                new AgentRunner(new SleepingMillisIdleStrategy(), ArchiveReplicator::errorHandler, null, hostAgent);

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
