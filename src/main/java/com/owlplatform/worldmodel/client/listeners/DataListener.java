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
package com.owlplatform.worldmodel.client.listeners;

import com.owlplatform.worldmodel.client.ClientWorldModelInterface;
import com.owlplatform.worldmodel.client.protocol.messages.AbstractRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.AttributeAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.DataResponseMessage;
import com.owlplatform.worldmodel.client.protocol.messages.OriginAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.OriginPreferenceMessage;
import com.owlplatform.worldmodel.client.protocol.messages.IdSearchResponseMessage;

/**
 * Interface for classes that should respond to message-related events from a
 * {@link ClientWorldModelInterface}.
 * 
 * @author Robert Moore
 * 
 */
public interface DataListener {

  /**
   * Called when a range or streaming request is finished.
   * @param source the source of the message.
   * @param message the message.
   */
  public void requestCompleted(ClientWorldModelInterface source,
      AbstractRequestMessage message);

  /**
   * Called when data is received in response to a request.
   * @param source the source of the message.
   * @param message the message.
   */
  public void dataResponseReceived(ClientWorldModelInterface source,
      DataResponseMessage message);

  /**
   * Called when a search response is received.
   * @param source the source of the message.
   * @param message the message.
   */
  public void idSearchResponseReceived(ClientWorldModelInterface source,
      IdSearchResponseMessage message);

  /**
   * Called when attribute aliases are received from the world model.
   * @param source the source of the message.
   * @param message the message.
   */
  public void attributeAliasesReceived(ClientWorldModelInterface source,
      AttributeAliasMessage message);

  /**
   * Called when origin aliases are recieved from the world model.
   * @param source the source of the message.
   * @param message the message.
   */
  public void originAliasesReceived(ClientWorldModelInterface source,
      OriginAliasMessage message);

  /**
   * Called when an origin preference message has been sent to the world model.
   * @param source the source of the message.
   * @param message the message.
   */
  public void originPreferenceSent(ClientWorldModelInterface source,
      OriginPreferenceMessage message);
}
