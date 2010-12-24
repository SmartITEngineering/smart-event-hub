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
public class ReverseIndexObjectConverter extends AbstractObjectRowConverter<ReverseIdIndex, String> {

  private static final byte[] FAMILY_SELF = Bytes.toBytes("self");
  private static final byte[] CELL_REVERSE_INDEX = Bytes.toBytes("reverse");

  @Override
  protected String[] getTablesToAttainLock() {
    return new String[]{getInfoProvider().getMainTableName()};
  }

  @Override
  protected void getPutForTable(ReverseIdIndex instance, ExecutorService service, Put put) {
    try {
      put.add(FAMILY_SELF, CELL_REVERSE_INDEX, Bytes.toBytes(instance.getReverseId()));
    }
    catch (Exception ex) {
      logger.error("Could not convert error!", ex);
      throw new RuntimeException(ex);
    }
  }

  @Override
  protected void getDeleteForTable(ReverseIdIndex instance, ExecutorService service, Delete put) {
    //Do nothing
  }

  @Override
  public ReverseIdIndex rowsToObject(Result startRow, ExecutorService executorService) {
    try {
      ReverseIdIndex uuid = new ReverseIdIndex();
      uuid.setId(Bytes.toString(startRow.getRow()));
      uuid.setReverseId(Bytes.toString(startRow.getValue(FAMILY_SELF, CELL_REVERSE_INDEX)));
      return uuid;
    }
    catch (Exception ex) {
      logger.error("Could not convert error!", ex);
    }
    return null;

  }
}
