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
	private String uri;

	/**
	 * The ticket number used to identify which request this data is associated
	 * with.
	 */
	private long ticketNumber;

	/**
	 * The attributes associated with this URI, if any.
	 */
	private Attribute[] attributes;

	public int getMessageLength() {
		// Message Type, URI length
		int length = 1 + 4;

		// URI bytes
		if (this.uri != null) {
			try {
				length += this.uri.getBytes("UTF-16BE").length;
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
				length += attr.getLength();
			}
		}

		return length;
	}	

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public long getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(long ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public Attribute[] getAttributes() {
		return attributes;
	}

	public void setAttributes(Attribute[] attributes) {
		this.attributes = attributes;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer("Data Response Message (");
		if (this.uri != null) {
			sb.append(this.uri);
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
