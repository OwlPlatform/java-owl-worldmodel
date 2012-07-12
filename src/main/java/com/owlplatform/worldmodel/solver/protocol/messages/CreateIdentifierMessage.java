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

package com.owlplatform.worldmodel.solver.protocol.messages;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message for creating new Identifiers in the World Model.
 * @author Robert Moore
 *
 */
public class CreateIdentifierMessage {
	
	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(CreateIdentifierMessage.class);
	
	/**
	 * Message type value.
	 */
	public static final byte MESSAGE_TYPE = 5;
	
	/**
	 * The identifier to create.
	 */
	private String identifier;
	
	/**
	 * The time at which it was created.
	 */
	private long creationTime;
	
	/**
	 * The origin/source of the Identifier.
	 */
	private String origin;
	
	/**
	 * The length of this message when encoded according to the Solver-World Model protocol.
	 * @return the length, in bytes, of the encoded form of this message.
	 */
	public int getMessageLength()
	{
		// Message type, Id length
		int length = 1 + 4;
		
		if(this.identifier != null){
			try {
				length += this.identifier.getBytes("UTF-16BE").length;
			} catch (UnsupportedEncodingException e) {
				log.error("Unable to encode to UTF-16BE.");
			}
		}
		
		// Creation time
		length += 8;
		
		if(this.origin != null){
			try {
				length += this.origin.getBytes("UTF-16BE").length;
			} catch (UnsupportedEncodingException e) {
				log.error("Unable to encode to UTF-16BE.");
			}
		}
		
		return length;
	}

	/**
	 * Gets the Identifier being created.
	 * @return the Identifier to create.
	 */
	public String getId() {
		return this.identifier;
	}

	/**
	 * Sets the Identifier to be created.
	 * @param identifier the new Identifier value.
	 */
	public void setId(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Gets the creation time to use in the world model.
	 * @return the creation time of the Identifier
	 */
	public long getCreationTime() {
		return this.creationTime;
	}

	/**
	 * Sets the creation time to use in the world model.
	 * @param creationTime the new creation timestamp.
	 */
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * Gets the origin of this Identifier.
	 * @return the origin value.
	 */
	public String getOrigin() {
		return this.origin;
	}

	/**
	 * Sets the origin value for this Identifier.
	 * @param origin the new origin value.
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}
}
