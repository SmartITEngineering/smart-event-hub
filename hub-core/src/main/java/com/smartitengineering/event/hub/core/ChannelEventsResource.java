/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.event.hub.core;

import com.smartitengineering.event.hub.api.Event;
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import java.awt.print.Book;
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
@Path ("/events")
public class ChannelEventsResource  extends AbstractEventResource{
    static final UriBuilder EVENTS_URI_BUILDER;
    static final UriBuilder EVENTS_AFTER_BUILDER;
    static final UriBuilder EVENTS_BEFORE_BUILDER;
    private final Map<Event, String> contentCache = new WeakHashMap<Event, String>();
    static {
        EVENTS_URI_BUILDER = UriBuilder.fromResource(ChannelEventsResource.class);
        EVENTS_BEFORE_BUILDER = UriBuilder.fromResource(ChannelEventsResource.class);
        try {
            EVENTS_BEFORE_BUILDER.path(ChannelEventsResource.class.getMethod("getBefore", String.class));
        }
        catch (Exception ex) {
            throw new InstantiationError();
        }
        EVENTS_AFTER_BUILDER = UriBuilder.fromResource(ChannelEventsResource.class);
        try {
            EVENTS_AFTER_BUILDER.path(ChannelEventsResource.class.getMethod("getAfter", String.class));
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
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Path("/after/{eventPlaceholderId}")
  public Response getAfter(@PathParam("eventPlaceholderId") String afterEvent) {
    return get(afterEvent, false);
  }
  
  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    return get("1", true);
  }



  public Response get(String placeholderId, boolean isBefore) {
    if (count == null) {
      count = 10;
    }
    ResponseBuilder responseBuilder = Response.ok();
    Feed atomFeed = getFeed("Events", new Date());

    Link eventsLink = abderaFactory.newLink();
    eventsLink.setHref(UriBuilder.fromResource(RootResource.class).build().toString());
    eventsLink.setRel("root");

    atomFeed.addLink(eventsLink);

    Collection<Event> events= HubPersistentStorerSPI.getInstance().getStorer().getEvents(placeholderId, null, count);

    if(events !=null && !events.isEmpty())
    {
        MultivaluedMap<String,String> queryParams= uriInfo.getQueryParameters();

        List<Event> eventList=new ArrayList<Event>(events);
        Link nextLink=abderaFactory.newLink();
        nextLink.setRel(Link.REL_PREVIOUS);
        Event lastEvent=eventList.get(0);
        final UriBuilder nextUri=EVENTS_AFTER_BUILDER.clone();
        final UriBuilder previousUri=EVENTS_BEFORE_BUILDER.clone();

        for(String key : queryParams.keySet())
        {
            final Object[] values=queryParams.get(key).toArray();
            nextUri.queryParam(key, values);
            previousUri.queryParam(key, values);
        }

        nextLink.setHref(nextUri.build(lastEvent.getPlaceholderId()).toString());
        atomFeed.addLink(nextLink);

        Link previousLink= abderaFactory.newLink();
        previousLink.setRel(Link.REL_NEXT);
        Event firstEvent=eventList.get(events.size()-1);
        previousLink.setHref(previousUri.build(firstEvent.getPlaceholderId()).toString());
        atomFeed.addLink(previousLink);

        for(Event event :events)
        {
            Entry eventEntry = abderaFactory.newEntry();

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

            Link eventLink=abderaFactory.newLink();

            eventLink.setHref(EventResource.EVENT_URI_BUILDER.clone().build(event.getUniversallyUniqueID()).toString());
            eventLink.setRel(Link.REL_ALTERNATE);
            eventLink.setMimeType(MediaType.APPLICATION_ATOM_XML);

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
