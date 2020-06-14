// AGRONA FLYWEIGHT GENERATED BY EIDER AT 2020-06-14T13:16:24.508770Z. SPEC: CreateRfqCommandSpec. DO NOT MODIFY
package com.aeroncookbook.cluster.rfq.gen;

import java.lang.String;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public class CreateRfqCommand {
  /**
   * The eider spec id for this type. Useful in switch statements to detect type in first 16bits.
   */
  public static final short EIDER_ID = 5001;

  /**
   * The eider group id for this type. Useful in switch statements to detect group in second 16bits.
   */
  public static final short EIDER_GROUP_ID = 4;

  /**
   * The offset for the EIDER_ID within the buffer.
   */
  private static final int HEADER_OFFSET = 0;

  /**
   * The offset for the EIDER_GROUP_IP within the buffer.
   */
  private static final int HEADER_GROUP_OFFSET = 2;

  /**
   * The length offset. Required for segmented buffers.
   */
  private static final int LENGTH_OFFSET = 4;

  /**
   * The byte offset in the byte array for this INT. Byte length is 4.
   */
  private static final int CORRELATION_OFFSET = 8;

  /**
   * The byte offset in the byte array for this LONG. Byte length is 8.
   */
  private static final int EXPIRETIMEMS_OFFSET = 12;

  /**
   * The byte offset in the byte array for this LONG. Byte length is 8.
   */
  private static final int QUANTITY_OFFSET = 20;

  /**
   * The byte offset in the byte array for this LONG. Byte length is 8.
   */
  private static final int LIMITPRICE_OFFSET = 28;

  /**
   * The byte offset in the byte array for this FIXED_STRING. Byte length is 1.
   */
  private static final int SIDE_OFFSET = 36;

  /**
   * The byte offset in the byte array for this FIXED_STRING. Byte length is 13.
   */
  private static final int CLORDID_OFFSET = 37;

  /**
   * The byte offset in the byte array for this FIXED_STRING. Byte length is 9.
   */
  private static final int CUSIP_OFFSET = 50;

  /**
   * The total bytes required to store the object.
   */
  public static final int BUFFER_LENGTH = 59;

  /**
   * Indicates if this flyweight holds a fixed length object.
   */
  public static final boolean FIXED_LENGTH = true;

  /**
   * The internal DirectBuffer.
   */
  private DirectBuffer buffer = null;

  /**
   * The internal DirectBuffer used for mutatation opertions. Valid only if a mutable buffer was provided.
   */
  private MutableDirectBuffer mutableBuffer = null;

  /**
   * The internal UnsafeBuffer. Valid only if an unsafe buffer was provided.
   */
  private UnsafeBuffer unsafeBuffer = null;

  /**
   * The starting offset for reading and writing.
   */
  private int initialOffset;

  /**
   * Flag indicating if the buffer is mutable.
   */
  private boolean isMutable = false;

  /**
   * Flag indicating if the buffer is an UnsafeBuffer.
   */
  private boolean isUnsafe = false;

  /**
   * Uses the provided {@link org.agrona.DirectBuffer} from the given offset.
   * @param buffer - buffer to read from and write to.
   * @param offset - offset to begin reading from/writing to in the buffer.
   */
  public void setUnderlyingBuffer(DirectBuffer buffer, int offset) {
    this.initialOffset = offset;
    this.buffer = buffer;
    if (buffer instanceof UnsafeBuffer) {
      unsafeBuffer = (UnsafeBuffer) buffer;
      mutableBuffer = (MutableDirectBuffer) buffer;
      isUnsafe = true;
      isMutable = true;
    }
    else if (buffer instanceof MutableDirectBuffer) {
      mutableBuffer = (MutableDirectBuffer) buffer;
      isUnsafe = false;
      isMutable = true;
    }
    else {
      isUnsafe = false;
      isMutable = false;
    }
    buffer.checkLimit(initialOffset + BUFFER_LENGTH);
  }

  /**
   * Uses the provided {@link org.agrona.DirectBuffer} from the given offset.
   * @param buffer - buffer to read from and write to.
   * @param offset - offset to begin reading from/writing to in the buffer.
   */
  public void setBufferWriteHeader(DirectBuffer buffer, int offset) {
    setUnderlyingBuffer(buffer, offset);
    writeHeader();
  }

  /**
   * Returns the eider sequence.
   * @return EIDER_ID.
   */
  public short eiderId() {
    return EIDER_ID;
  }

  /**
   * Writes the header data to the buffer.
   */
  public void writeHeader() {
    if (!isMutable) throw new RuntimeException("cannot write to immutable buffer");
    mutableBuffer.putShort(initialOffset + HEADER_OFFSET, EIDER_ID, java.nio.ByteOrder.LITTLE_ENDIAN);
    mutableBuffer.putShort(initialOffset + HEADER_GROUP_OFFSET, EIDER_GROUP_ID, java.nio.ByteOrder.LITTLE_ENDIAN);
    mutableBuffer.putInt(initialOffset + LENGTH_OFFSET, BUFFER_LENGTH, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Validates the length and eiderSpecId in the header against the expected values. False if invalid.
   */
  public boolean validateHeader() {
    final short eiderId = buffer.getShort(initialOffset + HEADER_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
    final short eiderGroupId = buffer.getShort(initialOffset + HEADER_GROUP_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
    final int bufferLength = buffer.getInt(initialOffset + LENGTH_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
    if (eiderId != EIDER_ID) return false;
    if (eiderGroupId != EIDER_GROUP_ID) return false;
    return bufferLength == BUFFER_LENGTH;
  }

  /**
   * Reads correlation as stored in the buffer.
   */
  public int readCorrelation() {
    return buffer.getInt(initialOffset + CORRELATION_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Writes correlation to the buffer. 
   * @param value Value for the correlation to write to buffer.
   */
  public void writeCorrelation(int value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    mutableBuffer.putInt(initialOffset + CORRELATION_OFFSET, value, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Reads expireTimeMs as stored in the buffer.
   */
  public long readExpireTimeMs() {
    return buffer.getLong(initialOffset + EXPIRETIMEMS_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Writes expireTimeMs to the buffer. 
   * @param value Value for the expireTimeMs to write to buffer.
   */
  public void writeExpireTimeMs(long value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    mutableBuffer.putLong(initialOffset + EXPIRETIMEMS_OFFSET, value, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Reads quantity as stored in the buffer.
   */
  public long readQuantity() {
    return buffer.getLong(initialOffset + QUANTITY_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Writes quantity to the buffer. 
   * @param value Value for the quantity to write to buffer.
   */
  public void writeQuantity(long value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    mutableBuffer.putLong(initialOffset + QUANTITY_OFFSET, value, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Reads limitPrice as stored in the buffer.
   */
  public long readLimitPrice() {
    return buffer.getLong(initialOffset + LIMITPRICE_OFFSET, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Writes limitPrice to the buffer. 
   * @param value Value for the limitPrice to write to buffer.
   */
  public void writeLimitPrice(long value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    mutableBuffer.putLong(initialOffset + LIMITPRICE_OFFSET, value, java.nio.ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * Reads side as stored in the buffer.
   */
  public String readSide() {
    return buffer.getStringWithoutLengthAscii(initialOffset + SIDE_OFFSET, 1).trim();
  }

  /**
   * Writes side to the buffer. Warning! Does not pad the string.
   * @param value Value for the side to write to buffer.
   */
  public void writeSide(String value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    if (value.length() > 1) throw new RuntimeException("Field side is longer than maxLength=1");
    mutableBuffer.putStringWithoutLengthAscii(initialOffset + SIDE_OFFSET, value);
  }

  /**
   * Writes side to the buffer with padding. 
   * @param value Value for the side to write to buffer.
   */
  public void writeSideWithPadding(String value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    if (value.length() > 1) throw new RuntimeException("Field side is longer than maxLength=1");
    final String padded = String.format("%1s", value);
    mutableBuffer.putStringWithoutLengthAscii(initialOffset + SIDE_OFFSET, padded);
  }

  /**
   * Reads clOrdId as stored in the buffer.
   */
  public String readClOrdId() {
    return buffer.getStringWithoutLengthAscii(initialOffset + CLORDID_OFFSET, 13).trim();
  }

  /**
   * Writes clOrdId to the buffer. Warning! Does not pad the string.
   * @param value Value for the clOrdId to write to buffer.
   */
  public void writeClOrdId(String value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    if (value.length() > 13) throw new RuntimeException("Field clOrdId is longer than maxLength=13");
    mutableBuffer.putStringWithoutLengthAscii(initialOffset + CLORDID_OFFSET, value);
  }

  /**
   * Writes clOrdId to the buffer with padding. 
   * @param value Value for the clOrdId to write to buffer.
   */
  public void writeClOrdIdWithPadding(String value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    if (value.length() > 13) throw new RuntimeException("Field clOrdId is longer than maxLength=13");
    final String padded = String.format("%13s", value);
    mutableBuffer.putStringWithoutLengthAscii(initialOffset + CLORDID_OFFSET, padded);
  }

  /**
   * Reads cusip as stored in the buffer.
   */
  public String readCusip() {
    return buffer.getStringWithoutLengthAscii(initialOffset + CUSIP_OFFSET, 9).trim();
  }

  /**
   * Writes cusip to the buffer. Warning! Does not pad the string.
   * @param value Value for the cusip to write to buffer.
   */
  public void writeCusip(String value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    if (value.length() > 9) throw new RuntimeException("Field cusip is longer than maxLength=9");
    mutableBuffer.putStringWithoutLengthAscii(initialOffset + CUSIP_OFFSET, value);
  }

  /**
   * Writes cusip to the buffer with padding. 
   * @param value Value for the cusip to write to buffer.
   */
  public void writeCusipWithPadding(String value) {
    if (!isMutable) throw new RuntimeException("Cannot write to immutable buffer");
    if (value.length() > 9) throw new RuntimeException("Field cusip is longer than maxLength=9");
    final String padded = String.format("%9s", value);
    mutableBuffer.putStringWithoutLengthAscii(initialOffset + CUSIP_OFFSET, padded);
  }

  /**
   * True if transactions are supported; false if not.
   */
  public boolean supportsTransactions() {
    return false;
  }
}
