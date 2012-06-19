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
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.owlplatform.worldmodel.client.protocol.messages.RangeRequestMessage;

public class RangeRequestDecoder implements MessageDecoder {

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
			if (messageType == RangeRequestMessage.MESSAGE_TYPE) {
				return MessageDecoderResult.OK;
			}
			return MessageDecoderResult.NOT_OK;
		}
		return MessageDecoderResult.NEED_DATA;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer buffer,
			ProtocolDecoderOutput out) throws Exception {
		RangeRequestMessage message = new RangeRequestMessage();

		int messageLength = buffer.getInt();

		byte messageType = buffer.get();
		--messageLength;

		int ticketNumber = buffer.getInt();
		messageLength -= 4;
		message.setTicketNumber(ticketNumber);
		
		int queryLength = buffer.getInt();
		messageLength -= 4;

		byte[] queryBytes = new byte[queryLength];
		buffer.get(queryBytes);
		messageLength -= queryLength;
		String query = new String(queryBytes, "UTF-16BE");
		
		message.setIdRegex(query);

		int numAttributes = buffer.getInt();
		messageLength -= 4;

		if (numAttributes > 0) {
			String[] attributes = new String[numAttributes];
			for (int i = 0; i < attributes.length; ++i) {
				int attribLength = buffer.getInt();
				messageLength -= 4;

				byte[] attribBytes = new byte[attribLength];
				buffer.get(attribBytes);
				messageLength -= attribLength;
				
				String attribute = new String(attribBytes,"UTF-16BE");
				attributes[i] = attribute;
			}
			
			message.setAttributeRegexes(attributes);
		}

		long beginTime = buffer.getLong();
		messageLength -= 8;
		message.setBeginTimestamp(beginTime);
		
		long endTime = buffer.getLong();
		messageLength -= 8;
		message.setEndTimestamp(endTime);
		
		out.write(message);
		
		return MessageDecoderResult.OK;
	}

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
