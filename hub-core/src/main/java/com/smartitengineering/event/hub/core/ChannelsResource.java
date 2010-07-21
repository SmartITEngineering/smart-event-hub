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

import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.api.impl.APIFactory;
import com.smartitengineering.event.hub.api.impl.APIFactory.ChannelBuilder;
import com.smartitengineering.event.hub.common.ChannelJsonProvider;
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import com.sun.jersey.api.view.Viewable;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
@Path("/channels")
public class ChannelsResource {

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getHtml() {
    final ResponseBuilder builder = Response.ok();
    Collection<Channel> channels = HubPersistentStorerSPI.getInstance().getStorer().getChannels(0, 10);
    Viewable viewable = new Viewable("channels", channels, ChannelsResource.class);
    builder.entity(viewable);
    return builder.build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_HTML)
  public Response createChannel(MultivaluedMap<String, String> formInputs) {
    final ResponseBuilder builder;
    String name = formInputs.getFirst(ChannelJsonProvider.NAME);
    if (StringUtils.isNotBlank(name) && HubPersistentStorerSPI.getInstance().getStorer().getChannel(name) == null) {
      String description = formInputs.getFirst(ChannelJsonProvider.DESCRIPTION);
      String authToken = formInputs.getFirst(ChannelJsonProvider.AUTH_TOKEN);
      ChannelBuilder channelBuilder = APIFactory.getChannelBuilder(name);
      if (StringUtils.isNotBlank(description)) {
        channelBuilder.description(description);
      }
      if (StringUtils.isNotBlank(authToken)) {
        channelBuilder.authToken(authToken);
      }
      Channel newChannel = channelBuilder.build();
      boolean error = false;
      String errorMessage = "";
      try {
        HubPersistentStorerSPI.getInstance().getStorer().create(newChannel);
      }
      catch(Exception ex) {
        error = true;
        errorMessage = ex.getMessage();
      }
      if(error) {
        builder = Response.status(Response.Status.BAD_REQUEST);
        builder.entity(errorMessage);
      }
      else {
        Collection<Channel> channels = HubPersistentStorerSPI.getInstance().getStorer().getChannels(0, 10);
        Viewable viewable = new Viewable("channels", channels, ChannelsResource.class);
        builder = Response.ok(viewable);
      }
    }
    else {
      builder = Response.status(Response.Status.BAD_REQUEST);
    }
    return builder.build();
  }
}
