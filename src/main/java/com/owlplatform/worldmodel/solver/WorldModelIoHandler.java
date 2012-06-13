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

package com.owlplatform.worldmodel.solver;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.worldmodel.client.protocol.messages.OriginPreferenceMessage;
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

public class WorldModelIoHandler implements IoHandler {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(WorldModelIoHandler.class);

	private SolverIoAdapter ioAdapter;

	public WorldModelIoHandler(final SolverIoAdapter ioAdapter) {
		this.ioAdapter = ioAdapter;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.warn("Exception for {}: {}", session, cause);
		if (this.ioAdapter != null) {
			this.ioAdapter.exceptionCaught(session, cause);
		}
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		log.debug("Received message from {}: {}", session, message);
		if (this.ioAdapter == null) {
			log.warn("No IoAdapter defined, ignoring message from {}.\n{}",
					session, message);
			return;
		}
		if (message instanceof DataTransferMessage) {
			this.ioAdapter.dataTransferReceived(session,
					(DataTransferMessage) message);
		} else if (message instanceof DeleteAttributeMessage) {
			this.ioAdapter.deleteAttributeReceived(session,
					(DeleteAttributeMessage) message);
		} else if (message instanceof ExpireAttributeMessage) {
			this.ioAdapter.expireAttributeReceived(session,
					(ExpireAttributeMessage) message);
		} else if (message instanceof DeleteURIMessage) {
			this.ioAdapter.deleteUriReceived(session,
					(DeleteURIMessage) message);
		} else if (message instanceof ExpireURIMessage) {
			this.ioAdapter.expireUriReceived(session,
					(ExpireURIMessage) message);
		} else if (message instanceof CreateURIMessage) {
			this.ioAdapter.createUriReceived(session,
					(CreateURIMessage) message);
		} else if (message instanceof StopTransientMessage) {
			this.ioAdapter.stopTransientReceived(session,
					(StopTransientMessage) message);
		} else if (message instanceof StartTransientMessage) {
			this.ioAdapter.startTransientReceived(session,
					(StartTransientMessage) message);
		} else if (message instanceof TypeAnnounceMessage) {
			this.ioAdapter.typeAnnounceReceived(session,
					(TypeAnnounceMessage) message);
		} else if (message instanceof KeepAliveMessage) {
			this.ioAdapter.keepAliveReceived(session,
					(KeepAliveMessage) message);
		} else if (message instanceof HandshakeMessage) {
			this.ioAdapter.handshakeReceived(session,
					(HandshakeMessage) message);
		} else {
			log.warn("Unknown message type Received from {}: {}", session,
					message);
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		log.debug("Sent message to {}: {}", session, message);
		if (this.ioAdapter == null) {
			log.warn("No IoAdapter defined, ignoring message to {}.\n{}",
					session, message);
			return;
		}
		if (message instanceof DataTransferMessage) {
			this.ioAdapter.dataTransferSent(session,
					(DataTransferMessage) message);
		} else if (message instanceof DeleteAttributeMessage) {
			this.ioAdapter.deleteAttributeSent(session,
					(DeleteAttributeMessage) message);
		} else if (message instanceof ExpireAttributeMessage) {
			this.ioAdapter.expireAttributeSent(session,
					(ExpireAttributeMessage) message);
		} else if (message instanceof DeleteURIMessage) {
			this.ioAdapter.deleteUriSent(session, (DeleteURIMessage) message);
		} else if (message instanceof ExpireURIMessage) {
			this.ioAdapter.expireUriSent(session, (ExpireURIMessage) message);
		} else if (message instanceof CreateURIMessage) {
			this.ioAdapter.createUriSent(session, (CreateURIMessage) message);
		} else if (message instanceof StopTransientMessage) {
			this.ioAdapter.stopTransientSent(session,
					(StopTransientMessage) message);
		} else if (message instanceof StartTransientMessage) {
			this.ioAdapter.startTransientSent(session,
					(StartTransientMessage) message);
		} else if (message instanceof TypeAnnounceMessage) {
			this.ioAdapter.typeAnnounceSent(session,
					(TypeAnnounceMessage) message);
		} else if (message instanceof KeepAliveMessage) {
			this.ioAdapter.keepAliveSent(session, (KeepAliveMessage) message);
		} else if (message instanceof HandshakeMessage) {
			this.ioAdapter.handshakeSent(session, (HandshakeMessage) message);
		}  else {
			log.warn("Unknown message type Sent to {}: {}", session, message);
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		log.debug("Session closed {}.", session);
		if (this.ioAdapter != null) {
			this.ioAdapter.connectionClosed(session);
		}
	}

	@Override
	public void sessionCreated(IoSession arg0) throws Exception {
		// Ignored
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		log.debug("Session idle{}.", session, status);
		if (this.ioAdapter != null) {
			this.ioAdapter.sessionIdle(session, status);
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.debug("Session opened {}.", session);
		if (this.ioAdapter != null) {
			this.ioAdapter.connectionOpened(session);
		}
	}

}
