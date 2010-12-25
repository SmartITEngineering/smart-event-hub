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
package com.smartitengineering.event.hub.spi.hbase;

import com.google.inject.Inject;
import com.smartitengineering.dao.common.CommonReadDao;
import com.smartitengineering.dao.common.CommonWriteDao;
import com.smartitengineering.dao.common.queryparam.MatchMode;
import com.smartitengineering.dao.common.queryparam.QueryParameter;
import com.smartitengineering.dao.common.queryparam.QueryParameterFactory;
import com.smartitengineering.dao.impl.hbase.spi.RowCellIncrementor;
import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.api.Event;
import com.smartitengineering.event.hub.spi.HubPersistentStorer;
import com.smartitengineering.event.hub.spi.hbase.persistents.RowAutoIdIndex;
import com.smartitengineering.event.hub.spi.hbase.persistents.ChannelAdapterHelper;
import com.smartitengineering.event.hub.spi.hbase.persistents.EventAdapterHelper;
import com.smartitengineering.event.hub.spi.hbase.persistents.EventId;
import com.smartitengineering.event.hub.spi.hbase.persistents.EventUUID;
import com.smartitengineering.event.hub.spi.hbase.persistents.PersistentChannel;
import com.smartitengineering.event.hub.spi.hbase.persistents.PersistentEvent;
import com.smartitengineering.event.hub.spi.hbase.persistents.ReverseIdIndex;
import com.smartitengineering.util.bean.adapter.GenericAdapter;
import com.smartitengineering.util.bean.adapter.GenericAdapterImpl;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
public class HubPersistentStorerImpl implements HubPersistentStorer {

  private static final String CHANNELS_ROW_ID_N_PREFIX = "channels";
  private static final String EVENTS_ROW_ID_N_PREFIX = "events";
  private static final PersistentChannel[] EMPTY_CHANNEL_ARRAY = new PersistentChannel[0];
  private static final PersistentEvent[] EMPTY_EVENT_ARRAY = new PersistentEvent[0];
  public static final int MAX_LENGTH = String.valueOf(Long.MAX_VALUE).length();
  @Inject
  protected CommonWriteDao<PersistentChannel> channelWrtDao;
  @Inject
  protected CommonWriteDao<PersistentEvent> eventWrtDao;
  @Inject
  protected CommonWriteDao<EventUUID> eventUUIDWrtDao;
  @Inject
  protected CommonWriteDao<RowAutoIdIndex> autoIdWrtDao;
  @Inject
  protected CommonWriteDao<ReverseIdIndex> reverseIdIndexWrtDao;
  @Inject
  protected CommonReadDao<PersistentChannel, Long> channelRdDao;
  @Inject
  protected CommonReadDao<PersistentEvent, EventId> eventRdDao;
  @Inject
  protected CommonReadDao<EventUUID, String> eventUUIDRdDao;
  @Inject
  protected CommonReadDao<RowAutoIdIndex, String> autoIdRdDao;
  @Inject
  protected CommonReadDao<ReverseIdIndex, String> reverseIdIndexRdDao;
  @Inject
  protected RowCellIncrementor<Channel, RowAutoIdIndex, String> idIncrementor;
  @Inject
  protected RowCellIncrementor<ReverseIdIndex, RowAutoIdIndex, String> reverseIdIncrementor;
  protected boolean channelAutoIdInitialized = false;
  protected boolean eventAutoIdInitialized = false;
  protected final GenericAdapter<Channel, PersistentChannel> channelAdapter;
  protected final GenericAdapter<Event, PersistentEvent> eventAdapter;
  protected Logger logger = LoggerFactory.getLogger(getClass());

  public HubPersistentStorerImpl() {
    GenericAdapterImpl<Channel, PersistentChannel> lChannelAdapter =
                                                   new GenericAdapterImpl<Channel, PersistentChannel>();
    lChannelAdapter.setHelper(new ChannelAdapterHelper());
    this.channelAdapter = lChannelAdapter;
    GenericAdapterImpl<Event, PersistentEvent> lEventAdapter = new GenericAdapterImpl<Event, PersistentEvent>();
    lEventAdapter.setHelper(new EventAdapterHelper());
    this.eventAdapter = lEventAdapter;
  }

  @Override
  public void create(Channel channel) {
    checkAndInitializeAutoId();
    if (!channelAutoIdInitialized) {
      throw new IllegalStateException("Channel ID could not be generated!");
    }
    if (channel == null) {
      return;
    }
    PersistentChannel pChannel = channelAdapter.convert(channel);
    if (!pChannel.isValid()) {
      throw new IllegalStateException("Channel not in valid state!");
    }
    Channel eChannel = getChannel(pChannel.getName());
    if (eChannel != null) {
      throw new IllegalStateException("Channel already exists!");
    }
    try {
      long channelId = idIncrementor.incrementAndGet(CHANNELS_ROW_ID_N_PREFIX, -1);
      long channelReverseId = reverseIdIncrementor.incrementAndGet(CHANNELS_ROW_ID_N_PREFIX, 1);
      pChannel.setId(channelId);
      final Date date = new Date();
      pChannel.setCreationDateTime(date);
      pChannel.setLastModifiedDateTime(date);

      RowAutoIdIndex channelEventId = new RowAutoIdIndex();
      channelEventId.setAutoIdValue(Long.MAX_VALUE);
      channelEventId.setId(getChannelIdIndexName(pChannel.getName()));
      channelEventId.setReverseId(String.valueOf(channelId));

      ReverseIdIndex idIndex = new ReverseIdIndex();
      idIndex.setId(getChannelIdIndexName(String.valueOf(channelReverseId)));
      idIndex.setReverseId(String.valueOf(channelId));

      autoIdWrtDao.save(channelEventId);
      channelWrtDao.save(pChannel);
      reverseIdIndexWrtDao.save(idIndex);
    }
    catch (RuntimeException ex) {
      logger.error("Could not create channel or its events' auto id generator!", ex);
      throw ex;
    }
  }

  @Override
  public void update(Channel channel) {
    if (channel == null) {
      return;
    }
    checkAndInitializeAutoId();
    PersistentChannel pChannel = getMergedPersistentChannel(channel);
    if (!pChannel.isValid()) {
      throw new IllegalStateException("Channel not in valid state!");
    }
    try {
      pChannel.setLastModifiedDateTime(new Date());
      channelWrtDao.update(pChannel);
    }
    catch (RuntimeException ex) {
      logger.error("Could not update channel!", ex);
      throw ex;
    }
  }

  @Override
  public void delete(Channel channel) {
    if (channel == null) {
      return;
    }
    checkAndInitializeAutoId();
    PersistentChannel pChannel = getMergedPersistentChannel(channel);
    if (!pChannel.isValid()) {
      throw new IllegalStateException("Channel not in valid state!");
    }
    try {
      channelWrtDao.delete(pChannel);
    }
    catch (RuntimeException ex) {
      logger.error("Could not update channel!", ex);
      throw ex;
    }
  }

  @Override
  public Channel getChannel(String channelName) {
    if (StringUtils.isBlank(channelName)) {
      return null;
    }
    return channelAdapter.convertInversely(getPersistentChannel(channelName));
  }

  @Override
  public Collection<Channel> getChannels(int startIndex, int count) {
    if (count == 0) {
      return Collections.emptyList();
    }
    final QueryParameter<Integer> maxResultsParam = QueryParameterFactory.getMaxResultsParam(Math.abs(count));
    if (count < 0) {
      final QueryParameter param;
      long index = Long.MAX_VALUE - startIndex;
      if (logger.isInfoEnabled()) {
        logger.info("Reverse Index value " + index);
      }
      param = QueryParameterFactory.getGreaterThanPropertyParam("id", Bytes.toBytes(index));
      final List<PersistentChannel> list = channelRdDao.getList(param, maxResultsParam);
      if (logger.isInfoEnabled()) {
        logger.info("Result " + list);
      }
      return channelAdapter.convertInversely(list.toArray(EMPTY_CHANNEL_ARRAY));
    }
    else {
      final QueryParameter param = QueryParameterFactory.getGreaterThanPropertyParam("id", Bytes.toBytes(getChannelIdIndexName(String.
          valueOf(startIndex))));
      List<ReverseIdIndex> reverseIndexes = reverseIdIndexRdDao.getList(param, maxResultsParam);
      List<Long> ids = new ArrayList<Long>(reverseIndexes.size());
      for (ReverseIdIndex index : reverseIndexes) {
        final long longVal = NumberUtils.toLong(index.getReverseId());
        if (longVal > -1) {
          ids.add(longVal);
        }
      }
      final Set<PersistentChannel> byIds = channelRdDao.getByIds(ids);
      if (logger.isInfoEnabled()) {
        logger.info("Result " + reverseIndexes + " - " + byIds);
      }
      return channelAdapter.convertInversely(byIds.toArray(EMPTY_CHANNEL_ARRAY));
    }
  }

  @Override
  public Event create(Channel channel, Event event) {
    PersistentEvent persistentEvent = eventAdapter.convert(event);
    if (persistentEvent != null && channel != null) {
      String eventsId = EVENTS_ROW_ID_N_PREFIX;
      long placheholderId = idIncrementor.incrementAndGet(eventsId, -1);
      long revPlacheholderId = reverseIdIncrementor.incrementAndGet(eventsId, 1);
      persistentEvent.setPlaceholderId(String.valueOf(placheholderId));
      persistentEvent.setChannelId(channel.getName());
      persistentEvent.getId().setEventIdForChannel(placheholderId);
      persistentEvent.setCreationDateTime(new Date());
      if (StringUtils.isBlank(persistentEvent.getUuid())) {
        UUID uuid = UUID.randomUUID();
        persistentEvent.setUuid(uuid.toString());
      }
      else {
        Event possDupEvent = getEventByUUID(persistentEvent.getUuid());
        if (possDupEvent != null) {
          throw new IllegalArgumentException("Duplication event!");
        }
      }

      EventUUID eUuid = new EventUUID();
      eUuid.setEventId(persistentEvent.getId());
      eUuid.setId(persistentEvent.getUuid());

      ReverseIdIndex reverseIdIndex = new ReverseIdIndex();
      reverseIdIndex.setReverseId(persistentEvent.getId().toString());
      reverseIdIndex.setId(
          new StringBuilder().append(leftPadNumberWithZero(revPlacheholderId)).append(':').
          append(channel.getName()).toString());

      eventWrtDao.save(persistentEvent);
      reverseIdIndexWrtDao.save(reverseIdIndex);
      eventUUIDWrtDao.save(eUuid);
      final Event convertInversely = eventAdapter.convertInversely(persistentEvent);
      if (logger.isInfoEnabled()) {
        logger.info("Event's ID, UUID and PlaceholderID: " + persistentEvent.getId().toString() + " " + convertInversely.
            getUniversallyUniqueID() + " " + convertInversely.getPlaceholderId());
      }
      return convertInversely;
    }
    return null;
  }

  @Override
  public void delete(Event event) {
    if (event == null) {
      return;
    }
    final PersistentEvent persistentEvent = getPersistentEvent(event.getPlaceholderId());
    if (persistentEvent == null) {
      return;
    }
    eventWrtDao.delete(persistentEvent);
  }

  @Override
  public Event getEvent(String placeholderId) {
    final PersistentEvent persistentEvent = getPersistentEvent(placeholderId);
    final Event convertInversely = eventAdapter.convertInversely(persistentEvent);
    if (logger.isInfoEnabled()) {
      if (persistentEvent != null && convertInversely != null) {
        logger.info("Event's ID, UUID and PlaceholderID: " + persistentEvent.getId().toString() + " " + convertInversely.
            getUniversallyUniqueID() + " " + convertInversely.getPlaceholderId());
      }
      else {
        logger.info("EVENT IS NULL!");
      }
    }
    return convertInversely;
  }

  @Override
  public Event getEventByUUID(String uuid) {
    if (StringUtils.isBlank(uuid)) {
      return null;
    }
    EventUUID eUuid = eventUUIDRdDao.getById(uuid);
    if (eUuid != null) {
      PersistentEvent event = eventRdDao.getById(eUuid.getEventId());
      if (event != null) {
        return eventAdapter.convertInversely(event);
      }
    }
    return null;
  }

  @Override
  public LinkedHashSet<Event> getEvents(String placeholderId, final String channelId, int count) {
    if (count == 0) {
      return new LinkedHashSet<Event>();
    }
    final QueryParameter<Integer> maxResultsParam = QueryParameterFactory.getMaxResultsParam(Math.abs(count));
    final String eventChannelId;
    if (StringUtils.isNotBlank(placeholderId) && StringUtils.isBlank(channelId)) {
      PersistentEvent pEvent = getPersistentEvent(placeholderId);
      if (pEvent != null) {
        eventChannelId = pEvent.getChannelId();
      }
      else {
        eventChannelId = "";
      }
    }
    else {
      eventChannelId = channelId;
    }
    if (count < 0) {
      final List<QueryParameter> params = new ArrayList<QueryParameter>();
      params.add(maxResultsParam);
      if (StringUtils.isNotBlank(placeholderId)) {
        final StringBuilder searchId = new StringBuilder(leftPadNumberWithZero(NumberUtils.toLong(placeholderId))).
            append(':');
        searchId.append(eventChannelId);
        if (logger.isInfoEnabled()) {
          logger.info("Event Id to search greater or smaller than: " + searchId);
        }
        params.add(QueryParameterFactory.getGreaterThanPropertyParam("id", Bytes.toBytes(searchId.toString())));
      }
      if (StringUtils.isNotBlank(channelId)) {
        params.add(QueryParameterFactory.getStringLikePropertyParam("id", Bytes.toBytes(new StringBuilder(':').append(
            channelId).toString()), MatchMode.END));
      }
      return new LinkedHashSet<Event>(eventAdapter.convertInversely(
          eventRdDao.getList(params).toArray(EMPTY_EVENT_ARRAY)));
    }
    else {
      final List<QueryParameter> params = new ArrayList<QueryParameter>();
      params.add(maxResultsParam);
      if (StringUtils.isNotBlank(placeholderId)) {
        final long toLong = NumberUtils.toLong(placeholderId);
        final long reversePlaceholderId = Long.MAX_VALUE - toLong;
        params.add(QueryParameterFactory.getGreaterThanPropertyParam("id", Bytes.toBytes(new StringBuilder(leftPadNumberWithZero(
            reversePlaceholderId)).append(':').append(eventChannelId).toString())));
      }
      if (StringUtils.isNotBlank(channelId)) {
        params.add(QueryParameterFactory.getStringLikePropertyParam("id", Bytes.toBytes(new StringBuilder(':').append(
            channelId).toString()), MatchMode.END));
      }
      List<ReverseIdIndex> indexes = reverseIdIndexRdDao.getList(params);
      List<EventId> eventIds = new ArrayList<EventId>(indexes.size());
      for (ReverseIdIndex index : indexes) {
        eventIds.add(EventId.fromString(index.getReverseId()));
      }
      Collections.reverse(eventIds);
      return new LinkedHashSet<Event>(eventAdapter.convertInversely(eventRdDao.getByIds(eventIds).toArray(
          EMPTY_EVENT_ARRAY)));
    }
  }

  protected String getChannelIdIndexName(String channelName) {
    return new StringBuilder(CHANNELS_ROW_ID_N_PREFIX).append(':').append(channelName).toString();
  }

  protected String getEventIdIndexName(String eventId) {
    return new StringBuilder(EVENTS_ROW_ID_N_PREFIX).append(':').append(eventId).toString();
  }

  protected void checkAndInitializeAutoId() throws RuntimeException {
    if (!channelAutoIdInitialized) {
      channelAutoIdInitialized = checkAndInitializeAutoId(CHANNELS_ROW_ID_N_PREFIX);
    }
    if (!eventAutoIdInitialized) {
      eventAutoIdInitialized = checkAndInitializeAutoId(EVENTS_ROW_ID_N_PREFIX);
    }
  }

  protected boolean checkAndInitializeAutoId(String autoId) throws RuntimeException {
    RowAutoIdIndex id = autoIdRdDao.getById(autoId);
    if (id == null) {
      id = new RowAutoIdIndex();
      id.setAutoIdValue(Long.MAX_VALUE);
      id.setReverseAutoIdValue(0l);
      id.setId(autoId);
      try {
        autoIdWrtDao.save(id);
        return true;
      }
      catch (RuntimeException ex) {
        logger.error("Could not initialize channel auto id!", ex);
        throw ex;
      }
    }
    else {
      return true;
    }
  }

  protected String leftPadNumberWithZero(long revPlacheholderId) {
    return StringUtils.leftPad(String.valueOf(revPlacheholderId), MAX_LENGTH, '0');
  }

  protected PersistentChannel getMergedPersistentChannel(Channel channel) {
    if (channel == null || StringUtils.isBlank(channel.getName())) {
      return null;
    }
    PersistentChannel persistentChannel = getPersistentChannel(channel.getName());
    if (logger.isInfoEnabled()) {
      logger.info("Persistent channel found is " + persistentChannel);
      logger.info("Persistent channel ID is " + persistentChannel.getId());
    }
    Map.Entry<Channel, PersistentChannel> entry;
    entry = new SimpleEntry<Channel, PersistentChannel>(channel, persistentChannel);
    channelAdapter.merge(entry);
    if (logger.isInfoEnabled()) {
      logger.info("Persistent channel ID is " + persistentChannel.getId());
    }
    return persistentChannel;
  }

  protected PersistentChannel getPersistentChannel(String channelName) {
    if (StringUtils.isBlank(channelName)) {
      return null;
    }
    final String channelIdIndexName = getChannelIdIndexName(channelName.toLowerCase());
    if (logger.isInfoEnabled()) {
      logger.info("Getting channel with name in reverse lookup table " + channelIdIndexName);
    }
    RowAutoIdIndex idIndex = autoIdRdDao.getById(channelIdIndexName);
    if (idIndex != null) {
      logger.info("Found ID in channel name index");
      final long channelId = NumberUtils.toLong(idIndex.getReverseId());
      if (logger.isInfoEnabled()) {
        logger.info("Found reverse index for channel " + channelId);
      }
      PersistentChannel persistentChannel = channelRdDao.getById(channelId);
      return persistentChannel;
    }
    else {
      logger.info("Did not found ID in channel name index");
      PersistentChannel persistentChannel = channelRdDao.getSingle(QueryParameterFactory.getStringLikePropertyParam(
          PersistentChannel.NAME, channelName.toLowerCase(), MatchMode.EXACT));
      return persistentChannel;
    }
  }

  protected PersistentEvent getMergedPersistentEvent(Event event) {
    if (event == null) {
      return null;
    }
    PersistentEvent persistentEvent = getPersistentEvent(event.getPlaceholderId());
    Map.Entry<Event, PersistentEvent> entry;
    entry = new SimpleEntry<Event, PersistentEvent>(event, persistentEvent);
    eventAdapter.merge(entry);
    return persistentEvent;
  }

  protected PersistentEvent getPersistentEvent(String placeholderId) {
    long placeholderIdInt = NumberUtils.toLong(placeholderId);
    if (placeholderIdInt <= 0) {
      logger.info("Invalid place holder id!");
      return null;
    }
    PersistentEvent persistentEvent =
                    eventRdDao.getSingle(
        QueryParameterFactory.getStringLikePropertyParam("id", leftPadNumberWithZero(placeholderIdInt), MatchMode.START));
    return persistentEvent;
  }
}
