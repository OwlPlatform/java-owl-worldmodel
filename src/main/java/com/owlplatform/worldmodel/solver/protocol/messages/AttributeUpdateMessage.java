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

import com.owlplatform.worldmodel.Attribute;

/**
 * This message contains updated attribute values sent by the solver to the
 * World Model.
 */
public class AttributeUpdateMessage {

  /**
   * Message type value.
   */
  public static final byte MESSAGE_TYPE = 4;

  /**
   * 1-byte boolean value (0 for false, 1 for true) that indicates whether Identifiers
   * should be created when a solution is for a Identifier that is not present in the
   * world model.
   */
  private boolean createIdentifier = false;

  /**
   * The attributes associated with this Identifier, if any.
   */
  private Attribute[] attributes;

  /**
   * The length of this message when encoded according to the Solver-World Model
   * protocol.
   * 
   * @return the length, in bytes, of the encoded form of this message.
   */
  public int getMessageLength() {
    // Message Type, createIdentifier
    int length = 1 + 1;

    // Number of solutions
    length += 4;

    // Each solution
    if (this.attributes != null) {
      for (Attribute attr : this.attributes) {
        length += attr.getSolverLength();
      }
    }

    return length;
  }

  /**
   * Gets if this message's Identifier should be automatically created in the world model or not.
   * @return {@code true} if the Identifier should be automatically created in the world model.
   */
  public boolean getCreateId() {
    return this.createIdentifier;
  }

  /**
   * Sets if this message's Identifier should be automatically created in the world model or not.
   * @param createId {@code true} if the Identifier should be automatically created in the world model.
   */
  public void setCreateId(boolean createId) {
    this.createIdentifier = createId;
  }

  /**
   * Gets the attributes in this message.
   * @return the attributes.
   */
  public Attribute[] getAttributes() {
    return this.attributes;
  }

  /**
   * Sets the attributes in this message.
   * @param attributes the new attributes.
   */
  public void setAttributes(Attribute[] attributes) {
    this.attributes = attributes;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer("Attribute Update");
    if (this.createIdentifier) {
      sb.append("[Auto-Create]");
    }
    sb.append('\n');

    if (this.attributes != null) {
      for (Attribute sol : this.attributes) {
        sb.append(sol);
      }

    }
    return sb.toString();
  }
}
