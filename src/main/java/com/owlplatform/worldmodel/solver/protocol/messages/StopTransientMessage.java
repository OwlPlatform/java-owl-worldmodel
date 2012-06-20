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

public class StopTransientMessage {
	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(StopTransientMessage.class);
	
	public static final byte MESSAGE_TYPE = 3;
	
	private TransientRequest[] transientRequests = null;
	
	public static class TransientRequest{
		private static final Logger log = LoggerFactory.getLogger(TransientRequest.class);
		
		private int transientAlias;
		
		private String[] uriPatterns = null;
		
		public int getLength(){
			// Alias, number of patterns
			int length = 4 + 4;
			
			if(this.uriPatterns != null){
				for(String uri : this.uriPatterns){
					try {
						length += (4 + uri.getBytes("UTF-16BE").length);
					} catch (UnsupportedEncodingException e) {
						log.error("Unable to encode to UTF-16BE.");
					}
				}
			}
			return length;
		}

		public int getTransientAlias() {
			return transientAlias;
		}

		public void setTransientAlias(int transientAlias) {
			this.transientAlias = transientAlias;
		}

		public String[] getUriPatterns() {
			return uriPatterns;
		}

		public void setUriPatterns(String[] uriPatterns) {
			this.uriPatterns = uriPatterns;
		}
	}
	
	/**
   * The length of this message when encoded according to the Solver-World Model protocol.
   * @return the length, in bytes, of the encoded form of this message.
   */
	public int getMessageLength(){
		// Message type, number of transients
		int length = 1 + 4;
		if(this.transientRequests != null){
			for(TransientRequest request : this.transientRequests){
				length += request.getLength();
			}
		}
		
		return length;
	}

	public TransientRequest[] getTransientRequests() {
		return transientRequests;
	}

	public void setTransientRequests(TransientRequest[] transientRequests) {
		this.transientRequests = transientRequests;
	}
}
