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
