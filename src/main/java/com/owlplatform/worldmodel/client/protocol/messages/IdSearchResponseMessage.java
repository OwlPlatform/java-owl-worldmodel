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
 * The message sent in response to a Identifier Search Request. It may contain zero or
 * more Identifier String values that matched the requested Identifier regular expression.
 * 
 * @author Robert Moore
 * 
 */
public class IdSearchResponseMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(IdSearchResponseMessage.class);

	/**
	 * Message Type byte value for the Identifier Search Response message.
	 */
	public static final byte MESSAGE_TYPE = 10;

	/**
	 * Array of Ids that matched the Identifier Search message's regular expression.
	 */
	private String[] matchingIds = null;

	/**
	 * Gets the length of this message when encoded according to the Client-World Model protocol.
	 * @return the length, in bytes, of the encoded form of this message
	 */
	public int getMessageLength() {
		int length = 1;

		if (this.matchingIds != null) {
			for (String id : this.matchingIds) {
				try {
					length += 4;
					length += id.getBytes("UTF-16BE").length;
				} catch (UnsupportedEncodingException uee) {
					log.error("Unable to encode UTF-16BE String: {}", uee);
				}
			}
		}
		return length;
	}

	/**
	 * Returns the Identifier values that matched the search request.
	 * @return the matching identifiers.
	 */
	public String[] getMatchingIds() {
		return this.matchingIds;
	}

	/**
	 * Sets the Identifier values that matched the search request.
	 * @param matchingIds the matching identifiers.
	 */
	public void setMatchingIds(String[] matchingIds) {
		this.matchingIds = matchingIds;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Identifier Search Response:");
		if (this.matchingIds != null) {
			for (String s : this.matchingIds) {
				sb.append(s).append("\n");
			}
		}

		return sb.toString();
	}
}
