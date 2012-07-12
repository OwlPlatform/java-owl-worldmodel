package com.owlplatform.worldmodel.solver.protocol.messages;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class to represent on-demand requests to solvers.
 * @author Robert Moore
 *
 */
public class OnDemandRequest {
  /**
   * Logger for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(OnDemandRequest.class);

  /**
   * Alias value for this on-demand attribute.
   */
  private int attributeAlias;

  /**
   * The set of Identifier patterns the world model has requested.  Interpretation
   * of this data is up to the solver.
   */
  private String[] identifierPatterns = null;

  /**
   * Length of this object when encoded according to the Solver-World Model protocol.
   * @return the length, in bytes, of the encoded form of this object.
   */
  public int getLength() {
    // Alias, number of patterns
    int length = 4 + 4;

    if (this.identifierPatterns != null) {
      for (String identifier : this.identifierPatterns) {
        try {
          length += (4 + identifier.getBytes("UTF-16BE").length);
        } catch (UnsupportedEncodingException e) {
          log.error("Unable to encode to UTF-16BE.");
        }
      }
    }
    return length;
  }

  /**
   * Returns the attribute alias value for this request.
   * @return the alias value.
   */
  public int getAttributeAlias() {
    return this.attributeAlias;
  }

  /**
   * Sets the attribute alias value for this request.
   * @param attributeAlias the new alias value.
   */
  public void setAttributeAlias(int attributeAlias) {
    this.attributeAlias = attributeAlias;
  }

  /**
   * Gets the Identifier patterns for this request.
   * @return the Identifier patterns.
   */
  public String[] getIdPatterns() {
    return this.identifierPatterns;
  }

  /**
   * Sets the Identifier patterns for this request.
   * @param idPatterns the new Identifier pattern values.
   */
  public void setIdPatterns(String[] idPatterns) {
    this.identifierPatterns = idPatterns;
  }
}