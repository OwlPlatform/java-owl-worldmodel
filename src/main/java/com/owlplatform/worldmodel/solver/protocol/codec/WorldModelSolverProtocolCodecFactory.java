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

import com.owlplatform.worldmodel.solver.protocol.messages.CreateIdentifierMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.AttributeUpdateMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DeleteAttributeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DeleteIdentifierMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.ExpireAttributeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.ExpireIdentifierMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.HandshakeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.KeepAliveMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StartOnDemandMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StopOnDemandMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.AttributeAnnounceMessage;

/**
 * Protocol codec factory for the Solver-World Model protocol.  Generates
 * codecs for the solver or world model components.
 * @author Robert Moore
 *
 */
public class WorldModelSolverProtocolCodecFactory extends
		DemuxingProtocolCodecFactory {

  /**
   * Unique name for the codec.
   */
	public static final String CODEC_NAME = "Owl Platform Solver-World Model codec";

	/**
	 * Constructs a new codec factory for the Solver-World Model protocol.
	 * @param isSolver {@code true} if the codec is for a solver, or {@code false} if it
	 * is for the World Model.
	 */
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
			super.addMessageEncoder(AttributeUpdateMessage.class, AttributeUpdateEncoder.class);
			super.addMessageEncoder(AttributeAnnounceMessage.class, AttributeAnnounceEncoder.class);
			super.addMessageEncoder(CreateIdentifierMessage.class, CreateIdentifierEncoder.class);
			super.addMessageEncoder(ExpireIdentifierMessage.class, ExpireIdentifierEncoder.class);
			super.addMessageEncoder(DeleteIdentifierMessage.class, DeleteIdentifierEncoder.class);
			super.addMessageEncoder(ExpireAttributeMessage.class, ExpireAttributeEncoder.class);
			super.addMessageEncoder(DeleteAttributeMessage.class, DeleteAttributeEncoder.class);
			
			// Decoders for solver
			super.addMessageDecoder(StartOnDemandDecoder.class);
			super.addMessageDecoder(StopOnDemandDecoder.class);
		} else {
			// Encoders for World Model
			super.addMessageEncoder(StartOnDemandMessage.class, StartOnDemandEncoder.class);
			super.addMessageEncoder(StopOnDemandMessage.class, StopOnDemandEncoder.class);
			
			// Decoders for World Model
			super.addMessageDecoder(AttributeUpdateDecoder.class);
			super.addMessageDecoder(AttributeAnnounceDecoder.class);
			super.addMessageDecoder(CreateIdentifierDecoder.class);
			super.addMessageDecoder(ExpireIdentifierDecoder.class);
			super.addMessageDecoder(DeleteIdentifierDecoder.class);
			super.addMessageDecoder(ExpireAttributeDecoder.class);
			super.addMessageDecoder(DeleteAttributeDecoder.class);
		}
	}

}
