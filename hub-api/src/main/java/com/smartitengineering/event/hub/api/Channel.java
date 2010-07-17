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
package com.smartitengineering.event.hub.api;

import java.net.URI;
import java.util.Date;

/**
 * A channel for broadcasting events. Consumers can only listen to channels that
 * are currently available and GET request will not create any channel. Events
 * can not be broadcasted with a channel.
 * @author imyousuf
 * @since 0.1
 */
public interface Channel {

  static final String HUB_SUB_RESOURCE_PATH = "hub";

  public int getPosition();

  /**
   * Retrieve the name of the channel. It has to be unique and case insensitive
   * in nature.
   * @return Name of the channel; should never be null or blank.
   */
  public String getName();

  public String getDescription();

  /**
   * Return the auth token for the channel. All clients intending to listen to
   * this channel must supply auth token besides the authentication tokens.
   * @return Authorization token for this channel
   */
  public String getAuthToken();

  /**
   * Retrieves the creation date time of channel. It will be either created by
   * the {@link HubPersistentStorer persistent storer} if configured or by the
   * RESTful service which receives the channel information.
   * @return Creation date time of this channel
   */
  public Date getCreationDateTime();

  public Date getLastModifiedDate();

  /**
   * Return the auto expiry date time of this channel. If this channel is not
   * deleted then at the designated date time this channel will be deleted from
   * the server.
   * @return The expiration date time.
   */
  public Date getAutoExpiryDateTime();

  /**
   * Retrieves the current filter for this channel
   * @return Filter of this channel
   */
  public Filter getFilter();

  public URI getHubUri();
}
