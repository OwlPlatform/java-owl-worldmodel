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

import java.io.UnsupportedEncodingException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Attribute converter for UTF16-BE strings.
 * @author Robert Moore
 *
 */
public class StringConverter implements TypeConverter<String> {

  /**
   * Logger for this class.
   */
	private static final Logger log = LoggerFactory.getLogger(StringConverter.class);

	/**
   * Returns a thread-safe instance of this converter.
   * @return a thread-safe instance of the converter.
   */
	public static StringConverter get(){
	  return THE_ONE;
	}
	
	/**
	 * Singleton instance of the converter.
	 */
	private static final StringConverter THE_ONE = new StringConverter();
	
	/**
	 * Private constructor to prevent external instantiation.
	 */
	private StringConverter(){super();}
	
	@Override
	public String decode(byte[] data) {
		try {
			return new String(data,"UTF-16BE");
		} catch (UnsupportedEncodingException e) {
			log.error("Unable to decode UTF-16BE strings.",e);
			return null;
		}
	}

	@Override
	public byte[] encode(String object) {
		try {
			return object.getBytes("UTF-16BE");
		} catch (UnsupportedEncodingException e) {
			log.error("Unable to decode UTF-16BE strings.",e);
			return null;
		}
	}

	@Override
	public String getTypeName(){
		return "String";
	}

	@Override
	public String decode(String asString) {
		return asString;
	}

  @Override
  public String asString(String object) {
    return object;
  }
	
}
