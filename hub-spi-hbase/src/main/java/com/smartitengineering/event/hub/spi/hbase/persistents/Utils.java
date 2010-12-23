/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.event.hub.spi.hbase.persistents;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.commons.codec.binary.StringUtils;

/**
 *
 * @author imyousuf
 */
public final class Utils {

  private Utils() {
  }

  public static String readStringInUTF8(DataInput in) throws IOException, UnsupportedEncodingException {
    int allocationBlockSize = 2000;
    int capacity = allocationBlockSize;
    int length = 0;
    ByteBuffer buffer = ByteBuffer.allocate(allocationBlockSize);
    boolean notEof = true;
    while (notEof) {
      try {
        buffer.put(in.readByte());
        if (++length >= capacity) {
          capacity += allocationBlockSize;
          buffer.limit(capacity);
        }
      }
      catch (EOFException ex) {
        notEof = false;
      }
    }
    String string = StringUtils.newStringUtf8(Arrays.copyOf(buffer.array(), length));
    return string;
  }
}
