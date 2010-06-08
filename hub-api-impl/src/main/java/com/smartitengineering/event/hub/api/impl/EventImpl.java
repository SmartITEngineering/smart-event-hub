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

import com.smartitengineering.event.hub.api.Content;
import com.smartitengineering.event.hub.api.Event;
import java.util.Date;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
class EventImpl
    implements Event,
               Cloneable {

  private Content eventContent;
  private String placeholderId;
  private String universallyUniqueID;
  private Date creationDate;

  public void setEventContent(Content eventContent) {
    this.eventContent = eventContent;
  }

  public void setPlaceholderId(String placeholderId) {
    this.placeholderId = placeholderId;
  }

  public void setCreationDate(Date creationDate) {
    if (creationDate == null) {
      this.creationDate = null;
    }
    else {
      this.creationDate = new Date(creationDate.getTime());
    }
  }

  /**
   * Sets the universal unique ID if and only if its blank
   * @param universallyUniqueID The UUID to be set
   */
  public void setUniversallyUniqueID(String universallyUniqueID) {
    if (StringUtils.isNotBlank(universallyUniqueID)) {
      this.universallyUniqueID = universallyUniqueID;
    }
  }

  public String getPlaceholderId() {
    return placeholderId;
  }

  public String getUniversallyUniqueID() {
    if (StringUtils.isBlank(universallyUniqueID)) {
      initUUID();
    }
    return universallyUniqueID;
  }

  public Content getEventContent() {
    return eventContent;
  }

  public Date getCreationDate() {
    if (creationDate == null) {
      return null;
    }
    return new Date(creationDate.getTime());
  }

  protected synchronized void initUUID() {
    //Extra check for successive access to initUUID of an instance
    if (StringUtils.isBlank(universallyUniqueID)) {
      universallyUniqueID = UUID.randomUUID().toString();
    }
  }

  @Override
  public EventImpl clone() {
    EventImpl clone = new EventImpl();
    clone.setEventContent(eventContent);
    if (StringUtils.isNotBlank(universallyUniqueID)) {
      clone.setUniversallyUniqueID(getUniversallyUniqueID());
    }
    clone.setPlaceholderId(placeholderId);
    clone.setCreationDate(creationDate);
    return clone;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!Event.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    final Event other = (Event) obj;
    final String otherUniversallyUniqueID = other.getUniversallyUniqueID();
    if (otherUniversallyUniqueID == null ||
        !this.getUniversallyUniqueID().equals(otherUniversallyUniqueID)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 23 * hash + this.getUniversallyUniqueID().hashCode();
    return hash;
  }

  @Override
  public String toString() {
    return getPlaceholderId();
  }
}
