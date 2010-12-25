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

import com.smartitengineering.dao.impl.hbase.spi.Externalizable;
import com.smartitengineering.event.hub.spi.hbase.HubPersistentStorerImpl;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
public class EventId implements Externalizable, Comparable<EventId> {

  private String channelName;
  private Long eventIdForChannel;
  protected transient Logger logger = LoggerFactory.getLogger(getClass());

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  public Long getEventIdForChannel() {
    return eventIdForChannel;
  }

  public void setEventIdForChannel(Long eventIdForChannel) {
    this.eventIdForChannel = eventIdForChannel;
  }

  public static EventId fromString(String idString) {
    EventId id = new EventId();
    try {
      id.readIdFromString(idString);
    }
    catch (Exception ex) {
      id.logger.error("Could not read event id from string ( '" + idString + "' )", ex);
      throw new RuntimeException(ex);
    }
    return id;
  }

  @Override
  public String toString() {
    String thisId =
           StringUtils.leftPad(ObjectUtils.toString(eventIdForChannel), HubPersistentStorerImpl.MAX_LENGTH, '0');
    return new StringBuilder().append(thisId).append(':').append(ObjectUtils.toString(
        channelName)).toString();
  }

  @Override
  public void writeExternal(DataOutput output) throws IOException {
    output.write(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(toString()));
  }

  @Override
  public void readExternal(DataInput input) throws IOException, ClassNotFoundException {
    String idString = Utils.readStringInUTF8(input);
    readIdFromString(idString);
  }

  protected void readIdFromString(String idString) throws IOException {
    if (logger.isInfoEnabled()) {
      logger.info("Trying to parse content id: " + idString);
    }
    if (StringUtils.isBlank(idString)) {
      throw new IOException("No content!");
    }
    String[] params = idString.split(":");
    if (params == null || params.length != 2) {
      throw new IOException("Object should have been in the format eventId:channelName!");
    }
    channelName = params[1];
    eventIdForChannel = NumberUtils.toLong(params[0]);
  }

  @Override
  public int compareTo(EventId o) {
    if (o == null) {
      return 1;
    }
    if (equals(o)) {
      return 0;
    }
    return toString().compareTo(o.toString());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final EventId other = (EventId) obj;
    if ((this.channelName == null) ? (other.channelName != null) : !this.channelName.equals(other.channelName)) {
      return false;
    }
    if (this.eventIdForChannel != other.eventIdForChannel &&
        (this.eventIdForChannel == null || !this.eventIdForChannel.equals(other.eventIdForChannel))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + (this.channelName != null ? this.channelName.hashCode() : 0);
    hash = 29 * hash + (this.eventIdForChannel != null ? this.eventIdForChannel.hashCode() : 0);
    return hash;
  }
}
