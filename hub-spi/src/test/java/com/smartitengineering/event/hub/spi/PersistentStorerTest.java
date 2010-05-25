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
  }
}
