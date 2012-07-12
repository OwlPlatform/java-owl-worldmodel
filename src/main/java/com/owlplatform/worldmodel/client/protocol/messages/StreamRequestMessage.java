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
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Stream Request message is sent by a client to the World Model in order to
 * request all Identifiers and attributes created or updated after a specified point in time.
 * The World Model server will then stream all requested data to the requesting client
 * as it arrives from solvers.
 * 
 * 
 * @author Robert Moore
 * 
 */
public class StreamRequestMessage extends AbstractRequestMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(StreamRequestMessage.class);

	/**
	 * Message type value for Stream Request messages.
	 */
	public static final byte MESSAGE_TYPE = 3;

	/**
	 * Regular expression to match identifiers.
	 */
	private String identifierRegex;

	/**
	 * Regular expressions to match attributes.
	 */
	private String[] attributeRegexes;

	/**
	 * Timestamp of the earliest attribute values to stream.
	 */
	private long beginTimestamp;

	/**
	 * The minimum time between data value changes sent by the World Model server, specified in milliseconds.
	 */
	private long updateInterval;

	/**
	 * Gets the update interval for this request.
	 * @return the update interval in milliseconds.
	 */
	public long getUpdateInterval() {
		return this.updateInterval;
	}

	/**
	 * Sets the update interval for this request.
	 * @param updateInterval the new update interval, in milliseconds.
	 */
	public void setUpdateInterval(long updateInterval) {
		this.updateInterval = updateInterval;
	}

	/**
	 * Returns the message type for the Stream Request message.
	 * 
	 * @return the message type value for the Range Request message.
	 */
	public byte getMessageType() {
		return MESSAGE_TYPE;
	}

	/**
	 * Returns the length of the message, in bytes, excluding the message length
	 * prefix value.
	 * 
	 * @return the length of the message, in bytes, excluding the message length
	 *         prefix value.
	 */
	public int getMessageLength() {
		// Message type, ticket #
		int messageLength = 1 + 4;

		// Identifiers length prefix
		messageLength += 4;

		if (this.identifierRegex != null) {
			try {
				messageLength += this.identifierRegex.getBytes("UTF-16BE").length;
			} catch (UnsupportedEncodingException e) {
				log.error("Unable to encode String into UTF-16.");
				e.printStackTrace();
			}
		}

		// Number of query attributes length prefix
		messageLength += 4;

		// Add length prefix and String length for each attribute
		if (this.attributeRegexes != null) {
			for (String attrib : this.attributeRegexes) {
				messageLength += 4;
				try {
					messageLength += attrib.getBytes("UTF-16BE").length;
				} catch (UnsupportedEncodingException e) {
					log.error("Unable to encode String into uTF-16");
					e.printStackTrace();
				}
			}
		}

		// Begin and end timestamps
		messageLength += 16;

		return messageLength;
	}

	/**
	 * Gets the identifier regular expression for this request.
	 * @return the identifier regular expression.
	 */
	public String getIdRegex() {
		return this.identifierRegex;
	}

	/**
	 * Sets the identifier regular expression for this request.
	 * @param idRegex the new identifier regular expression.
	 */
	public void setIdRegex(String idRegex) {
		this.identifierRegex = idRegex;
	}

	/**
	 * Gets the attribute regular expressions for this request.
	 * @return the attribute regular expressions for this request.
	 */
	public String[] getAttributeRegexes() {
		return this.attributeRegexes;
	}

	/**
	 * Sets the attribute regular expressions for this request.
	 * @param attributeRegexes the new attribute regular expressions.
	 */
	public void setAttributeRegexes(String[] attributeRegexes) {
		this.attributeRegexes = attributeRegexes;
	}

	/**
	 * Gets the begin timestamp for this request.
	 * @return the earliest timestamp of attribute values to stream. 
	 */
	public long getBeginTimestamp() {
		return this.beginTimestamp;
	}

	/**
	 * Sets the begin timestamp for this request.
	 * @param beginTimestamp the new beginning timestamp.
	 */
	public void setBeginTimestamp(long beginTimestamp) {
		this.beginTimestamp = beginTimestamp;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer("Stream Request (");
		sb.append(this.identifierRegex).append(")");
		sb.append(" from ").append(new Date(this.beginTimestamp)).append(" every ").append(this.updateInterval).append(" ms:\n");
		
		if(this.attributeRegexes != null){
			for(String attrib : this.attributeRegexes){
				sb.append(attrib).append('\n');
			}
		}
		return sb.toString();
	}
}
