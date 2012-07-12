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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.common.util.NumericUtils;

/**
 * Convenience class for converting Attribute values between their binary
 * representation and Java objects. Attribute names must be mapped to their
 * types using {@link #putConverter(String, String)} before
 * {@link #decode(String, byte[])} can be called.
 * 
 * @author Robert Moore
 * 
 */
public class DataConverter {
  /**
   * Logger for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(DataConverter.class);

  /**
   * Converter names to determine which types can be converted.
   */
  private static final Map<String, TypeConverter<?>> converterClasses = new ConcurrentHashMap<String, TypeConverter<?>>();

  static {

    converterClasses.put(BooleanConverter.get().getTypeName(),
        BooleanConverter.get());
    converterClasses.put(ByteArrayConverter.get().getTypeName(),
        ByteArrayConverter.get());
    converterClasses.put(DoubleConverter.get().getTypeName(),
        DoubleConverter.get());
    converterClasses.put(IntegerConverter.get().getTypeName(),
        IntegerConverter.get());
    converterClasses.put(LongConverter.get().getTypeName(),
        LongConverter.get());
    converterClasses.put(StringConverter.get().getTypeName(),
        StringConverter.get());
  }

  /**
   * Mapped converters for different Attribute names.
   */
  private static final Map<String, TypeConverter<?>> attributeConverters = new ConcurrentHashMap<String, TypeConverter<?>>();

  static {
    attributeConverters.put("location.x_offset", DoubleConverter.get());
    attributeConverters.put("location.y_offset", DoubleConverter.get());
    attributeConverters.put("location.xoffset", DoubleConverter.get());
    attributeConverters.put("location.yoffset", DoubleConverter.get());
    attributeConverters.put("location.maxx", DoubleConverter.get());
    attributeConverters.put("location.maxy", DoubleConverter.get());
    attributeConverters.put("location.minx", DoubleConverter.get());
    attributeConverters.put("location.miny", DoubleConverter.get());
    attributeConverters.put("location.xstddev", DoubleConverter.get());
    attributeConverters.put("location.ystddev", DoubleConverter.get());
    attributeConverters.put("location.uri", StringConverter.get());
    attributeConverters.put("units", StringConverter.get());
    attributeConverters.put("room_num", StringConverter.get());
    attributeConverters.put("dimension.width", DoubleConverter.get());
    attributeConverters.put("dimension.height", DoubleConverter.get());
    attributeConverters.put("dimension.units", StringConverter.get());
    attributeConverters.put("closed", BooleanConverter.get());
    attributeConverters.put("on", BooleanConverter.get());
    attributeConverters.put("image.url", StringConverter.get());
    attributeConverters.put("room", StringConverter.get());
    attributeConverters.put("channel", IntegerConverter.get());
    attributeConverters.put("creation", LongConverter.get());
    attributeConverters.put("mobility", BooleanConverter.get());
    attributeConverters.put("sensor.mobility", ByteArrayConverter.get());
    attributeConverters.put("zip code", StringConverter.get());
    attributeConverters.put("empty", BooleanConverter.get());
    attributeConverters.put("idle", BooleanConverter.get());
    attributeConverters.put("region", StringConverter.get());
    attributeConverters.put("fingerprinting on", BooleanConverter.get());
  }

  /**
   * Encodes attribute data into the standard binary representation for the
   * type.
   * @param attributeName the name of the attribute
   * @param obj the object to encode
   * @return the encoded form of the object as a byte[].
   */
  @SuppressWarnings({ "rawtypes", "unchecked"})
  public static byte[] encode(final String attributeName, final Object obj) {
    TypeConverter converter = DataConverter.attributeConverters
        .get(attributeName);
    if (converter == null) {
      log.warn(
          "Unable to find a suitable data converter for attribute {}.",
          attributeName);
      throw new IllegalArgumentException(
          "Unable to find a suitable data converter for attribute \""
              + attributeName + "\".");
    }

    if (obj instanceof String) {
      return converter.encode((String) obj);
    }
    return converter.encode(obj);
  }

  /**
   * Decodes a byte[] into the Java type mapped to the Attribute name provided.
   * @param attributeName the name of the Attribute being decoded.
   * @param encodedBytes the encoded form of the Attribute data.
   * @return an Object of the mapped type, decoded from the byte[].
   */
  public static Object decode(final String attributeName,
      final byte[] encodedBytes) {
    @SuppressWarnings("rawtypes")
    TypeConverter converter = DataConverter.attributeConverters
        .get(attributeName);
    if (converter == null) {
      log.warn(
          "Unable to find a suitable data converter for attribute {}.",
          attributeName);
      throw new IllegalArgumentException(
          "Unable to find a suitable data converter for attribute \""
              + attributeName + "\".");
    }

    return converter.decode(encodedBytes);
  }

  /**
   * Returns true if a {@code TypeConverter} has been mapped for the Attribute name.
   * @param attributeName the name of the Attribute
   * @return {@code true} if there exists a mapping for the attribute name, else {@code false}.
   */
  public static boolean hasConverterForAttribute(final String attributeName) {
    return DataConverter.attributeConverters.containsKey(attributeName);
  }

  /**
   * Determines if there is a mapped converter for the Java type name.
   * @param type the common name of the java type.
   * @return {@code true} if a converter mapping exists, else {@code false}.
   * @see #getSupportedTypes()
   */
  public static boolean hasConverterForType(final String type) {
    return DataConverter.converterClasses.containsKey(type);
  }

  /**
   * Returns an array of Java type names that are currently supported.
   * @return an array of Java type names that are currently supported
   */
  public static String[] getSupportedTypes() {
    return DataConverter.converterClasses.keySet().toArray(new String[] {});
  }

  /**
   * Maps a converter for an Attribute name.
   * @param attributeName the name of the Attribute.
   * @param type the type of the Attribute (<i>e.g.</i>, "String", "Integer", "byte[]")
   * @return the converter that was mapped
   * @throws IllegalArgumentException if no converter is available for the type
   * @see #getSupportedTypes()
   */
  public static TypeConverter<?> putConverter(final String attributeName,
      final String type) {
    TypeConverter<?> conv = DataConverter.converterClasses.get(type);
    if (conv == null) {
      log.warn("Could not find a converter for data type {}.", type);
      throw new IllegalArgumentException(
          "Could not find a converter for data type \"" + type + "\".");
    }
    return DataConverter.attributeConverters.put(attributeName, conv);
  }

  /**
   * Decodes the Attribute data into its String form.  A convenience method
   * for components that want to print or otherwise provide Attribute values as 
   * Strings.  If no appropriate converter is mapped, then returns {@code encodedBytes}
   * as a hexadecimal string
   * @param attributeName the name of the attribute
   * @param encodedBytes the encoded form of the attribute data
   * @return the attribute data, decoded to a String representation.
   * @see NumericUtils#toHexString(byte)
   */
  @SuppressWarnings("unchecked")
  public static String asString(final String attributeName,
      final byte[] encodedBytes) {
    @SuppressWarnings("rawtypes")
    TypeConverter converter = DataConverter.attributeConverters
        .get(attributeName);
    if (converter == null) {
      log.warn(
          "Unable to find a suitable data converter for attribute {}.",
          attributeName);
      return NumericUtils.toHexString(encodedBytes);
    }

    return converter.asString(converter.decode(encodedBytes));
  }
}
