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

import com.sun.jersey.api.view.Viewable;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
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
  @Context
  private HttpServletRequest servletRequest;

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    ResponseBuilder responseBuilder = Response.ok();
    Feed atomFeed = getFeed("Event Hub", INIT_DATE);
    Link eventsLink = Abdera.getNewFactory().newLink();
    eventsLink.setHref(getRelativeURIBuilder().path(AllEventsResource.class).build().toString());
    eventsLink.setRel("events");
    atomFeed.addLink(eventsLink);
    Link channelsLink = Abdera.getNewFactory().newLink();
    channelsLink.setHref(getRelativeURIBuilder().path(ChannelsResource.class).build().toString());
    channelsLink.setRel("channels");
    atomFeed.addLink(channelsLink);
    responseBuilder.entity(atomFeed);
    return responseBuilder.build();
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getInHTML() {
    ResponseBuilder responseBuilder = Response.ok();
    String eventsLink = getRelativeURIBuilder().path(AllEventsResource.class).build().toString();
    String channelLink = getRelativeURIBuilder().path(ChannelsResource.class).build().toString();
    servletRequest.setAttribute("channelLink", channelLink);
    Viewable viewable = new Viewable("root", eventsLink, RootResource.class);
    responseBuilder.entity(viewable);
    return responseBuilder.build();
  }

  @Override
  protected String getEventName() {
    return eventName;
  }
}
