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

public class DataConverter {
  private static final Logger log = LoggerFactory
      .getLogger(DataConverter.class);

  private static final Map<String, TypeConverter> converterNames = new ConcurrentHashMap<String, TypeConverter>();

  static {

    converterNames.put(BooleanConverter.CONVERTER.getTypeName(),
        BooleanConverter.CONVERTER);
    converterNames.put(ByteArrayConverter.CONVERTER.getTypeName(),
        ByteArrayConverter.CONVERTER);
    converterNames.put(DoubleConverter.CONVERTER.getTypeName(),
        DoubleConverter.CONVERTER);
    converterNames.put(IntegerConverter.CONVERTER.getTypeName(),
        IntegerConverter.CONVERTER);
    converterNames.put(LongConverter.CONVERTER.getTypeName(),
        LongConverter.CONVERTER);
    converterNames.put(StringConverter.CONVERTER.getTypeName(),
        StringConverter.CONVERTER);
  }

  private static final Map<String, TypeConverter> attributeConverters = new ConcurrentHashMap<String, TypeConverter>();

  static {
    attributeConverters.put("location.x_offset", DoubleConverter.CONVERTER);
    attributeConverters.put("location.y_offset", DoubleConverter.CONVERTER);
    attributeConverters.put("location.xoffset", DoubleConverter.CONVERTER);
    attributeConverters.put("location.yoffset", DoubleConverter.CONVERTER);
    attributeConverters.put("location.maxx", DoubleConverter.CONVERTER);
    attributeConverters.put("location.maxy", DoubleConverter.CONVERTER);
    attributeConverters.put("location.xstddev", DoubleConverter.CONVERTER);
    attributeConverters.put("location.ystddev", DoubleConverter.CONVERTER);
    attributeConverters.put("location.uri",StringConverter.CONVERTER);
    attributeConverters.put("units", StringConverter.CONVERTER);
    attributeConverters.put("room_num", StringConverter.CONVERTER);
    attributeConverters.put("dimension.width", DoubleConverter.CONVERTER);
    attributeConverters.put("dimension.height", DoubleConverter.CONVERTER);
    attributeConverters.put("dimension.units", StringConverter.CONVERTER);
    attributeConverters.put("closed", BooleanConverter.CONVERTER);
    attributeConverters.put("on", BooleanConverter.CONVERTER);
    attributeConverters.put("image.url", StringConverter.CONVERTER);
    attributeConverters.put("room", StringConverter.CONVERTER);
    attributeConverters.put("channel", IntegerConverter.CONVERTER);
    attributeConverters.put("creation", LongConverter.CONVERTER);
    attributeConverters.put("mobility", BooleanConverter.CONVERTER);
    attributeConverters.put("sensor.mobility", ByteArrayConverter.CONVERTER);
		attributeConverters.put("zip code", StringConverter.CONVERTER);
		attributeConverters.put("empty", BooleanConverter.CONVERTER);
		attributeConverters.put("idle", BooleanConverter.CONVERTER);
		attributeConverters.put("region", StringConverter.CONVERTER);
		
  }

  public static byte[] encodeUri(final String attributeUri, final Object obj) {
    TypeConverter converter = DataConverter.attributeConverters
        .get(attributeUri);
    if (converter == null) {
      log.warn(
          "Unable to find a suitable data converter for attribute URI {}.",
          attributeUri);
      throw new IllegalArgumentException(
          "Unable to find a suitable data converter for attribute URI \""
              + attributeUri + "\".");
    }

    if (obj instanceof String) {
      return converter.encode((String) obj);
    }
    return converter.encode(obj);
  }

  public static Object decodeUri(final String attributeUri,
      final byte[] encodedBytes) {
    TypeConverter converter = DataConverter.attributeConverters
        .get(attributeUri);
    if (converter == null) {
      log.warn(
          "Unable to find a suitable data converter for attribute URI {}.",
          attributeUri);
      throw new IllegalArgumentException(
          "Unable to find a suitable data converter for attribute URI \""
              + attributeUri + "\".");
    }

    return converter.decode(encodedBytes);
  }

  public static boolean hasConverterForURI(final String attributeUri) {
    return DataConverter.attributeConverters.containsKey(attributeUri);
  }

  public static boolean hasConverterForType(final String type) {
    return DataConverter.converterNames.containsKey(type);
  }

  public static String[] getSupportedTypes() {
    return DataConverter.converterNames.keySet().toArray(new String[] {});
  }

  public static TypeConverter putConverter(final String attributeUri,
      final String type) {
    TypeConverter conv = DataConverter.converterNames.get(type);
    if (conv == null) {
      log.warn("Could not find a converter for data type {}.", type);
      throw new IllegalArgumentException(
          "Could not find a converter for data type \"" + type + "\".");
    }
    return DataConverter.attributeConverters.put(attributeUri, conv);
  }

  
  public static String asString(final String attributeUri,
      final byte[] encodedBytes) {
    TypeConverter converter = DataConverter.attributeConverters
        .get(attributeUri);
    if (converter == null) {
      log.warn(
          "Unable to find a suitable data converter for attribute URI {}.",
          attributeUri);
      return NumericUtils.toHexString(encodedBytes);
    }

    return converter.asString(converter.decode(encodedBytes));
  }
}
