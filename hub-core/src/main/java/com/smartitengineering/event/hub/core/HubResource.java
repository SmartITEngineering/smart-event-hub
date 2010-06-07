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
import com.smartitengineering.event.hub.api.impl.APIFactory;
import com.smartitengineering.event.hub.spi.HubPersistentStorer;
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;

/**
 *
 * @author imyousuf
 */
@Path("/{channel}")
public class HubResource {

  @PathParam("channel")
  private Broadcaster broadcaster;
  @PathParam("channel")
  private String channelName;

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response put(Channel channel) {
    Response response;
    try {
      if (!channelName.equals(channel.getName())) {
        throw new IllegalArgumentException("Names do not match!");
      }
      HubPersistentStorer storer = HubPersistentStorerSPI.getInstance().
          getStorer();
      Channel myChannel = storer.getChannel(channelName);
      if (myChannel == null) {
        storer.create(channel);
      }
      else {
        storer.update(APIFactory.getChannelBuilder(channel).creationDateTime(
            myChannel.getCreationDateTime()).build());
      }
      response = Response.created(UriBuilder.fromResource(HubResource.class).
          build(channelName)).build();
    }
    catch (Throwable th) {
      throw new WebApplicationException(th, Response.Status.BAD_REQUEST);
    }
    return response;
  }

  @GET
  @Suspend(outputComments = false)
  @Produces
  public Broadcastable register() {
    checkChannelExistence();
    return new Broadcastable(broadcaster);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("info")
  public Response getChannelInfo() {
    Channel channel = HubPersistentStorerSPI.getInstance().getStorer().
        getChannel(channelName);
    if (channel != null) {
      return Response.ok(channel).build();
    }
    else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @Broadcast
  @POST
  @Consumes
  public Broadcastable broadcast(String message) {
    checkChannelExistence();
    return new Broadcastable(message, broadcaster);
  }

  @DELETE
  public Response delete() {
    Channel channel = checkChannelExistence();
    HubPersistentStorerSPI.getInstance().getStorer().delete(channel);
    return Response.ok().build();
  }

  protected Channel checkChannelExistence()
      throws WebApplicationException {
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    Channel channel = storer.getChannel(channelName);
    if (channel == null) {
      throw new WebApplicationException(Response.status(
          Response.Status.NOT_FOUND).build());
    }
    return channel;
  }
}
