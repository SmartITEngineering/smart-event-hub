/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.event.hub.core;

import java.awt.print.Book;
import java.util.Collection;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

/**
 *
 * @author kaisar
 */
@Path ("/events")
public class ChannelEventsResource  extends AbstractEventResource{
    static final UriBuilder EVENTS_URI_BUILDER;
  static final UriBuilder EVENTS_AFTER_ISBN_BUILDER;
  static final UriBuilder EVENTS_BEFORE_ISBN_BUILDER;
  static {
    EVENTS_URI_BUILDER = UriBuilder.fromResource(ChannelEventsResource.class);
    EVENTS_BEFORE_ISBN_BUILDER = UriBuilder.fromResource(ChannelEventsResource.class);
    try {
      EVENTS_BEFORE_ISBN_BUILDER.path(ChannelEventsResource.class.getMethod("getBefore", String.class));
    }
    catch (Exception ex) {
      throw new InstantiationError();
    }
    EVENTS_AFTER_ISBN_BUILDER = UriBuilder.fromResource(ChannelEventsResource.class);
    try {
      EVENTS_AFTER_ISBN_BUILDER.path(ChannelEventsResource.class.getMethod("getAfter", String.class));
    }
    catch (Exception ex) {
      throw new InstantiationError();
    }
  }
   @QueryParam("id")
  private String placeholderId;
  @QueryParam("universally_unique_id")
  private String universallyUniqueID;
  @QueryParam("count")
  private Integer count;

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Path("/before/{event_no}")
  public Response getBefore(@PathParam("event_no") String beforeEventNo) {
    return get(beforeEventNo, true);
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Path("/after/{event_no}")
  public Response getAfter(@PathParam("event_no") String afterEventNo) {
    return get(afterEventNo, false);
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    return get(null, true);
  }




  public Response get(String eventNo, boolean isBefore) {
    if (count == null) {
      count = 10;
    }
    ResponseBuilder responseBuilder = Response.ok();
    Feed atomFeed = getFeed("Books", new Date());
    Link eventsLink = abderaFactory.newLink();
    eventsLink.setHref(UriBuilder.fromResource(RootResource.class).build().toString());
    eventsLink.setRel("root");
    atomFeed.addLink(eventsLink);


    return responseBuilder.build();
  }
  @Override
  protected String getEventName() {
    return placeholderId;
  }


}
