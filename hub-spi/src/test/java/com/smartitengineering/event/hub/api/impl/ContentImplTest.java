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

import java.io.InputStream;
import junit.framework.TestCase;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 *
 * @author imyousuf
 */
public class ContentImplTest
    extends TestCase {

  private final Mockery mockery = new Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  public void testGetSetContentType() {
    String contentType = "type";
    ContentImpl contentImpl = new ContentImpl();
    assertNull(contentImpl.getContentType());
    contentImpl.setContentType(contentType);
    assertEquals(contentType, contentImpl.getContentType());
  }

  public void testGetSetContent() {
    InputStream mockedIStream = mockery.mock(InputStream.class);
    ContentImpl contentImpl = new ContentImpl();
    assertNull(contentImpl.getContent());
    contentImpl.setContent(mockedIStream);
    assertEquals(mockedIStream, contentImpl.getContent());
  }

  public void testCreateContent() {
    String contentType = "type";
    InputStream mockedIStream = mockery.mock(InputStream.class);
    assertNotNull(APIFactory.getContent(contentType, mockedIStream));
    tryContentCreation(contentType, null);
    tryContentCreation(null, mockedIStream);
    tryContentCreation(null, null);
  }

  protected void tryContentCreation(String contentType,
                                    InputStream mockedIStream) {
    try {
      APIFactory.getContent(contentType, mockedIStream);
      fail("Should be able to initialize");
    }
    catch (IllegalArgumentException exception) {
      //Success!
    }
    catch (Throwable th) {
      fail(th.getMessage());
    }
  }
}
