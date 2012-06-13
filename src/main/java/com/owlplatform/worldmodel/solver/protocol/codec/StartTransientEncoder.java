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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.worldmodel.solver.protocol.messages.StartTransientMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StartTransientMessage.TransientRequest;

public class StartTransientEncoder implements
		MessageEncoder<StartTransientMessage> {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(StartTransientEncoder.class);
	
	@Override
	public void encode(IoSession session, StartTransientMessage message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(message.getMessageLength()+4);
		
		// Message length
		buffer.putInt(message.getMessageLength());
		
		// Message type
		buffer.put(StartTransientMessage.MESSAGE_TYPE);
		
		if(message.getTransientRequests() != null){
			// Number of transient requests
			buffer.putInt(message.getTransientRequests().length);
			for(TransientRequest request : message.getTransientRequests()){
				// Transient Alias
				buffer.putInt(request.getTransientAlias());
				if(request.getUriPatterns()!= null){
					// Number of URI patterns
					buffer.putInt(request.getUriPatterns().length);
					for(String uriPattern : request.getUriPatterns()){
						byte[] uriPatternByte = uriPattern.getBytes("UTF-16BE");
						buffer.putInt(uriPatternByte.length);
						buffer.put(uriPatternByte);
					}
				}
				// No URI patterns
				else{
					buffer.putInt(0);
				}
			}
		}
		// No transient requests
		else{
			buffer.putInt(0);
		}
		
		buffer.flip();
		
		out.write(buffer);
		
		buffer.free();

	}

}
