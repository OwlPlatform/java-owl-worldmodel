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

import com.owlplatform.worldmodel.solver.protocol.messages.TypeAnnounceMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.TypeAnnounceMessage.TypeSpecification;

public class TypeAnnounceEncoder implements MessageEncoder<TypeAnnounceMessage> {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(TypeAnnounceEncoder.class);
	
	@Override
	public void encode(IoSession session, TypeAnnounceMessage message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(message.getMessageLength()+4);
		
		// Message length
		buffer.putInt(message.getMessageLength());
		
		// Message type
		buffer.put(TypeAnnounceMessage.MESSAGE_TYPE);
		
		TypeSpecification[] typeSpecs = message.getTypeSpecifications();
		if(typeSpecs != null){
			// Number of Type specifications
			buffer.putInt(typeSpecs.length);
			for(TypeSpecification spec : typeSpecs){
				// Type alias 
				buffer.putInt(spec.getTypeAlias());
				
				// Solution URI name
				if(spec.getUriName() != null){
					byte[] uriNameByte = spec.getUriName().getBytes("UTF-16BE");
					buffer.putInt(uriNameByte.length);
					buffer.put(uriNameByte);
				}
				// No solution URI name
				else{
					buffer.putInt(0);
				}
				// Is transient (boolean as byte)
				buffer.put(spec.getIsTransient() ? (byte)1 : (byte)0);
			}
		}
		// No type specifications
		else{
			buffer.putInt(0);
		}
		
		if(message.getOrigin() != null){
			buffer.put(message.getOrigin().getBytes("UTF-16BE"));
		}
		
		buffer.flip();
		
		out.write(buffer);
		
		buffer.free();
	}

}
