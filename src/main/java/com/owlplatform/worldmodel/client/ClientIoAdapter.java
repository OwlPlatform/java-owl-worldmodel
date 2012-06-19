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

package com.owlplatform.worldmodel.client;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.owlplatform.worldmodel.client.protocol.messages.AttributeAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.CancelRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.DataResponseMessage;
import com.owlplatform.worldmodel.client.protocol.messages.HandshakeMessage;
import com.owlplatform.worldmodel.client.protocol.messages.KeepAliveMessage;
import com.owlplatform.worldmodel.client.protocol.messages.OriginAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.OriginPreferenceMessage;
import com.owlplatform.worldmodel.client.protocol.messages.RangeRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.RequestCompleteMessage;
import com.owlplatform.worldmodel.client.protocol.messages.SnapshotRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.StreamRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.IdSearchMessage;
import com.owlplatform.worldmodel.client.protocol.messages.IdSearchResponseMessage;

public interface ClientIoAdapter {

	public void exceptionCaught(IoSession session, Throwable cause);
	
	public void sessionIdle(IoSession session, IdleStatus status);
	
	public void connectionOpened(IoSession session);
	
	public void connectionClosed(IoSession session);
	
	public void handshakeReceived(IoSession session, HandshakeMessage message);
	
	public void keepAliveReceived(IoSession session, KeepAliveMessage message);
	
	public void snapshotRequestReceived(IoSession session, SnapshotRequestMessage message);
	
	public void rangeRequestReceived(IoSession session, RangeRequestMessage message);
	
	public void streamRequestReceived(IoSession session, StreamRequestMessage message);
	
	public void attributeAliasReceived(IoSession session, AttributeAliasMessage message);
	
	public void originAliasReceived(IoSession session, OriginAliasMessage message);
	
	public void requestCompleteReceived(IoSession session, RequestCompleteMessage message);
	
	public void cancelRequestReceived(IoSession session, CancelRequestMessage message);
	
	public void dataResponseReceived(IoSession session, DataResponseMessage message);
	
	public void URISearchReceived(IoSession session, IdSearchMessage message);
	
	public void URISearchResponseReceived(IoSession session, IdSearchResponseMessage message);
	
	public void originPreferenceReceived(IoSession session, OriginPreferenceMessage message);
	
	public void handshakeSent(IoSession session, HandshakeMessage message);
	
	public void keepAliveSent(IoSession session, KeepAliveMessage message);
	
	public void snapshotRequestSent(IoSession session, SnapshotRequestMessage message);
	
	public void rangeRequestSent(IoSession session, RangeRequestMessage message);
	
	public void streamRequestSent(IoSession session, StreamRequestMessage message);
	
	public void attributeAliasSent(IoSession session, AttributeAliasMessage message);
	
	public void originAliasSent(IoSession session, OriginAliasMessage message);
	
	public void requestCompleteSent(IoSession session, RequestCompleteMessage message);
	
	public void cancelRequestSent(IoSession session, CancelRequestMessage message);
	
	public void dataResponseSent(IoSession session, DataResponseMessage message);
	
	public void URISearchSent(IoSession session, IdSearchMessage message);
	
	public void URISearchResponseSent(IoSession session, IdSearchResponseMessage message);

	public void OriginPreferenceSent(IoSession session, OriginPreferenceMessage message);
}
