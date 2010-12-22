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
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author imyousuf
 */
public class PersistentEvent
    extends AbstractHBaseDomain<PersistentEvent, Long> {

  static final String PLACE_HOLDER_ID = "id";
  static final String UUID = "uuid";
  static final String CHANNEL_ID = "channelId";
  private String contentType, channelId, uuid;
  private byte[] content;
  private Date creationDateTime;

  public void setPlaceholderId(String placeholder) {
    setId(NumberUtils.toLong(placeholder, -1));
  }

  public String getPlaceholderId() {
    if (getId() == null) {
      return "";
    }
    return Long.toString(getId());
  }

  public String getChannelId() {
    return channelId;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public void setCreationDateTime(Date creationDateTime) {
    if (creationDateTime != null) {
      this.creationDateTime = new Date(creationDateTime.getTime());
    }
    else {
      this.creationDateTime = null;
    }
  }

  public Date getCreationDateTime() {
    if (creationDateTime == null) {
      return null;
    }
    return new Date(creationDateTime.getTime());
  }

  public byte[] getContent() {
    if (content == null) {
      return null;
    }
    return Arrays.copyOf(content, content.length);
  }

  public void setContent(byte[] content) {
    this.content = Arrays.copyOf(content, content.length);
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public InputStream getContentStream() {
    return IOUtils.toInputStream(new String(content));
  }

  public void setContentStream(InputStream stream) {
    try {
      content = IOUtils.toByteArray(stream);
    }
    catch (Exception ex) {
      //TODO log exception
    }
    finally {
      IOUtils.closeQuietly(stream);
    }
    if (content == null) {
      content = new byte[0];
    }
  }

  @Override
  public boolean isValid() {
    return getContent() != null && getContent().length > 0 && StringUtils.isNotBlank(getContentType());
  }
}
