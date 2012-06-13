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
 * The Range Request message is sent by a client to the World Model in order
 * to request all URIs created or modified in the World Model server during the specified
 * time range.
 * 
 * <a href="http://sourceforge.net/apps/mediawiki/grailrtls/index.php?title=Client-World_Model_protocol">Documentation is available</a> on the project Wiki.
 * 
 * @author Robert Moore
 * 
 */
public class RangeRequestMessage extends AbstractRequestMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(RangeRequestMessage.class);
	
	/**
	 * Message type value for Range Request messages.
	 */
	public static final byte MESSAGE_TYPE = 2;
	
	private String queryURI;
	
	private String[] queryAttributes = null;
	
	private long beginTimestamp;
	
	private long endTimestamp;
	
	/**
	 * Returns the message type for the Range Request message.
	 * @return the message type value for the Range Request message.
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
		StringBuffer sb = new StringBuffer("Range (");
		sb.append(new Date(this.beginTimestamp)).append("--");
		sb.append(new Date(this.endTimestamp)).append(")\n");
		sb.append("URL: ").append(this.queryURI).append("\n");
		sb.append("Attributes:\n");
		if(this.queryAttributes != null){
			for(String attrib : this.queryAttributes){
				sb.append("\t").append(attrib).append("\n");
			}
		}
		return sb.toString();
	}
}
