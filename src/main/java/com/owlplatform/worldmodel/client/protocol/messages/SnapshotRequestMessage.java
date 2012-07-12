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

package com.owlplatform.worldmodel.client.protocol.messages;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Snapshot Request message is sent by a client to the World Model in order
 * to request the current values of a set of Identifiers at a specific point in
 * time.
 * 
 * @author Robert Moore
 * 
 */
public class SnapshotRequestMessage extends AbstractRequestMessage {

  /**
   * Logging facility for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(SnapshotRequestMessage.class);

  /**
   * Message type value for Snapshot Request messages.
   */
  public static final byte MESSAGE_TYPE = 1;

  /**
   * Regular expression to match identifiers in the world model.
   */
  private String identifierRegex;

  /**
   * Regular expressions to match attributes in the world model.
   */
  private String[] attributeRegexes;

  /**
   * The earliest point in time to include data.
   */
  private long beginTimestamp;

  /**
   * The lastest point in time to include data.
   */
  private long endTimestamp;

  /**
   * Returns the message type for the Snapshot Request message.
   * 
   * @return the message type value for the Snapshot Request message.
   */
  public byte getMessageType() {
    return MESSAGE_TYPE;
  }

  /**
   * Returns the length of the message, in bytes, excluding the message length
   * prefix value.
   * 
   * @return the length of the message, in bytes, excluding the message length
   *         prefix value.
   */
  public int getMessageLength() {
    // Message type, ticket #
    int messageLength = 1 + 4;

    // Identifier length prefix
    messageLength += 4;

    if (this.identifierRegex != null) {
      try {
        messageLength += this.identifierRegex.getBytes("UTF-16BE").length;
      } catch (UnsupportedEncodingException e) {
        log.error("Unable to encode String into UTF-16.");
        e.printStackTrace();
      }
    }

    // Number of query attributes length prefix
    messageLength += 4;

    // Add length prefix and String length for each attribute
    if (this.attributeRegexes != null) {
      for (String attrib : this.attributeRegexes) {
        messageLength += 4;
        try {
          messageLength += attrib.getBytes("UTF-16BE").length;
        } catch (UnsupportedEncodingException e) {
          log.error("Unable to encode String into uTF-16");
          e.printStackTrace();
        }
      }
    }

    // Begin and end timestamps
    messageLength += 16;

    return messageLength;
  }

  /**
   * Gets the identifier regular expression for this request.
   * 
   * @return the identifier regular expression value.
   */
  public String getIdRegex() {
    return this.identifierRegex;
  }

  /**
   * Sets the identifier regular expression for this request.
   * 
   * @param idRegex
   *          the identifier regular expression.
   */
  public void setIdRegex(String idRegex) {
    this.identifierRegex = idRegex;
  }

  /**
   * Gets the attribute regular expressions for this request.
   * 
   * @return the attribute regular expressions for this request.
   */
  public String[] getAttributeRegexes() {
    return this.attributeRegexes;
  }

  /**
   * Sets the attribute regular expressions for this request.
   * 
   * @param attributeRegexes
   *          the attribute regular expressions for this request.
   */
  public void setAttributeRegexes(String[] attributeRegexes) {
    this.attributeRegexes = attributeRegexes;
  }

  /**
   * Gets the beginning timestamp for this request.
   * @return the beginning timestamp.
   */
  public long getBeginTimestamp() {
    return this.beginTimestamp;
  }

  /**
   * Sets the beginning timestamp for this request.
   * @param beginTimestamp the new beginning timestamp.
   */
  public void setBeginTimestamp(long beginTimestamp) {
    this.beginTimestamp = beginTimestamp;
  }

  /**
   * Gets the ending timestamp for this request.
   * @return the ending timestamp.
   */
  public long getEndTimestamp() {
    return this.endTimestamp;
  }

  /**
   * Sets the ending timestamp for this request.
   * @param endTimestamp the new ending timestamp.
   */
  public void setEndTimestamp(long endTimestamp) {
    this.endTimestamp = endTimestamp;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer("Snapshot Request (");
    sb.append(this.identifierRegex).append(")");
    sb.append(" from ").append(new Date(this.beginTimestamp)).append(" to ")
        .append(new Date(this.endTimestamp)).append(":\n");

    if (this.attributeRegexes != null) {
      for (String attrib : this.attributeRegexes) {
        sb.append(attrib).append('\n');
      }
    }
    return sb.toString();
  }
}
