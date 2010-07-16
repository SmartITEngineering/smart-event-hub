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
package com.smartitengineering.event.hub.spi.db;

import com.smartitengineering.event.hub.api.Event;
import com.smartitengineering.event.hub.api.impl.APIFactory;
import com.smartitengineering.util.bean.adapter.AbstractAdapterHelper;

/**
 *
 * @author imyousuf
 */
public class EventAdapterHelper
    extends AbstractAdapterHelper<Event, PersistentEvent> {

  @Override
  protected PersistentEvent newTInstance() {
    return new PersistentEvent();
  }

  @Override
  protected void mergeFromF2T(Event fromBean,
                              PersistentEvent toBean) {
    toBean.setPlaceholderId(fromBean.getPlaceholderId());
    toBean.setUuid(fromBean.getUniversallyUniqueID());
    if (fromBean.getEventContent() != null) {
      toBean.setContentType(fromBean.getEventContent().getContentType());
      toBean.setContentStream(fromBean.getEventContent().getContent());
    }
  }

  @Override
  protected Event convertFromT2F(PersistentEvent toBean) {
    return APIFactory.getEventBuilder().eventContent(APIFactory.getContent(toBean.
        getContentType(), toBean.getContentStream())).placeholder(toBean.
        getPlaceholderId()).uuid(toBean.getUuid()).creationDate(toBean.getCreationDateTime()).build();
  }
}
