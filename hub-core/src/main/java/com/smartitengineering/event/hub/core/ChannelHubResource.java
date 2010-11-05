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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Cluster;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;
import org.atmosphere.plugin.cluster.jgroups.JGroupsFilter;

/**
 *
 * @author imyousuf
 */
@Path("/channels/{" + Constants.RSRC_PATH_CHANNEL + "}/" + Constants.RSRC_PATH_CHANNEL_HUB)
public class ChannelHubResource extends AbstractChannelResource {

  @PathParam(Constants.RSRC_PATH_CHANNEL)
  private Broadcaster broadcaster;
  @PathParam(Constants.RSRC_PATH_CHANNEL)
  private String channelName;
  @HeaderParam(Constants.AUTH_TOKEN_HEADER_NAME)
  private String authToken;
  @Context
  private Request request;

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
  @Cluster(name = "EventHub", value=JGroupsFilter.class)
  public Response broadcast(@HeaderParam("Content-type") String contentType, String message) {
    checkAuthToken();
    checkChannelExistence();
    final String eventContentType;
    //HTTP Request entity body can not be blank
    if (StringUtils.isBlank(message)) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    final boolean isHtmlPost;
    if (StringUtils.isBlank(contentType)) {
      eventContentType = MediaType.APPLICATION_OCTET_STREAM;
      isHtmlPost = false;
    }
    else if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
      eventContentType = MediaType.APPLICATION_OCTET_STREAM;
      isHtmlPost = true;
      try {
        //Will search for the first '=' if not found will take the whole string
        final int startIndex = message.indexOf("=") + 1;
        //Consider the first '=' as the start of a value point and take rest as value
        final String realMsg = message.substring(startIndex);
        //Decode the message to ignore the form encodings and make them human readable
        message = URLDecoder.decode(realMsg, "UTF-8");
      }
      catch (UnsupportedEncodingException ex) {
        ex.printStackTrace();
      }
    }
    else {
      eventContentType = contentType;
      isHtmlPost = false;
    }
    Event event = APIFactory.getEventBuilder().eventContent(APIFactory.getContent(eventContentType, IOUtils.
        toInputStream(message))).build();
    final Channel channel = HubPersistentStorerSPI.getInstance().getStorer().getChannel(channelName);
    event = HubPersistentStorerSPI.getInstance().getStorer().create(channel, event);
    //Add a new line at the end of the message to ensure that the message is flushed to its listeners
    message = message + "\n";
    Broadcastable broadcastable = new Broadcastable(message, broadcaster);
    ResponseBuilder builder = Response.ok(broadcastable);
    builder.location(setBaseUri(EventResource.EVENT_URI_BUILDER.clone()).build(event.getPlaceholderId()));
    if (isHtmlPost) {
      builder.status(Response.Status.SEE_OTHER);
      builder.location(setBaseUri(ChannelEventsResource.EVENTS_URI_BUILDER.clone()).build(channelName));
    }
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
