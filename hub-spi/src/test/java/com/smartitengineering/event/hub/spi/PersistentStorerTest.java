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
package com.smartitengineering.event.hub.spi;

import com.smartitengineering.util.bean.BeanFactory;
import com.smartitengineering.util.bean.BeanFactoryRegistrar;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * Unit test for simple App.
 */
public class PersistentStorerTest
    extends TestCase {

  private Mockery mockery = new Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public PersistentStorerTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(PersistentStorerTest.class);
  }

  public void testSpiInjection() {
    final BeanFactory factory = mockery.mock(BeanFactory.class);
    final HubPersistentStorer storer = mockery.mock(HubPersistentStorer.class);
    mockery.checking(new Expectations(){{
      exactly(1).of(factory).containsBean("storer");
      will(returnValue(Boolean.TRUE));
      exactly(1).of(factory).getBean("storer", HubPersistentStorer.class);
      will(returnValue(storer));
    }});
    BeanFactoryRegistrar.registerBeanFactory(
        "com.smartitengineering.event.hub.spi", factory);
    assertEquals(storer, HubPersistentStorerSPI.getInstance().getStorer());
    //Ensure BeanFactory is not called more than once
    assertEquals(storer, HubPersistentStorerSPI.getInstance().getStorer());
  }
}
