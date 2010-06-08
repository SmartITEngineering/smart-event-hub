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
package com.smartitengineering.event.hub.spi.db;

import com.smartitengineering.domain.AbstractPersistentDTO;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
public class PersistentChannel
    extends AbstractPersistentDTO<PersistentChannel> {

  static final String NAME = "name";
  private String name, description, authToken, filterType, script;
  private Date creationDateTime, autoExpiryDateTime, lastModifiedDateTime;

  public String getAuthToken() {
    return authToken;
  }

  public Date getAutoExpiryDateTime() {
    if (autoExpiryDateTime == null) {
      return null;
    }
    return new Date(autoExpiryDateTime.getTime());
  }

  public Date getCreationDateTime() {
    if (creationDateTime == null) {
      return null;
    }
    return new Date(creationDateTime.getTime());
  }

  public Date getLastModifiedDateTime() {
    if (lastModifiedDateTime == null) {
      return null;
    }
    return new Date(lastModifiedDateTime.getTime());
  }

  public String getDescription() {
    return description;
  }

  public String getFilterType() {
    return filterType;
  }

  public String getName() {
    return name;
  }

  public String getScript() {
    return script;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public void setAutoExpiryDateTime(Date autoExpiryDateTime) {
    if (autoExpiryDateTime != null) {
      this.autoExpiryDateTime = new Date(autoExpiryDateTime.getTime());
    }
    else {
      this.autoExpiryDateTime = null;
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

  public void setLastModifiedDateTime(Date lastModifiedDate) {
    if (lastModifiedDate == null) {
      this.lastModifiedDateTime = null;
    }
    else {
      this.lastModifiedDateTime = new Date(lastModifiedDate.getTime());
    }
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFilterType(String filterType) {
    this.filterType = filterType;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setScript(String script) {
    this.script = script;
  }

  public boolean isValid() {
    return StringUtils.isNotBlank(name);
  }
}
