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
package com.smartitengineering.event.hub.api.impl;

import com.smartitengineering.event.hub.api.Event;
import com.smartitengineering.event.hub.api.Filter;
import org.apache.commons.lang.StringUtils;
import org.jruby.embed.ScriptingContainer;

/**
 *
 * @author imyousuf
 */
class FilterImpl
    implements Filter {

  private SupportedMimeType mimeType;
  private String filterScript;
  private ScriptingContainer rubyScriptingContainer;

  public SupportedMimeType getMimeType() {
    return mimeType;
  }

  public void setMimeType(SupportedMimeType mimeType) {
    if (mimeType == null) {
      throw new IllegalArgumentException();
    }
    this.mimeType = mimeType;
  }

  public String getFilterScript() {
    return filterScript;
  }

  public void setFilterScript(String filterScript) {
    this.filterScript = filterScript;
  }

  public boolean allowBroadcast(Event event) {
    if (mimeType == null || StringUtils.isBlank(filterScript)) {
      return true;
    }
    switch (mimeType) {
      case RUBY:
        return allowBroadcastTestUsingRuby(event);
      default:
        return true;
    }
  }

  protected synchronized void initRuby() {
    if (StringUtils.isNotBlank(filterScript) && (rubyScriptingContainer == null)) {
      rubyScriptingContainer = new ScriptingContainer();
      rubyScriptingContainer.runScriptlet(filterScript);
    }
  }

  protected synchronized boolean allowBroadcastTestUsingRuby(Event event) {
    if (rubyScriptingContainer == null) {
      initRuby();
    }
    final Boolean bool;
    bool = rubyScriptingContainer.callMethod(null,
        ALLOW_BROADCAST_METHOD_NAME, new Object[] {event}, Boolean.class);
    return bool;
  }
}
