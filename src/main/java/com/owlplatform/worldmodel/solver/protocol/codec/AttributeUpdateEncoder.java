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

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.owlplatform.worldmodel.Attribute;
import com.owlplatform.worldmodel.solver.protocol.messages.AttributeUpdateMessage;

/**
 * Encoder for Attribute Update messages.
 * 
 * @author Robert Moore
 * 
 */
public class AttributeUpdateEncoder implements
    MessageEncoder<AttributeUpdateMessage> {

  @Override
  public void encode(IoSession session, AttributeUpdateMessage message,
      ProtocolEncoderOutput out) throws Exception {
    IoBuffer buffer = IoBuffer.allocate(message.getMessageLength() + 4);

    // Message length
    buffer.putInt(message.getMessageLength());

    // Message type
    buffer.put(AttributeUpdateMessage.MESSAGE_TYPE);

    // Create Identifier boolean value
    buffer.put(message.getCreateId() ? (byte) 1 : (byte) 0);

    if (message.getAttributes() != null) {
      buffer.putInt(message.getAttributes().length);
      for (Attribute attr : message.getAttributes()) {
        buffer.putInt(attr.getAttributeNameAlias());
        buffer.putLong(attr.getCreationDate());
        if (attr.getId() != null) {
          byte[] targetBytes = attr.getId().getBytes("UTF-16BE");
          buffer.putInt(targetBytes.length);
          buffer.put(targetBytes);
        } else {
          buffer.putInt(0);
        }
        if (attr.getData() != null) {
          buffer.putInt(attr.getData().length);
          buffer.put(attr.getData());
        } else {
          buffer.putInt(0);
        }
      }
    } else {
      buffer.putInt(0);
    }

    buffer.flip();

    out.write(buffer);

    buffer.free();
  }

}
