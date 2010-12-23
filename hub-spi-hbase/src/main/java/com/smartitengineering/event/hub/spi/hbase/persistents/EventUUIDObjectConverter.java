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

import com.google.inject.Inject;
import com.smartitengineering.dao.impl.hbase.spi.ExecutorService;
import com.smartitengineering.dao.impl.hbase.spi.SchemaInfoProvider;
import com.smartitengineering.dao.impl.hbase.spi.impl.AbstractObjectRowConverter;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 *
 * @author imyousuf
 */
public class EventUUIDObjectConverter extends AbstractObjectRowConverter<EventUUID, String> {

  @Inject
  private SchemaInfoProvider<PersistentEvent, EventId> eventSchemaInfoProvider;
  private static final byte[] FAMILY_SELF = Bytes.toBytes("self");
  private static final byte[] CELL_EVENT_ID = Bytes.toBytes("eventId");

  @Override
  protected String[] getTablesToAttainLock() {
    return new String[]{getInfoProvider().getMainTableName()};
  }

  @Override
  protected void getPutForTable(EventUUID instance, ExecutorService service, Put put) {
    try {
      put.add(FAMILY_SELF, CELL_EVENT_ID, eventSchemaInfoProvider.getRowIdFromId(instance.getEventId()));
    }
    catch (Exception ex) {
      logger.error("Could not convert error!", ex);
      throw new RuntimeException(ex);
    }
  }

  @Override
  protected void getDeleteForTable(EventUUID instance, ExecutorService service, Delete put) {
    //Do nothing
  }

  @Override
  public EventUUID rowsToObject(Result startRow, ExecutorService executorService) {
    try {
      EventUUID uuid = new EventUUID();
      uuid.setId(Bytes.toString(startRow.getRow()));
      uuid.setEventId(eventSchemaInfoProvider.getIdFromRowId(startRow.getValue(FAMILY_SELF, CELL_EVENT_ID)));
      return uuid;
    }
    catch (Exception ex) {
      logger.error("Could not convert error!", ex);
    }
    return null;

  }
}
