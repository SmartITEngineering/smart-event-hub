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

import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.api.Event;
import com.smartitengineering.event.hub.api.Filter;
import com.smartitengineering.event.hub.api.Filter.SupportedMimeType;
import com.smartitengineering.event.hub.api.impl.APIFactory;
import com.smartitengineering.event.hub.spi.HubPersistentStorer;
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author imyousuf
 */
public class DBHubPersistorITCase
    extends TestCase {

  private static ApplicationContext applicationContext;

  @Override
  protected void setUp()
      throws Exception {
    super.setUp();
    if (applicationContext == null) {
      applicationContext = new ClassPathXmlApplicationContext(
          "com/smartitengineering/event/hub/spi/db/app-max-context.xml");
    }
  }

  public void testSPIInjection() {
    assertNotNull(applicationContext);
    assertNotNull(HubPersistentStorerSPI.getInstance().getStorer());
  }

  public void testCreateChannel() {
    final String someName = "someName";
    Channel channel = APIFactory.getChannelBuilder(someName).build();
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    storer.create((Channel) null);
    assertNotNull(storer);
    assertEquals(someName.toLowerCase(), channel.getName());
    storer.create(channel);
    channel = storer.getChannel(someName);
    assertNotNull(channel);
    assertNotNull(channel.getName());
    assertEquals(someName.toLowerCase(), channel.getName());
    assertNotNull(channel.getCreationDateTime());
    assertNull(channel.getAuthToken());
    assertNull(channel.getAutoExpiryDateTime());
    assertNull(channel.getDescription());
    assertNull(channel.getFilter());
    String desc = "Desc", authToken = "auth_token", script = "script",
        name = "name";
    Date expiry = new Date();
    final SupportedMimeType mime = SupportedMimeType.JYTHON;
    Filter filter = APIFactory.getFilter(mime, script);
    channel = APIFactory.getChannelBuilder(name).filter(filter).description(
        desc).authToken(authToken).autoExpiryDateTime(expiry).build();
    storer.create(channel);
    channel = storer.getChannel(name);
    assertNotNull(channel);
    assertNotNull(channel.getName());
    assertEquals(name, channel.getName());
    assertNotNull(channel.getCreationDateTime());
    assertEquals(authToken, channel.getAuthToken());
    assertEquals(expiry, channel.getAutoExpiryDateTime());
    assertEquals(desc, channel.getDescription());
    assertEquals(mime, channel.getFilter().getMimeType());
    assertEquals(script, channel.getFilter().getFilterScript());
    try {
      storer.create(channel);
      fail("Created duplicate!");
    }
    catch (ConstraintViolationException ex) {
      //expected
    }
  }

  public void testUpdateChannel() {
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    assertNotNull(storer);
    String desc = "Desc", authToken = "auth_token", script = "script",
        name = "name";
    Date expiry = new Date();
    SupportedMimeType mime = SupportedMimeType.JYTHON;
    Filter filter = APIFactory.getFilter(mime, script);
    Channel channel;
    channel = storer.getChannel(name);
    assertNotNull(channel);
    expiry = channel.getAutoExpiryDateTime();
    assertNotNull(channel.getName());
    assertEquals(name, channel.getName());
    assertNotNull(channel.getCreationDateTime());
    assertEquals(authToken, channel.getAuthToken());
    assertEquals(expiry, channel.getAutoExpiryDateTime());
    assertEquals(desc, channel.getDescription());
    assertEquals(mime, channel.getFilter().getMimeType());
    assertEquals(script, channel.getFilter().getFilterScript());
    mime = SupportedMimeType.RUBY;
    filter = APIFactory.getFilter(mime, script);
    channel = APIFactory.getChannelBuilder(name).filter(filter).description(
        desc).authToken(authToken).creationDateTime(
        channel.getCreationDateTime()).autoExpiryDateTime(expiry).build();
    storer.update(channel);
    channel = storer.getChannel(name);
    assertNotNull(channel);
    storer.update(channel);
    channel = storer.getChannel(name);
    assertNotNull(channel);
    assertNotNull(channel.getName());
    assertEquals(name, channel.getName());
    assertNotNull(channel.getCreationDateTime());
    assertEquals(authToken, channel.getAuthToken());
    assertEquals(expiry, channel.getAutoExpiryDateTime());
    assertEquals(desc, channel.getDescription());
    assertEquals(mime, channel.getFilter().getMimeType());
    assertEquals(script, channel.getFilter().getFilterScript());
    storer.update(null);
    Channel dummyChannel = APIFactory.getChannelBuilder("someName").build();
    storer.update(dummyChannel);
  }

  public void testDeleteChannel() {
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    assertNotNull(storer);
    String name = "name";
    Channel channel;
    channel = storer.getChannel(name);
    assertNotNull(channel);
    assertNotNull(channel.getName());
    storer.delete(channel);
    channel = storer.getChannel(name);
    assertNull(channel);
    storer.delete((Channel) null);
    Channel dummyChannel = APIFactory.getChannelBuilder("someName").build();
    storer.delete(dummyChannel);
  }

  public void testCreateEvent() {
    final String content = "<xml>some xml</xml>";
    final String contentType = "application/xml";
    Event event = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(contentType, IOUtils.toInputStream(content))).build();
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    storer.create((Event) null);
    event = storer.create(event);
    assertNotNull(event);
    assertNotNull(event.getEventContent());
    assertNotNull(event.getEventContent().getContent());
    try {
      assertEquals(content,
          IOUtils.toString(event.getEventContent().getContent()));
    }
    catch (IOException ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    }
    assertNotNull(event.getEventContent().getContentType());
    assertEquals(contentType, event.getEventContent().getContentType());
    assertNotNull(event.getPlaceholderId());
    assertNotNull(event.getUniversallyUniqueID());
    event = storer.getEvent(event.getPlaceholderId());
    assertNotNull(event);
    assertNotNull(event.getEventContent());
    assertNotNull(event.getEventContent().getContent());
    try {
      assertEquals(content,
          IOUtils.toString(event.getEventContent().getContent()));
    }
    catch (IOException ex) {
      ex.printStackTrace();
      fail(ex.getMessage());
    }
    assertNotNull(event.getEventContent().getContentType());
    assertEquals(contentType, event.getEventContent().getContentType());
    assertNotNull(event.getPlaceholderId());
    assertNotNull(event.getUniversallyUniqueID());
    Event event2 = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(contentType, IOUtils.toInputStream(content))).placeholder(
        event.getPlaceholderId()).build();
    event2 = storer.create(event2);
    assertFalse(event.getPlaceholderId().equals(event2.getPlaceholderId()));
    assertFalse(event.getUniversallyUniqueID().equals(event2.
        getUniversallyUniqueID()));
    event2 = storer.getEvent(event2.getPlaceholderId());
    assertNotNull(event2);
    Event event3 = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(contentType, IOUtils.toInputStream(content))).uuid(
        event.getUniversallyUniqueID()).build();
    try {
      storer.create(event3);
      fail("Created duplicate Event!");
    }
    catch (ConstraintViolationException ex) {
      //expected
    }
    UUID uuid = UUID.randomUUID();
    String uuidStr = uuid.toString();
    event3 = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(
        contentType, IOUtils.toInputStream(content))).uuid(uuidStr).build();
    event3 = storer.create(event3);
    assertEquals(uuidStr, event3.getUniversallyUniqueID());
    event3 = storer.getEvent(event3.getPlaceholderId());
    assertEquals(uuidStr, event3.getUniversallyUniqueID());
  }

  public void testGetEventByUUID() {
    final String content = "<xml>some xml</xml>";
    final String contentType = "application/xml";
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    UUID uuid = UUID.randomUUID();
    String uuidStr = uuid.toString();
    Event event = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(
        contentType, IOUtils.toInputStream(content))).uuid(uuidStr).build();
    event = storer.create(event);
    String placeholderId = event.getPlaceholderId();
    assertEquals(uuidStr, event.getUniversallyUniqueID());
    event = storer.getEvent(event.getPlaceholderId());
    assertEquals(uuidStr, event.getUniversallyUniqueID());
    event = storer.getEventByUUID(uuidStr);
    assertEquals(uuidStr, event.getUniversallyUniqueID());
    assertEquals(placeholderId, event.getPlaceholderId());
    assertNull(storer.getEventByUUID(null));
    assertNull(storer.getEventByUUID(""));
    assertNull(storer.getEventByUUID("aab"));
  }

  public void testGetEvents() {
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    assertTrue(storer.getEvents("-1", 0).size() == 0);
    assertTrue(storer.getEvents("  ", 0).size() == 0);
    assertTrue(storer.getEvents(null, 0).size() == 0);
    assertTrue(storer.getEvents("1", 0).size() == 0);
    final String content = "<xml>some xml</xml>";
    final String contentType = "application/xml";
    Event event1 = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(contentType, IOUtils.toInputStream(content))).build();
    storer.create(event1);
    Event event2 = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(contentType, IOUtils.toInputStream(content))).build();
    storer.create(event2);
    Event event3 = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(contentType, IOUtils.toInputStream(content))).build();
    storer.create(event3);
    Event event4 = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(contentType, IOUtils.toInputStream(content))).build();
    event4 = storer.create(event4);
    Integer placeholderId = NumberUtils.toInt(event4.getPlaceholderId()) + 1;
    System.out.println("Selected PlaceholderID: " + placeholderId);
    Event event5 = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(contentType, IOUtils.toInputStream(content))).build();
    storer.create(event5);
    Event event6 = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(contentType, IOUtils.toInputStream(content))).build();
    storer.create(event6);
    Event event7 = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(contentType, IOUtils.toInputStream(content))).build();
    storer.create(event7);
    final Comparator<Event> comparator = new Comparator<Event>() {

      public int compare(Event o1,
                         Event o2) {
        if (o1 == null && o2 == null) {
          return 0;
        }
        else {
          if (o1 == null && o2 != null) {
            return 1;
          }
          else {
            if (o1 != null && o2 == null) {
              return -1;
            }
            else {
              final int compareTo =
                        new Integer(NumberUtils.toInt(o1.getPlaceholderId())).
                  compareTo(NumberUtils.toInt(o2.getPlaceholderId()));
              return compareTo * -1;
            }
          }
        }
      }
    };
    final int count = 3;
    Collection<Event> events = storer.getEvents(placeholderId.toString(), count);
    assertNotNull(events);
    assertTrue(events.size() == count);
    List<Event> sortTestList = new ArrayList<Event>(events);
    Collections.sort(sortTestList, comparator);
    List<Event> origList = new ArrayList<Event>(events);
    System.out.println(origList + " " + sortTestList);
    assertTrue(origList.equals(sortTestList));
    assertEquals(placeholderId.toString(), origList.get(origList.size() - 1).
        getPlaceholderId());
    events = storer.getEvents(placeholderId.toString(), -1 * count);
    assertNotNull(events);
    assertTrue(events.size() == count);
    sortTestList = new ArrayList<Event>(events);
    Collections.sort(sortTestList, comparator);
    origList = new ArrayList<Event>(events);
    System.out.println(origList + " " + sortTestList);
    assertTrue(origList.equals(sortTestList));
    assertEquals(placeholderId.toString(), origList.get(0).getPlaceholderId());
  }

  public void testDeleteEvent() {
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    storer.delete((Event) null);
    final String content = "<xml>some xml</xml>";
    final String contentType = "application/xml";
    Event event = APIFactory.getEventBuilder().eventContent(APIFactory.
        getContent(contentType, IOUtils.toInputStream(content))).build();
    storer.delete(event);
    event = storer.create(event);
    Event toDeleteEvent = storer.getEvent(event.getPlaceholderId());
    assertNotNull(toDeleteEvent);
    storer.delete(toDeleteEvent);
    toDeleteEvent = storer.getEvent(event.getPlaceholderId());
    assertNull(toDeleteEvent);
  }
}
