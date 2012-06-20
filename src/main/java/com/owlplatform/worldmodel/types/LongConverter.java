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

import java.nio.ByteBuffer;

/**
 * Converter for {@code Long} objects.
 * @author Robert Moore
 *
 */
public class LongConverter implements TypeConverter<Long>{

  /**
   * Returns a thread-safe instance of this converter.
   * @return a thread-safe instance of the converter.
   */
  public static LongConverter get(){
    return THE_ONE;
  }
  
  /**
   * Singleton instance
   */
	private static final LongConverter THE_ONE = new LongConverter();
	
	/**
	 * Private constructor to prevent external instantiation.
	 */
	private LongConverter(){super();}
	
	@Override
	public Long decode(byte[] data) {
		ByteBuffer buff = ByteBuffer.wrap(data);
		return Long.valueOf(buff.getLong());
	}

	@Override
	public byte[] encode(Long object) {
		ByteBuffer buff = ByteBuffer.allocate(8);
		buff.putLong(object.longValue());
		return buff.array();
	}

	@Override
	public String getTypeName(){
		return "Long";
	}

	@Override
	public Long decode(String asString) {
		return Long.valueOf(asString);
	}

	@Override
	public byte[] encode(String asString) {
		return this.encode(this.decode(asString));
	}

  @Override
  public String asString(Long object) {
    return object.toString();
  }
	
}
