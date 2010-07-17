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
    Feed atomFeed = getFeed("ROA Demo", INIT_DATE);
    Link booksLink = Abdera.getNewFactory().newLink();
    booksLink.setHref(UriBuilder.fromResource(ChannelEventsResource.class).build().toString());
    booksLink.setRel("Events");
    atomFeed.addLink(booksLink);
    Link authorsLink = Abdera.getNewFactory().newLink();
    authorsLink.setHref(UriBuilder.fromResource(ChannelHubResource.class).build().toString());
    authorsLink.setRel("authors");
    atomFeed.addLink(authorsLink);
    responseBuilder.entity(atomFeed);
    return responseBuilder.build();
  }
  @Override
  protected String getEventName() {
    return eventName;
  }
}
