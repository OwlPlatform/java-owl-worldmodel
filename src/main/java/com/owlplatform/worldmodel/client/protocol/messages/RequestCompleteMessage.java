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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This message is sent to a client to indicate that a non-streaming request has
 * been completed or a streaming request has been canceled with the Cancel
 * Request message.
 * 
 * @author Robert Moore
 * 
 */
public class RequestCompleteMessage {
	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(RequestCompleteMessage.class);

	/**
	 * Message type value for Request Complete messages.
	 */
	public static final byte MESSAGE_TYPE = 6;

	/**
	 * The unsigned 32 bit integer ticket number of this request.
	 */
	private long ticketNumber;
	
	public int getMessageLength(){
		return 1 + 4;
	}

	public long getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(int ticketNumber) {
		this.ticketNumber = ticketNumber & 0xFFFFFFFF;
	}
	
	public void setTicketNumber(long ticketNumber){
		this.ticketNumber = ticketNumber;
	}

}
