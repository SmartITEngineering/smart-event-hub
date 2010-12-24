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
package com.smartitengineering.event.hub.spi.hbase.persistents;

import com.smartitengineering.dao.impl.hbase.spi.domain.AbstractHBaseDomain;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
public class PersistentChannel extends AbstractHBaseDomain<PersistentChannel, Long> {

  public static final String NAME = "name";
  private String description, authToken, filterType, script, name;
  private Date creationDateTime, autoExpiryDateTime, lastModifiedDateTime;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public Date getAutoExpiryDateTime() {
    return autoExpiryDateTime;
  }

  public void setAutoExpiryDateTime(Date autoExpiryDateTime) {
    this.autoExpiryDateTime = autoExpiryDateTime;
  }

  public Date getCreationDateTime() {
    return creationDateTime;
  }

  public void setCreationDateTime(Date creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFilterType() {
    return filterType;
  }

  public void setFilterType(String filterType) {
    this.filterType = filterType;
  }

  public Date getLastModifiedDateTime() {
    return lastModifiedDateTime;
  }

  public void setLastModifiedDateTime(Date lastModifiedDateTime) {
    this.lastModifiedDateTime = lastModifiedDateTime;
  }

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  @Override
  public boolean isValid() {
    return StringUtils.isNotBlank(getName());
  }
}
