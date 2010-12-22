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
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 *
 * @author imyousuf
 */
public class AutoIdObjectConverter extends AbstractObjectRowConverter<AutoId, String> {

  private static final byte[] FAMILY_SELF = Bytes.toBytes("self");
  private static final byte[] CELL_ID_VAL = Bytes.toBytes("idValue");

  @Override
  protected String[] getTablesToAttainLock() {
    return new String[]{getInfoProvider().getMainTableName()};
  }

  @Override
  protected void getPutForTable(AutoId instance, ExecutorService service, Put put) {
    put.add(FAMILY_SELF, CELL_ID_VAL, Bytes.toBytes(instance.getAutoIdValue()));
  }

  @Override
  protected void getDeleteForTable(AutoId instance, ExecutorService service, Delete put) {
    //Nothing to do
  }

  @Override
  public AutoId rowsToObject(Result startRow, ExecutorService executorService) {
    try {
      AutoId id = new AutoId();
      id.setAutoIdValue(Bytes.toLong(startRow.getValue(FAMILY_SELF, CELL_ID_VAL)));
      id.setId(getInfoProvider().getIdFromRowId(startRow.getRow()));
      return id;
    }
    catch (Exception ex) {
      logger.error("Could not convert to auto id!", ex);
    }
    return null;
  }
}
