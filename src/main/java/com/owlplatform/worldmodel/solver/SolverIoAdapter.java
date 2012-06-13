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

public interface SolverIoAdapter {

	public void exceptionCaught(IoSession session, Throwable cause);

	public void sessionIdle(IoSession session, IdleStatus status);

	public void connectionOpened(IoSession session);

	public void connectionClosed(IoSession session);

	public void handshakeReceived(IoSession session, HandshakeMessage message);

	public void keepAliveReceived(IoSession session, KeepAliveMessage message);
	
	public void typeAnnounceReceived(IoSession session, TypeAnnounceMessage message);
	
	public void startTransientReceived(IoSession session, StartTransientMessage message);
	
	public void stopTransientReceived(IoSession session, StopTransientMessage message);
	
	public void dataTransferReceived(IoSession session, DataTransferMessage message);
	
	public void createUriReceived(IoSession session, CreateURIMessage message);
	
	public void expireUriReceived(IoSession session, ExpireURIMessage message);
	
	public void deleteUriReceived(IoSession session, DeleteURIMessage message);
	
	public void expireAttributeReceived(IoSession session, ExpireAttributeMessage message);
	
	public void deleteAttributeReceived(IoSession session, DeleteAttributeMessage message);
	
	public void handshakeSent(IoSession session, HandshakeMessage message);

	public void keepAliveSent(IoSession session, KeepAliveMessage message);

	public void typeAnnounceSent(IoSession session, TypeAnnounceMessage message);
	
	public void startTransientSent(IoSession session, StartTransientMessage message);
	
	public void stopTransientSent(IoSession session, StopTransientMessage message);
	
	public void dataTransferSent(IoSession session, DataTransferMessage message);
	
	public void createUriSent(IoSession session, CreateURIMessage message);
	
	public void expireUriSent(IoSession session, ExpireURIMessage message);
	
	public void deleteUriSent(IoSession session, DeleteURIMessage message);
	
	public void expireAttributeSent(IoSession session, ExpireAttributeMessage message);
	
	public void deleteAttributeSent(IoSession session, DeleteAttributeMessage message);
}
