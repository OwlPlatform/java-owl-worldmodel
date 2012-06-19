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

/**
 * Interface for classes that respond to connection-related events from a
 * {@link ClientWorldModelInterface}.
 * 
 * @author Robert Moore
 * 
 */
public interface ConnectionListener {

  /**
   * Called when the connection closes. A reconnect attempt made be made.
   * 
   * @param worldModel
   *          the interface that closed the connection.
   */
  public void connectionInterrupted(ClientWorldModelInterface worldModel);

  /**
   * Called when the connection closes and no reconnect attempt will be made.
   * 
   * @param worldModel
   *          the interface that closed the connection.
   */
  public void connectionEnded(ClientWorldModelInterface worldModel);

  /**
   * Called when the connection is established. This method only indicates that
   * the underlying TCP/IP connection succeeded, but does not imply that
   * handshakes have been exchanged.
   * 
   * @param worldModel
   *          the interface that established the connection.
   */
  public void connectionEstablished(ClientWorldModelInterface worldModel);
}
