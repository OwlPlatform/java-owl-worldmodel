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

import com.owlplatform.worldmodel.Attribute;

/**
 * This message contains data requested by the client and is sent by the server
 * in response to a request message.
 */
public class DataResponseMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(DataResponseMessage.class);

	/**
	 * Message type value identifying Data Response messages.
	 */
	public static final byte MESSAGE_TYPE = 8;

	/**
	 * The subject of the data, identified as a UTF-16BE string.
	 */
	private String identifier;

	/**
	 * The ticket number used to identify which request this data is associated
	 * with.
	 */
	private long ticketNumber;

	/**
	 * The attributes associated with this Identifier, if any.
	 */
	private Attribute[] attributes;

	/**
	 * Returns the length of this message when encoded according to the Client-World Model protocol.
	 * @return the length, in bytes, of the encoded form of this message.
	 */
	public int getMessageLength() {
		// Message Type, Identifier length
		int length = 1 + 4;

		// Identifier bytes
		if (this.identifier != null) {
			try {
				length += this.identifier.getBytes("UTF-16BE").length;
			} catch (UnsupportedEncodingException e) {
				log.error("UTF-16BE is unsupported in this environment.");
			}
		}

		// Ticket number
		length += 4;

		// Number of attributes
		length += 4;

		// Each attribute
		if (this.attributes != null) {
			for (Attribute attr : this.attributes) {
				length += attr.getClientLength();
			}
		}

		return length;
	}	

	/**
	 * Returns the identifier for this message.
	 * @return the identifier for this message.
	 */
	public String getId() {
		return this.identifier;
	}

	/**
	 * Sets the identifier for this message.
	 * @param identifier the new identifier for this message.
	 */
	public void setId(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * The ticket number of the request that this message was sent in response to.
	 * @return the ticket number of the request that produced this message.
	 */
	public long getTicketNumber() {
		return this.ticketNumber;
	}

	/**
	 * Sets the ticket number for this message.
	 * @param ticketNumber the new ticket number, the same ticket number as the request that generated this message.
	 */
	public void setTicketNumber(long ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	/**
	 * Returns the attributes in this message.
	 * @return the attributes in this message, or {@code null} if there are none.
	 */
	public Attribute[] getAttributes() {
		return this.attributes;
	}

	/**
	 * Sets the attributes for this message.
	 * @param attributes the new attributes for this message.
	 */
	public void setAttributes(Attribute[] attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer("Data Response Message (");
		if (this.identifier != null) {
			sb.append(this.identifier);
		} else {
			sb.append("NULL");
		}
		sb.append(")\n");
		sb.append("Tix #").append(Long.valueOf(this.ticketNumber))
				.append('\n');
		if (this.attributes != null) {
			for (Attribute attrib : this.attributes) {
				sb.append("\t").append(attrib.toString()).append("\n");
			}
		}

		return sb.toString();
	}

}
