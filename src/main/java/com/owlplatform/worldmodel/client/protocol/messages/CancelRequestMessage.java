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
 * This message cancels a streaming request using its ticket number. Other
 * requests cannot be canceled as they are executed and completed immediately
 * after receiving the request message before receiving any new messages.
 * 
 * @author Robert Moore
 * 
 */
public class CancelRequestMessage {
	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(CancelRequestMessage.class);

	/**
	 * Message type value for Request Ticket messages.
	 */
	public static final byte MESSAGE_TYPE = 7;

	/**
	 * The unsigned 32 bit integer ticket number of the request to cancel.
	 * Cancellation success is indicated by a Request Complete message being
	 * sent.
	 */
	private long ticketNumber;

	public long getTicketNumber() {
		return ticketNumber & 0xFFFFFFFFL;
	}

	public void setTicketNumber(long ticketNumber) {
		this.ticketNumber = ticketNumber & 0xFFFFFFFFL;
	}

}
