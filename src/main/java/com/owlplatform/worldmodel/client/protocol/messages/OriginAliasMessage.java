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
 * This message specifies aliases for some origin names to save on bandwidth.
 * Each attribute has a UTF16 big endian name and alias. Sending these
 * repeatedly would be a waste of bandwidth so aliases will be used to refer to
 * the strings. Any time the world model would send an attribute with a name or
 * origin that is previously unseen by a client an attribute alias or origin
 * alias message will be sent to define aliases for the new attribute names and
 * origins.
 * 
 * @author Robert Moore
 * 
 */
public class OriginAliasMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(OriginAliasMessage.class);

	/**
	 * Message type for the Origin Alias message.
	 */
	public static final byte MESSAGE_TYPE = 5;

	/**
	 * The origin aliases contained in this message.
	 */
	protected OriginAlias[] aliases;

	/**
	 * Returns the length of this message when encoded according to the Client-World Model protocol.
	 * @return the length, in bytes, of the encoded form of this message.
	 */
	public int getMessageLength() {
		// Message type
		int messageLength = 1;

		// Number of aliases
		messageLength += 4;

		if (this.aliases != null) {
			for (OriginAlias alias : this.aliases) {
				// Alias number, name length
				messageLength += 8;
				try {
					messageLength += alias.origin.getBytes("UTF-16BE").length;
				} catch (UnsupportedEncodingException e) {
					log.error("Unable to encode strings into UTF-16.");
					e.printStackTrace();
				}
			}
		}

		return messageLength;
	}

	/**
	 * Returns the message type value for this message ({@link #MESSAGE_TYPE}).
	 * @return the message type of this message.
	 */
	public byte getMessageType() {
		return MESSAGE_TYPE;
	}

	/**
	 * Returns the origin aliases for this message.
	 * @return the origin aliases in this message, or {@code null} if there are none.
	 */
	public OriginAlias[] getAliases() {
		return this.aliases;
	}

	/**
	 * Sets the origin aliases for this message.
	 * @param aliases the new origin aliases for this message.
	 */
	public void setAliases(OriginAlias[] aliases) {
		this.aliases = aliases;
	}

	/**
	 * Simple class to encapsulate an origin alias binding.
	 * @author Robert Moore
	 *
	 */
	public static class OriginAlias {
	  /**
	   * The numeric alias value.
	   */
		public final int aliasNumber;
		/**
		 * The origin name.
		 */
		public final String origin;

		/**
		 * Creates a new origin alias object using the provided alias value and origin name.
		 * @param aliasNumber the alias value.
		 * @param origin the origin name.
		 */
		public OriginAlias(int aliasNumber, String origin) {
			this.aliasNumber = aliasNumber;
			this.origin = origin;
		}
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer("Origin Alias\n");
		for(OriginAlias alias : this.aliases){
			sb.append(alias.aliasNumber).append("->").append(alias.origin).append('\n');
		}
		return sb.toString();
	}

}
