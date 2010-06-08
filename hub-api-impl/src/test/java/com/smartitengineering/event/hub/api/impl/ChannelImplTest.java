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

import com.smartitengineering.event.hub.api.impl.*;
import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.api.Filter;
import java.util.Date;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

/**
 *
 * @author imyousuf
 */
public class ChannelImplTest
    extends MockObjectTestCase {

  public void testGetSetNameAndConstructor() {
    String nullName = null;
    String emptyName = "";
    String blankName = " \t\n";
    String trimmableProperName = " \n aB \t";
    String properName = "ab";
    useNameInConstructor(nullName);
    useNameInConstructor(emptyName);
    useNameInConstructor(blankName);
    useNameInFactory(nullName);
    useNameInFactory(emptyName);
    useNameInFactory(blankName);
    useNameWithSetter(nullName);
    useNameWithSetter(emptyName);
    useNameWithSetter(blankName);
    new ChannelImpl(trimmableProperName);
    new ChannelImpl(properName);
    final ChannelImpl channelImpl = new ChannelImpl("de");
    channelImpl.setName(trimmableProperName);
    assertEquals(properName, channelImpl.getName());
    final Channel channel = APIFactory.getChannelBuilder("de").name(
        trimmableProperName).build();
    assertEquals(properName, channel.getName());
  }

  public void testGetSet() {
    final ChannelImpl channelImpl = new ChannelImpl("ab");
    assertNull(channelImpl.getDescription());
    final String description = "Description";
    channelImpl.setDescription(description);
    assertEquals(description, channelImpl.getDescription());
    assertNull(channelImpl.getAuthToken());
    final String authToken = "auth-token";
    channelImpl.setAuthToken(authToken);
    assertEquals(authToken, channelImpl.getAuthToken());
    assertNull(channelImpl.getCreationDateTime());
    final Date creationDateTime = new Date();
    channelImpl.setCreationDateTime(creationDateTime);
    assertEquals(creationDateTime, channelImpl.getCreationDateTime());
    assertNull(channelImpl.getAutoExpiryDateTime());
    final Date autoExpiryDateTime = new Date();
    channelImpl.setAutoExpiryDateTime(autoExpiryDateTime);
    assertEquals(autoExpiryDateTime, channelImpl.getAutoExpiryDateTime());
    assertNull(channelImpl.getFilter());
    final Filter filter = mock(Filter.class);
    channelImpl.setFilter(filter);
    assertEquals(filter, channelImpl.getFilter());
  }

  public void testBuilder() {
    final ChannelImpl modelImpl = new ChannelImpl("ab");
    modelImpl.setDescription("d");
    modelImpl.setAuthToken("a");
    modelImpl.setCreationDateTime(new Date());
    modelImpl.setAutoExpiryDateTime(new Date());
    final Filter mockedFilter = mock(Filter.class);
    modelImpl.setFilter(mockedFilter);
    checking(new Expectations() {

      {
        exactly(2).of(mockedFilter).getMimeType();
        will(returnValue(Filter.SupportedMimeType.RUBY));
        exactly(2).of(mockedFilter).getFilterScript();
        will(returnValue("Some script!"));
      }
    });
    final Channel sample = APIFactory.getChannelBuilder(modelImpl.getName()).
        description(modelImpl.getDescription()).authToken(
        modelImpl.getAuthToken()).creationDateTime(
        modelImpl.getCreationDateTime()).autoExpiryDateTime(modelImpl.
        getAutoExpiryDateTime()).filter(modelImpl.getFilter()).build();
    assertEquals(modelImpl.getAuthToken(), sample.getAuthToken());
    assertEquals(modelImpl.getAutoExpiryDateTime(),
        sample.getAutoExpiryDateTime());
    assertEquals(modelImpl.getCreationDateTime(), sample.getCreationDateTime());
    assertEquals(modelImpl.getDescription(), sample.getDescription());
    assertEquals(modelImpl.getName(), sample.getName());
    assertEquals(modelImpl.getFilter().getFilterScript(), sample.getFilter().
        getFilterScript());
    assertEquals(modelImpl.getFilter().getMimeType(), sample.getFilter().
        getMimeType());
  }

  public void testEqualsAndHashCode() {
    final ChannelImpl modelImpl = new ChannelImpl("test");
    assertFalse(modelImpl.equals(null));
    assertFalse(modelImpl.equals("String"));
    final Channel mockedChannel = mock(Channel.class);
    checking(new Expectations() {

      {
        allowing(mockedChannel).getName();
        will(returnValue(null));
      }
    });
    assertFalse(modelImpl.equals(mockedChannel));
    final Channel mockedChannel2 = mock(Channel.class, "Channel 2");
    checking(new Expectations() {

      {
        allowing(mockedChannel2).getName();
        will(returnValue("something"));
      }
    });
    assertFalse(modelImpl.equals(mockedChannel2));
    ChannelImpl sampleImpl = new ChannelImpl("test");
    assertEquals(modelImpl, sampleImpl);
    assertEquals(modelImpl.hashCode(), sampleImpl.hashCode());
  }

  protected void useNameInConstructor(String name) {
    try {
      new ChannelImpl(name);
      fail("Should not pass!");
    }
    catch (Exception ex) {
      //Epected
    }
  }

  protected void useNameInFactory(String name) {
    try {
      APIFactory.getChannelBuilder(name);
      fail("Should not pass!");
    }
    catch (Exception ex) {
      //Epected
    }
  }

  protected void useNameWithSetter(String name) {
    try {
      new ChannelImpl("de").setName(name);
      fail("Should not pass!");
    }
    catch (Exception ex) {
      //Epected
    }
  }
}
