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

import com.owlplatform.worldmodel.client.protocol.messages.OriginAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.OriginAliasMessage.OriginAlias;

public class OriginAliasDecoder implements MessageDecoder {

	private static final Logger log = LoggerFactory
			.getLogger(OriginAliasDecoder.class);

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
			if (messageType == OriginAliasMessage.MESSAGE_TYPE) {
				return MessageDecoderResult.OK;
			}
			return MessageDecoderResult.NOT_OK;
		}
		return MessageDecoderResult.NEED_DATA;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer buffer,
			ProtocolDecoderOutput out) throws Exception {

		OriginAliasMessage message = new OriginAliasMessage();

		int messageLength = buffer.getInt();

		byte messageType = buffer.get();
		--messageLength;

		int numAliases = buffer.getInt();
		messageLength -= 4;

		if (numAliases > 0) {
			ArrayList<OriginAlias> aliases = new ArrayList<OriginAlias>();
			log.debug("Decoding {} aliases.", numAliases);

			for (int i = 0; i < numAliases; ++i) {
				int aliasNumber = buffer.getInt();
				log.debug("Next origin number is {}.", aliasNumber);
				int nameLength = buffer.getInt();
				log.debug("Next origin name is {} bytes.", nameLength);
				messageLength -= 8;
				
				// Default to an empty origin name, to handle null names
				String name = "";
				if (nameLength != 0) {
					byte[] nameBytes = new byte[nameLength];
					buffer.get(nameBytes);
					messageLength -= nameLength;

					name = new String(nameBytes, "UTF-16BE");

				}else{
					log.warn("World Model sent an empty origin name for alias number {}.",aliasNumber);
				}

				aliases.add(new OriginAlias(aliasNumber, name));
				log.debug("Alias[{}] {}->" + name, Integer.valueOf(i),
						Integer.valueOf(aliasNumber));
			}

			message.setAliases(aliases.toArray(new OriginAlias[] {}));
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
