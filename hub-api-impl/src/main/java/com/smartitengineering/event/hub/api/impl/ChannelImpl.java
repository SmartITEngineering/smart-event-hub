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

import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.api.Filter;
import java.net.URI;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
class ChannelImpl
    implements Channel,
               Cloneable {

  private String name;
  private String description, authToken;
  private Date creationDateTime, autoExpiryDateTime, lastModifiedDate;
  private Filter filter;
  private URI hubUri;
  private int position;

  public ChannelImpl(String name) {
    setName(name);
  }

  public ChannelImpl(Channel channel) {
    setName(channel.getName());
    setDescription(channel.getDescription());
    setAuthToken(channel.getAuthToken());
    setCreationDateTime(channel.getCreationDateTime());
    setAutoExpiryDateTime(channel.getAutoExpiryDateTime());
    setLastModifiedDate(channel.getLastModifiedDate());
    final Filter otherFilter = channel.getFilter();
    if (otherFilter != null) {
      setFilter(APIFactory.getFilter(otherFilter.getMimeType(), otherFilter.
          getFilterScript()));
    }
    setHubUri(channel.getHubUri());
    setPosition(channel.getPosition());
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public void setName(String name) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException();
    }
    this.name = name.trim().toLowerCase();
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public void setAutoExpiryDateTime(Date autoExpiryDateTime) {
    if (autoExpiryDateTime == null) {
      this.autoExpiryDateTime = null;
    }
    else {
      this.autoExpiryDateTime = new Date(autoExpiryDateTime.getTime());
    }
  }

  public void setCreationDateTime(Date creationDateTime) {
    if (creationDateTime != null) {
      this.creationDateTime = new Date(creationDateTime.getTime());
    }
    else {
      this.creationDateTime = null;
    }
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    if (lastModifiedDate == null) {
      this.lastModifiedDate = null;
    }
    else {
      this.lastModifiedDate = new Date(lastModifiedDate.getTime());
    }
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  public void setHubUri(URI hubUri) {
    this.hubUri = hubUri;
  }

  @Override
  public URI getHubUri() {
    return hubUri;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getAuthToken() {
    return authToken;
  }

  @Override
  public Date getCreationDateTime() {
    if (creationDateTime == null) {
      return null;
    }
    return new Date(creationDateTime.getTime());
  }

  @Override
  public Date getAutoExpiryDateTime() {
    if (autoExpiryDateTime == null) {
      return null;
    }
    return new Date(autoExpiryDateTime.getTime());
  }

  @Override
  public Filter getFilter() {
    return filter;
  }

  @Override
  public Date getLastModifiedDate() {
    if (lastModifiedDate == null) {
      return null;
    }
    return new Date(lastModifiedDate.getTime());
  }

  @Override
  public int getPosition() {
    return position;
  }

  @Override
  public ChannelImpl clone() {
    return new ChannelImpl(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!Channel.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    final Channel other = (Channel) obj;
    final String otherName = other.getName();
    if (otherName == null || !this.name.equals(otherName)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 19 * hash + this.name.hashCode();
    return hash;
  }

  @Override
  public String toString() {
    return getName();
  }
}
