package com.aeroncookbook.archive.multihost;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.archive.Archive;
import io.aeron.archive.ArchiveThreadingMode;
import io.aeron.archive.ArchivingMediaDriver;
import io.aeron.archive.client.AeronArchive;
import io.aeron.archive.client.RecordingEventsAdapter;
import io.aeron.archive.client.RecordingSignalAdapter;
import io.aeron.archive.codecs.SourceLocation;
import io.aeron.archive.status.RecordingPos;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import org.agrona.CloseHelper;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.EpochClock;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.agrona.concurrent.SystemEpochClock;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.status.CountersReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Objects;

public class ArchiveHostAgent implements Agent
{
    public static final String AERON_UDP_ENDPOINT = "aeron:udp?endpoint=";
    private static final EpochClock CLOCK = SystemEpochClock.INSTANCE;
    private static final int STREAM_ID = 100;
    private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveHostAgent.class);
    private final ArchivingMediaDriver archivingMediaDriver;
    private final Aeron aeron;
    private final IdleStrategy idleStrategy;
    private final String host;
    private final int controlChannelPort;
    private final int recordingEventsPort;
    private final MutableDirectBuffer mutableDirectBuffer;
    private RecordingSignalAdapter recordingSignalAdapter;
    private RecordingEventsAdapter recordingEventsAdapter;
    private AeronArchive archive;
    private Publication publication;
    private long nextAppend = Long.MIN_VALUE;
    private long lastSeq = 0;
    private State currentState;

    public ArchiveHostAgent(String host, int controlChannelPort, int recordingEventsPort)
    {
        this.host = localHost(host);
        this.controlChannelPort = controlChannelPort;
        this.recordingEventsPort = recordingEventsPort;
        this.idleStrategy = new SleepingMillisIdleStrategy();
        this.archivingMediaDriver = launchMediaDriver(host, controlChannelPort, recordingEventsPort);
        this.mutableDirectBuffer = new UnsafeBuffer(ByteBuffer.allocate(Long.BYTES));
        this.aeron = launchAeron(archivingMediaDriver);
        LOGGER.info("Media Driver directory is {}; Archive directory is {}",
            archivingMediaDriver.mediaDriver().aeronDirectoryName(),
            archivingMediaDriver.archive().context().archiveDirectoryName());
        currentState = State.AERON_READY;
    }

    private Aeron launchAeron(ArchivingMediaDriver archivingMediaDriver)
    {
        LOGGER.info("launching aeron");
        return Aeron.connect(new Aeron.Context()
            .aeronDirectoryName(archivingMediaDriver.mediaDriver().aeronDirectoryName())
            .errorHandler(this::errorHandler)
            .idleStrategy(new SleepingMillisIdleStrategy()));
    }

    private ArchivingMediaDriver launchMediaDriver(String host, int controlChannelPort, int recordingEventsPort)
    {
        LOGGER.info("launching ArchivingMediaDriver");
        final String controlChannel = AERON_UDP_ENDPOINT + host + ":" + controlChannelPort;
        final String recordingEventsChannel =
            "aeron:udp?control-mode=dynamic|control=" + host + ":" + recordingEventsPort;

        final Archive.Context archiveContext = new Archive.Context()
            .deleteArchiveOnStart(true)
            .errorHandler(this::errorHandler)
            .controlChannel(controlChannel)
            .recordingEventsChannel(recordingEventsChannel)
            .idleStrategySupplier(SleepingMillisIdleStrategy::new)
            .threadingMode(ArchiveThreadingMode.SHARED);

        final MediaDriver.Context mediaDriverContext = new MediaDriver.Context()
            .spiesSimulateConnection(true)
            .errorHandler(this::errorHandler)
            .threadingMode(ThreadingMode.SHARED)
            .sharedIdleStrategy(new SleepingMillisIdleStrategy())
            .dirDeleteOnStart(true);

        return ArchivingMediaDriver.launch(mediaDriverContext, archiveContext);
    }

    private void errorHandler(Throwable throwable)
    {
        LOGGER.error("unexpected failure {}", throwable.getMessage(), throwable);
    }

    @Override
    public void onStart()
    {
        LOGGER.info("Starting up");
        Agent.super.onStart();
    }

    @Override
    public int doWork()
    {
        switch (currentState)
        {
            case AERON_READY:
                createArchiveAndRecord();
                break;
            case ARCHIVE_READY:
                appendData();
                break;
            default:
                LOGGER.info("unknown state {}", currentState);
        }

        if (recordingSignalAdapter != null)
        {
            recordingSignalAdapter.poll();
        }

        if (recordingEventsAdapter != null)
        {
            recordingEventsAdapter.poll();
        }

        return 0;
    }

    private void appendData()
    {
        lastSeq += 1;
        mutableDirectBuffer.putLong(0, lastSeq);
        publication.offer(mutableDirectBuffer, 0, Long.BYTES);
        nextAppend = CLOCK.time() + 2000;
        LOGGER.info("appended {}", lastSeq);
    }

    private void createArchiveAndRecord()
    {
        Objects.requireNonNull(archivingMediaDriver);
        Objects.requireNonNull(aeron);

        final String controlRequestChannel = AERON_UDP_ENDPOINT + host + ":" + controlChannelPort;
        final String controlResponseChannel = AERON_UDP_ENDPOINT + host + ":0";
        final String recordingEventsChannel = 
            "aeron:udp?control-mode=dynamic|control=" + host + ":" + recordingEventsPort;

        LOGGER.info("creating archive");

        archive = AeronArchive.connect(new AeronArchive.Context()
            .aeron(aeron)
            .controlRequestChannel(controlRequestChannel)
            .controlResponseChannel(controlResponseChannel)
            .recordingEventsChannel(recordingEventsChannel)
            .idleStrategy(new SleepingMillisIdleStrategy()));

        LOGGER.info("creating publication");

        publication = aeron.addExclusivePublication("aeron:ipc", STREAM_ID);

        LOGGER.info("starting recording");

        archive.startRecording("aeron:ipc", STREAM_ID, SourceLocation.LOCAL);

        LOGGER.info("waiting for recording to start for session {}", publication.sessionId());
        final CountersReader counters = aeron.countersReader();
        int counterId = RecordingPos.findCounterIdBySession(counters, publication.sessionId());
        while (CountersReader.NULL_COUNTER_ID == counterId)
        {
            idleStrategy.idle();
            counterId = RecordingPos.findCounterIdBySession(counters, publication.sessionId());
        }
        final long recordingId = RecordingPos.getRecordingId(counters, counterId);

        final ArchiveActivityListener activityListener = new ArchiveActivityListener();
        recordingSignalAdapter = new RecordingSignalAdapter(archive.controlSessionId(), activityListener,
            activityListener, archive.controlResponsePoller().subscription(), 10);

        final String recordingChannel = archive.context().recordingEventsChannel();
        final int recordingStreamId = archive.context().recordingEventsStreamId();
        final Subscription recordingEvents = aeron.addSubscription(recordingChannel, recordingStreamId);
        final ArchiveProgressListener progressListener = new ArchiveProgressListener();
        recordingEventsAdapter = new RecordingEventsAdapter(progressListener, recordingEvents, 10);

        LOGGER.info("archive recording started; recording id is {}", recordingId);

        this.currentState = State.ARCHIVE_READY;
    }

    @Override
    public void onClose()
    {
        Agent.super.onClose();
        this.currentState = State.SHUTTING_DOWN;
        LOGGER.info("Shutting down");
        CloseHelper.quietClose(publication);
        CloseHelper.quietClose(aeron);
        CloseHelper.quietClose(archivingMediaDriver);
    }

    public String localHost(String fallback)
    {
        try
        {
            final Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (interfaceEnumeration.hasMoreElements())
            {
                final NetworkInterface networkInterface = interfaceEnumeration.nextElement();

                if (networkInterface.getName().startsWith("eth0"))
                {
                    Enumeration<InetAddress> interfaceAddresses = networkInterface.getInetAddresses();
                    while (interfaceAddresses.hasMoreElements())
                    {
                        InetAddress inet4Address = interfaceAddresses.nextElement();
                        if (inet4Address instanceof Inet4Address)
                        {
                            LOGGER.info("detected ip4 address as {}", inet4Address.getHostAddress());
                            return inet4Address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e)
        {
            LOGGER.info("Failed to get address");
        }
        return fallback;
    }

    @Override
    public String roleName()
    {
        return "agent-host";
    }

}
