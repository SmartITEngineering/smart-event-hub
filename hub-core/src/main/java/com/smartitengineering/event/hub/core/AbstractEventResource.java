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
import com.smartitengineering.util.rest.atom.server.AbstractResource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author imyousuf
 */
public abstract class AbstractEventResource extends AbstractResource {

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
    final HubPersistentStorer storer = HubPersistentStorerSPI.getInstance().getStorer();
    Event Event = storer.getEvent(getEventName());
    return Event;
  }

  @Override
  protected String getAuthor() {
    return "info@smartitengineering.com";
  }

  protected abstract String getEventName();
}
