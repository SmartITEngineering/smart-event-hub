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
public class EventObjectConverter extends AbstractObjectRowConverter<PersistentEvent, EventId> {

  private static final byte[] FAMILY_SELF = Bytes.toBytes("self");
  private static final byte[] CELL_CHANNEL_ID = Bytes.toBytes("channelId");
  private static final byte[] CELL_CONTENT_TYPE = Bytes.toBytes("contentType");
  private static final byte[] CELL_UUID = Bytes.toBytes("uuid");
  private static final byte[] CELL_CREATION_DATE = Bytes.toBytes("creationDate");
  private static final byte[] CELL_CONTENT = Bytes.toBytes("content");

  @Override
  protected String[] getTablesToAttainLock() {
    return new String[]{getInfoProvider().getMainTableName()};
  }

  @Override
  protected void getPutForTable(PersistentEvent instance, ExecutorService service, Put put) {
    put.add(FAMILY_SELF, CELL_CHANNEL_ID, Bytes.toBytes(instance.getChannelId()));
    put.add(FAMILY_SELF, CELL_CONTENT_TYPE, Bytes.toBytes(instance.getContentType()));
    put.add(FAMILY_SELF, CELL_UUID, Bytes.toBytes(instance.getUuid()));
    put.add(FAMILY_SELF, CELL_CONTENT, instance.getContent());
    put.add(FAMILY_SELF, CELL_CREATION_DATE, Utils.toBytes(instance.getCreationDateTime()));
  }

  @Override
  protected void getDeleteForTable(PersistentEvent instance, ExecutorService service, Delete put) {
    //Nothing needed
  }

  @Override
  public PersistentEvent rowsToObject(Result startRow, ExecutorService executorService) {
    try {
      PersistentEvent event = new PersistentEvent();
      event.setId(getInfoProvider().getIdFromRowId(startRow.getRow()));
      event.setChannelId(Bytes.toString(startRow.getValue(FAMILY_SELF, CELL_CHANNEL_ID)));
      event.setContentType(Bytes.toString(startRow.getValue(FAMILY_SELF, CELL_CONTENT_TYPE)));
      event.setUuid(Bytes.toString(startRow.getValue(FAMILY_SELF, CELL_UUID)));
      event.setContent(startRow.getValue(FAMILY_SELF, CELL_CONTENT));
      event.setCreationDateTime(Utils.toDate(startRow.getValue(FAMILY_SELF, CELL_CREATION_DATE)));
      return event;
    }
    catch (Exception ex) {
      logger.error("Could not convert error!", ex);
    }
    return null;
  }
}
