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

package com.owlplatform.worldmodel.solver.protocol.messages;

/**
 * Message to tell a Solver to start sending specific on-demand attributes.
 * 
 * @author Robert Moore
 * 
 */
public class StartOnDemandMessage {

  /**
   * Message type value.
   */
  public static final byte MESSAGE_TYPE = 2;

  /**
   * Set of on-demand requests made to the solver.
   */
  private OnDemandRequest[] transientRequests = null;

  /**
   * The length of this message when encoded according to the Solver-World Model
   * protocol.
   * 
   * @return the length, in bytes, of the encoded form of this message.
   */
  public int getMessageLength() {
    // Message type, number of transients
    int length = 1 + 4;
    if (this.transientRequests != null) {
      for (OnDemandRequest request : this.transientRequests) {
        length += request.getLength();
      }
    }

    return length;
  }

  /**
   * Gets the on-demand attribute requests in this message.
   * @return the on-demand attribute requests.
   */
  public OnDemandRequest[] getRequests() {
    return this.transientRequests;
  }

  /**
   * Sets the on-demand attribute requests for this message.
   * @param transientRequests the new requests.
   */
  public void setRequests(OnDemandRequest[] transientRequests) {
    this.transientRequests = transientRequests;
  }
}
