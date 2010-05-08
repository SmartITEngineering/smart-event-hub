package com.smartitengineering.event.hub;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.Broadcaster;

/**
 *
 * @author imyousuf
 */
@Path("/")
public class HubResource {

  @Context private Broadcaster broadcaster;

  @GET
  @Suspend(outputComments=false)
  public Response register() {
    return Response.noContent().build();
  }

  @Broadcast
  @POST
  public Response broadcast() {
    return Response.ok("Hello!", MediaType.TEXT_PLAIN).build();
  }

}
