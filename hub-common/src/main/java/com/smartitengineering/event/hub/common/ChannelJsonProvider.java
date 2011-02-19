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

import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.api.Filter;
import com.smartitengineering.event.hub.api.Filter.SupportedMimeType;
import com.smartitengineering.event.hub.api.impl.APIFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author imyousuf
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChannelJsonProvider
    implements MessageBodyReader<Channel>,
               MessageBodyWriter<Channel> {

  private final ObjectMapper mapper = new ObjectMapper();
  public static final String NAME = "name";
  public static final String DESCRIPTION = "description";
  private static final String HUB_URI = "hubUri";
  private static final String EVENT_URI = "eventUri";
  public static final String AUTH_TOKEN = "authToken";
  public static final String AUTO_EXPIRE = "autoExpire";
  private static final String CREATED = "createdAt";
  public static final String FILTER_TYPE = "filterType";
  public static final String FILTER = "filter";
  private static final String LAST_MODIFIED = "lastModified";
  private static final String DATE_ISO8601_PATTERN =
                              DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern();
  @Context
  private UriInfo uriInfo;

  @Override
  public boolean isReadable(Class<?> type,
                            Type genericType,
                            Annotation[] annotations,
                            MediaType mediaType) {
    return Channel.class.isAssignableFrom(type) && MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
  }

  @Override
  public Channel readFrom(Class<Channel> type,
                          Type genericType,
                          Annotation[] annotations,
                          MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream)
      throws IOException,
             WebApplicationException {
    Map<String, String> parsedJsonContentMap = mapper.readValue(entityStream,
                                                                HashMap.class);
    final String name = parsedJsonContentMap.get(NAME);
    if (StringUtils.isBlank(name)) {
      throw new WebApplicationException(new NullPointerException(
          "Name is blank!"), Status.BAD_REQUEST);
    }
    final String description = parsedJsonContentMap.get(DESCRIPTION);
    final String uriStr = parsedJsonContentMap.get(HUB_URI);
    final String authToken = parsedJsonContentMap.get(AUTH_TOKEN);
    final String filterTypeStr = parsedJsonContentMap.get(FILTER_TYPE);
    final SupportedMimeType mimeType;
    try {
      if (StringUtils.isNotBlank(filterTypeStr)) {
        mimeType = SupportedMimeType.valueOf(filterTypeStr.toUpperCase());
      }
      else {
        mimeType = null;
      }
    }
    catch (Throwable th) {
      throw new WebApplicationException(th, Status.BAD_REQUEST);
    }
    final String filterScript = parsedJsonContentMap.get(FILTER);
    final Date expireDate;
    try {
      final String expireStr = parsedJsonContentMap.get(AUTO_EXPIRE);
      expireDate = parseDate(expireStr);
    }
    catch (ParseException ex) {
      throw new WebApplicationException(ex, Status.BAD_REQUEST);
    }
    final Date creationDate;
    try {
      final String createdAtStr = parsedJsonContentMap.get(CREATED);
      creationDate = parseDate(createdAtStr);
    }
    catch (ParseException ex) {
      throw new WebApplicationException(ex, Status.BAD_REQUEST);
    }
    final Date lastModifiedDate;
    try {
      final String lastModifiedStr = parsedJsonContentMap.get(LAST_MODIFIED);
      lastModifiedDate = parseDate(lastModifiedStr);
    }
    catch (ParseException ex) {
      throw new WebApplicationException(ex, Status.BAD_REQUEST);
    }
    final Filter filter;
    if (mimeType != null && StringUtils.isNotBlank(filterScript)) {
      filter = APIFactory.getFilter(mimeType, filterScript);
    }
    else {
      filter = null;
      if (mimeType != null || StringUtils.isNotBlank(filterScript)) {
        throw new WebApplicationException(new IllegalArgumentException(
            "Filter should have both type and script specified."),
                                          Status.BAD_REQUEST);
      }
    }
    return APIFactory.getChannelBuilder(name).description(description).authToken(
        authToken).autoExpiryDateTime(expireDate).creationDateTime(creationDate).
        filter(filter).lastModifiedDate(lastModifiedDate).build();
  }

  @Override
  public boolean isWriteable(Class<?> type,
                             Type genericType,
                             Annotation[] annotations,
                             MediaType mediaType) {
    return Channel.class.isAssignableFrom(type) && MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
  }

  @Override
  public long getSize(Channel t,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType) {
    if (isWriteable(type, genericType, annotations, mediaType)) {
      return getJsonString(t).length();
    }
    else {
      return 0;
    }
  }

  @Override
  public void writeTo(Channel t,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders,
                      OutputStream entityStream)
      throws IOException,
             WebApplicationException {
    if (isWriteable(type, genericType, annotations, mediaType)) {
      IOUtils.write(getJsonString(t), entityStream);
    }
  }

  public String getJsonString(Channel channel) {
    if (channel == null) {
      return "";
    }
    Map<String, String> jsonMap = new LinkedHashMap<String, String>();
    jsonMap.put(NAME, channel.getName());
    if (StringUtils.isNotBlank(channel.getAuthToken())) {
      jsonMap.put(AUTH_TOKEN, channel.getAuthToken());
    }
    if (StringUtils.isNotBlank(channel.getDescription())) {
      jsonMap.put(DESCRIPTION, channel.getDescription());
    }
    if (channel.getCreationDateTime() != null) {
      jsonMap.put(CREATED, formatDate(channel.getCreationDateTime()));
    }
    if (channel.getAutoExpiryDateTime() != null) {
      jsonMap.put(AUTO_EXPIRE, formatDate(channel.getAutoExpiryDateTime()));
    }
    if (channel.getLastModifiedDate() != null) {
      jsonMap.put(LAST_MODIFIED, formatDate(channel.getLastModifiedDate()));
    }
    jsonMap.put(HUB_URI, uriInfo.getBaseUriBuilder().path(Constants.RSRC_PATH_CHANNEL_PREFIX).path(channel.getName()).
        path(Constants.RSRC_PATH_CHANNEL_HUB).build().toASCIIString());
    jsonMap.put(EVENT_URI, uriInfo.getBaseUriBuilder().path(Constants.RSRC_PATH_CHANNEL_PREFIX).path(
        channel.getName()).path(Constants.RSRC_PATH_CHANNEL_EVENTS).build().toASCIIString());
    if (channel.getFilter() != null) {
      Filter filter = channel.getFilter();
      jsonMap.put(FILTER, filter.getFilterScript());
      jsonMap.put(FILTER_TYPE, filter.getMimeType().name());
    }
    try {
      return mapper.writeValueAsString(jsonMap);
    }
    catch (Exception ex) {
      return "";
    }
  }

  protected String formatDate(Date date) {
    return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(date);
  }

  protected Date parseDate(final String dateStr)
      throws ParseException {
    Date date;
    if (StringUtils.isNotBlank(dateStr)) {
      date = DateUtils.parseDate(dateStr, new String[]{DATE_ISO8601_PATTERN});
    }
    else {
      date = null;
    }
    return date;
  }
}
