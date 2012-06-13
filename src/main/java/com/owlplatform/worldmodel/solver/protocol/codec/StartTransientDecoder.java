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
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.worldmodel.solver.protocol.messages.StartTransientMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StartTransientMessage.TransientRequest;

public class StartTransientDecoder implements MessageDecoder {

	private static final Logger log = LoggerFactory.getLogger(StartTransientDecoder.class);
	
	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer buffer) {
		if (buffer.prefixedDataAvailable(4, 65536)) {
			buffer.mark();
			int messageLength = buffer.getInt();
			if (messageLength < 1) {
				buffer.reset();
				return MessageDecoderResult.NOT_OK;
			}

			byte messageType = buffer.get();
			buffer.reset();
			if (messageType == StartTransientMessage.MESSAGE_TYPE) {
				return MessageDecoderResult.OK;
			}
			return MessageDecoderResult.NOT_OK;
		}
		return MessageDecoderResult.NEED_DATA;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer buffer,
			ProtocolDecoderOutput out) throws Exception {
		StartTransientMessage message =new StartTransientMessage();
		
		int messageLength = buffer.getInt();
		byte messageType = buffer.get();
		--messageLength;
		
		int numTransients = buffer.getInt();
		messageLength -= 4;
		
		if(numTransients > 0){
			TransientRequest[] transients = new TransientRequest[numTransients];
			
			for(int i = 0; i < numTransients; ++i){
				TransientRequest request = new TransientRequest();
				request.setTransientAlias(buffer.getInt());
				messageLength -= 4;
				
				int numUriPatterns = buffer.getInt();
				messageLength -= 4;
				
				if(numUriPatterns > 0){
					String[] uriPatterns = new String[numUriPatterns];
					
					for(int j = 0; j < numUriPatterns; ++j){
						int uriLength = buffer.getInt();
						messageLength -= 4;
						
						byte[] uriBytes = new byte[uriLength];
						buffer.get(uriBytes);
						messageLength -= uriLength;
						
						uriPatterns[j] = new String(uriBytes,"UTF-16BE");
					}
					
					request.setUriPatterns(uriPatterns);
				}
				
			}
			
			message.setTransientRequests(transients);
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
