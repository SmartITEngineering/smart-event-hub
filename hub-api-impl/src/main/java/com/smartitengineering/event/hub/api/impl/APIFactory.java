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

import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.api.Content;
import com.smartitengineering.event.hub.api.Event;
import com.smartitengineering.event.hub.api.Filter;
import com.smartitengineering.event.hub.api.Filter.SupportedMimeType;
import java.io.InputStream;
import java.util.Date;

/**
 *
 * @author imyousuf
 */
public final class APIFactory {

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

  public static ChannelBuilder getChannelBuilder(String channelName) {
    return new ChannelBuilder(channelName);
  }

  public static ChannelBuilder getChannelBuilder(Channel channel) {
    return new ChannelBuilder(channel);
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

    private EventBuilder() {
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
      if (builderEvent.getEventContent() != null) {
        return builderEvent.clone();
      }
      else {
        throw new IllegalStateException(
            "Event Content must be set before building!");
      }
    }
  }

  public static class ChannelBuilder {

    private ChannelImpl channelImpl;

    private ChannelBuilder(String name) {
      channelImpl = new ChannelImpl(name);
    }

    private ChannelBuilder(Channel channel) {
      channelImpl = new ChannelImpl(channel);
    }

    public ChannelBuilder name(String name) {
      channelImpl.setName(name);
      return this;
    }

    public ChannelBuilder description(String description) {
      channelImpl.setDescription(description);
      return this;
    }

    public ChannelBuilder authToken(String authToken) {
      channelImpl.setAuthToken(authToken);
      return this;
    }

    public ChannelBuilder creationDateTime(Date creationDateTime) {
      channelImpl.setCreationDateTime(creationDateTime);
      return this;
    }

    public ChannelBuilder autoExpiryDateTime(Date autoExpiryDateTime) {
      channelImpl.setAutoExpiryDateTime(autoExpiryDateTime);
      return this;
    }

    public ChannelBuilder filter(Filter filter) {
      channelImpl.setFilter(filter);
      return this;
    }

    public Channel build() {
      return channelImpl.clone();
    }
  }
}
