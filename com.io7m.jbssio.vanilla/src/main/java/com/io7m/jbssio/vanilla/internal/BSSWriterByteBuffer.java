/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jbssio.vanilla.internal;

import com.io7m.ieee754b16.Binary16;
import com.io7m.jbssio.api.BSSExceptionConstructorType;
import com.io7m.jbssio.api.BSSWriterRandomAccessType;
import com.io7m.seltzer.api.SStructuredErrorType;
import com.io7m.seltzer.io.SClosedChannelException;
import com.io7m.seltzer.io.SEOFException;
import com.io7m.seltzer.io.SIOException;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.Callable;

import static com.io7m.jbssio.vanilla.internal.BSSPaths.PATH_SEPARATOR;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * A random access writer based on a byte buffer.
 */

public final class BSSWriterByteBuffer
  extends BSSRandomAccess<BSSWriterRandomAccessType>
  implements BSSWriterRandomAccessType
{
  private final ByteBuffer map;
  private final BSSRangeHalfOpen physicalBounds;

  private BSSWriterByteBuffer(
    final BSSWriterByteBuffer inParent,
    final URI inURI,
    final BSSRangeHalfOpen inParentRangeRelative,
    final String inName,
    final ByteBuffer inMap,
    final Callable<Void> inOnClose)
  {
    super(
      inParent,
      inParentRangeRelative,
      inOnClose,
      inURI,
      inName);

    this.map =
      Objects.requireNonNull(inMap, "map");
    this.physicalBounds =
      BSSRangeHalfOpen.create(0L, inMap.capacity());
  }

  /**
   * Create a writer.
   *
   * @param uri    The target URI
   * @param buffer The target buffer
   * @param name   The name
   *
   * @return A writer
   */

  public static BSSWriterRandomAccessType createFromByteBuffer(
    final URI uri,
    final ByteBuffer buffer,
    final String name)
  {
    return new BSSWriterByteBuffer(
      null,
      uri,
      new BSSRangeHalfOpen(
        0L,
        OptionalLong.of(Integer.toUnsignedLong(buffer.capacity()))),
      name,
      buffer,
      () -> null);
  }

  private static int longPositionTo2GBLimitedByteBufferPosition(final long position)
  {
    return Math.toIntExact(position);
  }

  @Override
  public BSSWriterRandomAccessType createSubWriterAt(
    final String inName,
    final long offset)
    throws SEOFException, SClosedChannelException
  {
    Objects.requireNonNull(inName, "path");

    this.checkNotClosed();

    final var newName =
      new StringBuilder(32)
        .append(this.path())
        .append(PATH_SEPARATOR)
        .append(inName)
        .toString();

    return new BSSWriterByteBuffer(
      this,
      this.uri,
      this.createOffsetSubRange(offset),
      newName,
      this.map,
      () -> null);
  }

  @Override
  public BSSWriterRandomAccessType createSubWriterAtBounded(
    final String inName,
    final long offset,
    final long size)
    throws SEOFException, SClosedChannelException
  {
    Objects.requireNonNull(inName, "path");

    this.checkNotClosed();

    final var newName =
      new StringBuilder(32)
        .append(this.path())
        .append(PATH_SEPARATOR)
        .append(inName)
        .toString();

    return new BSSWriterByteBuffer(
      this,
      this.uri,
      this.createSubRange(offset, size),
      newName,
      this.map,
      () -> null);
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[BSSWriterByteBuffer ")
      .append(this.uri())
      .append(" ")
      .append(this.path())
      .append("]")
      .toString();
  }

  private void writeS8p(
    final String name,
    final int b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 1L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(1L);
    this.map.put(
      longPositionTo2GBLimitedByteBufferPosition(position),
      (byte) b);
  }

  private void writeU8p(
    final String name,
    final int b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 1L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(1L);
    this.map.put(
      longPositionTo2GBLimitedByteBufferPosition(position),
      (byte) (b & 0xff));
  }

  @Override
  public void writeS8(final int b)
    throws SIOException
  {
    this.writeS8p(null, b);
  }

  @Override
  public void writeU8(final int b)
    throws SIOException
  {
    this.writeU8p(null, b);
  }

  @Override
  public void writeS8(
    final String name,
    final int b)
    throws SIOException
  {
    this.writeS8p(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU8(
    final String name,
    final int b)
    throws SIOException
  {
    this.writeU8p(Objects.requireNonNull(name, "name"), b);
  }

  private void writeS16LEp(
    final String name,
    final int b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putShort(
      longPositionTo2GBLimitedByteBufferPosition(position),
      (short) b);
  }

  private void writeU16LEp(
    final String name,
    final int b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putChar(
      longPositionTo2GBLimitedByteBufferPosition(position),
      (char) (b & 0xffff));
  }

  private void writeS16BEp(
    final String name,
    final int b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(BIG_ENDIAN);
    this.map.putShort(
      longPositionTo2GBLimitedByteBufferPosition(position),
      (short) b);
  }

  private void writeU16BEp(
    final String name,
    final int b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(BIG_ENDIAN);
    this.map.putChar(
      longPositionTo2GBLimitedByteBufferPosition(position),
      (char) (b & 0xffff));
  }

  @Override
  public void writeS16LE(final int b)
    throws SIOException
  {
    this.writeS16LEp(null, b);
  }

  @Override
  public void writeS16BE(final int b)
    throws SIOException
  {
    this.writeS16BEp(null, b);
  }

  @Override
  public void writeU16LE(final int b)
    throws SIOException
  {
    this.writeU16LEp(null, b);
  }

  @Override
  public void writeU16BE(final int b)
    throws SIOException
  {
    this.writeU16BEp(null, b);
  }

  @Override
  public void writeS16LE(
    final String name,
    final int b)
    throws SIOException
  {
    this.writeS16LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeS16BE(
    final String name,
    final int b)
    throws SIOException
  {
    this.writeS16BEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU16LE(
    final String name,
    final int b)
    throws SIOException
  {
    this.writeU16LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU16BE(
    final String name,
    final int b)
    throws SIOException
  {
    this.writeU16BEp(Objects.requireNonNull(name, "name"), b);
  }

  private void writeS32LEp(
    final String name,
    final long b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putInt(
      longPositionTo2GBLimitedByteBufferPosition(position),
      (int) b);
  }

  private void writeU32LEp(
    final String name,
    final long b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putInt(
      longPositionTo2GBLimitedByteBufferPosition(position),
      (int) (b & 0xffff_ffffL));
  }

  private void writeS32BEp(
    final String name,
    final long b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(BIG_ENDIAN);
    this.map.putInt(
      longPositionTo2GBLimitedByteBufferPosition(position),
      (int) b);
  }

  private void writeU32BEp(
    final String name,
    final long b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(BIG_ENDIAN);
    this.map.putInt(
      longPositionTo2GBLimitedByteBufferPosition(position),
      (int) (b & 0xffff_ffffL));
  }

  @Override
  public void writeS32LE(final long b)
    throws SIOException
  {
    this.writeS32LEp(null, b);
  }

  @Override
  public void writeS32BE(final long b)
    throws SIOException
  {
    this.writeS32BEp(null, b);
  }

  @Override
  public void writeU32LE(final long b)
    throws SIOException
  {
    this.writeU32LEp(null, b);
  }

  @Override
  public void writeU32BE(final long b)
    throws SIOException
  {
    this.writeU32BEp(null, b);
  }

  @Override
  public void writeS32LE(
    final String name,
    final long b)
    throws SIOException
  {
    this.writeS32LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeS32BE(
    final String name,
    final long b)
    throws SIOException
  {
    this.writeS32BEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU32LE(
    final String name,
    final long b)
    throws SIOException
  {
    this.writeU32LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU32BE(
    final String name,
    final long b)
    throws SIOException
  {
    this.writeU32BEp(Objects.requireNonNull(name, "name"), b);
  }

  private void writeS64LEp(
    final String name,
    final long b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putLong(longPositionTo2GBLimitedByteBufferPosition(position), b);
  }

  private void writeU64LEp(
    final String name,
    final long b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putLong(longPositionTo2GBLimitedByteBufferPosition(position), b);
  }

  private void writeS64BEp(
    final String name,
    final long b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(BIG_ENDIAN);
    this.map.putLong(longPositionTo2GBLimitedByteBufferPosition(position), b);
  }

  private void writeU64BEp(
    final String name,
    final long b)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(BIG_ENDIAN);
    this.map.putLong(longPositionTo2GBLimitedByteBufferPosition(position), b);
  }

  @Override
  public void writeS64LE(final long b)
    throws SIOException
  {
    this.writeS64LEp(null, b);
  }

  @Override
  public void writeS64BE(final long b)
    throws SIOException
  {
    this.writeS64BEp(null, b);
  }

  @Override
  public void writeU64LE(final long b)
    throws SIOException
  {
    this.writeU64LEp(null, b);
  }

  @Override
  public void writeU64BE(final long b)
    throws SIOException
  {
    this.writeU64BEp(null, b);
  }

  @Override
  public void writeS64LE(
    final String name,
    final long b)
    throws SIOException
  {
    this.writeS64LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeS64BE(
    final String name,
    final long b)
    throws SIOException
  {
    this.writeS64BEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU64LE(
    final String name,
    final long b)
    throws SIOException
  {
    this.writeU64LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU64BE(
    final String name,
    final long b)
    throws SIOException
  {
    this.writeU64BEp(Objects.requireNonNull(name, "name"), b);
  }

  private void writeBytesP(
    final String name,
    final byte[] buffer,
    final int offset,
    final int length)
    throws SIOException
  {
    Objects.requireNonNull(buffer, "buffer");
    this.checkNotClosed();
    final var llength = Integer.toUnsignedLong(length);
    this.checkHasBytesRemaining(name, llength);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(llength);
    this.map.position(longPositionTo2GBLimitedByteBufferPosition(position));
    this.map.put(buffer, offset, length);
  }

  @Override
  public void writeBytes(
    final String name,
    final byte[] buffer)
    throws SIOException
  {
    this.writeBytesP(
      Objects.requireNonNull(name, "name"),
      buffer,
      0,
      buffer.length);
  }

  @Override
  public void writeBytes(
    final String name,
    final byte[] buffer,
    final int offset,
    final int length)
    throws SIOException
  {
    this.writeBytesP(name, buffer, offset, length);
  }

  @Override
  public void writeBytes(final byte[] buffer)
    throws SIOException
  {
    this.writeBytesP(null, buffer, 0, buffer.length);
  }

  @Override
  public void writeBytes(
    final byte[] buffer,
    final int offset,
    final int length)
    throws SIOException
  {
    this.writeBytesP(null, buffer, offset, length);
  }

  private void writeF64p(
    final String name,
    final double b,
    final ByteOrder order)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(order);
    this.map.putDouble(longPositionTo2GBLimitedByteBufferPosition(position), b);
  }

  private void writeF32p(
    final String name,
    final double b,
    final ByteOrder order)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(order);
    this.map.putFloat(
      longPositionTo2GBLimitedByteBufferPosition(position),
      (float) b);
  }

  private void writeF16p(
    final String name,
    final double b,
    final ByteOrder order)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(order);
    this.map.putChar(
      longPositionTo2GBLimitedByteBufferPosition(position),
      Binary16.packDouble(b));
  }

  @Override
  public void writeF64BE(
    final String name,
    final double b)
    throws SIOException
  {
    this.writeF64p(Objects.requireNonNull(name, "name"), b, BIG_ENDIAN);
  }

  @Override
  public void writeF64BE(final double b)
    throws SIOException
  {
    this.writeF64p(null, b, BIG_ENDIAN);
  }

  @Override
  public void writeF32BE(
    final String name,
    final double b)
    throws SIOException
  {
    this.writeF32p(Objects.requireNonNull(name, "name"), b, BIG_ENDIAN);
  }

  @Override
  public void writeF32BE(final double b)
    throws SIOException
  {
    this.writeF32p(null, b, BIG_ENDIAN);
  }

  @Override
  public void writeF64LE(
    final String name,
    final double b)
    throws SIOException
  {
    this.writeF64p(Objects.requireNonNull(name, "name"), b, LITTLE_ENDIAN);
  }

  @Override
  public void writeF64LE(final double b)
    throws SIOException
  {
    this.writeF64p(null, b, LITTLE_ENDIAN);
  }

  @Override
  public void writeF32LE(
    final String name,
    final double b)
    throws SIOException
  {
    this.writeF32p(Objects.requireNonNull(name, "name"), b, LITTLE_ENDIAN);
  }

  @Override
  public void writeF32LE(final double b)
    throws SIOException
  {
    this.writeF32p(null, b, LITTLE_ENDIAN);
  }

  @Override
  public void writeF16BE(
    final String name,
    final double b)
    throws SIOException
  {
    this.writeF16p(Objects.requireNonNull(name, "name"), b, BIG_ENDIAN);
  }

  @Override
  public void writeF16BE(final double b)
    throws SIOException
  {
    this.writeF16p(null, b, BIG_ENDIAN);
  }

  @Override
  public void writeF16LE(
    final String name,
    final double b)
    throws SIOException
  {
    this.writeF16p(Objects.requireNonNull(name, "name"), b, LITTLE_ENDIAN);
  }

  @Override
  public void writeF16LE(final double b)
    throws SIOException
  {
    this.writeF16p(null, b, LITTLE_ENDIAN);
  }

  @Override
  protected BSSRangeHalfOpen physicalSourceAbsoluteBounds()
  {
    return this.physicalBounds;
  }

  @Override
  public <E extends Exception & SStructuredErrorType<String>> E createException(
    final String message,
    final Throwable cause,
    final Map<String, String> attributes,
    final BSSExceptionConstructorType<E> constructor)
  {
    return BSSExceptions.create(
      this,
      Optional.of(cause),
      message,
      attributes,
      constructor
    );
  }
}
