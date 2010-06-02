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
import com.smartitengineering.event.hub.api.Filter;
import com.smartitengineering.event.hub.api.Filter.SupportedMimeType;
import com.smartitengineering.event.hub.api.impl.APIFactory;
import com.smartitengineering.event.hub.spi.HubPersistentStorer;
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import java.util.Date;
import junit.framework.TestCase;
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
    Filter filter = APIFactory.getFilter( mime, script);
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
  }
}
