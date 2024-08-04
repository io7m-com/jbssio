/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import java.util.Map;
import java.util.Optional;

/**
 * An exception constructor.
 *
 * @param <S> The type of exception
 *
 * @since 3.0.0
 */

public interface BSSExceptionConstructorType<S extends Exception & SStructuredErrorType<String>>
{
  /**
   * Create a new exception.
   *
   * @param message    The exception message
   * @param cause      The cause, if any
   * @param attributes The attributes
   *
   * @return A new exception
   */

  S create(
    String message,
    Optional<Throwable> cause,
    Map<String, String> attributes
  );
}
