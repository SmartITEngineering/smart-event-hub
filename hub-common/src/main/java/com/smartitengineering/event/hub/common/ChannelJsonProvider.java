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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;

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

  

  public boolean isReadable(Class<?> type,
                            Type genericType,
                            Annotation[] annotations,
                            MediaType mediaType) {
    return Channel.class.isAssignableFrom(type) &&
           MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
  }

  public Channel readFrom(Class<Channel> type,
                          Type genericType,
                          Annotation[] annotations,
                          MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream)
      throws IOException,
             WebApplicationException {

    JsonParser parser = new JsonFactory().createJsonParser(entityStream);
    Map<String, Object> parsedJsonContentMap = parser.readValueAs(Map.class);
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isWriteable(Class<?> type,
                             Type genericType,
                             Annotation[] annotations,
                             MediaType mediaType) {
    return Channel.class.isAssignableFrom(type) &&
           MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
  }

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
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
