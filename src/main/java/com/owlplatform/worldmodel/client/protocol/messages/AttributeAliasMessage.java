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
 * The Attribute Alias message is sent from the World Model server to the client
 * to provide efficient representations of attribute names in Data Response
 * messages. Each UTF-16 Identifier value is bound to a 4-byte integer alias to avoid
 * repeatedly sending long Identifier values.
 * 
 * @author Robert Moore
 * 
 */
public class AttributeAliasMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(AttributeAliasMessage.class);

	/**
	 * Message type for the Attribute Alias message.
	 */
	public static final byte MESSAGE_TYPE = 4;

	/**
	 * The attribute aliases contained in this message.
	 */
	protected AttributeAlias[] aliases = null;

	/**
	 * Returns the length of this message as encoded according to the
	 * Client-World Model protocol.
	 * @return the length of the encoded form of this message, in bytes.
	 */
	public int getMessageLength() {
		// Message type
		int messageLength = 1;

		// Number of aliases
		messageLength += 4;

		if (this.aliases != null) {
			for (AttributeAlias alias : this.aliases) {
				// Alias number, name length
				messageLength += 8;
				try {
					messageLength += alias.attributeName.getBytes("UTF-16BE").length;
				} catch (UnsupportedEncodingException e) {
					log.error("Unable to encode strings into UTF-16.");
					e.printStackTrace();
				}
			}
		}

		return messageLength;
	}

	/**
	 * Returns the type value for this message.
	 * @return {@link #MESSAGE_TYPE}.
	 */
	public byte getMessageType() {
		return MESSAGE_TYPE;
	}

	/**
	 * Returns the array of attribute aliases contained in this message.
	 * @return the attribute aliases for this message, or {@code null} if there are none.
	 */
	public AttributeAlias[] getAliases() {
		return this.aliases;
	}

	/**
	 * Sets the new set of attribute aliases for this message.
	 * @param aliases the new attribute aliases for this message.
	 */
	public void setAliases(AttributeAlias[] aliases) {
		this.aliases = aliases;
	}

	/**
	 * Simple class for binding attribute names to alias values.
	 * @author Robert Moore
	 *
	 */
	public static class AttributeAlias {
	  /**
	   * The alias value for the attribute.
	   */
		public final int aliasNumber;
		/**
		 * The attribute name.
		 */
		public final String attributeName;

		/**
		 * Creates a new {@code AttributeAlias} with the provided alias value and attribute name.
		 * @param aliasNumber the alias value for the attribute.
		 * @param attributesName the attribute name.
		 */
		public AttributeAlias(int aliasNumber, String attributesName) {
			this.aliasNumber = aliasNumber;
			this.attributeName = attributesName;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Attribute Alias Message\n");
		if (this.aliases != null) {
			for (AttributeAlias alias : this.aliases) {
				sb.append(Integer.valueOf(alias.aliasNumber)).append("->")
						.append(alias.attributeName).append('\n');
			}
		}
		return sb.toString();
	}
}
