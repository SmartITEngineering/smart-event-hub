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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author imyousuf
 */
@Path("/all-channels")
public class ChannelsResource extends AbstractChannelResource {

  static final UriBuilder CHANNELS_URI_BUILDER;
  static final UriBuilder CHANNELS_AFTER_BUILDER;
  static final UriBuilder CHANNELS_BEFORE_BUILDER;

  static {
    CHANNELS_URI_BUILDER = UriBuilder.fromResource(ChannelsResource.class);
    CHANNELS_BEFORE_BUILDER = UriBuilder.fromResource(ChannelsResource.class);
    try {
      CHANNELS_BEFORE_BUILDER.path(ChannelsResource.class.getMethod("getBefore", String.class));
    }
    catch (Exception ex) {
      throw new InstantiationError();
    }
    CHANNELS_AFTER_BUILDER = UriBuilder.fromResource(ChannelsResource.class);
    try {
      CHANNELS_AFTER_BUILDER.path(ChannelsResource.class.getMethod("getAfter", String.class));
    }
    catch (Exception ex) {
      throw new InstantiationError();
    }
  }

  private Integer position;
  @QueryParam("count")
  @DefaultValue("10")
  private Integer count;
  private String channelName;
  private String authToken;

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Path("/before/{channelPosition}")
  public Response getBefore(@PathParam("channelPosition") String beforeChannel) {
    this.position = NumberUtils.toInt(beforeChannel);
    return get(position, true);
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("/before/{channelPosition}")
  public Response getBeforeHtml(@PathParam("channelPosition") String beforeChannel) {
    this.position = NumberUtils.toInt(beforeChannel);
    return getInHtml(position, true);
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Path("/after/{channelPosition}")
  public Response getAfter(@PathParam("channelPosition") String afterChannel) {
    this.position = NumberUtils.toInt(afterChannel);
    return get(position, false);
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("/after/{channelPosition}")
  public Response getAfterHtml(@PathParam("channelPosition") String afterChannel) {
    this.position = NumberUtils.toInt(afterChannel);
    return getInHtml(position, false);
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    return get(Integer.MAX_VALUE, true);
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getHtml() {
    return getInHtml(Integer.MAX_VALUE, true);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_HTML)
  public Response createChannel(MultivaluedMap<String, String> formInputs) {
    final ResponseBuilder builder;
    String name = formInputs.getFirst(ChannelJsonProvider.NAME);
    if (StringUtils.isNotBlank(name) && HubPersistentStorerSPI.getInstance().getStorer().getChannel(name) == null) {
      String description = formInputs.getFirst(ChannelJsonProvider.DESCRIPTION);
      this.authToken = formInputs.getFirst(ChannelJsonProvider.AUTH_TOKEN);
      ChannelBuilder channelBuilder = APIFactory.getChannelBuilder(name);
      if (StringUtils.isNotBlank(description)) {
        channelBuilder.description(description);
      }
      if (StringUtils.isNotBlank(this.authToken)) {
        channelBuilder.authToken(this.authToken);
      }
      Channel newChannel = channelBuilder.build();
      boolean error = false;
      String errorMessage = "";
      try {
        HubPersistentStorerSPI.getInstance().getStorer().create(newChannel);
      }
      catch (Exception ex) {
        error = true;
        errorMessage = ex.getMessage();
      }
      if (error) {
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

  public Response getInHtml(int startIndex, boolean isBefore) {
    if (count == null) {
      count = 10;
    }
    Integer realCount = count;
    if (isBefore) {
      realCount = count * -1;
    }
    final ResponseBuilder builder = Response.ok();
    Collection<Channel> channels = HubPersistentStorerSPI.getInstance().getStorer().getChannels(startIndex, realCount);
    Viewable viewable = new Viewable("channels", channels, ChannelsResource.class);
    builder.entity(viewable);
    return builder.build();

  }

  public Response get(int startIndex, boolean isBefore) {
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

    Collection<Channel> channels = HubPersistentStorerSPI.getInstance().getStorer().getChannels(startIndex, thisCount);

    if (channels != null && !channels.isEmpty()) {
      MultivaluedMap<String, String> queryParams = getUriInfo().getQueryParameters();

      List<Channel> channelList = new ArrayList<Channel>(channels);
      Link nextLink = getAbderaFactory().newLink();
      nextLink.setRel(Link.REL_PREVIOUS);
      Channel lastChannel = channelList.get(0);
      final UriBuilder nextUri = CHANNELS_AFTER_BUILDER.clone();
      final UriBuilder previousUri = CHANNELS_BEFORE_BUILDER.clone();

      for (String key : queryParams.keySet()) {
        final Object[] values = queryParams.get(key).toArray();
        nextUri.queryParam(key, values);
        previousUri.queryParam(key, values);
      }

      nextLink.setHref(nextUri.build(lastChannel.getPosition()).toString());
      atomFeed.addLink(nextLink);

      Link previousLink = getAbderaFactory().newLink();
      previousLink.setRel(Link.REL_NEXT);
      Channel firstChannel = channelList.get(channels.size() - 1);
      previousLink.setHref(previousUri.build(firstChannel.getPosition()).toString());
      atomFeed.addLink(previousLink);

      for (Channel channel : channels) {
        Entry channelEntry = getAbderaFactory().newEntry();

        channelEntry.setId(channel.getName());
        channelEntry.setTitle(channel.getName().toString());

        String content = channel.getDescription();
        channelEntry.setContent(content);
        channelEntry.setUpdated(channel.getCreationDateTime());
        Link channelLink = getAbderaFactory().newLink();

        channelLink.setHref(ChannelsResource.CHANNELS_URI_BUILDER.clone().build(channel.getPosition()).toString());
        channelLink.setRel(Link.REL_ALTERNATE);
        channelLink.setMimeType(MediaType.APPLICATION_JSON);

        channelEntry.addLink(channelLink);
        atomFeed.addEntry(channelEntry);
      }
    }

    responseBuilder.entity(atomFeed);
    return responseBuilder.build();
  }

  @Override
  protected String getChannelName() {
    return channelName;


  }

  @Override
  protected String getAuthToken() {
    return authToken;

  }
}
