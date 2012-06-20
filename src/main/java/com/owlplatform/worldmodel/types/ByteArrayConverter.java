/*
 * Owl Platform World Model Library for Java
 * Copyright (C) 2012 Robert Moore and the Owl Platform
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.owlplatform.worldmodel.types;

import com.owlplatform.common.util.NumericUtils;

/**
 * Converter for {@code byte[]} objects.  This is really more of a convenience for
 * applications that make use of {@link DataConverter} and don't handle byte[] separately.
 * @author Robert Moore
 *
 */
public class ByteArrayConverter implements TypeConverter<byte[]> {
  /**
   * Returns a thread-safe instance of this converter.
   * @return a thread-safe instance of the converter.
   */
  public static ByteArrayConverter get(){
    return CONVERTER;
  }
  
  /**
   * Singleton instance.
   */
  private static final ByteArrayConverter CONVERTER = new ByteArrayConverter();

  /**
   * Private constructor to prevent external instantiation.
   */
  private ByteArrayConverter() {
    super();
  }

  @Override
  public byte[] decode(byte[] data) {
    return data;
  }

  @Override
  public byte[] encode(byte[] object) {
    return object;
  }

  /**
   * Returns a byte[] from a hexadecimal string with no leading identifiers. For
   * example, "0002F" and "2F" are equivalent. "0x234" would be invalid.
   */
  @Override
  public byte[] decode(String asString) {
    return hexStringToByteArray(asString.length() % 2 == 1 ? "0" + asString
        : asString);
  }

  /**
   * From http://stackoverflow.com/questions/140131/convert-a-string-
   * representation-of-a-hex-dump-to-a-byte-array-using-java by Dave L.
   * (http://stackoverflow.com/users/3093/dave-l)
   * 
   * @param s
   *          a String to convert to a byte[].
   * @return a byte[] containing the same value as the hexadecimal String
   *         parameter.
   */
  public static byte[] hexStringToByteArray(final String s) {
    int len = s.length();
    byte[] data = new byte[(int)Math.ceil(len / 2f)];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4));
      if (i+1<len) {
        data[i / 2] += (byte) Character.digit(s.charAt(i + 1), 16);
      }
    }
    return data;
  }

  @Override
  public byte[] encode(String asString) {
    return encode(decode(asString));
  }

  @Override
  public String getTypeName() {
    return "byte[]";
  }

  @Override
  public String asString(byte[] object) {
    return NumericUtils.toHexString(object);
  }

}
