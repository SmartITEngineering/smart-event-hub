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

import com.smartitengineering.dao.common.CommonReadDao;
import com.smartitengineering.dao.common.CommonWriteDao;
import com.smartitengineering.dao.common.queryparam.MatchMode;
import com.smartitengineering.dao.common.queryparam.Order;
import com.smartitengineering.dao.common.queryparam.QueryParameter;
import com.smartitengineering.dao.common.queryparam.QueryParameterFactory;
import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.api.Event;
import com.smartitengineering.event.hub.spi.HubPersistentStorer;
import com.smartitengineering.util.bean.adapter.GenericAdapter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * A Smart DAO based {@link HubPersistentStorer} implementation.
 * @author imyousuf
 */
public class DBPersistentStorer
    implements HubPersistentStorer {

  private CommonReadDao<PersistentChannel> channelReadDao;
  private CommonReadDao<PersistentEvent> eventReadDao;
  private CommonWriteDao<PersistentChannel> channelWriteDao;
  private CommonWriteDao<PersistentEvent> eventWriteDao;
  private GenericAdapter<Channel, PersistentChannel> channelConverter;
  private GenericAdapter<Event, PersistentEvent> eventConverter;

  public void setChannelConverter(
      GenericAdapter<Channel, PersistentChannel> channelConverter) {
    this.channelConverter = channelConverter;
  }

  public void setChannelReadDao(CommonReadDao<PersistentChannel> channelReadDao) {
    this.channelReadDao = channelReadDao;
  }

  public void setChannelWriteDao(
      CommonWriteDao<PersistentChannel> channelWriteDao) {
    this.channelWriteDao = channelWriteDao;
  }

  public void setEventConverter(
      GenericAdapter<Event, PersistentEvent> eventConverter) {
    this.eventConverter = eventConverter;
  }

  public void setEventReadDao(CommonReadDao<PersistentEvent> eventReadDao) {
    this.eventReadDao = eventReadDao;
  }

  public void setEventWriteDao(CommonWriteDao<PersistentEvent> eventWriteDao) {
    this.eventWriteDao = eventWriteDao;
  }

  protected GenericAdapter<Channel, PersistentChannel> getChannelConverter() {
    return channelConverter;
  }

  protected CommonReadDao<PersistentChannel> getChannelReadDao() {
    return channelReadDao;
  }

  protected CommonWriteDao<PersistentChannel> getChannelWriteDao() {
    return channelWriteDao;
  }

  protected GenericAdapter<Event, PersistentEvent> getEventConverter() {
    return eventConverter;
  }

  protected CommonReadDao<PersistentEvent> getEventReadDao() {
    return eventReadDao;
  }

  protected CommonWriteDao<PersistentEvent> getEventWriteDao() {
    return eventWriteDao;
  }

  @Override
  public void create(Channel channel) {
    PersistentChannel persistentChannel = getChannelConverter().convert(channel);
    if (persistentChannel == null) {
      return;
    }
    final Date date = new Date();
    persistentChannel.setCreationDateTime(date);
    persistentChannel.setLastModifiedDateTime(date);
    getChannelWriteDao().save(persistentChannel);
  }

  @Override
  public void update(Channel channel) {
    PersistentChannel persistentChannel = getMergedPersistentChannel(channel);
    if (persistentChannel != null) {
      persistentChannel.setLastModifiedDateTime(new Date());
      getChannelWriteDao().update(persistentChannel);
    }
  }

  @Override
  public void delete(Channel channel) {
    PersistentChannel persistentChannel = getMergedPersistentChannel(channel);
    if (persistentChannel != null) {
      getChannelWriteDao().delete(persistentChannel);
    }
  }

  @Override
  public Channel getChannel(String channelName) {
    PersistentChannel persistentChannel = getPersistentChannel(channelName);
    return getChannelConverter().convertInversely(persistentChannel);
  }

  @Override
  public Event create(Channel channel, Event event) {
    PersistentEvent persistentEvent = getEventConverter().convert(event);
    if (persistentEvent != null && channel != null) {
      persistentEvent.setChannelId(channel.getName());
      persistentEvent.setCreationDateTime(new Date());
      getEventWriteDao().save(persistentEvent);
      return getEventConverter().convertInversely(persistentEvent);
    }
    return null;
  }

  @Override
  public void delete(Event event) {
    PersistentEvent persistentEvent = getMergedPersistentEvent(event);
    if (persistentEvent != null) {
      getEventWriteDao().delete(persistentEvent);
    }
  }

  @Override
  public Event getEvent(String placeholderId) {
    PersistentEvent persistentEvent = getPersistentEvent(placeholderId);
    return getEventConverter().convertInversely(persistentEvent);
  }

  @Override
  public Event getEventByUUID(String uuid) {
    if (StringUtils.isBlank(uuid)) {
      return null;
    }
    PersistentEvent event = getEventReadDao().getSingle(QueryParameterFactory.getStringLikePropertyParam(
        PersistentEvent.UUID, uuid, MatchMode.EXACT));
    return getEventConverter().convertInversely(event);
  }

  @Override
  public LinkedHashSet<Event> getEvents(String placeholderId, String channelId,
                                        int count) {
    int placeholderIdInt = NumberUtils.toInt(placeholderId);
    if (placeholderIdInt <= 0 || count == 0) {
      return new LinkedHashSet<Event>();
    }
    else {
      List<QueryParameter> params = new ArrayList<QueryParameter>();
      final QueryParameter<Integer> propertyParam;
      if (count > 0) {
        propertyParam =
        QueryParameterFactory.getGreaterThanPropertyParam(PersistentEvent.PLACE_HOLDER_ID, placeholderIdInt);
      }
      else {
        propertyParam = QueryParameterFactory.getLesserThanPropertyParam(PersistentEvent.PLACE_HOLDER_ID,
                                                                         placeholderIdInt);
      }
      if (StringUtils.isNotBlank(channelId)) {
        params.add(QueryParameterFactory.getStringLikePropertyParam(PersistentEvent.CHANNEL_ID, channelId,
                                                                    MatchMode.EXACT));
      }
      params.addAll(Arrays.asList(propertyParam, QueryParameterFactory.getMaxResultsParam(
          Math.abs(count)), QueryParameterFactory.getOrderByParam(
          PersistentEvent.PLACE_HOLDER_ID, Order.DESC)));
      List<PersistentEvent> persistentEvents = getEventReadDao().getList(params);
      if (persistentEvents == null || persistentEvents.isEmpty()) {
        return new LinkedHashSet<Event>();
      }
      return new LinkedHashSet<Event>(getEventConverter().convertInversely(persistentEvents.toArray(new PersistentEvent[persistentEvents.
          size()])));
    }
  }

  @Override
  public Collection<Channel> getChannels(int startIndex, int count) {
    if (startIndex < 0 || count == 0) {
      return Collections.emptyList();
    }
    final QueryParameter<Integer> idParam;
    if (count > 0) {
      idParam = QueryParameterFactory.getGreaterThanPropertyParam("id", startIndex);
    }
    else {
      idParam = QueryParameterFactory.getLesserThanPropertyParam("id", startIndex);
    }
    List<PersistentChannel> channels = getChannelReadDao().getList(idParam, QueryParameterFactory.getMaxResultsParam(
        Math.abs(count)));
    return getChannelConverter().convertInversely(channels.toArray(new PersistentChannel[channels.size()]));
  }

  protected PersistentChannel getMergedPersistentChannel(Channel channel) {
    if (channel == null) {
      return null;
    }
    PersistentChannel persistentChannel =
                      getPersistentChannel(channel.getName());
    Map.Entry<Channel, PersistentChannel> entry;
    entry = new SimpleEntry<Channel, PersistentChannel>(channel,
                                                        persistentChannel);
    getChannelConverter().merge(entry);
    return persistentChannel;
  }

  protected PersistentChannel getPersistentChannel(String channelName) {
    PersistentChannel persistentChannel = getChannelReadDao().getSingle(
        QueryParameterFactory.getStringLikePropertyParam(PersistentChannel.NAME,
                                                         channelName.toLowerCase(), MatchMode.EXACT));
    return persistentChannel;
  }

  protected PersistentEvent getMergedPersistentEvent(Event event) {
    if (event == null) {
      return null;
    }
    PersistentEvent persistentEvent =
                    getPersistentEvent(event.getPlaceholderId());
    Map.Entry<Event, PersistentEvent> entry;
    entry = new SimpleEntry<Event, PersistentEvent>(event,
                                                    persistentEvent);
    getEventConverter().merge(entry);
    return persistentEvent;
  }

  protected PersistentEvent getPersistentEvent(String placeholderId) {
    int placeholderIdInt = NumberUtils.toInt(placeholderId);
    if (placeholderIdInt <= 0) {
      return null;
    }
    PersistentEvent persistentEvent = getEventReadDao().getSingle(
        QueryParameterFactory.getEqualPropertyParam(
        PersistentEvent.PLACE_HOLDER_ID, placeholderIdInt));
    return persistentEvent;
  }
}
