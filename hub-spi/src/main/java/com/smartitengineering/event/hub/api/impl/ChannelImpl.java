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
  private Date creationDateTime, autoExpiryDateTime;
  private Filter filter;

  public ChannelImpl(String name) {
    setName(name);
  }

  public void setName(String name) {
    if(StringUtils.isBlank(name)) {
      throw new IllegalArgumentException();
    }
    this.name = name.trim().toLowerCase();
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public void setAutoExpiryDateTime(Date autoExpiryDateTime) {
    this.autoExpiryDateTime = autoExpiryDateTime;
  }

  public void setCreationDateTime(Date creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getAuthToken() {
    return authToken;
  }

  public Date getCreationDateTime() {
    return creationDateTime;
  }

  public Date getAutoExpiryDateTime() {
    return autoExpiryDateTime;
  }

  public Filter getFilter() {
    return filter;
  }

  @Override
  public ChannelImpl clone() {
    ChannelImpl clone = new ChannelImpl(name);
    clone.authToken = authToken;
    clone.description = description;
    clone.creationDateTime = creationDateTime;
    clone.autoExpiryDateTime = autoExpiryDateTime;
    clone.filter = filter;
    return clone;
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
}
