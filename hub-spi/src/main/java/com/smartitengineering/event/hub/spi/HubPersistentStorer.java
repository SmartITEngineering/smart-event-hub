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

import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.api.Event;
import java.util.LinkedHashSet;

/**
 *
 * @author imyousuf
 */
public interface HubPersistentStorer {

  public void create(Channel channel);

  public void update(Channel channel);

  public void delete(Channel channel);

  public Channel getChannel(String channelName);

  public void create(Event event);

  public void delete(Event event);

  /**
   * Get the event persisted at the given placeholderId
   * @param placeholderId The event to fetch
   * @return Event at the placeholder else null if no such placeholder currently
   *         in persistent storage.
   */
  public Event getEvent(String placeholderId);

  /**
   * Retrieve all events from a certain placeholder and in one direction
   * @param placeholderId The placeholder id to start selecting from
   * @param count Signed integer with numer of event to return. Its sign acts as
   *        after and before when, +ve and -ve respectively
   * @return Ordered set of events for the matching criteria
   */
  public LinkedHashSet<Event> getEvents(String placeholderId,
                                        int count);
}
