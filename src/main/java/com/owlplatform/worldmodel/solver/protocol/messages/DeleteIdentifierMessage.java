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

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message to delete an entire Identifier from the World Model. Deleted
 * Identifiers have all of their Attributes and Attribute histories completely
 * removed as if they never existed. To simply expire all current Attribute
 * value for an Identifier instead, see {@link ExpireIdentifierMessage}.
 * 
 * @author Robert Moore
 * 
 */
public class DeleteIdentifierMessage {

  /**
   * Logging facility for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(DeleteIdentifierMessage.class);

  /**
   * Message type for this message.
   */
  public static final byte MESSAGE_TYPE = 7;

  /**
   * The Identifier to delete from the world model.
   */
  private String identifier;

  /**
   * The origin of the Identifier to delete.
   */
  private String origin;

  /**
   * The length of this message when encoded according to the Solver-World Model
   * protocol.
   * 
   * @return the length, in bytes, of the encoded form of this message.
   */
  public int getMessageLength() {
    // Message type, Id length
    int length = 1 + 4;

    if (this.identifier != null) {
      try {
        length += this.identifier.getBytes("UTF-16BE").length;
      } catch (UnsupportedEncodingException e) {
        log.error("Unable to encode to UTF-16BE.");
      }
    }

    if (this.origin != null) {
      try {
        length += this.origin.getBytes("UTF-16BE").length;
      } catch (UnsupportedEncodingException e) {
        log.error("Unable to encode to UTF-16BE.");
      }
    }

    return length;
  }

  /**
   * Gets the Identifier to delete.
   * 
   * @return the Identifier name.
   */
  public String getId() {
    return this.identifier;
  }

  /**
   * Sets the Identifier to delete.
   * 
   * @param identifier
   *          the new Identifier name.
   */
  public void setId(String identifier) {
    this.identifier = identifier;
  }

  /**
   * Gets the Origin of the Identifier.
   * 
   * @return the Origin of the Identifier.
   */
  public String getOrigin() {
    return this.origin;
  }

  /**
   * Sets the Origin of the Identifier.
   * 
   * @param origin
   *          the new Origin value.
   */
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @Override
  public String toString() {
    return "Delete Identifier " + this.identifier + ":" + this.origin;
  }
}
