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

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.owlplatform.worldmodel.solver.protocol.messages.AttributeAnnounceMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.AttributeUpdateMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.CreateIdentifierMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DeleteAttributeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DeleteIdentifierMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.ExpireAttributeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.ExpireIdentifierMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.HandshakeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.KeepAliveMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StartOnDemandMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StopOnDemandMessage;

/**
 * Interface for low-level classes that want to respond to all event types
 * on an IOSession for Solver-World Model connections.
 * @author Robert Moore
 *
 */
public interface SolverIoAdapter {

  /**
   * Called when a Throwable is caught on the session.
   * @param session the origin of the Throwable
   * @param cause what was thrown
   */
	public void exceptionCaught(IoSession session, Throwable cause);

	/**
	 * Called when a session is idle.
	 * @param session the idle session
	 * @param status the idle status
	 */
	public void sessionIdle(IoSession session, IdleStatus status);

	/**
	 * Called when a session is fully connected to the other side.
	 * @param session the connected session.
	 */
	public void connectionOpened(IoSession session);

	/**
	 * Called when a session is closed.
	 * @param session the closed session.
	 */
	public void connectionClosed(IoSession session);

	/**
	 * Called when a handshake message is received.
	 * @param session the session that received the handshake.
	 * @param message the handshake message.
	 */
	public void handshakeReceived(IoSession session, HandshakeMessage message);

	/**
	 * Called when a keep-alive message is received.
	 * @param session the session that received the message.
	 * @param message the keep-alive message.
	 */
	public void keepAliveReceived(IoSession session, KeepAliveMessage message);
	
	/**
	 * Called when an Attribute Announce message is received.
	 * @param session the session that received the message.
	 * @param message the Attribute Announce message.
	 */
	public void attributeAnnounceReceived(IoSession session, AttributeAnnounceMessage message);
	
	/**
	 * Called when an OnDemand start message is received.
	 * @param session the session that received the message.
	 * @param message the start message.
	 */
	public void startOnDemandReceived(IoSession session, StartOnDemandMessage message);
	
	/**
	 * Called when an OnDemand stop message is received.
	 * @param session the session that received the message.
	 * @param message the received message.
	 */
	public void stopOnDemandReceived(IoSession session, StopOnDemandMessage message);
	
	/**
	 * Called when an Attribute Update message is received.
	 * @param session the session that received the message.
	 * @param message the received message.
	 */
	public void attributeUpdateReceived(IoSession session, AttributeUpdateMessage message);
	
	/**
	 * Called when a Create Identifier message is received.
	 * @param session the session that received the message.
	 * @param message the received message.
	 */
	public void createIdReceived(IoSession session, CreateIdentifierMessage message);
	
	/**
	 * Called when an Expire Identifier message is received.
	 * @param session the session that received the message.
   * @param message the received message.
	 */
	public void expireIdReceived(IoSession session, ExpireIdentifierMessage message);
	
	/**
	 * Called when a Delete Identifier message is received.
	 * @param session the session that received the message.
   * @param message the received message.
	 */
	public void deleteIdReceived(IoSession session, DeleteIdentifierMessage message);
	
	/**
	 * Called when an Expire Attribute message is received.
	 * @param session the session that received the message.
   * @param message the received message.
	 */
	public void expireAttributeReceived(IoSession session, ExpireAttributeMessage message);
	
	/**
	 * Called when a Delete Attribute message is received.
	 * @param session the session that received the message.
   * @param message the received message.
	 */
	public void deleteAttributeReceived(IoSession session, DeleteAttributeMessage message);
	
	/**
	 * Called when a Handshake message is sent.
	 * @param session the session that sent the message.
	 * @param message the sent message.
	 */
	public void handshakeSent(IoSession session, HandshakeMessage message);

	/**
	 * Called when a Keep-Alive message is sent.
	 * @param session the session that sent the message.
   * @param message the sent message.
	 */
	public void keepAliveSent(IoSession session, KeepAliveMessage message);

	/**
	 * Called when an Attribute Announce message is sent.
	 * @param session the session that sent the message.
   * @param message the sent message.
	 */
	public void attributeAnnounceSent(IoSession session, AttributeAnnounceMessage message);
	
	/**
	 * Called when an On-Demand start message is sent.
	 * @param session the session that sent the message.
   * @param message the sent message.
	 */
	public void startOnDemandSent(IoSession session, StartOnDemandMessage message);
	
	/**
	 * Called when an On-Demand stop message is sent.
	 * @param session the session that sent the message.
   * @param message the sent message.
	 */
	public void stopOnDemandSent(IoSession session, StopOnDemandMessage message);
	
	/**
	 * Called when an Attribute Update message is sent.
	 * @param session the session that sent the message.
   * @param message the sent message.
	 */
	public void attributeUpdateSent(IoSession session, AttributeUpdateMessage message);
	
	/**
	 * Called when a Create Identifier message is sent.
	 * @param session the session that sent the message.
   * @param message the sent message.
	 */
	public void createIdSent(IoSession session, CreateIdentifierMessage message);
	
	/**
	 * Called when an Expire Identifier message is sent.
	 * @param session the session that sent the message.
   * @param message the sent message.
	 */
	public void expireIdSent(IoSession session, ExpireIdentifierMessage message);
	
	/**
	 * Called when a Delete Identifier message is sent.
	 * @param session the session that sent the message.
   * @param message the sent message.
	 */
	public void deleteIdSent(IoSession session, DeleteIdentifierMessage message);
	
	/**
	 * Called when an Expire Attribute message is sent.
	 * @param session the session that sent the message.
   * @param message the sent message.
	 */
	public void expireAttributeSent(IoSession session, ExpireAttributeMessage message);
	
	/**
	 * Called when a Delete Attribute message is sent.
	 * @param session the session that sent the message.
   * @param message the sent message.
	 */
	public void deleteAttributeSent(IoSession session, DeleteAttributeMessage message);
}
