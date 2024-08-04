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

import com.io7m.jbssio.api.BSSAddressableType;
import com.io7m.jbssio.api.BSSExceptionConstructorType;
import com.io7m.seltzer.api.SStructuredErrorType;
import com.io7m.seltzer.io.SEOFException;
import com.io7m.seltzer.io.SIOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class BSSExceptions
{
  private BSSExceptions()
  {

  }

  public static <E extends Exception & SStructuredErrorType<String>> E create(
    final BSSAddressableType source,
    final Optional<Throwable> cause,
    final String message,
    final Map<String, String> attributes,
    final BSSExceptionConstructorType<E> constructor)
  {
    final var baseAttributes = new HashMap<String, String>(4 + attributes.size());
    baseAttributes.put("URI", source.uri().toString());
    baseAttributes.put("Path", source.path());
    baseAttributes.put(
      "Offset (Relative)",
      "0x" + Long.toUnsignedString(source.offsetCurrentRelative(), 16));
    baseAttributes.put(
      "Offset (Absolute)",
      "0x" + Long.toUnsignedString(source.offsetCurrentAbsolute(), 16));
    baseAttributes.putAll(attributes);

    return constructor.create(
      message,
      cause,
      Map.copyOf(baseAttributes)
    );
  }

  public static SIOException wrap(
    final BSSAddressableType source,
    final Exception cause,
    final String message,
    final Map<String, String> attributes)
  {
    final var baseAttributes =
      new HashMap<String, String>(4 + attributes.size());

    baseAttributes.put("URI", source.uri().toString());
    baseAttributes.put("Path", source.path());
    baseAttributes.put(
      "Offset (Relative)",
      "0x" + Long.toUnsignedString(source.offsetCurrentRelative(), 16));
    baseAttributes.put(
      "Offset (Absolute)",
      "0x" + Long.toUnsignedString(source.offsetCurrentAbsolute(), 16));
    baseAttributes.putAll(attributes);

    return new SIOException(
      message,
      cause,
      "error-io",
      attributes
    );
  }

  public static SIOException createIO(
    final BSSAddressableType source,
    final String message,
    final Map<String, String> attributes)
  {
    return create(source, Optional.empty(), message, attributes, (msg, cause, attr) -> {
      return new SIOException(
        msg,
        "error-io",
        attr,
        Optional.empty()
      );
    });
  }

  public static SEOFException createEOF(
    final BSSAddressableType source,
    final String message,
    final Map<String, String> attributes)
  {
    return create(source, Optional.empty(), message, attributes, (msg, cause, attr) -> {
      return new SEOFException(
        msg,
        "error-eof",
        attr,
        Optional.empty()
      );
    });
  }

  public static <E extends Exception & SStructuredErrorType<String>> E create(
    final BSSAddressableType source,
    final String message,
    final Throwable cause,
    final Map<String, String> attributes,
    final BSSExceptionConstructorType<E> constructor)
  {
    return create(source, Optional.of(cause), message, attributes, constructor);
  }
}
