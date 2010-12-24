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
package com.smartitengineering.event.hub.spi.hbase.di;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.smartitengineering.dao.common.CommonReadDao;
import com.smartitengineering.dao.common.CommonWriteDao;
import com.smartitengineering.dao.impl.hbase.CommonDao;
import com.smartitengineering.dao.impl.hbase.spi.AsyncExecutorService;
import com.smartitengineering.dao.impl.hbase.spi.CellConfig;
import com.smartitengineering.dao.impl.hbase.spi.DomainIdInstanceProvider;
import com.smartitengineering.dao.impl.hbase.spi.FilterConfigs;
import com.smartitengineering.dao.impl.hbase.spi.LockAttainer;
import com.smartitengineering.dao.impl.hbase.spi.MergeService;
import com.smartitengineering.dao.impl.hbase.spi.ObjectRowConverter;
import com.smartitengineering.dao.impl.hbase.spi.RowCellIncrementor;
import com.smartitengineering.dao.impl.hbase.spi.SchemaInfoProvider;
import com.smartitengineering.dao.impl.hbase.spi.impl.CellConfigImpl;
import com.smartitengineering.dao.impl.hbase.spi.impl.DiffBasedMergeService;
import com.smartitengineering.dao.impl.hbase.spi.impl.LockAttainerImpl;
import com.smartitengineering.dao.impl.hbase.spi.impl.MixedExecutorServiceImpl;
import com.smartitengineering.dao.impl.hbase.spi.impl.RowCellIncrementorImpl;
import com.smartitengineering.dao.impl.hbase.spi.impl.SchemaInfoProviderBaseConfig;
import com.smartitengineering.dao.impl.hbase.spi.impl.SchemaInfoProviderImpl;
import com.smartitengineering.dao.impl.hbase.spi.impl.guice.GenericBaseConfigProvider;
import com.smartitengineering.dao.impl.hbase.spi.impl.guice.GenericFilterConfigsProvider;
import com.smartitengineering.domain.PersistentDTO;
import com.smartitengineering.event.hub.api.Channel;
import com.smartitengineering.event.hub.spi.HubPersistentStorer;
import com.smartitengineering.event.hub.spi.hbase.HubPersistentStorerImpl;
import com.smartitengineering.event.hub.spi.hbase.persistents.AutoIdObjectConverter;
import com.smartitengineering.event.hub.spi.hbase.persistents.ChannelObjectConverter;
import com.smartitengineering.event.hub.spi.hbase.persistents.DomainIdInstanceProviderImpl;
import com.smartitengineering.event.hub.spi.hbase.persistents.EventId;
import com.smartitengineering.event.hub.spi.hbase.persistents.EventObjectConverter;
import com.smartitengineering.event.hub.spi.hbase.persistents.EventUUID;
import com.smartitengineering.event.hub.spi.hbase.persistents.EventUUIDObjectConverter;
import com.smartitengineering.event.hub.spi.hbase.persistents.PersistentChannel;
import com.smartitengineering.event.hub.spi.hbase.persistents.PersistentEvent;
import com.smartitengineering.event.hub.spi.hbase.persistents.ReverseIdIndex;
import com.smartitengineering.event.hub.spi.hbase.persistents.ReverseIndexObjectConverter;
import com.smartitengineering.event.hub.spi.hbase.persistents.RowAutoIdIndex;
import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author imyousuf
 */
public class GuiceModule extends AbstractModule {

  private final long waitTime;

  public GuiceModule(Properties properties) {
    long toLong = NumberUtils.toLong(properties.getProperty("com.smartitengineering.event.waitTimeInSec"), 10L);
    waitTime = toLong > 0 ? toLong : 10l;
  }

  @Override
  protected void configure() {
    bind(HubPersistentStorer.class).annotatedWith(Names.named("storer")).to(HubPersistentStorerImpl.class).in(
        Scopes.SINGLETON);

    bind(AsyncExecutorService.class).to(MixedExecutorServiceImpl.class).in(Scopes.SINGLETON);
    bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
    bind(Integer.class).annotatedWith(Names.named("maxRows")).toInstance(new Integer(100));
    bind(Long.class).annotatedWith(Names.named("waitTime")).toInstance(waitTime);
    bind(TimeUnit.class).annotatedWith(Names.named("unit")).toInstance(TimeUnit.SECONDS);
    bind(Boolean.class).annotatedWith(Names.named("mergeEnabled")).toInstance(Boolean.TRUE);
    bind(DomainIdInstanceProvider.class).to(DomainIdInstanceProviderImpl.class).in(Scopes.SINGLETON);

    {
      bind(new TypeLiteral<ObjectRowConverter<PersistentEvent>>() {
      }).to(EventObjectConverter.class).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonReadDao<PersistentEvent, EventId>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentEvent, EventId>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonWriteDao<PersistentEvent>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentEvent, EventId>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentEvent, EventId>>() {
      }).to(new TypeLiteral<CommonDao<PersistentEvent, EventId>>() {
      }).in(Scopes.SINGLETON);
      final TypeLiteral<SchemaInfoProviderImpl<PersistentEvent, EventId>> typeLiteral = new TypeLiteral<SchemaInfoProviderImpl<PersistentEvent, EventId>>() {
      };
      bind(new TypeLiteral<MergeService<PersistentEvent, EventId>>() {
      }).to(new TypeLiteral<DiffBasedMergeService<PersistentEvent, EventId>>() {
      });
      bind(new TypeLiteral<LockAttainer<PersistentEvent, EventId>>() {
      }).to(new TypeLiteral<LockAttainerImpl<PersistentEvent, EventId>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<Class<EventId>>() {
      }).toInstance(EventId.class);
      bind(new TypeLiteral<SchemaInfoProvider<PersistentEvent, EventId>>() {
      }).to(typeLiteral).in(Scopes.SINGLETON);
      bind(new TypeLiteral<FilterConfigs<PersistentEvent>>() {
      }).toProvider(new GenericFilterConfigsProvider<PersistentEvent>(
          "com/smartitengineering/event/hub/spi/hbase/persistents/EventFilterConfigs.json")).in(Scopes.SINGLETON);
      bind(new TypeLiteral<SchemaInfoProviderBaseConfig<PersistentEvent>>() {
      }).toProvider(new GenericBaseConfigProvider<PersistentEvent>(
          "com/smartitengineering/event/hub/spi/hbase/persistents/EventSchemaBaseConfig.json")).in(Scopes.SINGLETON);
    }
    {
      bind(new TypeLiteral<ObjectRowConverter<PersistentChannel>>() {
      }).to(ChannelObjectConverter.class).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonReadDao<PersistentChannel, Long>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentChannel, Long>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonWriteDao<PersistentChannel>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentChannel, Long>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentChannel, Long>>() {
      }).to(new TypeLiteral<CommonDao<PersistentChannel, Long>>() {
      }).in(Scopes.SINGLETON);
      final TypeLiteral<SchemaInfoProviderImpl<PersistentChannel, Long>> typeLiteral = new TypeLiteral<SchemaInfoProviderImpl<PersistentChannel, Long>>() {
      };
      bind(new TypeLiteral<MergeService<PersistentChannel, Long>>() {
      }).to(new TypeLiteral<DiffBasedMergeService<PersistentChannel, Long>>() {
      });
      bind(new TypeLiteral<LockAttainer<PersistentChannel, Long>>() {
      }).to(new TypeLiteral<LockAttainerImpl<PersistentChannel, Long>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<Class<Long>>() {
      }).toInstance(Long.class);
      bind(new TypeLiteral<SchemaInfoProvider<PersistentChannel, Long>>() {
      }).to(typeLiteral).in(Scopes.SINGLETON);
      bind(new TypeLiteral<FilterConfigs<PersistentChannel>>() {
      }).toProvider(new GenericFilterConfigsProvider<PersistentChannel>(
          "com/smartitengineering/event/hub/spi/hbase/persistents/ChannelFilterConfigs.json")).in(Scopes.SINGLETON);
      bind(new TypeLiteral<SchemaInfoProviderBaseConfig<PersistentChannel>>() {
      }).toProvider(new GenericBaseConfigProvider<PersistentChannel>(
          "com/smartitengineering/event/hub/spi/hbase/persistents/ChannelSchemaBaseConfig.json")).in(Scopes.SINGLETON);
    }
    {
      bind(new TypeLiteral<ObjectRowConverter<RowAutoIdIndex>>() {
      }).to(AutoIdObjectConverter.class).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonReadDao<RowAutoIdIndex, String>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<RowAutoIdIndex, String>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonWriteDao<RowAutoIdIndex>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<RowAutoIdIndex, String>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<RowAutoIdIndex, String>>() {
      }).to(new TypeLiteral<CommonDao<RowAutoIdIndex, String>>() {
      }).in(Scopes.SINGLETON);
      final TypeLiteral<SchemaInfoProviderImpl<RowAutoIdIndex, String>> typeLiteral = new TypeLiteral<SchemaInfoProviderImpl<RowAutoIdIndex, String>>() {
      };
      bind(new TypeLiteral<MergeService<RowAutoIdIndex, String>>() {
      }).to(new TypeLiteral<DiffBasedMergeService<RowAutoIdIndex, String>>() {
      });
      bind(new TypeLiteral<LockAttainer<RowAutoIdIndex, String>>() {
      }).to(new TypeLiteral<LockAttainerImpl<RowAutoIdIndex, String>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<Class<String>>() {
      }).toInstance(String.class);
      bind(new TypeLiteral<SchemaInfoProvider<RowAutoIdIndex, String>>() {
      }).to(typeLiteral).in(Scopes.SINGLETON);
      bind(new TypeLiteral<FilterConfigs<RowAutoIdIndex>>() {
      }).toProvider(new GenericFilterConfigsProvider<RowAutoIdIndex>(
          "com/smartitengineering/event/hub/spi/hbase/persistents/AutoIdFilterConfigs.json")).in(Scopes.SINGLETON);
      bind(new TypeLiteral<SchemaInfoProviderBaseConfig<RowAutoIdIndex>>() {
      }).toProvider(new GenericBaseConfigProvider<RowAutoIdIndex>(
          "com/smartitengineering/event/hub/spi/hbase/persistents/AutoIdSchemaBaseConfig.json")).in(Scopes.SINGLETON);
    }
    {
      bind(new TypeLiteral<ObjectRowConverter<EventUUID>>() {
      }).to(EventUUIDObjectConverter.class).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonReadDao<EventUUID, String>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<EventUUID, String>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonWriteDao<EventUUID>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<EventUUID, String>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<EventUUID, String>>() {
      }).to(new TypeLiteral<CommonDao<EventUUID, String>>() {
      }).in(Scopes.SINGLETON);
      final TypeLiteral<SchemaInfoProviderImpl<EventUUID, String>> typeLiteral = new TypeLiteral<SchemaInfoProviderImpl<EventUUID, String>>() {
      };
      bind(new TypeLiteral<MergeService<EventUUID, String>>() {
      }).to(new TypeLiteral<DiffBasedMergeService<EventUUID, String>>() {
      });
      bind(new TypeLiteral<LockAttainer<EventUUID, String>>() {
      }).to(new TypeLiteral<LockAttainerImpl<EventUUID, String>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<SchemaInfoProvider<EventUUID, String>>() {
      }).to(typeLiteral).in(Scopes.SINGLETON);
      bind(new TypeLiteral<FilterConfigs<EventUUID>>() {
      }).toProvider(new GenericFilterConfigsProvider<EventUUID>(
          "com/smartitengineering/event/hub/spi/hbase/persistents/EventUUIDFilterConfigs.json")).in(Scopes.SINGLETON);
      bind(new TypeLiteral<SchemaInfoProviderBaseConfig<EventUUID>>() {
      }).toProvider(new GenericBaseConfigProvider<EventUUID>(
          "com/smartitengineering/event/hub/spi/hbase/persistents/EventUUIDSchemaBaseConfig.json")).in(Scopes.SINGLETON);
    }
    {
      bind(new TypeLiteral<ObjectRowConverter<ReverseIdIndex>>() {
      }).to(ReverseIndexObjectConverter.class).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonReadDao<ReverseIdIndex, String>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<ReverseIdIndex, String>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonWriteDao<ReverseIdIndex>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<ReverseIdIndex, String>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<ReverseIdIndex, String>>() {
      }).to(new TypeLiteral<CommonDao<ReverseIdIndex, String>>() {
      }).in(Scopes.SINGLETON);
      final TypeLiteral<SchemaInfoProviderImpl<ReverseIdIndex, String>> typeLiteral = new TypeLiteral<SchemaInfoProviderImpl<ReverseIdIndex, String>>() {
      };
      bind(new TypeLiteral<MergeService<ReverseIdIndex, String>>() {
      }).to(new TypeLiteral<DiffBasedMergeService<ReverseIdIndex, String>>() {
      });
      bind(new TypeLiteral<LockAttainer<ReverseIdIndex, String>>() {
      }).to(new TypeLiteral<LockAttainerImpl<ReverseIdIndex, String>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<SchemaInfoProvider<ReverseIdIndex, String>>() {
      }).to(typeLiteral).in(Scopes.SINGLETON);
      bind(new TypeLiteral<FilterConfigs<ReverseIdIndex>>() {
      }).toProvider(new GenericFilterConfigsProvider<ReverseIdIndex>(
          "com/smartitengineering/event/hub/spi/hbase/persistents/ReverseIdIndexFilterConfigs.json")).in(
          Scopes.SINGLETON);
      bind(new TypeLiteral<SchemaInfoProviderBaseConfig<ReverseIdIndex>>() {
      }).toProvider(new GenericBaseConfigProvider<ReverseIdIndex>(
          "com/smartitengineering/event/hub/spi/hbase/persistents/ReverseIdIndexSchemaBaseConfig.json")).in(
          Scopes.SINGLETON);
    }
    bind(new TypeLiteral<RowCellIncrementor<Channel, RowAutoIdIndex, String>>() {
    }).to(new TypeLiteral<RowCellIncrementorImpl<Channel, RowAutoIdIndex, String>>() {
    });
    CellConfigImpl<Channel> configImpl = new CellConfigImpl<Channel>();
    configImpl.setFamily("self");
    configImpl.setQualifier("idValue");
    bind(new TypeLiteral<CellConfig<Channel>>() {
    }).toInstance(configImpl);
    bind(new TypeLiteral<RowCellIncrementor<ReverseIdIndex, RowAutoIdIndex, String>>() {
    }).to(new TypeLiteral<RowCellIncrementorImpl<ReverseIdIndex, RowAutoIdIndex, String>>() {
    });
    CellConfigImpl<ReverseIdIndex> configRevImpl = new CellConfigImpl<ReverseIdIndex>();
    configImpl.setFamily("self");
    configImpl.setQualifier("reverseIdValue");
    bind(new TypeLiteral<CellConfig<ReverseIdIndex>>() {
    }).toInstance(configRevImpl);
  }

  private static abstract class HBaseDaoInjectionModule<P extends PersistentDTO<? extends PersistentDTO, ? extends Comparable, ? extends Long>, T extends Serializable>
      extends AbstractModule {

    protected final Class<? extends ObjectRowConverter<P>> orc;
    protected final Class<T> tClass;
    protected final String filterConfigPathInClasspath;
    protected final String baseConfigPathInClasspath;

    protected HBaseDaoInjectionModule(Class<? extends ObjectRowConverter<P>> orc,
                                      Class<T> tClass, String filterConfigPathInClasspath,
                                      String baseConfigPathInClasspath) {
      this.orc = orc;
      this.tClass = tClass;
      this.filterConfigPathInClasspath = filterConfigPathInClasspath;
      this.baseConfigPathInClasspath = baseConfigPathInClasspath;
    }

    @Override
    protected void configure() {
    }
  }
}
