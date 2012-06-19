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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the World Model with a list of preferred origins for the client.
 * Preferred origins will be provided to the client in order of preference and
 * availability. This means that of the available origins for an Attribute
 * value, the one with the highest preference value is returned. By default, all
 * origins have a preference value of 0.
 * 
 * @author Robert Moore
 * 
 */
public class OriginPreferenceMessage {
  /**
   * Logging facility for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(OriginPreferenceMessage.class);

  /**
   * The message type for the Origin Preference message.
   */
  public static final byte MESSAGE_TYPE = 11;

  /**
   * Mapping of origin strings to weight values.
   */
  private Map<String, Integer> weights;

  /**
   * Sets the new origin preference values for this message.
   * 
   * @param weights
   *          the new origin preference values.
   *          
   */
  public void setWeights(final Map<String, Integer> weights) {
    this.weights = weights;
  }

  /**
   * Returns the length of this message when encoded according to the
   * Client-World Model protocol.
   * 
   * @return the length, in bytes, of the encoded form of this message.
   */
  public int getMessageLength() {
    // Message ID
    int length = 1;

    if (this.weights != null) {
      for (String origin : this.weights.keySet()) {
        // String prefix, weight
        length += 8;
        try {
          length += origin.getBytes("UTF-16BE").length;
        } catch (UnsupportedEncodingException uee) {
          log.error("Unable to encode to UTF-16BE.");
        }
      }
    }

    return length;
  }

  /**
   * Returns the mapping of Origin name to weight value for this message.
   * 
   * @return the map of origin weights for this message, or {@code null} if none
   *         have been set.
   */
  public Map<String, Integer> getWeights() {
    return this.weights;
  }
}
