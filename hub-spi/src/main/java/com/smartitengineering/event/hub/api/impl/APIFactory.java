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
package com.smartitengineering.event.hub.api.impl;

import com.smartitengineering.event.hub.api.Content;
import com.smartitengineering.event.hub.api.Event;
import com.smartitengineering.event.hub.api.Filter;
import com.smartitengineering.event.hub.api.Filter.SupportedMimeType;
import java.io.InputStream;

/**
 *
 * @author imyousuf
 */
public class APIFactory {

  private APIFactory() {
  }

  public static Filter getFilter(SupportedMimeType mimeType,
                                 String script) {
    FilterImpl filterImpl = new FilterImpl();
    filterImpl.setFilterScript(script);
    filterImpl.setMimeType(mimeType);
    return filterImpl;
  }

  public static EventBuilder getEventBuilder() {
    return new EventBuilder();
  }

  public static Content getContent(String contentType,
                                   InputStream stream) {
    if (contentType == null || stream == null) {
      throw new IllegalArgumentException(
          "Stream or content type can not be null. They are mendatory fields");
    }
    ContentImpl contentImpl = new ContentImpl();
    contentImpl.setContent(stream);
    contentImpl.setContentType(contentType);
    return contentImpl;
  }

  public static class EventBuilder {

    private final EventImpl builderEvent;

    public EventBuilder() {
      builderEvent = new EventImpl();
    }

    public EventBuilder placeholder(String placeholderId) {
      builderEvent.setPlaceholderId(placeholderId);
      return this;
    }

    public EventBuilder eventContent(Content eventContent) {
      builderEvent.setEventContent(eventContent);
      return this;
    }

    public EventBuilder uuid(String uuid) {
      builderEvent.setUniversallyUniqueID(uuid);
      return this;
    }

    public Event build() {
      try {
        return (Event) builderEvent.clone();
      }
      catch (CloneNotSupportedException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
