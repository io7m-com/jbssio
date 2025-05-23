/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.jbssio.api;

import com.io7m.seltzer.api.SStructuredErrorType;
import com.io7m.seltzer.io.SEOFException;
import com.io7m.seltzer.io.SIOException;

import java.net.URI;
import java.util.Map;
import java.util.OptionalLong;

/**
 * A writer that throws {@link UnsupportedOperationException} for all write operations.
 *
 * @since 1.1.0
 */

public final class BSSWriterSequentialUnsupported
  implements BSSWriterSequentialType
{
  /**
   * Construct a writer.
   */

  public BSSWriterSequentialUnsupported()
  {

  }

  @Override
  public long offsetCurrentAbsolute()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long offsetCurrentRelative()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public URI uri()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String path()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isClosed()
  {
    return false;
  }

  @Override
  public void skip(final long size)
    throws SIOException, SEOFException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void align(final int size)
    throws SIOException, SEOFException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public OptionalLong bytesRemaining()
    throws SIOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeBytes(
    final String name,
    final byte[] buffer)
    throws SIOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeBytes(
    final String name,
    final byte[] buffer,
    final int offset,
    final int length)
    throws SIOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeBytes(final byte[] buffer)
    throws SIOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeBytes(
    final byte[] buffer,
    final int offset,
    final int length)
    throws SIOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF16BE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF16BE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF16LE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF16LE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF32BE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF32BE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF32LE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF32LE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF64BE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF64BE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF64LE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF64LE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS8(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS8(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS16LE(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS16BE(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS16LE(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS16BE(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS32LE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS32BE(final long b)
    throws SIOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS32LE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS32BE(
    final String name,
    final long b)
    throws SIOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS64LE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS64BE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS64LE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS64BE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU8(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU8(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU16LE(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU16BE(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU16LE(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU16BE(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU32LE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU32BE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU32LE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU32BE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU64LE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU64BE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU64LE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU64BE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public BSSWriterSequentialType createSubWriterAt(
    final String name,
    final long offset)
    throws SIOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public BSSWriterSequentialType createSubWriterAtBounded(
    final String name,
    final long offset,
    final long size)
    throws SIOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long padTo(
    final long offset,
    final byte value)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close()
    throws SIOException
  {

  }

  @Override
  public <E extends Exception & SStructuredErrorType<String>> E createException(
    final String message,
    final Map<String, String> attributes,
    final BSSExceptionConstructorType<E> constructor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public <E extends Exception & SStructuredErrorType<String>> E createException(
    final String message,
    final Throwable cause,
    final Map<String, String> attributes,
    final BSSExceptionConstructorType<E> constructor)
  {
    throw new UnsupportedOperationException();
  }
}
