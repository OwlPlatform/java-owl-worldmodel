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
 * Expires all current Attribute values for an Identifier.
 * 
 * @author Robert Moore
 * 
 */
public class ExpireIdentifierMessage {

  /**
   * Logging facility for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(ExpireIdentifierMessage.class);

  /**
   * Message type value.
   */
  public static final byte MESSAGE_TYPE = 6;

  /**
   * Identifier to expire.
   */
  private String identifier;

  /**
   * Expiration timestamp.
   */
  private long expirationTime;

  /**
   * Origin of the Identifier to expire.
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

    // Creation time
    length += 8;

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
   * Gets the Identifier to expire.
   * 
   * @return the Identifier.
   */
  public String getId() {
    return this.identifier;
  }

  /**
   * Sets the Identifier to expire.
   * 
   * @param identifier
   *          the new Identifier value.
   */
  public void setId(String identifier) {
    this.identifier = identifier;
  }

  /**
   * Gets the expiration timestamp for the Identifier.
   * 
   * @return the expiration timestamp.
   */
  public long getExpirationTime() {
    return this.expirationTime;
  }

  /**
   * Sets the expiration timestamp for the Identifier.
   * 
   * @param expirationTime
   *          the new expiration timestamp.
   */
  public void setExpirationTime(long expirationTime) {
    this.expirationTime = expirationTime;
  }

  /**
   * Gets the Origin of the Identifier to expire.
   * 
   * @return the Origin
   */
  public String getOrigin() {
    return this.origin;
  }

  /**
   * Sets the Origin of the Identifier to expire.
   * 
   * @param origin
   *          the new Origin value.
   */
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @Override
  public String toString() {
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
        DateFormat.LONG);
    return "Expire " + this.identifier + " @ "
        + df.format(new Date(this.expirationTime)) + " from " + this.origin;
  }
}
