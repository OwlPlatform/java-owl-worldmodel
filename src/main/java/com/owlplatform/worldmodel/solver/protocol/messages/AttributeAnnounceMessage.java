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
 * The Attribute Announce message tells the world model what Attributes to
 * expect from the solver.
 * 
 * @author Robert Moore
 * 
 */
public class AttributeAnnounceMessage {
  /**
   * Logging facility for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(AttributeAnnounceMessage.class);

  /**
   * Message type value.
   */
  public static final byte MESSAGE_TYPE = 1;

  /**
   * The Attributes that are being announced in this message.
   */
  private AttributeSpecification[] attributeSpecifications = null;

  /**
   * The origin of these Attributes.
   */
  private String origin = null;

  /**
   * The length of this message when encoded according to the Solver-World Model
   * protocol.
   * 
   * @return the length, in bytes, of the encoded form of this message.
   */
  public int getMessageLength() {
    // Message type, number of type specifications
    int length = 1 + 4;

    if (this.attributeSpecifications != null) {
      for (AttributeSpecification spec : this.attributeSpecifications) {
        // alias, Name length, transient byte
        length += (4 + 4 + 1);
        if (spec.getAttributeName() != null) {
          try {
            length += spec.getAttributeName().getBytes("UTF-16BE").length;
          } catch (UnsupportedEncodingException e) {
            log.error("Unable to encode UTF-16BE strings.");
          }
        }
      }
    }

    if (this.origin != null) {
      try {
        length += this.origin.getBytes("UTF-16BE").length;
      } catch (UnsupportedEncodingException e) {
        log.error("Unable to encode UTF-16 strings.");
        e.printStackTrace();
      }
    }

    return length;
  }

  /**
   * Gets the attribute specifications being announced in this message.
   * 
   * @return the attribute specifications being announced.
   */
  public AttributeSpecification[] getAttributeSpecifications() {
    return this.attributeSpecifications;
  }

  /**
   * Sets the attribute specifications being announced in this message.
   * 
   * @param attributeSpecifications
   *          the new attribute specifications.
   */
  public void setTypeSpecifications(
      AttributeSpecification[] attributeSpecifications) {
    this.attributeSpecifications = attributeSpecifications;
  }

  /**
   * A simple class used to store attribute specification and aliasing
   * information.
   * 
   * @author Robert Moore
   * 
   */
  public static class AttributeSpecification {
    /**
     * A numeric alias for this attribute. Used later when sending attribute
     * data to the world model.
     */
    private int alias;

    /**
     * The name of this attribute.
     */
    private String attributeName;

    /**
     * Indicates whether this attribute is transient or not.
     */
    private boolean isOnDemand = false;

    /**
     * Gets the alias value for this attribute.
     * 
     * @return the alias value.
     */
    public int getAlias() {
      return this.alias;
    }

    /**
     * Sets the alias value for this attribute.
     * 
     * @param alias
     *          the new alias value.
     */
    public void setAlias(int alias) {
      this.alias = alias;
    }

    /**
     * Gets the name of the attribute.
     * 
     * @return the name of the attribute.
     */
    public String getAttributeName() {
      return this.attributeName;
    }

    /**
     * Sets the attribute name.
     * 
     * @param attributeName
     *          the new attribute name.
     */
    public void setAttributeName(String attributeName) {
      this.attributeName = attributeName;
    }

    /**
     * 
     * @return {@code true} if this attribute is on-demand, else {@code false}.
     */
    public boolean getOnDemand() {
      return this.isOnDemand;
    }

    /**
     * Sets the attribute as on-demand or not.
     * 
     * @param onDemand
     *          the new value: {@code true} if the attribute is on-demand, or
     *          {@code false} if not.
     */
    public void setIsOnDemand(boolean onDemand) {
      this.isOnDemand = onDemand;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof AttributeSpecification) {
        return this.equals((AttributeSpecification) o);
      }
      return false;
    }

    /**
     * Compares this attribute specification to another based on the alias
     * value, name, and transient state.
     * 
     * @param o
     *          the other attribute specification.
     * @return {@code true} if they are equal, else {@code false}.
     */
    public boolean equals(AttributeSpecification o) {
      if (this.isOnDemand != o.isOnDemand) {
        return false;
      }
      if (this.attributeName == null && o.attributeName == null) {
        return true;
      }
      return this.attributeName.equals(o.attributeName);
    }

    @Override
    public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.isOnDemand) {
        sb.append("[T] ");
      }
      if (this.attributeName != null) {
        sb.append(this.attributeName).append("->");
      }
      sb.append(this.alias);
      return sb.toString();
    }

    @Override
    public int hashCode() {
      int hashcode = this.alias;
      if (this.attributeName != null) {
        hashcode |= this.attributeName.hashCode();
      }
      if (this.isOnDemand) {
        // Magic number!
        hashcode = hashcode ^ 0xE36A56C7;
      }
      return hashcode;
    }
  }

  /**
   * Gets the origin value for this attribute announce message.
   * 
   * @return the origin value.
   */
  public String getOrigin() {
    return this.origin;
  }

  /**
   * Sets the origin value for this attribute announce message.
   * 
   * @param origin
   *          the new origin value.
   */
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer("Type Announce ");
    if (this.origin != null) {
      sb.append('@').append(this.origin);
    }
    sb.append('\n');
    if (this.attributeSpecifications != null) {
      for (AttributeSpecification spec : this.attributeSpecifications) {
        sb.append('\t').append(spec.toString()).append('\n');
      }
    }

    return sb.toString();
  }
}
