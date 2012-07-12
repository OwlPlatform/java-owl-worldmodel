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
package com.owlplatform.worldmodel.client.protocol.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.worldmodel.Attribute;
import com.owlplatform.worldmodel.client.protocol.messages.DataResponseMessage;

/**
 * Encoder for Data Response messages.
 * @author Robert Moore
 *
 */
public class DataResponseEncoder implements MessageEncoder<DataResponseMessage> {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(DataResponseEncoder.class);

	@Override
	public void encode(IoSession session, DataResponseMessage message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(message.getMessageLength() + 4);

		// Message length
		buffer.putInt(message.getMessageLength());
		// Message type
		buffer.put(DataResponseMessage.MESSAGE_TYPE);

		// Message Identifier (this really shouldn't be null)
		if (message.getId() != null) {
			byte[] idBytes = message.getId().getBytes("UTF-16BE");
			buffer.putInt(idBytes.length);
			buffer.put(idBytes);
		} else {
			log.error("Message Identifier is null!");
		}

		// Ticket number
		buffer.putInt((int) message.getTicketNumber());

		Attribute[] attributes = message.getAttributes();
		// Number of attributes
		buffer.putInt(attributes == null ? 0 : attributes.length);
		
		// Each attribute
		if (attributes != null) {
			for (Attribute attr : attributes) {
				buffer.putInt(attr.getAttributeNameAlias());
				buffer.putLong(attr.getCreationDate());
				buffer.putLong(attr.getExpirationDate());
				buffer.putInt(attr.getOriginNameAlias());

			}
		}

		buffer.flip();

		out.write(buffer);

		buffer.free();
	}

}
