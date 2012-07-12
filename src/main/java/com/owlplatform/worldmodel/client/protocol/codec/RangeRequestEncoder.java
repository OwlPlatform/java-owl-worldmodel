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

import com.owlplatform.worldmodel.client.protocol.messages.RangeRequestMessage;

/**
 * Encoder for Range Request messages.
 * @author Robert Moore
 *
 */
public class RangeRequestEncoder implements
		MessageEncoder<RangeRequestMessage> {

	@Override
	public void encode(IoSession session, RangeRequestMessage message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(message.getMessageLength() + 4);
		
		buffer.putInt(message.getMessageLength());
		buffer.put(message.getMessageType());
		buffer.putInt((int)message.getTicketNumber());
		
		byte[] idQueryByte = message.getIdRegex().getBytes("UTF-16BE");
		buffer.putInt(idQueryByte.length);
		buffer.put(idQueryByte);
		
		String[] attributes = message.getAttributeRegexes();
		
		if(attributes == null){
			buffer.putInt(0);
		}
		else{
			buffer.putInt(attributes.length);
			for(String attribute : attributes){
				byte[] attribByte = attribute.getBytes("UTF-16BE");
				buffer.putInt(attribByte.length);
				buffer.put(attribByte);
			}
		}
		
		buffer.putLong(message.getBeginTimestamp());
		buffer.putLong(message.getEndTimestamp());
		
		buffer.flip();
		
		out.write(buffer);
		
		buffer.free();
	}

}
