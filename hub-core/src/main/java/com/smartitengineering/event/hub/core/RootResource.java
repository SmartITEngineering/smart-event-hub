/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
  private String eventName="";

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
