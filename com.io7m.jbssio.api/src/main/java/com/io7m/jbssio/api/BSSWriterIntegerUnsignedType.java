/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for
 * any purpose with or without fee is hereby granted, provided that the
 * above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 * WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR
 * BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES
 * OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
 * WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION,
 * ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS
 * SOFTWARE.
 */


package com.io7m.jbssio.api;

import com.io7m.seltzer.io.SEOFException;
import com.io7m.seltzer.io.SIOException;

/**
 * Functions for writing unsigned integers.
 */

public interface BSSWriterIntegerUnsignedType
{
  /**
   * Write an 8-bit unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU8(int b)
    throws SIOException;

  /**
   * Write a named 8-bit unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU8(
    String name,
    int b)
    throws SIOException;

  /**
   * Write a 16-bit little-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU16LE(int b)
    throws SIOException;

  /**
   * Write a 16-bit big-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU16BE(int b)
    throws SIOException;

  /**
   * Write a named 16-bit little-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU16LE(
    String name,
    int b)
    throws SIOException;

  /**
   * Write a named 16-bit big-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU16BE(
    String name,
    int b)
    throws SIOException;

  /**
   * Write a 32-bit little-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU32LE(long b)
    throws SIOException;

  /**
   * Write a 32-bit big-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU32BE(long b)
    throws SIOException;

  /**
   * Write a named 32-bit little-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU32LE(
    String name,
    long b)
    throws SIOException;

  /**
   * Write a named 32-bit big-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU32BE(
    String name,
    long b)
    throws SIOException;

  /**
   * Write a 64-bit little-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU64LE(long b)
    throws SIOException;

  /**
   * Write a 64-bit big-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU64BE(long b)
    throws SIOException;

  /**
   * Write a named 64-bit little-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU64LE(
    String name,
    long b)
    throws SIOException;

  /**
   * Write a named 64-bit big-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws SIOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws SEOFException If EOF is reached
   */

  void writeU64BE(
    String name,
    long b)
    throws SIOException;
}
