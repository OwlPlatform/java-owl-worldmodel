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
 * Keep-Alive messages are exchanged across a World Model client connection when no other message has been
 * sent for 30 seconds, or to actively check if a connection is still live.
 * 
 * <a href="http://sourceforge.net/apps/mediawiki/grailrtls/index.php?title=Client-World_Model_protocol">Documentation is available</a> on the project Wiki.
 * 
 * @author Robert Moore
 *
 */
public class KeepAliveMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(KeepAliveMessage.class);
	
	/**
	 * Static reference to an immutable KeepAlive message object.  Since all Keep Alive messages look the same, there's no real 
	 * reason to be able to create multiple instances.
	 */
	public static final KeepAliveMessage MESSAGE = new KeepAliveMessage();
	
	/**
	 * The message type value for the Keep Alive message is 0.
	 */
	public static final byte MESSAGE_TYPE = 0;
	
	/**
	 * Returns the message type byte value for the Keep Alive message.
	 * @return 0, the Keep Alive message type value.
	 */
	public byte getMessageType(){
		return MESSAGE_TYPE;
	}
	
	/**
	 * Returns the length of the Keep Alive message, excluding the 4-byte length prefix.
	 * @return 1, the length of the Keep Alive message.
	 */
	public int getMessageLength() {
		return 1;
	}
	
	@Override
	public String toString() {
		return "Keep-Alive (World Model Client)";
	}
}
