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
import java.text.DateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Expires the current value for an Attribute in the world model. To completely
 * remove (delete) an attribut from the world model, see
 * {@link DeleteAttributeMessage}.
 * 
 * @author Robert Moore
 * 
 */
public class ExpireAttributeMessage {

  /**
   * Logging facility for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(ExpireAttributeMessage.class);

  /**
   * Message type value.
   */
  public static final byte MESSAGE_TYPE = 8;

  /**
   * The Identifier of the Attribute to expire.
   */
  private String identifier;

  /**
   * The attribute to expire.
   */
  private String attributeName;

  /**
   * The time at which the attribute is expired.
   */
  private long expirationTime;

  /**
   * The origin of the attribute.
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

    // Expiration time
    length += 8;

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
   * Returns the Identifier of the Attribute to expire.
   * 
   * @return the Identifier.
   */
  public String getId() {
    return this.identifier;
  }

  /**
   * Sets the Identifier of the Attribute to expire.
   * 
   * @param identifier
   *          the new value of the Identifier.
   */
  public void setId(String identifier) {
    this.identifier = identifier;
  }

  /**
   * Gets the name of the Attribute to expire.
   * 
   * @return the name of the Attribute.
   */
  public String getAttributeName() {
    return this.attributeName;
  }

  /**
   * Sets the name of the Attribute to expire.
   * 
   * @param attributeName
   *          the name of the Attribute.
   */
  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  /**
   * Gets the expiration timestamp of the Attribute.
   * 
   * @return the expiration timestamp.
   */
  public long getExpirationTime() {
    return this.expirationTime;
  }

  /**
   * Sets the expiration timestamp for the Attribute.
   * 
   * @param expirationTime
   */
  public void setExpirationTime(long expirationTime) {
    this.expirationTime = expirationTime;
  }

  /**
   * Get the origin of the Attribute to expire.
   * 
   * @return the Origin value.
   */
  public String getOrigin() {
    return this.origin;
  }

  /**
   * Sets the Origin of the Attribute to expire.
   * 
   * @param origin
   *          the new Origin value.
   */
  public void setOrigin(String origin) {
    this.origin = origin;
  }
  
  @Override
  public String toString(){
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG);
    return "Expire " + this.identifier + "/" + this.attributeName + " @ " + df.format(new Date(this.expirationTime)) + " from " + this.origin;
  }

}
