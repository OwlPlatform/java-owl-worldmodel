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

import com.owlplatform.common.util.NumericUtils;
import com.owlplatform.worldmodel.solver.protocol.messages.DataTransferMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DataTransferMessage.Solution;

public class DataTransferEncoder implements MessageEncoder<DataTransferMessage> {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(DataTransferEncoder.class);
	
	@Override
	public void encode(IoSession session, DataTransferMessage message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(message.getMessageLength()+4);
		
		// Message length
		buffer.putInt(message.getMessageLength());
		
		// Message type
		buffer.put(DataTransferMessage.MESSAGE_TYPE);
		
		// Create URI boolean value
		buffer.put(message.getCreateUri()?(byte)1:(byte)0);
		
		if(message.getSolutions() != null){
			buffer.putInt(message.getSolutions().length);
			for(Solution soln : message.getSolutions()){
				buffer.putInt(soln.getAttributeNameAlias());
				buffer.putLong(soln.getTime());
				if(soln.getTargetName() != null){
					byte[] targetBytes = soln.getTargetName().getBytes("UTF-16BE");
					buffer.putInt(targetBytes.length);
					buffer.put(targetBytes);
				}
				else{
					buffer.putInt(0);
				}
				if(soln.getData() != null){
					buffer.putInt(soln.getData().length);
					buffer.put(soln.getData());
				}else{
					buffer.putInt(0);
				}
			}
		}else{
			buffer.putInt(0);
		}
		
		buffer.flip();
		
		byte[] bufBytes = buffer.array();
		log.debug("MSG: {}",NumericUtils.toHexString(bufBytes));
		
		out.write(buffer);
		
		buffer.free();
	}

}
