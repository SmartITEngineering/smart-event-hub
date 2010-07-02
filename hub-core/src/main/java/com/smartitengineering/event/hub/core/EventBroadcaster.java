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
package com.smartitengineering.event.hub.core;

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

  public EventBroadcaster(String channelName) {
    super();
  }

  @Override
  protected void broadcast(final AtmosphereResource r,
                           final AtmosphereResourceEvent e) {
    super.broadcast(r, e);
  }
}
