/*
It is a application for event distribution to event n-consumers with m-sources.
Copyright (C) 2010 "Imran M Yousuf <imran@smartitengineering.com>"

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or any later
version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.smartitengineering.event.hub.spi.db;

import junit.framework.TestCase;

/**
 *
 * @author imyousuf
 */
public class PersistencyDomainTest extends TestCase {

  public void testEventIsValid() {
    PersistentEvent persistentEvent = new PersistentEvent();
    assertFalse(persistentEvent.isValid());
    persistentEvent.setContent(new byte[0]);
    assertFalse(persistentEvent.isValid());
    persistentEvent.setContent(new byte[]{127});
    assertFalse(persistentEvent.isValid());
    persistentEvent.setContentType("");
    assertFalse(persistentEvent.isValid());
    persistentEvent.setContentType("someType");
  }

  public void testChannelIsValid() {
    PersistentChannel persistentChannel = new PersistentChannel();
    assertFalse(persistentChannel.isValid());
    persistentChannel.setName("");
    assertFalse(persistentChannel.isValid());
    persistentChannel.setName(" \t");
    assertFalse(persistentChannel.isValid());
    persistentChannel.setName("test");
    assertTrue(persistentChannel.isValid());
  }

}
