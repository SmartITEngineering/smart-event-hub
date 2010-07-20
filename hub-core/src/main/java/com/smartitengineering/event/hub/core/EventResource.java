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

import com.smartitengineering.event.hub.api.Event;
import com.smartitengineering.event.hub.core.EventResource;
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author imyousuf
 */
@Path("/event/{eventPlaceholderId}")
public class EventResource {

  static final UriBuilder EVENT_URI_BUILDER = UriBuilder.fromResource(EventResource.class);

  @Context
  private UriInfo uriInfo;
  private final Event event;

  public EventResource(@PathParam("eventPlaceholderId") String placeholderId) {
    event = HubPersistentStorerSPI.getInstance().getStorer().getEvent(placeholderId);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response get() {
    if (event != null) {
      return Response.ok(event).build();
    }
    else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @DELETE
  public Response delete() {
    try {
      HubPersistentStorerSPI.getInstance().getStorer().delete(event);
      return Response.ok().build();
    }
    catch (Exception ex) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    }
  }
}
