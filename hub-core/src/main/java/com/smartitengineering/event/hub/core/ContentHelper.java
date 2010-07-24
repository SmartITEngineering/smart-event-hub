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

import com.smartitengineering.event.hub.api.Content;
import com.smartitengineering.event.hub.api.Event;
import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author kaisar
 */
public class ContentHelper {

  public Content content;
  public InputStream contentStream;
  public String contentAsString = "is not null";
  //private final Map<Event, String> contentCache = new WeakHashMap<Event, String>();

  public void setContent(Content content) {
    this.content = content;
  }

  private void convert() {
    //contentStream=(InputStream)content;
    if (!content.equals(null)) {
      try {
        contentAsString = IOUtils.toString((InputStream) content);
      }
      catch (Exception ex) {
      }
    }
  }

  public String getContentAsString() {
    convert();
    return this.contentAsString;
  }
}
