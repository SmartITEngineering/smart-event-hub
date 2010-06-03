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
package com.smartitengineering.event.hub.spi.db;

import com.smartitengineering.domain.AbstractPersistentDTO;
import java.io.InputStream;
import java.sql.Blob;
import javax.sql.rowset.serial.SerialBlob;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
public class PersistentEvent
    extends AbstractPersistentDTO<PersistentEvent> {

  static final String PLACE_HOLDER_ID = "id";
  static final String UUID = "uuid";
  private String uuid, contentType;
  private byte[] content;

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getPlaceholderId() {
    return getId().toString();
  }

  public void setPlaceholderId(String placeholderId) {
    if (StringUtils.isNotBlank(placeholderId)) {
      setId(Integer.parseInt(placeholderId));
    }
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  Blob getContentBlob() {
    try {
      Blob blob = new SerialBlob(content);
      return blob;
    }
    catch (Exception ex) {
      return null;
    }
  }

  public InputStream getContentStream() {
    return IOUtils.toInputStream(new String(content));
  }

  void setContentBlob(Blob blob) {
    InputStream stream;
    try {
      stream = blob.getBinaryStream();
    }
    catch (Exception ex) {
      stream = null;
      //TODO log exception
    }
    setContentStream(stream);
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

  public boolean isValid() {
    return getContent() != null && getContent().length > 0 && StringUtils.
        isNotBlank(getContentType());
  }
}
