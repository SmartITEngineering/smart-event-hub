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
import com.smartitengineering.event.hub.common.Constants;
import com.smartitengineering.event.hub.spi.HubPersistentStorer;
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import com.sun.jersey.api.view.Viewable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

/**
 *
 * @author imyousuf
 */
@Path("/" + Constants.RSRC_PATH_CHANNEL_PREFIX + "/{" + Constants.RSRC_PATH_CHANNEL + "}")
public class ChannelResource extends AbstractChannelResource {

  @PathParam(Constants.RSRC_PATH_CHANNEL)
  private String channelName;
  @HeaderParam(Constants.AUTH_TOKEN_HEADER_NAME)
  private String authToken;

  private Response setData(Channel channel) {
    Response response;
    try {
      if (!channelName.equals(channel.getName())) {
        throw new IllegalArgumentException("Names do not match!");
      }
      HubPersistentStorer storer = HubPersistentStorerSPI.getInstance().
          getStorer();
      Channel myChannel = getChannel();
      if (myChannel == null) {
        storer.create(channel);
        response = Response.created(getUriInfo().getAbsolutePath()).build();
      }
      else {
        checkAuthToken(myChannel);
        storer.update(channel);
        response = Response.noContent().location(getUriInfo().getAbsolutePath()).build();
      }
    }
    catch (Throwable th) {
      th.printStackTrace();
      throw new WebApplicationException(th, Response.Status.BAD_REQUEST);
    }
    return response;
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response put(Channel channel) {
    Response response;
    response = setData(channel);
    return response;
  }

  private Channel getAuthChannel() {
    Channel channel = getChannel();
    checkAuthToken(channel);
    return channel;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getChannelInfo() {
    Channel channel = getAuthChannel();
    if (channel != null) {
      return Response.ok(channel).build();
    }
    else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response getChannelAtom() {
    Channel channel = getAuthChannel();
    if (channel != null) {
      final Feed feed = getFeed(channel.getName(), channel.getName(), channel.getLastModifiedDate());
      feed.addLink(getLink(getUriInfo().getRequestUri(), Link.REL_ALTERNATE, MediaType.APPLICATION_JSON));
      String hubUri = getUriInfo().getRequestUriBuilder().path(Constants.RSRC_PATH_CHANNEL_HUB).build().toASCIIString();
      feed.addLink(getLink(hubUri, Constants.RSRC_PATH_CHANNEL_HUB, MediaType.WILDCARD));
      String eventsUri = getUriInfo().getRequestUriBuilder().path(Constants.RSRC_PATH_CHANNEL_EVENTS).build().
          toASCIIString();
      feed.addLink(getLink(eventsUri, Constants.RSRC_PATH_CHANNEL_EVENTS, MediaType.APPLICATION_ATOM_XML));
      return Response.ok(feed).build();
    }
    else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getChannelInfoInHTML() {
    Viewable viewable;
    Channel channel = getAuthChannel();
    ResponseBuilder builder = Response.ok();
    if (channel != null) {
      viewable = new Viewable("channel", channel);
      builder.entity(viewable);
      return builder.build();
    }
    else {
      viewable = new Viewable("channel", "Not Found", ChannelResource.class);
      return Response.status(Response.Status.NOT_FOUND).build();
    }


  }

  @DELETE
  public Response delete() {
    Channel channel = checkChannelExistence();
    checkAuthToken(channel);
    HubPersistentStorerSPI.getInstance().getStorer().delete(channel);
    return Response.ok().build();
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
