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

import com.smartitengineering.dao.impl.hbase.spi.ExecutorService;
import com.smartitengineering.dao.impl.hbase.spi.impl.AbstractObjectRowConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 *
 * @author imyousuf
 */
public class ChannelObjectConverter extends AbstractObjectRowConverter<PersistentChannel, Long> {

  private static final byte[] FAMILY_SELF = Bytes.toBytes("self");
  private static final byte[] CELL_NAME = Bytes.toBytes("self");
  private static final byte[] CELL_DESC = Bytes.toBytes("self");
  private static final byte[] CELL_AUTH_TOKEN = Bytes.toBytes("self");
  private static final byte[] CELL_FILTER_TYPE = Bytes.toBytes("self");
  private static final byte[] CELL_SCRIPT = Bytes.toBytes("self");
  private static final byte[] CELL_AUTO_EXPIRY_DATE_TIME = Bytes.toBytes("self");
  private static final byte[] CELL_CREATION_DATE = Bytes.toBytes("self");
  private static final byte[] CELL_LAST_MODIFIED_DATE = Bytes.toBytes("self");

  @Override
  protected String[] getTablesToAttainLock() {
    return new String[]{getInfoProvider().getMainTableName()};
  }

  @Override
  protected void getPutForTable(PersistentChannel instance, ExecutorService service, Put put) {
    put.add(FAMILY_SELF, CELL_NAME, Bytes.toBytes(instance.getName()));
    if (StringUtils.isNotBlank(instance.getDescription())) {
      put.add(FAMILY_SELF, CELL_DESC, Bytes.toBytes(instance.getDescription()));
    }
    if (StringUtils.isNotBlank(instance.getAuthToken())) {
      put.add(FAMILY_SELF, CELL_AUTH_TOKEN, Bytes.toBytes(instance.getAuthToken()));
    }
    if (StringUtils.isNotBlank(instance.getFilterType())) {
      put.add(FAMILY_SELF, CELL_FILTER_TYPE, Bytes.toBytes(instance.getFilterType()));
    }
    if (StringUtils.isNotBlank(instance.getScript())) {
      put.add(FAMILY_SELF, CELL_SCRIPT, Bytes.toBytes(instance.getScript()));
    }
    if (instance.getAutoExpiryDateTime() != null) {
      put.add(FAMILY_SELF, CELL_AUTO_EXPIRY_DATE_TIME, Utils.toBytes(instance.getAutoExpiryDateTime()));
    }
    put.add(FAMILY_SELF, CELL_LAST_MODIFIED_DATE, Utils.toBytes(instance.getLastModifiedDateTime()));
    put.add(FAMILY_SELF, CELL_CREATION_DATE, Utils.toBytes(instance.getCreationDateTime()));
  }

  @Override
  protected void getDeleteForTable(PersistentChannel instance, ExecutorService service, Delete put) {
    //Nothing needed
  }

  @Override
  public PersistentChannel rowsToObject(Result startRow, ExecutorService executorService) {
    try {
      PersistentChannel channel = new PersistentChannel();
      channel.setId(getInfoProvider().getIdFromRowId(startRow.getRow()));
      channel.setName(Bytes.toString(startRow.getValue(FAMILY_SELF, CELL_NAME)));
      if (startRow.getValue(FAMILY_SELF, CELL_DESC) != null) {
        channel.setDescription(Bytes.toString(startRow.getValue(FAMILY_SELF, CELL_DESC)));
      }
      if (startRow.getValue(FAMILY_SELF, CELL_AUTH_TOKEN) != null) {
        channel.setAuthToken(Bytes.toString(startRow.getValue(FAMILY_SELF, CELL_AUTH_TOKEN)));
      }
      if (startRow.getValue(FAMILY_SELF, CELL_FILTER_TYPE) != null) {
        channel.setFilterType(Bytes.toString(startRow.getValue(FAMILY_SELF, CELL_FILTER_TYPE)));
      }
      if (startRow.getValue(FAMILY_SELF, CELL_SCRIPT) != null) {
        channel.setScript(Bytes.toString(startRow.getValue(FAMILY_SELF, CELL_SCRIPT)));
      }
      if (startRow.getValue(FAMILY_SELF, CELL_AUTO_EXPIRY_DATE_TIME) != null) {
        channel.setAutoExpiryDateTime(Utils.toDate(startRow.getValue(FAMILY_SELF, CELL_AUTO_EXPIRY_DATE_TIME)));
      }
      channel.setLastModifiedDateTime(Utils.toDate(startRow.getValue(FAMILY_SELF, CELL_LAST_MODIFIED_DATE)));
      channel.setCreationDateTime(Utils.toDate(startRow.getValue(FAMILY_SELF, CELL_CREATION_DATE)));
      return channel;
    }
    catch (Exception ex) {
      logger.error("Could not convert error!", ex);
    }
    return null;
  }
}
