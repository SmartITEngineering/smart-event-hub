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
public class AutoIdObjectConverter extends AbstractObjectRowConverter<RowAutoIdIndex, String> {

  private static final byte[] FAMILY_SELF = Bytes.toBytes("self");
  private static final byte[] CELL_ID_VAL = Bytes.toBytes("idValue");
  private static final byte[] CELL_REVERSE_ID_VAL = Bytes.toBytes("reverseIdValue");
  private static final byte[] CELL_REVERSE_ID = Bytes.toBytes("reverseId");

  @Override
  protected String[] getTablesToAttainLock() {
    return new String[]{getInfoProvider().getMainTableName()};
  }

  @Override
  protected void getPutForTable(RowAutoIdIndex instance, ExecutorService service, Put put) {
    put.add(FAMILY_SELF, CELL_ID_VAL, Bytes.toBytes(instance.getAutoIdValue()));
    put.add(FAMILY_SELF, CELL_REVERSE_ID_VAL, Bytes.toBytes(instance.getReverseAutoIdValue()));
    if (StringUtils.isNotBlank(instance.getReverseId())) {
      put.add(FAMILY_SELF, CELL_REVERSE_ID, Bytes.toBytes(instance.getReverseId()));
    }
  }

  @Override
  protected void getDeleteForTable(RowAutoIdIndex instance, ExecutorService service, Delete put) {
    //Nothing to do
  }

  @Override
  public RowAutoIdIndex rowsToObject(Result startRow, ExecutorService executorService) {
    try {
      RowAutoIdIndex id = new RowAutoIdIndex();
      id.setAutoIdValue(Bytes.toLong(startRow.getValue(FAMILY_SELF, CELL_ID_VAL)));
      id.setReverseAutoIdValue(Bytes.toLong(startRow.getValue(FAMILY_SELF, CELL_REVERSE_ID_VAL)));
      id.setId(getInfoProvider().getIdFromRowId(startRow.getRow()));
      if (startRow.getValue(FAMILY_SELF, CELL_REVERSE_ID) != null) {
        id.setReverseId(Bytes.toString(startRow.getValue(FAMILY_SELF, CELL_REVERSE_ID)));
      }
      return id;
    }
    catch (Exception ex) {
      logger.error("Could not convert to auto id!", ex);
    }
    return null;
  }
}
