/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.jbssio.vanilla;

import com.io7m.jbssio.api.BSSReaderProviderType;
import com.io7m.jbssio.api.BSSReaderSequentialType;
import org.osgi.service.component.annotations.Component;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

/**
 * A default provider of readers.
 */

@Component(service = BSSReaderProviderType.class)
public final class BSSReaders implements BSSReaderProviderType
{
  /**
   * Construct a provider.
   */

  public BSSReaders()
  {

  }

  @Override
  public BSSReaderSequentialType createReaderFromStream(
    final URI uri,
    final InputStream stream,
    final String name)
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(name, "path");
    return BSSReaderStream.create(uri, stream, name,0xffff_ffff_ffff_ffffL);
  }

  @Override
  public BSSReaderSequentialType createReaderFromStream(
    final URI uri,
    final InputStream stream,
    final String name,
    final long size)
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(name, "path");
    return BSSReaderStream.create(uri, stream, name, size);
  }
}
