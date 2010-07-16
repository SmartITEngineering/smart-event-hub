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
import com.smartitengineering.event.hub.api.Event;
import com.smartitengineering.event.hub.api.impl.APIFactory;
import com.smartitengineering.event.hub.common.Constants;
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.io.IOUtils;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;

/**
 *
 * @author imyousuf
 */
@Path("/{" + Constants.RSRC_PATH_CHANNEL + "}/" + Constants.RSRC_PATH_CHANNEL_HUB)
public class ChannelHubResource extends AbstractChannelResource {

  @PathParam(Constants.RSRC_PATH_CHANNEL)
  private Broadcaster broadcaster;
  @PathParam(Constants.RSRC_PATH_CHANNEL)
  private String channelName;
  @HeaderParam(Constants.AUTH_TOKEN_HEADER_NAME)
  private String authToken;

  @GET
  @Suspend(outputComments = false)
  @Produces
  public Broadcastable register() {
    checkChannelExistence();
    checkAuthToken();
    return new Broadcastable(broadcaster);
  }

  @Broadcast
  @POST
  @Consumes
  public Response broadcast(String message) {
    checkAuthToken();
    checkChannelExistence();
    Event event = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(MediaType.APPLICATION_OCTET_STREAM, IOUtils.
        toInputStream(message))).build();
    final Channel channel = HubPersistentStorerSPI.getInstance().getStorer().getChannel(channelName);
    event = HubPersistentStorerSPI.getInstance().getStorer().create(channel, event);
    Broadcastable broadcastable = new Broadcastable(message, broadcaster);
    ResponseBuilder builder = Response.ok(broadcastable);
    builder.location(setBaseUri(EventResource.EVENT_URI_BUILDER.clone()).build(event.getPlaceholderId()));
    return builder.build();
  }

  @Override
  protected String getChannelName() {
    return channelName;
  }

  @Override
  protected String getAuthToken() {
    return authToken;
  }
}
