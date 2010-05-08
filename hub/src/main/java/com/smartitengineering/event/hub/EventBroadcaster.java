package com.smartitengineering.event.hub;

import java.util.concurrent.atomic.AtomicBoolean;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.jersey.JerseyBroadcaster;
import org.atmosphere.util.LoggerUtils;

/**
 *
 * @author imyousuf
 */
public class EventBroadcaster
    extends JerseyBroadcaster {

  private final AtomicBoolean isSet = new AtomicBoolean(false);

  public EventBroadcaster() {
    super();
  }

  @Override
  protected void broadcast(final AtmosphereResource r,
                           final AtmosphereResourceEvent e) {
    if (!isSet.getAndSet(true)) {
      LoggerUtils.getLogger().info("This is just an example that demonstrate " +
                                   "how a Broadcaster can be customized using atmosphere.xml or by " +
                                   "defining it inside web.xml");
    }

    super.broadcast(r, e);
  }
}
