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

import com.smartitengineering.event.hub.api.Filter.SupportedMimeType;
import java.io.IOException;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
public class FilterImplTest
    extends TestCase {

  public FilterImplTest(String testName) {
    super(testName);
  }

  /**
   * Test of getMimeType method, of class FilterImpl.
   */
  public void testGetMimeType() {
    FilterImpl filterImpl = new FilterImpl();
    assertNull(filterImpl.getMimeType());
    filterImpl.setMimeType(SupportedMimeType.JYTHON);
    assertEquals(SupportedMimeType.JYTHON, filterImpl.getMimeType());
    filterImpl.setMimeType(SupportedMimeType.GROOVY);
    assertEquals(SupportedMimeType.GROOVY, filterImpl.getMimeType());
    filterImpl.setMimeType(SupportedMimeType.RUBY);
    assertEquals(SupportedMimeType.RUBY, filterImpl.getMimeType());
    filterImpl.setMimeType(SupportedMimeType.JAVA_SCRIPT);
    assertEquals(SupportedMimeType.JAVA_SCRIPT, filterImpl.getMimeType());
  }

  /**
   * Test of setMimeType method, of class FilterImpl.
   */
  public void testSetMimeType() {
    FilterImpl filterImpl = new FilterImpl();
    filterImpl.setMimeType(SupportedMimeType.JYTHON);
    assertEquals(SupportedMimeType.JYTHON, filterImpl.getMimeType());
    try {
      filterImpl.setMimeType(null);
      fail("Null mime type set!");
    }
    catch(IllegalArgumentException ex) {

    }
  }

  /**
   * Test of getFilterScript method, of class FilterImpl.
   */
  public void testGetFilterScript() {
    FilterImpl filterImpl = new FilterImpl();
    assertNull(filterImpl.getFilterScript());
    String script = "script";
    filterImpl.setFilterScript(script);
    assertNotNull(filterImpl.getFilterScript());
    assertEquals(script, filterImpl.getFilterScript());
  }

  /**
   * Test of setFilterScript method, of class FilterImpl.
   */
  public void testSetFilterScript() {
    FilterImpl filterImpl = new FilterImpl();
    filterImpl.setFilterScript(null);
    assertNull(filterImpl.getFilterScript());
    String script = "script";
    filterImpl.setFilterScript(script);
    assertEquals(script, filterImpl.getFilterScript());
  }

  /**
   * Test of allowBroadcast method, of class FilterImpl.
   */
  public void testAllowBroadcast() {
    FilterImpl filterImpl = new FilterImpl();
    assertTrue(filterImpl.allowBroadcast(null));
    filterImpl.setMimeType(SupportedMimeType.JYTHON);
    assertTrue(filterImpl.allowBroadcast(null));
    filterImpl.setMimeType(SupportedMimeType.RUBY);
    assertTrue(filterImpl.allowBroadcast(null));
    filterImpl.setFilterScript("\t\n ");
    assertTrue(filterImpl.allowBroadcast(null));
    /**
     * For ruby script
     */
    String script;
    try {
      script = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(
          "testrubyallow_success.rb"));
    }
    catch (IOException ex) {
      fail(ex.getMessage());
      script = "";
    }
    if(StringUtils.isBlank(script)) {
      fail("Script not found!");
    }
    filterImpl.setFilterScript(script);
    assertFalse(filterImpl.allowBroadcast(null));
  }

  /**
   * Test of allowBroadcastTestUsingRuby method, of class FilterImpl.
   */
  public void testAllowBroadcastTestUsingRuby() {
    FilterImpl filterImpl = new FilterImpl();
    filterImpl.setMimeType(SupportedMimeType.RUBY);
    String script;
    try {
      script = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(
          "testrubyallow_success.rb"));
    }
    catch (IOException ex) {
      fail(ex.getMessage());
      script = "";
    }
    if(StringUtils.isBlank(script)) {
      fail("Script not found!");
    }
    filterImpl.setFilterScript(script);
    assertFalse(filterImpl.allowBroadcastTestUsingRuby(null));
  }
}
