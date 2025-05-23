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

package com.io7m.jbssio.api;

import com.io7m.seltzer.io.SIOException;

import java.util.Optional;

/**
 * The type of sequential readers.
 */

public interface BSSReaderSequentialType extends BSSReaderType
{
  @Override
  Optional<BSSReaderSequentialType> parentReader();

  /**
   * Create a new sub reader with the given {@code name}.
   *
   * @param name The new name
   *
   * @return A new sub reader
   *
   * @throws SIOException On I/O errors
   */

  BSSReaderSequentialType createSubReader(
    String name)
    throws SIOException;

  /**
   * Create a new sub reader with the given {@code name}, allowing the new
   * sub reader to read at most {@code size} bytes.
   *
   * @param name The new name
   * @param size The maximum number of bytes read
   *
   * @return A new sub reader
   *
   * @throws SIOException On I/O errors
   */

  BSSReaderSequentialType createSubReaderBounded(
    String name,
    long size)
    throws SIOException;
}
