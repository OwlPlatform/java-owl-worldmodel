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

import com.owlplatform.worldmodel.client.protocol.messages.AttributeAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.AttributeAliasMessage.AttributeAlias;

/**
 * Encoder for Attribute Alias messages.
 * @author Robert Moore
 *
 */
public class AttributeAliasEncoder implements MessageEncoder<AttributeAliasMessage>{
	
	@Override
	public void encode(IoSession session, AttributeAliasMessage message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(message.getMessageLength() + 4);

		buffer.putInt(message.getMessageLength());
		buffer.put(AttributeAliasMessage.MESSAGE_TYPE);
		AttributeAlias[] aliases = message.getAliases();
		if(aliases != null){
			buffer.putInt(aliases.length);
			for(AttributeAlias alias : aliases){
				buffer.putInt(alias.aliasNumber);
				byte[] aliasBytes = alias.attributeName.getBytes("UTF-16BE");
				buffer.putInt(aliasBytes.length);
				buffer.put(aliasBytes);
			}
		}else{
			buffer.putInt(0);
		}
		

		buffer.flip();
		out.write(buffer);
		buffer.free();
	}
}
