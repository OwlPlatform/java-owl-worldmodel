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

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import com.owlplatform.worldmodel.solver.protocol.messages.CreateURIMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DataTransferMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DeleteAttributeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DeleteURIMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.ExpireAttributeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.ExpireURIMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.HandshakeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.KeepAliveMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StartTransientMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StopTransientMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.TypeAnnounceMessage;

public class WorldModelSolverProtocolCodecFactory extends
		DemuxingProtocolCodecFactory {

	public static final String CODEC_NAME = "Grail Solver-World Model codec";

	public WorldModelSolverProtocolCodecFactory(final boolean isSolver) {
		super();

		// Encoders for both sides
		super.addMessageEncoder(HandshakeMessage.class, HandshakeEncoder.class);
		super.addMessageEncoder(KeepAliveMessage.class, KeepAliveEncoder.class);

		// Decoders for both
		super.addMessageDecoder(HandshakeDecoder.class);
		super.addMessageDecoder(KeepAliveDecoder.class);

		if (isSolver) {
			// Encoders for solver
			super.addMessageEncoder(DataTransferMessage.class, DataTransferEncoder.class);
			super.addMessageEncoder(TypeAnnounceMessage.class, TypeAnnounceEncoder.class);
			super.addMessageEncoder(CreateURIMessage.class, CreateURIEncoder.class);
			super.addMessageEncoder(ExpireURIMessage.class, ExpireURIEncoder.class);
			super.addMessageEncoder(DeleteURIMessage.class, DeleteURIEncoder.class);
			super.addMessageEncoder(ExpireAttributeMessage.class, ExpireAttributeEncoder.class);
			super.addMessageEncoder(DeleteAttributeMessage.class, DeleteAttributeEncoder.class);
			
			// Decoders for solver
			super.addMessageDecoder(StartTransientDecoder.class);
			super.addMessageDecoder(StopTransientDecoder.class);
		} else {
			// Encoders for World Model
			super.addMessageEncoder(StartTransientMessage.class, StartTransientEncoder.class);
			super.addMessageEncoder(StopTransientMessage.class, StopTransientEncoder.class);
			
			// Decoders for World Model
			super.addMessageDecoder(DataTransferDecoder.class);
			super.addMessageDecoder(TypeAnnounceDecoder.class);
			super.addMessageDecoder(CreateURIDecoder.class);
			super.addMessageDecoder(ExpireURIDecoder.class);
			super.addMessageDecoder(DeleteURIDecoder.class);
			super.addMessageDecoder(ExpireAttributeDecoder.class);
			super.addMessageDecoder(DeleteAttributeDecoder.class);
		}
	}

}
