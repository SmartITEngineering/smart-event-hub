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
package com.smartitengineering.event.hub.common;

import com.smartitengineering.event.hub.api.Event;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author imyousuf
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class EventJsonProvider implements MessageBodyWriter<Event> {

  private final ObjectMapper mapper = new ObjectMapper();
  private static final String PLACEHOLDER_ID = "id";
  private static final String UNIVERSAL_UNIQUE_ID = "uniqueId";
  private static final String CONTENT_TYPE = "content-type";
  private static final String CONTENT_AS_STRING = "content-as-string";
  private static final String CREATION_DATE = "created-at";
  private final Map<Event, String> contentCache = new WeakHashMap<Event, String>();

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return Event.class.isAssignableFrom(type) && MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
  }

  @Override
  public long getSize(Event t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    if (isWriteable(type, genericType, annotations, mediaType)) {
      return getJsonString(t).length();
    }
    else {
      return 0;
    }
  }

  @Override
  public void writeTo(Event t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
                                                                                                    WebApplicationException {
    if (isWriteable(type, genericType, annotations, mediaType)) {
      IOUtils.write(getJsonString(t), entityStream);
    }
  }

  public String getJsonString(Event event) {
    if (event == null) {
      return "";
    }
    Map<String, String> jsonMap = new LinkedHashMap<String, String>();
    if (StringUtils.isNotBlank(event.getPlaceholderId())) {
      jsonMap.put(PLACEHOLDER_ID, event.getPlaceholderId());
    }
    if (StringUtils.isNotBlank(event.getUniversallyUniqueID())) {
      jsonMap.put(UNIVERSAL_UNIQUE_ID, event.getUniversallyUniqueID());
    }
    if (StringUtils.isNotBlank(event.getEventContent().getContentType())) {
      jsonMap.put(CONTENT_TYPE, event.getEventContent().getContentType());
    }
    InputStream contentStream = event.getEventContent().getContent();
    if (contentStream != null) {
      String contentAsString = "";
      if (contentCache.containsKey(event)) {
        contentAsString = contentCache.get(event);
      }
      else {
        try {
          if (contentStream.markSupported()) {
            contentStream.mark(Integer.MAX_VALUE);
          }
          contentAsString = IOUtils.toString(contentStream);
          contentCache.put(event, contentAsString);
          if (contentStream.markSupported()) {
            contentStream.reset();
          }
        }
        catch (IOException ex) {
        }
      }
      jsonMap.put(CONTENT_AS_STRING, contentAsString);
    }
    Date creationDate = event.getCreationDate();
    if (creationDate != null) {
      jsonMap.put(CREATION_DATE, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(creationDate));
    }
    try {
      return mapper.writeValueAsString(jsonMap);
    }
    catch (Exception ex) {
      return "";
    }
  }
}
