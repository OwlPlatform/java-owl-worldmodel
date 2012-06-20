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
 * Interface for converting between primitives/objects and their representation
 * as Attribute values in the Owl Platform world model.
 * @author Robert Moore
 *
 * @param <T> some type to convert.
 */
public interface TypeConverter <T>{
	
	/**
	 * Decodes a byte[] into the specified data type.
	 * @param data the object's value as a byte[].
	 * @return a new object of the specified type containing the same value.
	 */
	T decode(final byte[] data);
	
	/**
	 * Encodes an object to a byte[].
	 * @param object the object to encode.
	 * @return a new byte[] containing the encoded form of the object.
	 */
	byte[] encode(final T object);
	
	/**
	 * Decodes a String into the specified data type.
	 * @param asString the object's value as a String
	 * @return a new object of the specified type containing the same value.
	 */
	T decode(final String asString);
	
	/**
	 * Encodes a String-encoded form of an object into a byte[]-encoded form.
	 * @param asString the object's value as a String.
	 * @return the object's value as a byte[].
	 */
	byte[] encode(final String asString);

	/**
	 * Returns a human-readable name for the type that this converter handles.
	 * @return a human-readable name for the type of this converter.
	 */
	String getTypeName();
	
	/**
	 * Returns a String representation for the object provided.
	 * @param object the object to parse as a String.
	 * @return a String representing the object.
	 */
	String asString(final T object);
}
