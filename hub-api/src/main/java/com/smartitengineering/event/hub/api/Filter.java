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
package com.smartitengineering.event.hub.api;

/**
 * A filter for filtering whether to broadcast an event to a particular channel
 * or not.
 * @author imyousuf
 */
public interface Filter {

  public static final String ALLOW_BROADCAST_METHOD_NAME = "allowBroadcast";

  /**
   * Checks whether to allow broadcast or not.
   * @param event The event to broadcast
   * @return True if to allow broadcast and false if should not be broadcasted
   */
  public boolean allowBroadcast(Event event);

  public SupportedMimeType getMimeType();

  public String getFilterScript();

  public static enum SupportedMimeType {

    RUBY, GROOVY, JAVA_SCRIPT, JYTHON;
  }
}
