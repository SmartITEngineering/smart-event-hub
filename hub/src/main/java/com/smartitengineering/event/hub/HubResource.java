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
package com.smartitengineering.event.hub;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Schedule;
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

  @GET
  @Suspend(outputComments = false)
  @Produces
  public Broadcastable register() {
    return new Broadcastable(broadcaster);
  }

  @Schedule(period=10)
  @POST
  @Path("/ping")
  public Response ping() {
    return Response.noContent().build();
  }

  @Broadcast
  @POST
  @Consumes
  public Broadcastable broadcast(String message) {
    return new Broadcastable(message, broadcaster);
  }
}
