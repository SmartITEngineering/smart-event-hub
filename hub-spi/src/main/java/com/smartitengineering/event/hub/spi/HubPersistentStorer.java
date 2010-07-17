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
import com.smartitengineering.event.hub.api.Content;
import com.smartitengineering.event.hub.api.Event;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 *
 * @author imyousuf
 */
public interface HubPersistentStorer {

  /**
   * Persists a new channel in persistent storage supported. It will also set
   * the creation time of channel.
   * @param channel Channel to persist
   */
  public void create(Channel channel);

  public void update(Channel channel);

  public void delete(Channel channel);

  public Channel getChannel(String channelName);

  /**
   * Retrieve all channels in paginated manner.
   * @param startIndex Index to start retrieving channels
   * @param count Maximum number of channels to retrieve. Its sign acts as after or before when +ve and -ve respectively
   * @return Collection of channels as fits the parameters. Should never return null.
   */
  public Collection<Channel> getChannels(final int startIndex, final int count);

  /**
   * An event can be created only using its {@link Content content}. After its
   * persisted it will provide its UUID and Place holder ID and that is returned
   * as a new event instance.
   * @param event Event to be saved
   * @return The same event with placeholder id and UUID set
   */
  public Event create(Channel channel, Event event);

  public void delete(Event event);

  /**
   * Get the event persisted at the given placeholderId
   * @param placeholderId The event to fetch
   * @return Event at the placeholder else null if no such placeholder currently
   *         in persistent storage.
   */
  public Event getEvent(String placeholderId);

  public Event getEventByUUID(String uuid);

  /**
   * Retrieve all events from a certain placeholder and in one direction
   * @param placeholderId The placeholder id to start selecting from
   * @param count Signed integer with number of event to return. Its sign acts as
   *        after and before when, +ve and -ve respectively
   * @param channelId Channel name/ID to fetch the events for. It will be considered only if it is non-blank
   * @return Ordered set of events for the matching criteria
   */
  public LinkedHashSet<Event> getEvents(String placeholderId, String channelId,
                                        int count);
}
