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
 * The message sent in response to a URI Search Request. It may contain zero or
 * more URI String values that matched the requested URI regular expression.
 * 
 * @author Robert Moore
 * 
 */
public class URISearchResponseMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(URISearchResponseMessage.class);

	/**
	 * Message Type byte value for the URI Search Response message.
	 */
	public static final byte MESSAGE_TYPE = 10;

	/**
	 * Array of URIs that matched the URI Search message's regular expression.
	 */
	private String[] matchingUris = null;

	public int getMessageLength() {
		int length = 1;

		if (this.matchingUris != null) {
			for (String uri : this.matchingUris) {
				try {
					length += 4;
					length += uri.getBytes("UTF-16BE").length;
				} catch (UnsupportedEncodingException uee) {
					log.error("Unable to encode UTF-16BE String: {}", uee);
				}
			}
		}
		return length;
	}

	public String[] getMatchingUris() {
		return matchingUris;
	}

	public void setMatchingUris(String[] matchingUris) {
		this.matchingUris = matchingUris;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("URI Search Response:");
		if (this.matchingUris != null) {
			for (String s : this.matchingUris) {
				sb.append(s).append("\n");
			}
		}

		return sb.toString();
	}
}
