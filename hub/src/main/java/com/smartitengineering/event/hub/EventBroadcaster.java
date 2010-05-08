package com.smartitengineering.event.hub;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.jersey.JerseyBroadcaster;

/**
 *
 * @author imyousuf
 */
public class EventBroadcaster
    extends JerseyBroadcaster {

  public EventBroadcaster() {
    super();
  }

  @Override
  protected void broadcast(final AtmosphereResource r,
                           final AtmosphereResourceEvent e) {
    super.broadcast(r, e);
  }
}
