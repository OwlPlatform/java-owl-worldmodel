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
 * The Snapshot Request message is sent by a client to the World Model in order
 * to request the state of the server at a specific point in time.
 * 
 * <p>
 * From the Wiki:<br />
 * This request queries the state of the world model at some moment in time.
 * This means that any URIs that existed at that time and their most recent
 * attributes before that time are returned in the snapshot. As soon as the
 * world model receives this message it will respond with a Request Ticket
 * message. The world model server will then return 1 URI at a time and will
 * finish by sending a Request Complete message. This request enforces a logical
 * AND between all request attributes - URIs and their attributes are only
 * returned if all requested attributes match non-expired attributes of that URI
 * at the given end time. Logical OR is supported by the POSIX regex in the
 * requested attributes using the grouping (parenthesis) and OR (|) operator.
 * </p>
 * 
 * <a href="http://sourceforge.net/apps/mediawiki/grailrtls/index.php?title=Client-World_Model_protocol">Documentation is available</a> on the project Wiki.
 * 
 * @author Robert Moore
 * 
 */
public class SnapshotRequestMessage extends AbstractRequestMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(SnapshotRequestMessage.class);
	
	/**
	 * Message type value for Snapshot Request messages.
	 */
	public static final byte MESSAGE_TYPE = 1;
	
	private String queryURI;
	
	private String[] queryAttributes;
	
	private long beginTimestamp;
	
	private long endTimestamp;
	
	/**
	 * Returns the message type for the Snapshot Request message.
	 * @return the message type value for the Snapshot Request message.
	 */
	public byte getMessageType(){
		return MESSAGE_TYPE;
	}
	
	/**
	 * Returns the length of the message, in bytes, excluding the message length prefix value.
	 * @return the length of the message, in bytes, excluding the message length prefix value.
	 */
	public int getMessageLength(){
		// Message type, ticket #
		int messageLength = 1 + 4;
		
		// URI length prefix
		messageLength += 4;
		
		if(this.queryURI != null){
			try {
				messageLength += this.queryURI.getBytes("UTF-16BE").length;
			} catch (UnsupportedEncodingException e) {
				log.error("Unable to encode String into UTF-16.");
				e.printStackTrace();
			}
		}
		
		// Number of query attributes length prefix
		messageLength += 4;
		
		// Add length prefix and String length for each attribute
		if(this.queryAttributes != null){
			for(String attrib : this.queryAttributes){
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

	public String getQueryURI() {
		return queryURI;
	}

	public void setQueryURI(String queryURI) {
		this.queryURI = queryURI;
	}

	public String[] getQueryAttributes() {
		return queryAttributes;
	}

	public void setQueryAttributes(String[] queryAttributes) {
		this.queryAttributes = queryAttributes;
	}

	public long getBeginTimestamp() {
		return beginTimestamp;
	}

	public void setBeginTimestamp(long beginTimestamp) {
		this.beginTimestamp = beginTimestamp;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer("Snapshot Request (");
		sb.append(this.queryURI).append(")");
		sb.append(" from ").append(new Date(this.beginTimestamp)).append(" to ").append(new Date(this.endTimestamp)).append(":\n");
		
		if(this.queryAttributes != null){
			for(String attrib : this.queryAttributes){
				sb.append(attrib).append('\n');
			}
		}
		return sb.toString();
	}
}
