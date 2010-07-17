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
import com.smartitengineering.event.hub.spi.HubPersistentStorer;
import com.smartitengineering.event.hub.spi.HubPersistentStorerSPI;
import java.net.URI;
import java.util.Date;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Feed;
import org.apache.commons.lang.StringUtils;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;

/**
 *
 * @author imyousuf
 */
public abstract class AbstractEventResource {

  @Context
  private UriInfo uriInfo;
  protected final Factory abderaFactory=Abdera.getNewFactory();

  protected Event checkEventExistence()
      throws WebApplicationException {
    Event Event = getEvent();
    if (Event == null) {
      throw new WebApplicationException(Response.status(
          Response.Status.NOT_FOUND).build());
    }
    return Event;
  }

  protected Event getEvent() {
    final HubPersistentStorer storer =
                              HubPersistentStorerSPI.getInstance().getStorer();
    Event Event = storer.getEvent(getEventName());
    return Event;
  }

  protected UriBuilder setBaseUri(final UriBuilder builder) throws IllegalArgumentException {
    final URI baseUri = uriInfo.getBaseUri();
    builder.host(baseUri.getHost());
    builder.port(baseUri.getPort());
    builder.scheme(baseUri.getScheme());
    return builder;
  }
  protected Feed getFeed(String title, Date updated) {
   return getFeed(uriInfo.getRequestUri().toString(), title, updated);
  }
  protected Feed getFeed(String id, String title, Date updated) {
    Feed feed = getFeed();
    feed.setId(id);
    feed.setTitle(title);
    feed.setUpdated(updated);
    return feed;
  }
  protected Feed getFeed() {
    Feed feed = abderaFactory.newFeed();
    feed.addLink(getSelfLink());
     feed.addAuthor("author");     ///error in adding getDefaultAuthor();
    return feed;
  }
  protected Link getSelfLink() {
    Link selfLink = abderaFactory.newLink();
    selfLink.setHref(uriInfo.getRequestUri().toString());
    selfLink.setRel(Link.REL_SELF);
    return selfLink;
  }
 protected abstract String getEventName();
}
