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
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
public class AutoId extends AbstractHBaseDomain<AutoId, String> {

  private long autoIdValue;

  public long getAutoIdValue() {
    return autoIdValue;
  }

  public void setAutoIdValue(long autoIdValue) {
    this.autoIdValue = autoIdValue;
  }

  @Override
  public boolean isValid() {
    return StringUtils.isNotBlank(getId());
  }
}
