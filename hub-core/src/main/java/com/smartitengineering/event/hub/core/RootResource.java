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

import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

/**
 *
 * @author Kaisar
 */
@Path("/")
public class RootResource extends AbstractEventResource {

  private static final Date INIT_DATE = new Date();
  private String eventName = "";

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    ResponseBuilder responseBuilder = Response.ok();
    Feed atomFeed = getFeed("Event Hub", INIT_DATE);
    Link eventsLink = Abdera.getNewFactory().newLink();
    eventsLink.setHref(UriBuilder.fromResource(AllEventsResource.class).build().toString());
    eventsLink.setRel("Events");
    atomFeed.addLink(eventsLink);
    Link authorsLink = Abdera.getNewFactory().newLink();
    authorsLink.setHref(UriBuilder.fromResource(ChannelResource.class).build().toString());
    authorsLink.setRel("info@smartitengineering.com");
    atomFeed.addLink(authorsLink);
    responseBuilder.entity(atomFeed);
    return responseBuilder.build();
  }

  @Override
  protected String getEventName() {
    return eventName;
  }
}
