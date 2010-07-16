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
package com.smartitengineering.event.hub.core;

import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.spi.HubPersistentStorer;
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import java.net.URI;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
public abstract class AbstractChannelResource {

  @Context
  private UriInfo uriInfo;

  protected Channel checkChannelExistence()
      throws WebApplicationException {
    Channel channel = getChannel();
    if (channel == null) {
      throw new WebApplicationException(Response.status(
          Response.Status.NOT_FOUND).build());
    }
    return channel;
  }

  protected Channel getChannel() {
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    Channel channel = storer.getChannel(getChannelName());
    return channel;
  }

  protected void checkAuthToken() {
    checkAuthToken(getChannel());
  }

  protected void checkAuthToken(Channel myChannel) {
    if (myChannel == null || !StringUtils.equals(getAuthToken(), myChannel.getAuthToken())) {
      throw new WebApplicationException(Response.Status.FORBIDDEN);
    }
  }

  protected UriBuilder setBaseUri(final UriBuilder builder) throws IllegalArgumentException {
    final URI baseUri = uriInfo.getBaseUri();
    builder.host(baseUri.getHost());
    builder.port(baseUri.getPort());
    builder.scheme(baseUri.getScheme());
    return builder;
  }

  protected abstract String getChannelName();

  protected abstract String getAuthToken();
}
