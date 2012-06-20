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

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

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
 * Protocol codec factory for the Client-World Model protocol.
 * 
 * @author Robert Moore
 * 
 */
public class WorldModelClientProtocolCodecFactory extends
    DemuxingProtocolCodecFactory {

  /**
   * Unique codec name.
   */
  public static final String CODEC_NAME = "Owl Platform Client-World Model codec";

  /**
   * Generates a protocol codec factory depending on whether the connection is for the World Model (server)
   * or client.
   * @param isClient {@code true} if the codec will be for the client, or {@code false} if it is for the World Model.
   */
  public WorldModelClientProtocolCodecFactory(final boolean isClient) {
    super();

    // Encoders for both sides
    super.addMessageEncoder(HandshakeMessage.class, HandshakeEncoder.class);
    super.addMessageEncoder(KeepAliveMessage.class, KeepAliveEncoder.class);

    if (isClient) {
      // Encoders for client
      super.addMessageEncoder(SnapshotRequestMessage.class,
          SnapshotRequestEncoder.class);
      super.addMessageEncoder(RangeRequestMessage.class,
          RangeRequestEncoder.class);
      super.addMessageEncoder(StreamRequestMessage.class,
          StreamRequestEncoder.class);
      super.addMessageEncoder(CancelRequestMessage.class,
          CancelRequestEncoder.class);
      super.addMessageEncoder(IdSearchMessage.class, IdSearchEncoder.class);
      super.addMessageEncoder(OriginPreferenceMessage.class,
          OriginPreferenceEncoder.class);

      // Decoders for client
      super.addMessageDecoder(IdSearchResponseDecoder.class);
      super.addMessageDecoder(OriginAliasDecoder.class);
      super.addMessageDecoder(AttributeAliasDecoder.class);
      super.addMessageDecoder(DataResponseDecoder.class);
      super.addMessageDecoder(RequestCompleteDecoder.class);

    } else {
      // Encoders for World Model
      super.addMessageEncoder(DataResponseMessage.class,
          DataResponseEncoder.class);
      super.addMessageEncoder(IdSearchResponseMessage.class,
          IdSearchResponseEncoder.class);
      super.addMessageEncoder(RequestCompleteMessage.class,
          RequestCompleteEncoder.class);
      super.addMessageEncoder(OriginAliasMessage.class,
          OriginAliasEncoder.class);
      super.addMessageEncoder(AttributeAliasMessage.class,
          AttributeAliasEncoder.class);

      // Decoders for World Model
      super.addMessageDecoder(SnapshotRequestDecoder.class);
      super.addMessageDecoder(RangeRequestDecoder.class);
      super.addMessageDecoder(StreamRequestDecoder.class);
      super.addMessageDecoder(CancelRequestDecoder.class);
      super.addMessageDecoder(IdSearchDecoder.class);
      super.addMessageDecoder(OriginPreferenceDecoder.class);
    }
    // Decoders for both
    super.addMessageDecoder(KeepAliveDecoder.class);
    super.addMessageDecoder(HandshakeDecoder.class);

  }

}
