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

/**
 * Interface for classes that handle all session events for a Client-World Model
 * connection.
 * 
 * @author Robert Moore
 * 
 */
public interface ClientIoAdapter {

  /**
   * Called when an uncaught Throwable is generated on the session.
   * @param session the session that caused the Throwable.
   * @param cause the Throwable.
   */
  public void exceptionCaught(IoSession session, Throwable cause);

  /**
   * Called when a session is idle for the configured period of time.
   * @param session the idle session.
   * @param status the idle status.
   */
  public void sessionIdle(IoSession session, IdleStatus status);

  /**
   * Called when a session if fully established.
   * @param session the opened session.
   */
  public void connectionOpened(IoSession session);

  /**
   * Called when a session closes.
   * @param session the closed session.
   */
  public void connectionClosed(IoSession session);

  /**
   * Called after a handshake message is received.
   * @param session the session that received the handshake.
   * @param message the handshake.
   */
  public void handshakeReceived(IoSession session, HandshakeMessage message);

  /**
   * Called after a keep-alive message is received.
   * @param session the session that decoded the keep-alive.
   * @param message the keep-alive message.
   */
  public void keepAliveReceived(IoSession session, KeepAliveMessage message);

  /**
   * Called after a snapshot request message is received.
   * @param session the session that received the message.
   * @param message the message
   */
  public void snapshotRequestReceived(IoSession session,
      SnapshotRequestMessage message);

  /**
   * Called after a range request message is received.
   * @param session the session that received the message.
   * @param message the message.
   */
  public void rangeRequestReceived(IoSession session,
      RangeRequestMessage message);

  /**
   * Called after a stream request message is received.
   * @param session the session that received the message.
   * @param message the message.
   */
  public void streamRequestReceived(IoSession session,
      StreamRequestMessage message);

  /**
   * Called after an attribute alias message is received.
   * @param session the session that received the message.
   * @param message the message.
   */
  public void attributeAliasReceived(IoSession session,
      AttributeAliasMessage message);

  /**
   * Called after an origin alias message is received.
   * @param session the session that received the message.
   * @param message the message.
   */
  public void originAliasReceived(IoSession session, OriginAliasMessage message);

  /**
   * Called after a request complete message is received.
   * @param session the session that received the message.
   * @param message the message.
   */
  public void requestCompleteReceived(IoSession session,
      RequestCompleteMessage message);

  /**
   * Called after a cancel request message is received.
   * @param session the session that received the message.
   * @param message the message.
   */
  public void cancelRequestReceived(IoSession session,
      CancelRequestMessage message);

  /**
   * Called when a data response message is received.
   * @param session the session that received the message.
   * @param message the message.
   */
  public void dataResponseReceived(IoSession session,
      DataResponseMessage message);

  /**
   * Called when a search message is received. 
   * @param session the session that received the message.
   * @param message the message.
   */
  public void idSearchReceived(IoSession session, IdSearchMessage message);

  /**
   * Called when a search response message is received.
   * @param session the session that received the message.
   * @param message the message.
   */
  public void idSearchResponseReceived(IoSession session,
      IdSearchResponseMessage message);

  /**
   * Called when an origin preference message is received.
   * @param session the session that received the message.
   * @param message the message
   */
  public void originPreferenceReceived(IoSession session,
      OriginPreferenceMessage message);

  /**
   * Called when a handshake message is sent.
   * @param session the session that sent the message.
   * @param message the message
   */
  public void handshakeSent(IoSession session, HandshakeMessage message);

  /**
   * Called when a keep-alive message is sent.
   * @param session the session that sent the message.
   * @param message the message.
   */
  public void keepAliveSent(IoSession session, KeepAliveMessage message);

  /**
   * Called when a snapshot request message is sent.
   * @param session the session that sent the message.
   * @param message the message.
   */
  public void snapshotRequestSent(IoSession session,
      SnapshotRequestMessage message);

  /**
   * Called when a range request message is sent.
   * @param session the session that sent the message.
   * @param message the message
   */
  public void rangeRequestSent(IoSession session, RangeRequestMessage message);

  /**
   * Called when a stream request message is sent.
   * @param session the session that sent the message.
   * @param message the message
   */
  public void streamRequestSent(IoSession session, StreamRequestMessage message);

  /**
   * Called when an attribute alias message is sent.
   * @param session the session that sent the message.
   * @param message the message
   */
  public void attributeAliasSent(IoSession session,
      AttributeAliasMessage message);

  /**
   * Called when an origin alias message is sent.
   * @param session the session that sent the message.
   * @param message the message.
   */
  public void originAliasSent(IoSession session, OriginAliasMessage message);

  /**
   * Called when a request complete message is sent.
   * @param session the session that sent the message.
   * @param message the message
   */
  public void requestCompleteSent(IoSession session,
      RequestCompleteMessage message);

  /**
   * Called when a cancel request message is sent.
   * @param session the session that sent the message.
   * @param message the message
   */
  public void cancelRequestSent(IoSession session, CancelRequestMessage message);

  /**
   * Called when a data response message is sent.
   * @param session the session that sent the message.
   * @param message the message
   */
  public void dataResponseSent(IoSession session, DataResponseMessage message);

  /**
   * Called when an Id search message is sent.
   * @param session the session that sent the message.
   * @param message the message.
   */
  public void idSearchSent(IoSession session, IdSearchMessage message);

  /**
   * Called when an id search response is sent.
   * @param session the session that sent the message.
   * @param message the message.
   */
  public void idSearchResponseSent(IoSession session,
      IdSearchResponseMessage message);

  /**
   * Called when an orign preferense is sent.
   * @param session the session that sent the message.
   * @param message the message
   */
  public void OriginPreferenceSent(IoSession session,
      OriginPreferenceMessage message);
}
