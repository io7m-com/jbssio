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

package com.io7m.jbssio.tests;

import com.io7m.ieee754b16.Binary16;
import com.io7m.jbssio.vanilla.BSSReaders;
import com.io7m.seltzer.api.SStructuredErrorType;
import com.io7m.seltzer.io.SEOFException;
import com.io7m.seltzer.io.SIOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class BSSReadersSequentialTest
{
  private static final Logger LOG = LoggerFactory.getLogger(
    BSSReadersSequentialTest.class);

  @SuppressWarnings("unchecked")
  private static void checkExceptionMessageContains(
    final Exception e,
    final String text)
  {
    LOG.debug("ex: ", e);

    final var es = (SStructuredErrorType<String>) e;

    assertTrue(
      es.attributes().get("Field").contains(text),
      "Exception attributes %s contains %s".formatted(es.attributes(), text)
    );
  }

  @Test
  public void testEmptyStream()
    throws Exception
  {
    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(new byte[0])) {
      try (var reader = readers.createReaderFromStream(
        URI.create("urn:fake"),
        stream,
        "a")) {
        assertEquals(0L, reader.offsetCurrentAbsolute());
        assertEquals(0L, reader.offsetCurrentRelative());
        assertEquals(OptionalLong.empty(), reader.bytesRemaining());
      }
    }
  }

  @Test
  public void testNames()
    throws Exception
  {
    final var data = new byte[4];
    for (var index = 0; index < 4; ++index) {
      data[index] = (byte) index;
    }

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data)) {
      try (var reader = readers.createReaderFromStream(
        URI.create("urn:fake"),
        stream,
        "a")) {
        try (var s0 = reader.createSubReader("x")) {
          try (var s1 = s0.createSubReader("y")) {
            try (var s2 = s1.createSubReader("z")) {
              assertEquals("a/x/y/z", s2.path());
            }
            assertEquals("a/x/y", s1.path());
          }
          assertEquals("a/x", s0.path());
        }
        assertEquals("a", reader.path());
      }
    }
  }

  @Test
  public void testClosed()
    throws Exception
  {
    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(new byte[0])) {
      final var reader = readers.createReaderFromStream(
        URI.create("urn:fake"),
        stream,
        "a");
      Assertions.assertFalse(reader.isClosed());
      reader.close();
      assertTrue(reader.isClosed());
      Assertions.assertThrows(IOException.class, () -> reader.readS8());
    }
  }

  @Test
  public void testSeparateLimits()
    throws Exception
  {
    final var data = new byte[12];
    for (var index = 0; index < 12; ++index) {
      data[index] = (byte) index;
    }

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data)) {
      try (var reader = readers.createReaderFromStream(
        URI.create("urn:fake"),
        stream,
        "a")) {
        assertEquals(0L, reader.offsetCurrentAbsolute());
        assertEquals(0L, reader.offsetCurrentRelative());
        LOG.debug("reader:    {}", reader);

        try (var subReader = reader.createSubReaderBounded("s", 4L)) {
          assertEquals(
            OptionalLong.of(4L),
            subReader.bytesRemaining());

          assertEquals(0L, subReader.offsetCurrentRelative());
          assertEquals(0L, subReader.offsetCurrentAbsolute());
          assertEquals(0L, reader.offsetCurrentAbsolute());

          assertEquals(0, subReader.readS8());
          assertEquals(1L, subReader.offsetCurrentRelative());
          assertEquals(1L, subReader.offsetCurrentAbsolute());
          assertEquals(1L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);

          assertEquals(1, subReader.readU8());
          assertEquals(2L, subReader.offsetCurrentRelative());
          assertEquals(2L, subReader.offsetCurrentAbsolute());
          assertEquals(2L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);

          assertEquals(2, subReader.readS8());
          assertEquals(3L, subReader.offsetCurrentRelative());
          assertEquals(3L, subReader.offsetCurrentAbsolute());
          assertEquals(3L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);

          assertEquals(3, subReader.readU8());
          assertEquals(4L, subReader.offsetCurrentRelative());
          assertEquals(4L, subReader.offsetCurrentAbsolute());
          assertEquals(4L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);

          Assertions.assertThrows(SEOFException.class, subReader::readS8);
          Assertions.assertThrows(SEOFException.class, subReader::readU8);
        }

        try (var subReader = reader.createSubReader("s")) {
          assertEquals(
            OptionalLong.empty(),
            subReader.bytesRemaining());

          assertEquals(0L, subReader.offsetCurrentRelative());
          assertEquals(4L, subReader.offsetCurrentAbsolute());
          assertEquals(4L, reader.offsetCurrentAbsolute());

          assertEquals(4, subReader.readS8());
          assertEquals(1L, subReader.offsetCurrentRelative());
          assertEquals(5L, subReader.offsetCurrentAbsolute());
          assertEquals(5L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);

          assertEquals(5, subReader.readU8());
          assertEquals(2L, subReader.offsetCurrentRelative());
          assertEquals(6L, subReader.offsetCurrentAbsolute());
          assertEquals(6L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);

          assertEquals(6, subReader.readS8());
          assertEquals(3L, subReader.offsetCurrentRelative());
          assertEquals(7L, subReader.offsetCurrentAbsolute());
          assertEquals(7L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);

          assertEquals(7, subReader.readU8());
          assertEquals(4L, subReader.offsetCurrentRelative());
          assertEquals(8L, subReader.offsetCurrentAbsolute());
          assertEquals(8L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);
        }

        try (var subReader = reader.createSubReaderBounded("s", 4L)) {
          assertEquals(
            OptionalLong.of(4L),
            subReader.bytesRemaining());

          assertEquals(0L, subReader.offsetCurrentRelative());
          assertEquals(8L, subReader.offsetCurrentAbsolute());
          assertEquals(8L, reader.offsetCurrentAbsolute());

          assertEquals(8, subReader.readS8());
          assertEquals(1L, subReader.offsetCurrentRelative());
          assertEquals(9L, subReader.offsetCurrentAbsolute());
          assertEquals(9L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);

          assertEquals(9, subReader.readU8());
          assertEquals(2L, subReader.offsetCurrentRelative());
          assertEquals(10L, subReader.offsetCurrentAbsolute());
          assertEquals(10L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);

          assertEquals(10, subReader.readS8());
          assertEquals(3L, subReader.offsetCurrentRelative());
          assertEquals(11L, subReader.offsetCurrentAbsolute());
          assertEquals(11L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);

          assertEquals(11, subReader.readU8());
          assertEquals(4L, subReader.offsetCurrentRelative());
          assertEquals(12L, subReader.offsetCurrentAbsolute());
          assertEquals(12L, reader.offsetCurrentAbsolute());
          LOG.debug("reader:    {}", reader);
          LOG.debug("subReader: {}", subReader);

          Assertions.assertThrows(SEOFException.class, subReader::readS8);
          Assertions.assertThrows(SEOFException.class, subReader::readU8);
        }
      }
    }
  }

  @Test
  public void testSeparateLimitsExceeds()
    throws Exception
  {
    final var data = new byte[4];
    for (var index = 0; index < 4; ++index) {
      data[index] = (byte) index;
    }

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data)) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 4L)) {
        assertEquals(0L, reader.offsetCurrentAbsolute());
        assertEquals(0L, reader.offsetCurrentRelative());
        LOG.debug("reader:    {}", reader);

        final var ex =
          Assertions.assertThrows(SIOException.class, () -> {
            reader.createSubReaderBounded("s", 5L);
          });

        LOG.debug("ex: ", ex);
        assertEquals("4", ex.attributes().get("Size limit"));
      }
    }
  }

  @Test
  public void testSkip()
    throws Exception
  {
    final var data = new byte[16];

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data)) {
      try (var reader = readers.createReaderFromStream(
        URI.create("urn:fake"),
        stream,
        "a")) {
        reader.skip(4L);
        reader.skip(4L);
        reader.skip(4L);
        reader.skip(4L);
        Assertions.assertThrows(IOException.class, () -> reader.skip(1L));
      }
    }
  }

  @Test
  public void testAlign()
    throws Exception
  {
    final var data = new byte[9];

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data)) {
      try (var reader = readers.createReaderFromStream(
        URI.create("urn:fake"),
        stream,
        "a")) {
        reader.skip(1L);
        assertEquals(1L, reader.offsetCurrentAbsolute());
        assertEquals(1L, reader.offsetCurrentRelative());

        reader.align(4);
        assertEquals(4L, reader.offsetCurrentAbsolute());
        assertEquals(4L, reader.offsetCurrentRelative());

        reader.align(4);
        assertEquals(4L, reader.offsetCurrentAbsolute());
        assertEquals(4L, reader.offsetCurrentRelative());

        reader.skip(1L);
        assertEquals(5L, reader.offsetCurrentAbsolute());
        assertEquals(5L, reader.offsetCurrentRelative());

        reader.align(4);
        assertEquals(8L, reader.offsetCurrentAbsolute());
        assertEquals(8L, reader.offsetCurrentRelative());

        reader.skip(1L);
        assertEquals(9L, reader.offsetCurrentAbsolute());
        assertEquals(9L, reader.offsetCurrentRelative());

        Assertions.assertThrows(IOException.class, () -> reader.align(4));
      }
    }
  }

  @Test
  public void testReadShort()
    throws Exception
  {
    final var data = new byte[1];

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data)) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 0L)) {
        Assertions.assertThrows(IOException.class, reader::readS16BE);
        Assertions.assertThrows(IOException.class, reader::readS16LE);
        Assertions.assertThrows(IOException.class, reader::readS32BE);
        Assertions.assertThrows(IOException.class, reader::readS32LE);
        Assertions.assertThrows(IOException.class, reader::readS64BE);
        Assertions.assertThrows(IOException.class, reader::readS64LE);
        Assertions.assertThrows(IOException.class, reader::readS8);
        Assertions.assertThrows(IOException.class, reader::readU16BE);
        Assertions.assertThrows(IOException.class, reader::readU16LE);
        Assertions.assertThrows(IOException.class, reader::readU32BE);
        Assertions.assertThrows(IOException.class, reader::readU32LE);
        Assertions.assertThrows(IOException.class, reader::readU64BE);
        Assertions.assertThrows(IOException.class, reader::readU64LE);
        Assertions.assertThrows(IOException.class, reader::readU8);
      }
    }
  }

  @Test
  public void testReadShortNamed()
    throws Exception
  {
    final var data = new byte[1];

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data)) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 1L)) {
        checkExceptionMessageContains(Assertions.assertThrows(
          IOException.class,
          () -> reader.readS16BE("q")), "q");
      }
    }
  }

  @Test
  public void testReadS8()
    throws Exception
  {
    final var data = new byte[32];
    data[0] = Byte.MIN_VALUE;
    data[1] = Byte.MAX_VALUE;

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data)) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Byte.MIN_VALUE, reader.readS8());
        assertEquals(Byte.MAX_VALUE, reader.readS8());
      }
    }
  }

  @Test
  public void testReadS16LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putShort(0, Short.MIN_VALUE);
    data.putShort(2, Short.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Short.MIN_VALUE, reader.readS16LE());
        assertEquals(Short.MAX_VALUE, reader.readS16LE());
      }
    }
  }

  @Test
  public void testReadS32LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putInt(0, Integer.MIN_VALUE);
    data.putInt(4, Integer.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Integer.MIN_VALUE, reader.readS32LE());
        assertEquals(Integer.MAX_VALUE, reader.readS32LE());
      }
    }
  }

  @Test
  public void testReadS64LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putLong(0, Long.MIN_VALUE);
    data.putLong(8, Long.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Long.MIN_VALUE, reader.readS64LE());
        assertEquals(Long.MAX_VALUE, reader.readS64LE());
      }
    }
  }

  @Test
  public void testReadS16BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putShort(0, Short.MIN_VALUE);
    data.putShort(2, Short.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Short.MIN_VALUE, reader.readS16BE());
        assertEquals(Short.MAX_VALUE, reader.readS16BE());
      }
    }
  }

  @Test
  public void testReadS32BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putInt(0, Integer.MIN_VALUE);
    data.putInt(4, Integer.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Integer.MIN_VALUE, reader.readS32BE());
        assertEquals(Integer.MAX_VALUE, reader.readS32BE());
      }
    }
  }

  @Test
  public void testReadS64BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putLong(0, Long.MIN_VALUE);
    data.putLong(8, Long.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Long.MIN_VALUE, reader.readS64BE());
        assertEquals(Long.MAX_VALUE, reader.readS64BE());
      }
    }
  }

  @Test
  public void testReadU8()
    throws Exception
  {
    final var data = new byte[32];
    data[0] = 0;
    data[1] = (byte) 0xff;

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data)) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0, reader.readU8());
        assertEquals(0xff, reader.readU8());
      }
    }
  }

  @Test
  public void testReadU16LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putChar(0, (char) 0);
    data.putChar(2, (char) 0xffff);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0, reader.readU16LE());
        assertEquals(0xffff, reader.readU16LE());
      }
    }
  }

  @Test
  public void testReadU32LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putInt(0, 0);
    data.putInt(4, 0xffff_ffff);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0L, reader.readU32LE());
        assertEquals(0xffff_ffffL, reader.readU32LE());
      }
    }
  }

  @Test
  public void testReadU64LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putLong(0, 0L);
    data.putLong(8, 0xffff_ffff_ffff_ffffL);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0L, reader.readU64LE());
        assertEquals(0xffff_ffff_ffff_ffffL, reader.readU64LE());
      }
    }
  }

  @Test
  public void testReadU16BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putChar(0, (char) 0);
    data.putChar(2, (char) 0xffff);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0, reader.readU16BE());
        assertEquals(0xffff, reader.readU16BE());
      }
    }
  }

  @Test
  public void testReadU32BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putInt(0, 0);
    data.putInt(4, 0xffff_ffff);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0L, reader.readU32BE());
        assertEquals(0xffff_ffffL, reader.readU32BE());
      }
    }
  }

  @Test
  public void testReadU64BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putLong(0, 0L);
    data.putLong(8, 0xffff_ffff_ffff_ffffL);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0L, reader.readU64BE());
        assertEquals(0xffff_ffff_ffff_ffffL, reader.readU64BE());
      }
    }
  }

  @Test
  public void testReadDBE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putDouble(0, 1000.0);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1000.0, reader.readD64BE());
      }
    }
  }

  @Test
  public void testReadDLE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putDouble(0, 1000.0);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1000.0, reader.readD64LE());
      }
    }
  }

  @Test
  public void testReadFBE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putFloat(0, 1000.0f);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1000.0f, reader.readF32BE());
      }
    }
  }

  @Test
  public void testReadFLE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putFloat(0, 1000.0f);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1000.0f, reader.readF32LE());
      }
    }
  }

  @Test
  public void testReadBytes()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    final var buffer = new byte[16];

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(16, reader.readBytes(buffer, 0, buffer.length));
        assertEquals(16, reader.readBytes(buffer));
        Assertions.assertThrows(
          SEOFException.class,
          () -> reader.readBytes(buffer));
      }
    }
  }

  @Test
  public void testReadS8Named()
    throws Exception
  {
    final var data = new byte[32];
    data[0] = Byte.MIN_VALUE;
    data[1] = Byte.MAX_VALUE;

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data)) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Byte.MIN_VALUE, reader.readS8("q"));
        assertEquals(Byte.MAX_VALUE, reader.readS8("q"));
      }
    }
  }

  @Test
  public void testReadS16LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putShort(0, Short.MIN_VALUE);
    data.putShort(2, Short.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Short.MIN_VALUE, reader.readS16LE("q"));
        assertEquals(Short.MAX_VALUE, reader.readS16LE("q"));
      }
    }
  }

  @Test
  public void testReadS32LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putInt(0, Integer.MIN_VALUE);
    data.putInt(4, Integer.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Integer.MIN_VALUE, reader.readS32LE("q"));
        assertEquals(Integer.MAX_VALUE, reader.readS32LE("q"));
      }
    }
  }

  @Test
  public void testReadS64LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putLong(0, Long.MIN_VALUE);
    data.putLong(8, Long.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Long.MIN_VALUE, reader.readS64LE("q"));
        assertEquals(Long.MAX_VALUE, reader.readS64LE("q"));
      }
    }
  }

  @Test
  public void testReadS16BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putShort(0, Short.MIN_VALUE);
    data.putShort(2, Short.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Short.MIN_VALUE, reader.readS16BE("q"));
        assertEquals(Short.MAX_VALUE, reader.readS16BE("q"));
      }
    }
  }

  @Test
  public void testReadS32BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putInt(0, Integer.MIN_VALUE);
    data.putInt(4, Integer.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Integer.MIN_VALUE, reader.readS32BE("q"));
        assertEquals(Integer.MAX_VALUE, reader.readS32BE("q"));
      }
    }
  }

  @Test
  public void testReadS64BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putLong(0, Long.MIN_VALUE);
    data.putLong(8, Long.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(Long.MIN_VALUE, reader.readS64BE("q"));
        assertEquals(Long.MAX_VALUE, reader.readS64BE("q"));
      }
    }
  }

  @Test
  public void testReadU8Named()
    throws Exception
  {
    final var data = new byte[32];
    data[0] = 0;
    data[1] = (byte) 0xff;

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data)) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0, reader.readU8("q"));
        assertEquals(0xff, reader.readU8("q"));
      }
    }
  }

  @Test
  public void testReadU16LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putChar(0, (char) 0);
    data.putChar(2, (char) 0xffff);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0, reader.readU16LE("q"));
        assertEquals(0xffff, reader.readU16LE("q"));
      }
    }
  }

  @Test
  public void testReadU32LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putInt(0, 0);
    data.putInt(4, 0xffff_ffff);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0L, reader.readU32LE("q"));
        assertEquals(0xffff_ffffL, reader.readU32LE("q"));
      }
    }
  }

  @Test
  public void testReadU64LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putLong(0, 0L);
    data.putLong(8, 0xffff_ffff_ffff_ffffL);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0L, reader.readU64LE("q"));
        assertEquals(0xffff_ffff_ffff_ffffL, reader.readU64LE("q"));
      }
    }
  }

  @Test
  public void testReadU16BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putChar(0, (char) 0);
    data.putChar(2, (char) 0xffff);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0, reader.readU16BE("q"));
        assertEquals(0xffff, reader.readU16BE("q"));
      }
    }
  }

  @Test
  public void testReadU32BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putInt(0, 0);
    data.putInt(4, 0xffff_ffff);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0L, reader.readU32BE("q"));
        assertEquals(0xffff_ffffL, reader.readU32BE("q"));
      }
    }
  }

  @Test
  public void testReadU64BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putLong(0, 0L);
    data.putLong(8, 0xffff_ffff_ffff_ffffL);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(0L, reader.readU64BE("q"));
        assertEquals(0xffff_ffff_ffff_ffffL, reader.readU64BE("q"));
      }
    }
  }

  @Test
  public void testReadDBENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putDouble(0, 1000.0);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1000.0, reader.readD64BE("q"));
      }
    }
  }

  @Test
  public void testReadDLENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putDouble(0, 1000.0);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1000.0, reader.readD64LE("q"));
      }
    }
  }

  @Test
  public void testReadFBENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putFloat(0, 1000.0f);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1000.0f, reader.readF32BE("q"));
      }
    }
  }

  @Test
  public void testReadFLENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putFloat(0, 1000.0f);

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1000.0f, reader.readF32LE("q"));
      }
    }
  }

  @Test
  public void testReadBytesNamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    final var buffer = new byte[16];

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(
          16,
          reader.readBytes(
            "q",
            buffer,
            0,
            buffer.length));
        assertEquals(16, reader.readBytes("q", buffer));
        Assertions.assertThrows(
          SEOFException.class,
          () -> reader.readBytes(buffer));
      }
    }
  }

  @Test
  public void testReadF16BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putChar(0, Binary16.packDouble(1.0));

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1.0f, reader.readF16BE(), 0.001);
      }
    }
  }

  @Test
  public void testReadF16LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putChar(0, Binary16.packDouble(1.0));

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1.0f, reader.readF16LE(), 0.001);
      }
    }
  }

  @Test
  public void testReadF16BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putChar(0, Binary16.packDouble(1.0));

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1.0f, reader.readF16BE("x"), 0.001);
      }
    }
  }

  @Test
  public void testReadF16LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putChar(0, Binary16.packDouble(1.0));

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {
        assertEquals(1.0f, reader.readF16LE("x"), 0.001);
      }
    }
  }

  @Test
  public void testException()
    throws IOException
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putChar(0, Binary16.packDouble(1.0));

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {

        final var ex =
          reader.createException(
            "message",
            Map.ofEntries(
              Map.entry("x", "y"),
              Map.entry("z", "0.0")
            ),
            (message, cause, attributes) -> {
              return new SIOException(
                message,
                "error-io",
                attributes
              );
            }
          );

        LOG.debug("{}", ex.attributes());
        assertTrue(ex.getMessage().contains("message"));
        assertTrue(ex.attributes().containsKey("x"));
        assertEquals("y", ex.attributes().get("x"));
        assertTrue(ex.attributes().containsKey("z"));
        assertEquals("0.0", ex.attributes().get("z"));
        assertTrue(ex.attributes().containsKey("Offset (Absolute)"));
        assertTrue(ex.attributes().containsKey("Offset (Relative)"));
        assertTrue(ex.attributes().containsKey("URI"));
        assertTrue(ex.attributes().containsKey("Path"));
      }
    }
  }
}
