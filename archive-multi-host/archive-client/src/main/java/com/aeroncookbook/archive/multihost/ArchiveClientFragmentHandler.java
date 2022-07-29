package com.aeroncookbook.archive.multihost;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveClientFragmentHandler implements FragmentHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveClientFragmentHandler.class);

    @Override
    public void onFragment(DirectBuffer buffer, int offset, int length, Header header)
    {
        final long read = buffer.getLong(offset);
        LOGGER.info("received {}", read);
    }
}
