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

import com.io7m.jbssio.vanilla.BSSReaders;
import com.io7m.jbssio.vanilla.BSSWriters;

/**
 * Java bitstream structure I/O (Vanilla implementation)
 */

module com.io7m.jbssio.vanilla
{
  requires static org.osgi.annotation.bundle;
  requires static org.osgi.annotation.versioning;
  requires static org.osgi.service.component.annotations;

  requires com.io7m.ieee754b16.core;
  requires com.io7m.jbssio.api;
  requires com.io7m.seltzer.io;
  requires org.apache.commons.io;
  requires org.slf4j;
  requires com.io7m.seltzer.api;

  provides com.io7m.jbssio.api.BSSReaderProviderType with BSSReaders;
  provides com.io7m.jbssio.api.BSSWriterProviderType with BSSWriters;

  exports com.io7m.jbssio.vanilla;

  exports com.io7m.jbssio.vanilla.internal
    to com.io7m.jbssio.tests;
}
