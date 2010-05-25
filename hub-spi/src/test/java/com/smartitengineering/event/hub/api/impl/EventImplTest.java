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
package com.smartitengineering.event.hub.api.impl;

import com.smartitengineering.event.hub.api.Content;
import com.smartitengineering.event.hub.api.Event;
import org.jmock.integration.junit3.MockObjectTestCase;

/**
 *
 * @author imyousuf
 */
public class EventImplTest
    extends MockObjectTestCase {

  public void testGetSetEventContent() {
    final Content mockedContent = mock(Content.class);
    assertNotNull(mockedContent);
    final EventImpl eventImpl = new EventImpl();
    eventImpl.setEventContent(mockedContent);
    assertSame(mockedContent, eventImpl.getEventContent());
  }

  public void testGetSetPlaceholderId() {
    final String placeholderId = "123";
    final EventImpl eventImpl = new EventImpl();
    eventImpl.setPlaceholderId(placeholderId);
    assertEquals(placeholderId, eventImpl.getPlaceholderId());
  }

  public void testGetSetUniversallyUniqueID() {
    final String uuid = "123";
    EventImpl eventImpl = new EventImpl();
    assertNotNull(eventImpl.getUniversallyUniqueID());
    eventImpl.setUniversallyUniqueID(uuid);
    assertEquals(uuid, eventImpl.getUniversallyUniqueID());
    eventImpl = new EventImpl();
    eventImpl.setUniversallyUniqueID("");
    assertFalse("".equals(eventImpl.getUniversallyUniqueID()));
    eventImpl.setUniversallyUniqueID(" \t\n");
    assertFalse(" \t\n".equals(eventImpl.getUniversallyUniqueID()));
    eventImpl.setUniversallyUniqueID(null);
    assertNotNull(eventImpl.getUniversallyUniqueID());
    eventImpl.setUniversallyUniqueID("ABC");
    eventImpl.setUniversallyUniqueID(uuid);
    assertEquals(uuid, eventImpl.getUniversallyUniqueID());
    eventImpl.setUniversallyUniqueID("");
    assertEquals(uuid, eventImpl.getUniversallyUniqueID());
    eventImpl.setUniversallyUniqueID(" \t\n");
    assertEquals(uuid, eventImpl.getUniversallyUniqueID());
    eventImpl.setUniversallyUniqueID(null);
    assertEquals(uuid, eventImpl.getUniversallyUniqueID());
  }

  public void testEqualsAndHashCode() {
    final String uuid = "123";
    final EventImpl eventImpl = new EventImpl();
    eventImpl.setUniversallyUniqueID(new String(uuid));
    final EventImpl otherEqEventImpl = new EventImpl();
    otherEqEventImpl.setUniversallyUniqueID(new String(uuid));
    assertEquals(eventImpl, otherEqEventImpl);
    assertEquals(eventImpl.hashCode(), otherEqEventImpl.hashCode());
    final EventImpl otherNeqEventImpl1 = new EventImpl();
    otherNeqEventImpl1.setUniversallyUniqueID("ABC");
    assertEquals(eventImpl, otherEqEventImpl);
    assert !eventImpl.equals(otherNeqEventImpl1);
    final EventImpl otherNeqEventImpl2 = new EventImpl();
    assert !eventImpl.equals(otherNeqEventImpl2);
  }

  public void testClone() {
    EventImpl eventImpl = new EventImpl();
    EventImpl clone = eventImpl.cloneMe();
    assertNull(clone.getPlaceholderId());
    assertNotNull(clone.getUniversallyUniqueID());
    assertNull(clone.getEventContent());
    assertFalse(clone.equals(eventImpl));
    final Content mockedContent = mock(Content.class);
    eventImpl = new EventImpl();
    eventImpl.setEventContent(mockedContent);
    clone = eventImpl.cloneMe();
    assertNull(clone.getPlaceholderId());
    assertNotNull(clone.getUniversallyUniqueID());
    assertSame(mockedContent, clone.getEventContent());
    assertFalse(clone.equals(eventImpl));
    final String placeHolderId = "123";
    eventImpl = new EventImpl();
    eventImpl.setEventContent(mockedContent);
    eventImpl.setPlaceholderId(placeHolderId);
    clone = eventImpl.cloneMe();
    assertEquals(placeHolderId, clone.getPlaceholderId());
    assertNotNull(clone.getUniversallyUniqueID());
    assertSame(mockedContent, clone.getEventContent());
    assertFalse(clone.equals(eventImpl));
    final String uuid = "123";
    eventImpl = new EventImpl();
    eventImpl.setEventContent(mockedContent);
    eventImpl.setPlaceholderId(placeHolderId);
    eventImpl.setUniversallyUniqueID(uuid);
    clone = eventImpl.cloneMe();
    assertEquals(placeHolderId, clone.getPlaceholderId());
    assertEquals(uuid, clone.getUniversallyUniqueID());
    assertSame(mockedContent, clone.getEventContent());
    assertTrue(clone.equals(eventImpl));
  }

  public void testBuild() {
    String placeholderId = "123";
    String uuid = "234";
    Content mockedContent = mock(Content.class);
    final EventImpl eventImpl = new EventImpl();
    eventImpl.setEventContent(mockedContent);
    eventImpl.setPlaceholderId(placeholderId);
    Event build = APIFactory.getEventBuilder().eventContent(mockedContent).
        placeholder(placeholderId).build();
    assertFalse(eventImpl.equals(build));
    assertSame(eventImpl.getEventContent(), build.getEventContent());
    assertEquals(eventImpl.getPlaceholderId(), build.getPlaceholderId());
    eventImpl.setUniversallyUniqueID(uuid);
    build = APIFactory.getEventBuilder().eventContent(mockedContent).
        placeholder(placeholderId).uuid(uuid).build();
    assertTrue(eventImpl.equals(build));
    assertEquals(eventImpl, build);
    assertSame(eventImpl.getEventContent(), build.getEventContent());
    assertEquals(eventImpl.getPlaceholderId(), build.getPlaceholderId());
  }
}
