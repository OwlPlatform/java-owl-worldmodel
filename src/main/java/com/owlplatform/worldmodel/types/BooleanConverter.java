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

/**
 * Converter for {@code Boolean} objects.
 * @author Robert Moore
 *
 */
public class BooleanConverter implements TypeConverter<Boolean> {

  /**
   * Returns a thread-safe instance of this converter.
   * @return a thread-safe instance of the converter.
   */
  public static BooleanConverter get(){
    return CONVERTER;
  }
  
  /**
   * Singleton instance.
   */
	private static final BooleanConverter CONVERTER = new BooleanConverter();

	/**
	 * Private constructor to prevent external instantiation.
	 */
	private BooleanConverter() {super();}

	@Override
	public Boolean decode(byte[] data) {
		return data[0] == 0 ? Boolean.FALSE : Boolean.TRUE;
	}

	@Override
	public byte[] encode(Boolean object) {
		return new byte[] { object.booleanValue() ? (byte) 0xFF : 0 };
	}

	@Override
	public Boolean decode(String asString) {
		return Boolean.valueOf(asString);
	}

	@Override
	public byte[] encode(String asString) {
		return encode(decode(asString));
	}

	@Override
	public String getTypeName() {
		return "Boolean";
	}

  @Override
  public String asString(Boolean object) {
    return object.toString();
  }

}
