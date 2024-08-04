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
import com.io7m.jbssio.api.BSSReaderRandomAccessType;
import com.io7m.seltzer.api.SStructuredErrorType;
import com.io7m.seltzer.io.SIOException;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.Callable;

import static com.io7m.jbssio.vanilla.internal.BSSPaths.PATH_SEPARATOR;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * A random access reader based on a seekable byte channel.
 */

public final class BSSReaderSeekableChannel
  extends BSSRandomAccess<BSSReaderRandomAccessType>
  implements BSSReaderRandomAccessType
{
  private final SeekableByteChannel channel;
  private final ByteBuffer buffer;

  private BSSReaderSeekableChannel(
    final BSSReaderSeekableChannel inParent,
    final URI inURI,
    final BSSRangeHalfOpen inRange,
    final String inName,
    final SeekableByteChannel inChannel,
    final ByteBuffer inBuffer,
    final Callable<Void> inOnClose)
  {
    super(inParent, inRange, inOnClose, inURI, inName);

    this.channel =
      Objects.requireNonNull(inChannel, "channel");
    this.buffer =
      Objects.requireNonNull(inBuffer, "buffer");
  }

  /**
   * Create a random access reader based on a seekable byte channel.
   *
   * @param uri     The source URI
   * @param channel The source channel
   * @param name    The name
   * @param size    The size
   *
   * @return A random access reader
   */

  public static BSSReaderRandomAccessType createFromChannel(
    final URI uri,
    final SeekableByteChannel channel,
    final String name,
    final OptionalLong size)
  {
    final var buffer = ByteBuffer.allocateDirect(8);

    return new BSSReaderSeekableChannel(
      null,
      uri,
      new BSSRangeHalfOpen(0L, size),
      name,
      channel,
      buffer,
      () -> {
        channel.close();
        return null;
      });
  }

  @Override
  public Optional<BSSReaderRandomAccessType> parentReader()
  {
    return Optional.ofNullable((BSSReaderRandomAccessType) super.parent());
  }

  @Override
  public BSSReaderRandomAccessType createSubReaderAt(
    final String inName,
    final long offset)
    throws SIOException
  {
    Objects.requireNonNull(inName, "path");
    this.checkNotClosed();

    final var newName =
      new StringBuilder(32)
        .append(this.path)
        .append(PATH_SEPARATOR)
        .append(inName)
        .toString();

    return new BSSReaderSeekableChannel(
      this,
      this.uri,
      this.createOffsetSubRange(offset),
      newName,
      this.channel,
      this.buffer,
      () -> null);
  }

  @Override
  public BSSReaderRandomAccessType createSubReaderAtBounded(
    final String inName,
    final long offset,
    final long size)
    throws SIOException
  {
    Objects.requireNonNull(inName, "path");

    this.checkNotClosed();

    final var newName =
      new StringBuilder(32)
        .append(this.path)
        .append(PATH_SEPARATOR)
        .append(inName)
        .toString();

    return new BSSReaderSeekableChannel(
      this,
      this.uri,
      this.createSubRange(offset, size),
      newName,
      this.channel,
      this.buffer,
      () -> null);
  }

  @Override
  public String toString()
  {
    return String.format(
      "[BSSReaderSeekableChannel %s %s [absolute %s] [relative %s]]",
      this.uri(),
      this.path(),
      Long.toUnsignedString(this.offsetCurrentAbsolute()),
      Long.toUnsignedString(this.offsetCurrentRelative()));
  }

  private int readS8p(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 1L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(1L);

    this.buffer.position(0);
    this.buffer.limit(1);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(1);
    return this.buffer.get(0);
  }

  private int readU8p(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 1L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(1L);

    this.buffer.position(0);
    this.buffer.limit(1);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(1);
    return (int) this.buffer.get(0) & 0xff;
  }

  private int readS16LEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(2);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(2);
    return this.buffer.getShort(0);
  }

  private int readU16LEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(2);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(2);
    return this.buffer.getChar(0);
  }

  private long readS32LEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(4);
    return this.buffer.getInt(0);
  }

  private long readU32LEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(4);
    return (long) this.buffer.getInt(0) & 0xffff_ffffL;
  }

  private long readS64LEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(8);
    return this.buffer.getLong(0);
  }

  private long readU64LEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(8);
    return this.buffer.getLong(0);
  }

  private int readS16BEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(2);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(2);
    return this.buffer.getShort(0);
  }

  private int readU16BEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(2);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(2);
    return this.buffer.getChar(0);
  }

  private long readS32BEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(4);
    return this.buffer.getInt(0);
  }

  private long readU32BEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(4);
    return (long) this.buffer.getInt(0) & 0xffff_ffffL;
  }

  private long readS64BEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(8);
    return this.buffer.getLong(0);
  }

  private long readU64BEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(8);
    return this.buffer.getLong(0);
  }

  private float readF32BEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(4);
    return this.buffer.getFloat(0);
  }

  private float readF32LEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(4);
    return this.buffer.getFloat(0);
  }

  private double readD64BEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(8);
    return this.buffer.getDouble(0);
  }

  private double readD64LEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(8);
    return this.buffer.getDouble(0);
  }

  private float readF16BEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(2);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(2);
    return Binary16.unpackFloat(this.buffer.getChar(0));
  }

  private float readF16LEp(
    final String name)
    throws SIOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(2);
    try {
      this.channel.position(position);
      this.channel.read(this.buffer);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
    this.buffer.position(0);
    this.buffer.limit(2);
    return Binary16.unpackFloat(this.buffer.getChar(0));
  }

  private int readBytesP(
    final String name,
    final byte[] inBuffer,
    final int length)
    throws SIOException
  {
    this.checkNotClosed();
    final var llength = Integer.toUnsignedLong(length);
    this.checkHasBytesRemaining(name, llength);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(llength);

    final var wrapper = ByteBuffer.wrap(inBuffer);
    try {
      this.channel.position(position);
      return this.channel.read(wrapper);
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Failed to read channel.", Map.of()
      );
    }
  }

  @Override
  public int readS8(final String name)
    throws SIOException
  {
    return this.readS8p(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readU8(final String name)
    throws SIOException
  {
    return this.readU8p(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readS16LE(final String name)
    throws SIOException
  {
    return this.readS16LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readU16LE(final String name)
    throws SIOException
  {
    return this.readU16LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readS32LE(final String name)
    throws SIOException
  {
    return this.readS32LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readU32LE(final String name)
    throws SIOException
  {
    return this.readU32LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readS64LE(final String name)
    throws SIOException
  {
    return this.readS64LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readU64LE(final String name)
    throws SIOException
  {
    return this.readU64LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readS16BE(final String name)
    throws SIOException
  {
    return this.readS16BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readU16BE(final String name)
    throws SIOException
  {
    return this.readU16BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readS32BE(final String name)
    throws SIOException
  {
    return this.readS32BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readU32BE(final String name)
    throws SIOException
  {
    return this.readU32BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readS64BE(final String name)
    throws SIOException
  {
    return this.readS64BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readU64BE(final String name)
    throws SIOException
  {
    return this.readU64BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public float readF32BE(final String name)
    throws SIOException
  {
    return this.readF32BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public float readF32LE(final String name)
    throws SIOException
  {
    return this.readF32LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public double readD64BE(final String name)
    throws SIOException
  {
    return this.readD64BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public double readD64LE(final String name)
    throws SIOException
  {
    return this.readD64LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readS8()
    throws SIOException
  {
    return this.readS8p(null);
  }

  @Override
  public int readU8()
    throws SIOException
  {
    return this.readU8p(null);
  }

  @Override
  public int readS16LE()
    throws SIOException
  {
    return this.readS16LEp(null);
  }

  @Override
  public int readU16LE()
    throws SIOException
  {
    return this.readU16LEp(null);
  }

  @Override
  public long readS32LE()
    throws SIOException
  {
    return this.readS32LEp(null);
  }

  @Override
  public long readU32LE()
    throws SIOException
  {
    return this.readU32LEp(null);
  }

  @Override
  public long readS64LE()
    throws SIOException
  {
    return this.readS64LEp(null);
  }

  @Override
  public long readU64LE()
    throws SIOException
  {
    return this.readU64LEp(null);
  }

  @Override
  public int readS16BE()
    throws SIOException
  {
    return this.readS16BEp(null);
  }

  @Override
  public int readU16BE()
    throws SIOException
  {
    return this.readU16BEp(null);
  }

  @Override
  public long readS32BE()
    throws SIOException
  {
    return this.readS32BEp(null);
  }

  @Override
  public long readU32BE()
    throws SIOException
  {
    return this.readU32BEp(null);
  }

  @Override
  public long readS64BE()
    throws SIOException
  {
    return this.readS64BEp(null);
  }

  @Override
  public long readU64BE()
    throws SIOException
  {
    return this.readU64BEp(null);
  }

  @Override
  public float readF16BE()
    throws SIOException
  {
    return this.readF16BEp(null);
  }

  @Override
  public float readF16LE()
    throws SIOException
  {
    return this.readF16LEp(null);
  }

  @Override
  public float readF16BE(final String name)
    throws SIOException
  {
    return this.readF16BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public float readF16LE(final String name)
    throws SIOException
  {
    return this.readF16LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public float readF32BE()
    throws SIOException
  {
    return this.readF32BEp(null);
  }

  @Override
  public float readF32LE()
    throws SIOException
  {
    return this.readF32LEp(null);
  }

  @Override
  public double readD64BE()
    throws SIOException
  {
    return this.readD64BEp(null);
  }

  @Override
  public double readD64LE()
    throws SIOException
  {
    return this.readD64LEp(null);
  }

  @Override
  public int readBytes(
    final byte[] inBuffer,
    final int offset,
    final int length)
    throws SIOException
  {
    return this.readBytesP(null, inBuffer, length);
  }

  @Override
  public int readBytes(
    final String name,
    final byte[] inBuffer,
    final int offset,
    final int length)
    throws SIOException
  {
    return this.readBytesP(
      Objects.requireNonNull(name, "name"),
      inBuffer,
      length);
  }

  @Override
  protected BSSRangeHalfOpen physicalSourceAbsoluteBounds()
    throws SIOException
  {
    try {
      return BSSRangeHalfOpen.create(0L, this.channel.size());
    } catch (final IOException e) {
      throw BSSExceptions.wrap(
        this, e, "Could not retrieve channel size.", Map.of()
      );
    }
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
