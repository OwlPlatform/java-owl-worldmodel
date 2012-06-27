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

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * IOHandler for Client-World Model protocol events. Demultiplexes the messages
 * to pass to an IOAdapter.
 * 
 * @author Robert Moore
 * 
 */
public class ClientWorldModelIoHandler implements IoHandler {

  /**
   * Logging facility for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(ClientWorldModelIoHandler.class);

  /**
   * The recipient of the demultiplexed messages.
   */
  private ClientIoAdapter ioAdapter;

  /**
   * Creates a new IOHandler with the specified IOAdapter.
   * 
   * @param ioAdapter
   *          the IOAdapter that should receive the messages and events.
   */
  public ClientWorldModelIoHandler(final ClientIoAdapter ioAdapter) {
    this.ioAdapter = ioAdapter;
  }

  /**
   * Returns this handler's IOAdapter.
   * @return the IOAdapter.
   */
  public ClientIoAdapter getIoAdapter() {
    return this.ioAdapter;
  }

  /**
   * Sets the IOAdapter for this handler.
   * @param ioAdapter the new IOAdapter.
   */
  public void setIoAdapter(ClientIoAdapter ioAdapter) {
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
      log.warn("No IoAdapter defined, ignoring message from {}.\n{}", session,
          message);
      return;
    }
    if (message instanceof DataResponseMessage) {
      this.ioAdapter.dataResponseReceived(session,
          (DataResponseMessage) message);
    }

    else if (message instanceof KeepAliveMessage) {
      this.ioAdapter.keepAliveReceived(session, (KeepAliveMessage) message);
    } else if (message instanceof SnapshotRequestMessage) {
      this.ioAdapter.snapshotRequestReceived(session,
          (SnapshotRequestMessage) message);
    } else if (message instanceof RangeRequestMessage) {
      this.ioAdapter.rangeRequestReceived(session,
          (RangeRequestMessage) message);
    } else if (message instanceof StreamRequestMessage) {
      this.ioAdapter.streamRequestReceived(session,
          (StreamRequestMessage) message);
    } else if (message instanceof AttributeAliasMessage) {
      this.ioAdapter.attributeAliasReceived(session,
          (AttributeAliasMessage) message);
    } else if (message instanceof OriginAliasMessage) {
      this.ioAdapter.originAliasReceived(session, (OriginAliasMessage) message);
    } else if (message instanceof RequestCompleteMessage) {
      this.ioAdapter.requestCompleteReceived(session,
          (RequestCompleteMessage) message);
    } else if (message instanceof CancelRequestMessage) {
      this.ioAdapter.cancelRequestReceived(session,
          (CancelRequestMessage) message);
    } else if (message instanceof HandshakeMessage) {
      this.ioAdapter.handshakeReceived(session, (HandshakeMessage) message);
    } else if (message instanceof IdSearchMessage) {
      this.ioAdapter.idSearchReceived(session, (IdSearchMessage) message);
    } else if (message instanceof IdSearchResponseMessage) {
      this.ioAdapter.idSearchResponseReceived(session,
          (IdSearchResponseMessage) message);
    } else if (message instanceof OriginPreferenceMessage) {
      this.ioAdapter.originPreferenceReceived(session,
          (OriginPreferenceMessage) message);
    } else {
      log.warn("Unknown message type received from {}: {}", session, message);
    }
  }

  @Override
  public void messageSent(IoSession session, Object message) throws Exception {
    log.debug("Sent message to {}: {}", session, message);
    if (this.ioAdapter == null) {
      log.warn("No IoAdapter defined, ignoring message to {}.\n{}", session,
          message);
      return;
    }
    if (message instanceof DataResponseMessage) {
      this.ioAdapter.dataResponseSent(session, (DataResponseMessage) message);
    } else if (message instanceof KeepAliveMessage) {
      this.ioAdapter.keepAliveSent(session, (KeepAliveMessage) message);
    } else if (message instanceof SnapshotRequestMessage) {
      this.ioAdapter.snapshotRequestSent(session,
          (SnapshotRequestMessage) message);
    } else if (message instanceof RangeRequestMessage) {
      this.ioAdapter.rangeRequestSent(session, (RangeRequestMessage) message);
    } else if (message instanceof StreamRequestMessage) {
      this.ioAdapter.streamRequestSent(session, (StreamRequestMessage) message);
    } else if (message instanceof AttributeAliasMessage) {
      this.ioAdapter.attributeAliasSent(session,
          (AttributeAliasMessage) message);
    } else if (message instanceof OriginAliasMessage) {
      this.ioAdapter.originAliasSent(session, (OriginAliasMessage) message);
    } else if (message instanceof RequestCompleteMessage) {
      this.ioAdapter.requestCompleteSent(session,
          (RequestCompleteMessage) message);
    } else if (message instanceof CancelRequestMessage) {
      this.ioAdapter.cancelRequestSent(session, (CancelRequestMessage) message);
    } else if (message instanceof HandshakeMessage) {
      this.ioAdapter.handshakeSent(session, (HandshakeMessage) message);
    } else if (message instanceof IdSearchMessage) {
      this.ioAdapter.idSearchSent(session, (IdSearchMessage) message);
    } else if (message instanceof IdSearchResponseMessage) {
      this.ioAdapter.idSearchResponseSent(session,
          (IdSearchResponseMessage) message);
    } else if (message instanceof OriginPreferenceMessage) {
      this.ioAdapter.OriginPreferenceSent(session,
          (OriginPreferenceMessage) message);
    }

    else {
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
