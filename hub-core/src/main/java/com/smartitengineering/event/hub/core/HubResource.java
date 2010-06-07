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
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
  public Response create(Channel channel) {
    Response response;
    try {
      HubPersistentStorerSPI.getInstance().getStorer().create(channel);
      response = Response.created(UriBuilder.fromResource(HubResource.class).
          build(channelName)).build();
    }
    catch (Throwable th) {
      response = Response.status(Response.Status.BAD_REQUEST).entity(th).build();
    }
    return response;
  }

  @GET
  @Suspend(outputComments = false)
  @Produces
  public Broadcastable register() {
    return new Broadcastable(broadcaster);
  }

  @Broadcast
  @POST
  @Consumes
  public Broadcastable broadcast(String message) {
    return new Broadcastable(message, broadcaster);
  }
}
