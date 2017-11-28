package com.sp.web.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

/**
 * This class provides an alternative to the JDK's {@link Properties} class. It fixes the design
 * flaw of using inheritance over composition, while keeping up the same APIs as the original class.
 * Keys and values are guaranteed to be of type {@link String}.
 * <p/>
 * This class is not synchronized, contrary to the original implementation.
 * <p/>
 * As additional functionality, this class keeps its properties in a well-defined order. By default,
 * the order is the one in which the individual properties have been added, either through explicit
 * API calls or through reading them top-to-bottom from a properties file.
 * <p/>
 * Also, an optional flag can be set to omit the comment that contains the current date when storing
 * the properties to a properties file.
 * <p/>
 * Currently, this class does not support the concept of default properties, contrary to the
 * original implementation.
 * <p/>
 * <strong>Note that this implementation is not synchronized.</strong> If multiple threads access
 * ordered properties concurrently, and at least one of the threads modifies the ordered properties
 * structurally, it <em>must</em> be synchronized externally. This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the properties.
 * <p/>
 * Note that the actual (and quite complex) logic of parsing and storing properties from and to a
 * stream is delegated to the {@link Properties} class from the JDK.
 *
 * @see Properties
 */

public final class OrderedProperties extends Properties implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  public void store(Writer writer, String comments) throws IOException {
    store0((writer instanceof BufferedWriter) ? (BufferedWriter) writer
        : new BufferedWriter(writer), comments, false);
  }
  
  private void store0(BufferedWriter bw, String comments, boolean escUnicode) throws IOException {
    if (comments != null) {
      writeComments(bw, comments);
    }
    bw.write("#" + new Date().toString());
    bw.newLine();
    synchronized (this) {
      for (Enumeration<?> e = keys(); e.hasMoreElements();) {
        String key = (String) e.nextElement();
        String val = (String) get(key);
        key = saveConvert(key, true, escUnicode);
        /*
         * No need to escape embedded and trailing spaces for value, hence pass false to flag.
         */
        val = saveConvert(val, false, escUnicode);
        if (val.contains("&amp;")) {
          val = val.replace("&amp;", "&");
        }
        bw.write(key + "=" + val);
        bw.newLine();
      }
    }
    bw.flush();
  }
  
  /*
   * Converts unicodes to encoded &#92;uxxxx and escapes special characters with a preceding slash
   */
  private String saveConvert(String theString, boolean escapeSpace, boolean escapeUnicode) {
    int len = theString.length();
    int bufLen = len * 2;
    if (bufLen < 0) {
      bufLen = Integer.MAX_VALUE;
    }
    StringBuffer outBuffer = new StringBuffer(bufLen);
    
    for (int x = 0; x < len; x++) {
      char aChar = theString.charAt(x);
      // Handle common case first, selecting largest block that
      // avoids the specials below
      if ((aChar > 61) && (aChar < 127)) {
        if (aChar == '\\') {
          outBuffer.append('\\');
          outBuffer.append('\\');
          continue;
        }
        outBuffer.append(aChar);
        continue;
      }
      switch (aChar) {
      case ' ':
        if (x == 0 || escapeSpace)
          outBuffer.append('\\');
        outBuffer.append(' ');
        break;
      case '\t':
        outBuffer.append('\\');
        outBuffer.append('t');
        break;
      case '\n':
        outBuffer.append('\\');
        outBuffer.append('n');
        break;
      case '\r':
        outBuffer.append('\\');
        outBuffer.append('r');
        break;
      case '\f':
        outBuffer.append('\\');
        outBuffer.append('f');
        break;
      case '=': // Fall through
      case ':': // Fall through
      case '#': // Fall through
      case '!':
        outBuffer.append('\\');
        outBuffer.append(aChar);
        break;
      default:
        if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
          outBuffer.append('\\');
          outBuffer.append('u');
          outBuffer.append(toHex((aChar >> 12) & 0xF));
          outBuffer.append(toHex((aChar >> 8) & 0xF));
          outBuffer.append(toHex((aChar >> 4) & 0xF));
          outBuffer.append(toHex(aChar & 0xF));
        } else {
          outBuffer.append(aChar);
        }
      }
    }
    return outBuffer.toString();
  }
  
  private static void writeComments(BufferedWriter bw, String comments) throws IOException {
    bw.write("#");
    int len = comments.length();
    int current = 0;
    int last = 0;
    char[] uu = new char[6];
    uu[0] = '\\';
    uu[1] = 'u';
    while (current < len) {
      char c = comments.charAt(current);
      if (c > '\u00ff' || c == '\n' || c == '\r') {
        if (last != current)
          bw.write(comments.substring(last, current));
        if (c > '\u00ff') {
          uu[2] = toHex((c >> 12) & 0xf);
          uu[3] = toHex((c >> 8) & 0xf);
          uu[4] = toHex((c >> 4) & 0xf);
          uu[5] = toHex(c & 0xf);
          bw.write(new String(uu));
        } else {
          bw.newLine();
          if (c == '\r' && current != len - 1 && comments.charAt(current + 1) == '\n') {
            current++;
          }
          if (current == len - 1
              || (comments.charAt(current + 1) != '#' && comments.charAt(current + 1) != '!'))
            bw.write("#");
        }
        last = current + 1;
      }
      current++;
    }
    if (last != current)
      bw.write(comments.substring(last, current));
    bw.newLine();
  }
  
  /**
   * Convert a nibble to a hex character
   * 
   * @param nibble
   *          the nibble to convert.
   */
  private static char toHex(int nibble) {
    return hexDigit[(nibble & 0xF)];
  }
  
  /** A table of hex digits */
  private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
      'B', 'C', 'D', 'E', 'F' };
}
