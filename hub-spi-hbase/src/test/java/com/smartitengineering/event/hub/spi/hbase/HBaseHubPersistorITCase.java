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
package com.smartitengineering.event.hub.spi.hbase;

import com.google.inject.AbstractModule;
import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.api.Event;
import com.smartitengineering.event.hub.api.Filter;
import com.smartitengineering.event.hub.api.Filter.SupportedMimeType;
import com.smartitengineering.event.hub.api.impl.APIFactory;
import com.smartitengineering.event.hub.spi.HubPersistentStorer;
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import com.smartitengineering.event.hub.spi.hbase.persistents.Utils;
import com.smartitengineering.util.bean.guice.GuiceUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
public class HBaseHubPersistorITCase {

  private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();
  private static final Logger LOGGER = LoggerFactory.getLogger(AppTest.class);

  @BeforeClass
  public static void setUp()
      throws Exception {
    /*
     * Start HBase and initialize tables
     */
    //-Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
    System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                       "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
    try {
      TEST_UTIL.startMiniCluster();
    }
    catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
    /*
     * Ensure DIs done
     */
    Properties properties = new Properties();
    properties.setProperty(GuiceUtil.CONTEXT_NAME_PROP,
                           "com.smartitengineering.dao.impl.hbase");
    properties.setProperty(GuiceUtil.IGNORE_MISSING_DEP_PROP, Boolean.TRUE.toString());
    properties.setProperty(GuiceUtil.MODULES_LIST_PROP, ConfigurationModule.class.getName());
    GuiceUtil.getInstance(properties).register();
    new InitializerContextListener().contextInitialized(null);
  }

  @AfterClass
  public static void globalTearDown() throws Exception {
    TEST_UTIL.shutdownMiniCluster();
  }

  public void testSPIInjection() {
    Assert.assertNotNull(HubPersistentStorerSPI.getInstance().getStorer());
  }

  @Test
  public void testCreateChannel() {
    final String someName = "someName";
    Channel channel = APIFactory.getChannelBuilder(someName).build();
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    storer.create((Channel) null);
    Assert.assertNotNull(storer);
    Assert.assertEquals(someName.toLowerCase(), channel.getName());
    storer.create(channel);
    channel = storer.getChannel(someName);
    Assert.assertNotNull(channel);
    Assert.assertNotNull(channel.getName());
    Assert.assertEquals(someName.toLowerCase(), channel.getName());
    Assert.assertNotNull(channel.getCreationDateTime());
    Assert.assertNull(channel.getAuthToken());
    Assert.assertNull(channel.getAutoExpiryDateTime());
    Assert.assertNull(channel.getDescription());
    Assert.assertNull(channel.getFilter());
    String desc = "Desc", authToken = "auth_token", script = "script",
        name = "name";
    Date expiry = new Date();
    final SupportedMimeType mime = SupportedMimeType.JYTHON;
    Filter filter = APIFactory.getFilter(mime, script);
    channel = APIFactory.getChannelBuilder(name).filter(filter).description(
        desc).authToken(authToken).autoExpiryDateTime(expiry).build();
    storer.create(channel);
    channel = storer.getChannel(name);
    Assert.assertNotNull(channel);
    Assert.assertNotNull(channel.getName());
    Assert.assertEquals(name, channel.getName());
    Assert.assertNotNull(channel.getCreationDateTime());
    Assert.assertEquals(authToken, channel.getAuthToken());
    Assert.assertEquals(Utils.toDate(Utils.toBytes(expiry)), channel.getAutoExpiryDateTime());
    Assert.assertEquals(desc, channel.getDescription());
    Assert.assertEquals(mime, channel.getFilter().getMimeType());
    Assert.assertEquals(script, channel.getFilter().getFilterScript());
    try {
      storer.create(channel);
      Assert.fail("Created duplicate!");
    }
    catch (Exception ex) {
      //expected
    }
  }

  @Test
  public void testUpdateChannel() {
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    Assert.assertNotNull(storer);
    String desc = "Desc", authToken = "auth_token", script = "script",
        name = "name";
    Date expiry = new Date();
    SupportedMimeType mime = SupportedMimeType.JYTHON;
    Filter filter = APIFactory.getFilter(mime, script);
    Channel channel;
    channel = storer.getChannel(name);
    Assert.assertNotNull(channel);
    expiry = channel.getAutoExpiryDateTime();
    Assert.assertNotNull(channel.getName());
    Assert.assertEquals(name, channel.getName());
    Assert.assertNotNull(channel.getCreationDateTime());
    Assert.assertEquals(authToken, channel.getAuthToken());
    Assert.assertEquals(expiry, channel.getAutoExpiryDateTime());
    Assert.assertEquals(desc, channel.getDescription());
    Assert.assertEquals(script, channel.getFilter().getFilterScript());
    mime = SupportedMimeType.RUBY;
    filter = APIFactory.getFilter(mime, script);
    channel = storer.getChannel(name);
    channel = APIFactory.getChannelBuilder(channel).filter(filter).description(
        desc).authToken(authToken).creationDateTime(
        channel.getCreationDateTime()).autoExpiryDateTime(expiry).build();
    storer.update(channel);
    channel = storer.getChannel(name);
    Assert.assertNotNull(channel);
    storer.update(channel);
    channel = storer.getChannel(name);
    Assert.assertNotNull(channel);
    Assert.assertNotNull(channel.getName());
    Assert.assertEquals(name, channel.getName());
    Assert.assertNotNull(channel.getCreationDateTime());
    Assert.assertEquals(authToken, channel.getAuthToken());
    Assert.assertEquals(Utils.toDate(Utils.toBytes(expiry)), channel.getAutoExpiryDateTime());
    Assert.assertEquals(desc, channel.getDescription());
    Assert.assertEquals(mime, channel.getFilter().getMimeType());
    Assert.assertEquals(script, channel.getFilter().getFilterScript());
    storer.update(null);
    Channel dummyChannel = storer.getChannel("someName");
    storer.update(dummyChannel);
  }

  @Test
  public void testDeleteChannel() {
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    Assert.assertNotNull(storer);
    String name = "name";
    Channel channel;
    channel = storer.getChannel(name);
    Assert.assertNotNull(channel);
    Assert.assertNotNull(channel.getName());
    storer.delete(channel);
    channel = storer.getChannel(name);
    Assert.assertNull(channel);
    storer.delete((Channel) null);
    Channel dummyChannel = storer.getChannel("someName");
    storer.delete(dummyChannel);
  }

  @Test
  public void testGetChannels() {
    final HubPersistentStorer storer = HubPersistentStorerSPI.getInstance().getStorer();
    Assert.assertTrue(storer.getChannels(1, 0) == Collections.EMPTY_LIST);
    Assert.assertTrue(storer.getChannels(-1, 0) == Collections.EMPTY_LIST);
    Channel newChannel = APIFactory.getChannelBuilder("channel1").build();
    storer.create(newChannel);
    newChannel = APIFactory.getChannelBuilder("channel2").build();
    storer.create(newChannel);
    newChannel = APIFactory.getChannelBuilder("channel3").build();
    storer.create(newChannel);
    newChannel = APIFactory.getChannelBuilder("channel4").build();
    storer.create(newChannel);
    newChannel = APIFactory.getChannelBuilder("channel5").build();
    storer.create(newChannel);
    final Channel tChannel = storer.getChannel("channel5");
    System.out.println("Last Position: " + tChannel.getPosition() + " " + (Long.MAX_VALUE - tChannel.getPosition()));
    Collection<Channel> channels = storer.getChannels(6, 2);
    Assert.assertFalse(channels.isEmpty());
    for (Channel channel : channels) {
      System.out.println("Position is: " + channel.getPosition());
    }
    for (Channel channel : channels) {
      Assert.assertTrue("Position is: " + channel.getPosition(), channel.getPosition() > 6);
    }
    channels = storer.getChannels(6, -2);
    Assert.assertFalse(channels.isEmpty());
    for (Channel channel : channels) {
      System.out.println("Position is: " + channel.getPosition());
    }
    for (Channel channel : channels) {
      Assert.assertTrue("Position is: " + channel.getPosition(), channel.getPosition() < 6);
    }
  }

  @Test
  public void testCreateEvent() {
    System.out.println("----------------------- CREATE EVENT -----------------------");
    final String content = "<xml>some xml</xml>";
    final String contentType = "application/xml";
    Event event = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).build();
    Channel dummyChannel = APIFactory.getChannelBuilder("someName").build();
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    Event nullEvent = storer.create((Channel) null, (Event) null);
    Assert.assertNull(nullEvent);
    nullEvent = storer.create(null, APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.
        toInputStream(content))).build());
    Assert.assertNull(nullEvent);
    nullEvent = storer.create(dummyChannel, (Event) null);
    Assert.assertNull(nullEvent);
    event = storer.create(dummyChannel, event);
    Assert.assertNotNull(event);
    Assert.assertNotNull(event.getEventContent());
    Assert.assertNotNull(event.getEventContent().getContent());
    try {
      Assert.assertEquals(content,
                          IOUtils.toString(event.getEventContent().getContent()));
    }
    catch (IOException ex) {
      ex.printStackTrace();
      Assert.fail(ex.getMessage());
    }
    Assert.assertNotNull(event.getEventContent().getContentType());
    Assert.assertEquals(contentType, event.getEventContent().getContentType());
    Assert.assertNotNull(event.getPlaceholderId());
    Assert.assertNotNull(event.getUniversallyUniqueID());
    event = storer.getEvent(event.getPlaceholderId());
    Assert.assertNotNull(event);
    Assert.assertNotNull(event.getEventContent());
    Assert.assertNotNull(event.getEventContent().getContent());
    try {
      Assert.assertEquals(content,
                          IOUtils.toString(event.getEventContent().getContent()));
    }
    catch (IOException ex) {
      ex.printStackTrace();
      Assert.fail(ex.getMessage());
    }
    Assert.assertNotNull(event.getEventContent().getContentType());
    Assert.assertEquals(contentType, event.getEventContent().getContentType());
    Assert.assertNotNull(event.getPlaceholderId());
    Assert.assertNotNull(event.getUniversallyUniqueID());
    Event event2 = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).placeholder(
        event.getPlaceholderId()).build();
    event2 = storer.create(dummyChannel, event2);
    Assert.assertFalse(event.getPlaceholderId().equals(event2.getPlaceholderId()));
    Assert.assertFalse(event.getUniversallyUniqueID().equals(event2.getUniversallyUniqueID()));
    event2 = storer.getEvent(event2.getPlaceholderId());
    Assert.assertNotNull(event2);
    Event event3 = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).uuid(
        event.getUniversallyUniqueID()).build();
    try {
      storer.create(dummyChannel, event3);
      Assert.fail("Created duplicate Event!");
    }
    catch (Exception ex) {
      //expected
    }
    UUID uuid = UUID.randomUUID();
    String uuidStr = uuid.toString();
    event3 = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(
        contentType, IOUtils.toInputStream(content))).uuid(uuidStr).build();
    event3 = storer.create(dummyChannel, event3);
    Assert.assertEquals(uuidStr, event3.getUniversallyUniqueID());
    event3 = storer.getEvent(event3.getPlaceholderId());
    Assert.assertEquals(uuidStr, event3.getUniversallyUniqueID());
  }

  @Test
  public void testGetEventByUUID() {
    final String content = "<xml>some xml</xml>";
    final String contentType = "application/xml";
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    UUID uuid = UUID.randomUUID();
    Channel dummyChannel = APIFactory.getChannelBuilder("someName").build();
    String uuidStr = uuid.toString();
    Event event = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).uuid(uuidStr).build();
    event = storer.create(dummyChannel, event);
    String placeholderId = event.getPlaceholderId();
    Assert.assertEquals(uuidStr, event.getUniversallyUniqueID());
    event = storer.getEvent(event.getPlaceholderId());
    Assert.assertEquals(uuidStr, event.getUniversallyUniqueID());
    event = storer.getEventByUUID(uuidStr);
    Assert.assertEquals(uuidStr, event.getUniversallyUniqueID());
    Assert.assertEquals(placeholderId, event.getPlaceholderId());
    Assert.assertNull(storer.getEventByUUID(null));
    Assert.assertNull(storer.getEventByUUID(""));
    Assert.assertNull(storer.getEventByUUID("aab"));
  }

  @Test
  public void testGetEvents() {
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    Assert.assertTrue(storer.getEvents("-1", null, 0).size() == 0);
    Assert.assertTrue(storer.getEvents("  ", "  ", 0).size() == 0);
    Assert.assertTrue(storer.getEvents(null, null, 0).size() == 0);
    Assert.assertTrue(storer.getEvents("1", null, 0).size() == 0);
    final String content = "<xml>some xml</xml>";
    final String contentType = "application/xml";
    Channel dummyChannel = APIFactory.getChannelBuilder("someName").build();
    Event event1 = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).build();
    storer.create(dummyChannel, event1);
    Event event2 = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).build();
    storer.create(dummyChannel, event2);
    Event event3 = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).build();
    storer.create(dummyChannel, event3);
    Event event4 = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).build();
    event4 = storer.create(dummyChannel, event4);
    final Long placeholderId = NumberUtils.toLong(event4.getPlaceholderId(), 0);
    System.out.println("Selected PlaceholderID: " + placeholderId);
    Event event5 = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).build();
    storer.create(dummyChannel, event5);
    Event event6 = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).build();
    storer.create(dummyChannel, event6);
    Event event7 = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).build();
    storer.create(dummyChannel, event7);
    final Comparator<Event> comparator = new Comparator<Event>() {

      @Override
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
              final int compareTo = new Long(NumberUtils.toLong(o1.getPlaceholderId())).compareTo(NumberUtils.toLong(o2.
                  getPlaceholderId()));
              return compareTo;
            }
          }
        }
      }
    };
    final int count = 3;
    Collection<Event> events = storer.getEvents(placeholderId.toString(), null, count);
    Assert.assertNotNull(events);
    Assert.assertTrue(events.size() == count);
    List<Event> sortTestList = new ArrayList<Event>(events);
    Collections.sort(sortTestList, comparator);
    List<Event> origList = new ArrayList<Event>(events);
    System.out.println("*** EVENTS: " + origList + " " + sortTestList);
    Assert.assertTrue(origList.equals(sortTestList));
    Assert.assertEquals(Long.toString(placeholderId - 1), origList.get(origList.size() - 1).
        getPlaceholderId());
    events = storer.getEvents(placeholderId.toString(), "\t", -1 * count);
    Assert.assertNotNull(events);
    Assert.assertTrue(events.size() == count);
    sortTestList = new ArrayList<Event>(events);
    Collections.sort(sortTestList, comparator);
    origList = new ArrayList<Event>(events);
    System.out.println(origList + " " + sortTestList);
    Assert.assertTrue(origList.equals(sortTestList));
    Assert.assertEquals(Long.toString(placeholderId + 1), origList.get(0).getPlaceholderId());
    events = storer.getEvents(placeholderId.toString(), dummyChannel.getName(), -1 * count);
    Assert.assertNotNull(events);
    Assert.assertEquals(Math.abs(count), events.size());
    sortTestList = new ArrayList<Event>(events);
    Collections.sort(sortTestList, comparator);
    origList = new ArrayList<Event>(events);
    System.out.println(origList + " " + sortTestList);
    Assert.assertTrue(origList.equals(sortTestList));
    Assert.assertEquals(Long.toString(placeholderId + 1), origList.get(0).getPlaceholderId());
  }

  @Test
  public void testDeleteEvent() {
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    storer.delete((Event) null);
    final String content = "<xml>some xml</xml>";
    final String contentType = "application/xml";
    Channel dummyChannel = APIFactory.getChannelBuilder("someName").build();
    Event event = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(contentType, IOUtils.toInputStream(
        content))).build();
    storer.delete(event);
    event = storer.create(dummyChannel, event);
    Event toDeleteEvent = storer.getEvent(event.getPlaceholderId());
    Assert.assertNotNull(toDeleteEvent);
    storer.delete(toDeleteEvent);
    toDeleteEvent = storer.getEvent(event.getPlaceholderId());
    Assert.assertNull(toDeleteEvent);
  }

  public static class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
      bind(Configuration.class).toInstance(TEST_UTIL.getConfiguration());
    }
  }
}
