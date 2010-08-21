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
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import com.sun.jersey.api.view.Viewable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author kaisar
 */
@Path("/all-events")
public class AllEventsResource extends AbstractEventResource {

  static final UriBuilder EVENTS_URI_BUILDER;
  static final UriBuilder EVENTS_AFTER_BUILDER;
  static final UriBuilder EVENTS_BEFORE_BUILDER;
  private final Map<Event, String> contentCache = new WeakHashMap<Event, String>();

  static {
    EVENTS_URI_BUILDER = UriBuilder.fromResource(AllEventsResource.class);
    EVENTS_BEFORE_BUILDER = UriBuilder.fromResource(AllEventsResource.class);
    try {
      EVENTS_BEFORE_BUILDER.path(AllEventsResource.class.getMethod("getBefore", String.class));
    }
    catch (Exception ex) {
      throw new InstantiationError();
    }
    EVENTS_AFTER_BUILDER = UriBuilder.fromResource(AllEventsResource.class);
    try {
      EVENTS_AFTER_BUILDER.path(AllEventsResource.class.getMethod("getAfter", String.class));
    }
    catch (Exception ex) {
      throw new InstantiationError();
    }
  }
  private String placeholderId;
  @QueryParam("count")
  @DefaultValue("10")
  private Integer count;

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Path("/before/{eventPlaceholderId}")
  public Response getBefore(@PathParam("eventPlaceholderId") String beforeEvent) {
    return get(beforeEvent, true);
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("/before/{eventPlaceholderId}")
  public Response getBeforeHTML(@PathParam("eventPlaceholderId") String beforeEvent) {
    return getInHTML(beforeEvent, true);
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Path("/after/{eventPlaceholderId}")
  public Response getAfter(@PathParam("eventPlaceholderId") String afterEvent) {
    return get(afterEvent, false);
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("/after/{eventPlaceholderId}")
  public Response getAfterHTML(@PathParam("eventPlaceholderId") String afterEvent) {
    return getInHTML(afterEvent, false);
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    return get("-1", true);
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getHTML() {
    return getInHTML("-1", false);
  }
   public Response getInHTML(String placeholderId, boolean isBefore) {
    if (count == null) {
      count = 10;
    }
    int thisCount=count;
    if(isBefore)
    {
      thisCount=count*-1;
    }
    ResponseBuilder responseBuilder = Response.ok();
    Collection<Event> events = HubPersistentStorerSPI.getInstance().getStorer().getEvents(placeholderId, null,
                                                                                          thisCount);
    Viewable viewable = new Viewable("allevents", events, AllEventsResource.class);
    responseBuilder.entity(viewable);
    return responseBuilder.build();
  }

  public Response get(String placeholderId, boolean isBefore) {
    if (count == null) {
      count = 10;
    }
    int thisCount = count;
    if (isBefore) {
      thisCount = count * -1;
    }
    ResponseBuilder responseBuilder = Response.ok();
    Feed atomFeed = getFeed("Events", new Date());

    Link eventsLink = getAbderaFactory().newLink();
    eventsLink.setHref(UriBuilder.fromResource(RootResource.class).build().toString());
    eventsLink.setRel("root");

    atomFeed.addLink(eventsLink);

    Collection<Event> events = HubPersistentStorerSPI.getInstance().getStorer().getEvents(placeholderId, null, thisCount);

    if (events != null && !events.isEmpty()) {
      MultivaluedMap<String, String> queryParams = getUriInfo().getQueryParameters();

      List<Event> eventList = new ArrayList<Event>(events);
      Link nextLink = getAbderaFactory().newLink();
      nextLink.setRel(Link.REL_PREVIOUS);
      Event lastEvent = eventList.get(0);
      final UriBuilder nextUri = EVENTS_AFTER_BUILDER.clone();
      final UriBuilder previousUri = EVENTS_BEFORE_BUILDER.clone();

      for (String key : queryParams.keySet()) {
        final Object[] values = queryParams.get(key).toArray();
        nextUri.queryParam(key, values);
        previousUri.queryParam(key, values);
      }

      nextLink.setHref(nextUri.build(lastEvent.getPlaceholderId()).toString());
      atomFeed.addLink(nextLink);

      Link previousLink = getAbderaFactory().newLink();
      previousLink.setRel(Link.REL_NEXT);
      Event firstEvent = eventList.get(events.size() - 1);
      previousLink.setHref(previousUri.build(firstEvent.getPlaceholderId()).toString());
      atomFeed.addLink(previousLink);

      for (Event event : events) {
        Entry eventEntry = getAbderaFactory().newEntry();

        eventEntry.setId(event.getPlaceholderId());
        eventEntry.setTitle(event.getPlaceholderId().toString());

        InputStream contentStream = event.getEventContent().getContent();
        String contentAsString = "";

        if (contentStream != null) {
          if (contentCache.containsKey(event)) {
            contentAsString = contentCache.get(event);
          }
          else {
            try {
              if (contentStream.markSupported()) {
                contentStream.mark(Integer.MAX_VALUE);
              }
              contentAsString = IOUtils.toString(contentStream);
              contentCache.put(event, contentAsString);
              if (contentStream.markSupported()) {
                contentStream.reset();
              }
            }
            catch (IOException ex) {
            }
          }
        }


        eventEntry.setContent(contentAsString);
        eventEntry.setUpdated(event.getCreationDate());

        Link eventLink = getAbderaFactory().newLink();

        eventLink.setHref(EventResource.EVENT_URI_BUILDER.clone().build(event.getPlaceholderId()).toString());
        eventLink.setRel(Link.REL_ALTERNATE);
        eventLink.setMimeType(MediaType.APPLICATION_JSON);

        eventEntry.addLink(eventLink);
        atomFeed.addEntry(eventEntry);
      }
    }
    responseBuilder.entity(atomFeed);
    return responseBuilder.build();
  }

  @Override
  protected String getEventName() {
    return placeholderId;
  }
}
