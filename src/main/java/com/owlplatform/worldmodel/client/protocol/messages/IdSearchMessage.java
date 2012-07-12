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

package com.owlplatform.worldmodel.client.protocol.messages;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A World Model-Client message used to search the World Model for matching Identifier values.
 * @author Robert Moore
 *
 */
public class IdSearchMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(IdSearchMessage.class);
	
	/**	
	 * Message Type value for the Identifier Search message.
	 */
	public static final byte MESSAGE_TYPE = 9;
	
	/**
	 * A regular expression pattern represented as a UTF-16BE String.
	 */
	private String identifierRegex;
	
	/**
	 * Returns the length of this message when encoded according to the Client-World Model protocol.
	 * @return the length, in bytes, of the encoded form of this message.
	 */
	public int getMessageLength(){
		if(this.identifierRegex == null){
			return 1;
		}
		try {
			return this.identifierRegex.getBytes("UTF-16BE").length + 1;
		} catch (UnsupportedEncodingException e) {
			log.error("Unable to decode UTF-16BE String: {}", e);
			return 1;
		}
	}

	/**
	 * Gets the identifier regular expression for this message.
	 * @return the identifier regular expression for this message.
	 */
	public String getIdRegex() {
		return this.identifierRegex;
	}

	/**
	 * Sets the identifier regular expression for this message.
	 * @param idRegex the new identifier regular expression.
	 */
	public void setIdRegex(String idRegex) {
		this.identifierRegex = idRegex;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("Identifier Search (").append(this.identifierRegex).append(")");
		
		return sb.toString();
	}
}
