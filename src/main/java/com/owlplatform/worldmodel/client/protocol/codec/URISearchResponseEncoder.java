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

import com.owlplatform.worldmodel.client.protocol.messages.IdSearchResponseMessage;

public class URISearchResponseEncoder implements
		MessageEncoder<IdSearchResponseMessage> {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(URISearchResponseEncoder.class);
	
	@Override
	public void encode(IoSession session, IdSearchResponseMessage message,
			ProtocolEncoderOutput out) throws Exception {
		int prefixLength = message.getMessageLength();
		
		IoBuffer buffer = IoBuffer.allocate(prefixLength+4);
		
		buffer.putInt(prefixLength);
		buffer.put(IdSearchResponseMessage.MESSAGE_TYPE);
		
		if(message.getMatchingIds() != null){
			for(String uri : message.getMatchingIds()){
				byte[] uriByte = uri.getBytes("UTF-16BE");
				buffer.putInt(uriByte.length);
				buffer.put(uriByte);
			}
		}
		
		buffer.flip();
		
		out.write(buffer);
		
		buffer.free();
		
	}

}
