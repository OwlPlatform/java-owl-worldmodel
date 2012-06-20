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

package com.owlplatform.worldmodel.solver.listeners;

import com.owlplatform.worldmodel.solver.SolverWorldModelInterface;

/**
 * Interface for classes that respond to connection-related events with the
 * world model.
 * 
 * @author Robert Moore
 * 
 */
public interface ConnectionListener {
  /**
   * Called when a connection has been closed, but when a reconnection is possible if
   * configured.
   * @param worldModel the world model that disconnected.
   */
  public void connectionInterrupted(SolverWorldModelInterface worldModel);

  /**
   * Called when a connection has been closed and no reconnection attempt will be made.
   * @param worldModel the world model that disconnected.
   */
  public void connectionEnded(SolverWorldModelInterface worldModel);

  /**
   * Called when a connection is established to the world model.
   * @param worldModel the world model that connected.
   */
  public void connectionEstablished(SolverWorldModelInterface worldModel);
}
