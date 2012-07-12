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

import com.owlplatform.worldmodel.solver.protocol.messages.AttributeAnnounceMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.AttributeAnnounceMessage.AttributeSpecification;

/**
 * Encoder for Attribute Announce messages.
 * @author Robert Moore
 *
 */
public class AttributeAnnounceEncoder implements MessageEncoder<AttributeAnnounceMessage> {

	@Override
	public void encode(IoSession session, AttributeAnnounceMessage message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(message.getMessageLength()+4);
		
		// Message length
		buffer.putInt(message.getMessageLength());
		
		// Message type
		buffer.put(AttributeAnnounceMessage.MESSAGE_TYPE);
		
		AttributeSpecification[] typeSpecs = message.getAttributeSpecifications();
		if(typeSpecs != null){
			// Number of Type specifications
			buffer.putInt(typeSpecs.length);
			for(AttributeSpecification spec : typeSpecs){
				// Type alias 
				buffer.putInt(spec.getAlias());
				
				// Solution attribute name
				if(spec.getAttributeName() != null){
					byte[] nameByte = spec.getAttributeName().getBytes("UTF-16BE");
					buffer.putInt(nameByte.length);
					buffer.put(nameByte);
				}
				// No attribute name
				else{
					buffer.putInt(0);
				}
				// Is transient (boolean as byte)
				buffer.put(spec.getOnDemand() ? (byte)1 : (byte)0);
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
