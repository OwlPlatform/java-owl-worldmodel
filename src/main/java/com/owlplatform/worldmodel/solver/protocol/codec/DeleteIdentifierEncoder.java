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

package com.owlplatform.worldmodel.solver.protocol.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.owlplatform.worldmodel.solver.protocol.messages.DeleteIdentifierMessage;

/**
 * Encoder for Delete Identifier messages.
 * @author Robert Moore
 *
 */
public class DeleteIdentifierEncoder implements MessageEncoder<DeleteIdentifierMessage> {

	@Override
	public void encode(IoSession session, DeleteIdentifierMessage message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(message.getMessageLength()+4);
		
		// Message length
		buffer.putInt(message.getMessageLength());
		// Message type
		buffer.put(DeleteIdentifierMessage.MESSAGE_TYPE);
		
		// URI to delete
		byte[] uriBytes = message.getId().getBytes("UTF-16BE");
		buffer.putInt(uriBytes.length);
		buffer.put(uriBytes);
		
		// Origin
		byte[] originBytes = message.getOrigin().getBytes("UTF-16BE");
		buffer.put(originBytes);
		
		buffer.flip();
		
		out.write(buffer);
		
		buffer.free();
	}

}
