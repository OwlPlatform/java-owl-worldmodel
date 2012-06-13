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

package com.owlplatform.worldmodel.solver.protocol.messages;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteAttributeMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(DeleteAttributeMessage.class);
	
	public static final byte MESSAGE_TYPE = 9;
	
	private String uri;
	
	private String attributeName;
	
	private String origin;
	
	public int getMessageLength(){
		// Message type, uri length
		int length = 1 + 4;
		
		if(this.uri != null){
			try {
				length += this.uri.getBytes("UTF-16BE").length;
			} catch (UnsupportedEncodingException e) {
				log.error("Unable to encode to UTF-16BE.");
			}
		}
		
		// Attribute name length
		length += 4;
		
		if(this.attributeName != null){
			try {
				length += this.attributeName.getBytes("UTF-16BE").length;
			} catch (UnsupportedEncodingException e) {
				log.error("Unable to encode to UTF-16BE.");
			}
		}
		
		if(this.origin != null){
			try {
				length += this.origin.getBytes("UTF-16BE").length;
			}
			catch(UnsupportedEncodingException uee){
				log.error("Unable to encode to UTF-16BE.");
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

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
}
