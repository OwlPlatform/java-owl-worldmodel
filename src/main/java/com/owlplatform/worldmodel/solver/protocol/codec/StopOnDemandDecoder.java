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

import com.owlplatform.worldmodel.solver.protocol.messages.OnDemandRequest;
import com.owlplatform.worldmodel.solver.protocol.messages.StopOnDemandMessage;

/**
 * Decoder for Stop On-Demand messages.
 * @author Robert Moore
 *
 */
public class StopOnDemandDecoder implements MessageDecoder {


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
			if (messageType == StopOnDemandMessage.MESSAGE_TYPE) {
				return MessageDecoderResult.OK;
			}
			return MessageDecoderResult.NOT_OK;
		}
		return MessageDecoderResult.NEED_DATA;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer buffer,
			ProtocolDecoderOutput out) throws Exception {
		StopOnDemandMessage message = new StopOnDemandMessage();

		buffer.getInt();
		buffer.get();
		int numTransients = buffer.getInt();
		if (numTransients > 0) {
			OnDemandRequest[] transients = new OnDemandRequest[numTransients];

			for (int i = 0; i < numTransients; ++i) {
				OnDemandRequest request = new OnDemandRequest();
				request.setAttributeAlias(buffer.getInt());
				int numIdPatterns = buffer.getInt();
				if (numIdPatterns > 0) {
					String[] idPatterns = new String[numIdPatterns];

					for (int j = 0; j < numIdPatterns; ++j) {
						int idLength = buffer.getInt();
						byte[] idBytes = new byte[idLength];
						buffer.get(idBytes);
						idPatterns[j] = new String(idBytes, "UTF-16BE");
					}

					request.setIdPatterns(idPatterns);
				}

			}

			message.setRequests(transients);
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
