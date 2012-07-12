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
 * Message for deleting Attribute values in the world model. Deleted attributes
 * have their entire history removed. To simply remove the current value of an
 * attribute, see {@link ExpireAttributeMessage}.
 * 
 * @author Robert Moore
 * 
 */
public class DeleteAttributeMessage {

  /**
   * Logging facility for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(DeleteAttributeMessage.class);

  /**
   * Message type value.
   */
  public static final byte MESSAGE_TYPE = 9;

  /**
   * The Identifier for the Attribute.
   */
  private String identifier;

  /**
   * The name of the Attribute to delete.
   */
  private String attributeName;

  /**
   * The origin of the Attribute.
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

    // Attribute name length
    length += 4;

    if (this.attributeName != null) {
      try {
        length += this.attributeName.getBytes("UTF-16BE").length;
      } catch (UnsupportedEncodingException e) {
        log.error("Unable to encode to UTF-16BE.");
      }
    }

    if (this.origin != null) {
      try {
        length += this.origin.getBytes("UTF-16BE").length;
      } catch (UnsupportedEncodingException uee) {
        log.error("Unable to encode to UTF-16BE.");
      }
    }

    return length;
  }

  /**
   * Gets the Identifier for the Attribute.
   * @return the Identifier.
   */
  public String getId() {
    return this.identifier;
  }

  /**
   * Sets the Identifier for the Attribute.
   * @param identifier the new Identifier.
   */
  public void setId(String identifier) {
    this.identifier = identifier;
  }

  /**
   * Gets the name of the Attribute to delete.
   * @return the name of the Attribute.
   */
  public String getAttributeName() {
    return this.attributeName;
  }

  /**
   * Sets the name of the Attribute to delete.
   * @param attributeName the new Attribute name.
   */
  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  /**
   * Sets the origin of the Attribute to delete.
   * @return the origin of the Attribute.
   */
  public String getOrigin() {
    return this.origin;
  }

  /**
   * Sets the origin of the Attribute.
   * @param origin the new origin value.
   */
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  
  @Override
  public String toString(){
    return "Delete Attribute " + this.identifier + "/" + this.attributeName + ":" + this.origin;
  }
}
