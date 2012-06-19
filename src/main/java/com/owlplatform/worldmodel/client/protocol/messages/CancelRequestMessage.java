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


/**
 * This message cancels a request using its ticket number. Not all requests
 * can be cancelled, and not all cancel requests can be honored.  For instance, a snapshot
 * request may not be able to be cancelled if it was already fulfilled by the world model
 * and in-flight across the network.
 * 
 * @author Robert Moore
 * 
 */
public class CancelRequestMessage {

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

	/**
	 * Returns the 32-bit unsigned integer ticket number.
	 * @return the 32-bit unsigned integer ticket number
	 */
	public long getTicketNumber() {
		return this.ticketNumber & 0xFFFFFFFFL;
	}

	/**
	 * Sets the 32-bit unsigned integer ticket number for this message.
	 * @param ticketNumber the new ticket number for this message.
	 */
	public void setTicketNumber(long ticketNumber) {
		this.ticketNumber = ticketNumber & 0xFFFFFFFFL;
	}

}
