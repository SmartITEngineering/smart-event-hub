package com.smartitengineering.event.hub;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.atmosphere.annotation.Broadcast;
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
  public Broadcastable register() {
    return new Broadcastable(broadcaster);
  }

  @Broadcast(resumeOnBroadcast=true)
  @POST
  public Broadcastable broadcast() {
    return new Broadcastable("Hello!", broadcaster);
  }
}
