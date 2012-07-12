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

import java.util.ArrayList;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.worldmodel.Attribute;
import com.owlplatform.worldmodel.client.protocol.messages.DataResponseMessage;

/**
 * Decoder for Data Response messages.
 * @author Robert Moore
 *
 */
public class DataResponseDecoder implements MessageDecoder {
	
	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(DataResponseDecoder.class);

	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer buffer) {
		if (buffer.prefixedDataAvailable(4)) {
			buffer.mark();
			int messageLength = buffer.getInt();
			if (messageLength < 1) {
				buffer.reset();
				return MessageDecoderResult.NOT_OK;
			}

			byte messageType = buffer.get();
			buffer.reset();
			if (messageType == DataResponseMessage.MESSAGE_TYPE) {
				return MessageDecoderResult.OK;
			}
			return MessageDecoderResult.NOT_OK;
		}
		return MessageDecoderResult.NEED_DATA;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer buffer,
			ProtocolDecoderOutput out) throws Exception {
		
		DataResponseMessage message = new DataResponseMessage();
		
		int messageLength = buffer.getInt();
		
		buffer.get();
		--messageLength;
		
		int idLength = buffer.getInt();
		messageLength -= 4;
		
		if(idLength == 0){
			log.error("Identifier length is 0!");
			return MessageDecoderResult.NOT_OK;
		}
		byte[] idBytes = new byte[idLength];
		buffer.get(idBytes);
		messageLength -= idLength;
		
		String identifier = new String(idBytes,"UTF-16BE");
		message.setId(identifier);
		
		int ticketNumber = buffer.getInt();
		messageLength -= 4;
		
		message.setTicketNumber(ticketNumber);
		
		buffer.getInt();
		messageLength -= 4;
		
		// Decode any attributes
		if(messageLength > 0){
			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			
			while(messageLength > 0){
				Attribute attrib = new Attribute();
				int attribNameAlias = buffer.getInt();
				messageLength -= 4;
				attrib.setAttributeNameAlias(attribNameAlias);
		
				long creationDate = buffer.getLong();
				messageLength -= 8;
				attrib.setCreationDate(creationDate);
				
				long expirationDate = buffer.getLong();
				messageLength -= 8;
				attrib.setExpirationDate(expirationDate);
				
				int originNameAlias = buffer.getInt();
				messageLength -= 4;
				attrib.setOriginNameAlias(originNameAlias);
				
				int dataLength = buffer.getInt();
				messageLength -= 4;
				if(dataLength > 0){
					byte[] data = new byte[dataLength];
					buffer.get(data);
					messageLength -= dataLength;
					attrib.setData(data);
				}
				
				attributes.add(attrib);
			}
			
			if(attributes.size() > 0){
				message.setAttributes(attributes.toArray(new Attribute[attributes.size()]));
			}
		}
		
		out.write(message);
		
		return MessageDecoderResult.OK;
	}

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
