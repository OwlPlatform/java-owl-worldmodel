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
package com.owlplatform.worldmodel.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.worldmodel.Attribute;
import com.owlplatform.worldmodel.solver.listeners.ConnectionListener;
import com.owlplatform.worldmodel.solver.listeners.DataListener;
import com.owlplatform.worldmodel.solver.protocol.messages.AttributeAnnounceMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.AttributeAnnounceMessage.AttributeSpecification;
import com.owlplatform.worldmodel.solver.protocol.messages.StartOnDemandMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StopOnDemandMessage;

/**
 * A simple class for solvers that need to push data into the World Model. This
 * interface is not suitable for solvers that produce transient data. Transient
 * solvers should use the lower-level {@link SolverWorldModelInterface} class
 * instead.
 * 
 * @author Robert Moore
 * 
 */
public class SolverWorldConnection {

  /**
   * A private event handler for this connection. Acts as a proxy to the World
   * Model Interface so that the required public methods aren't visible to
   * classes using SolverWorldConnection.
   * 
   * @author Robert Moore
   * 
   */
  private static final class Handler implements ConnectionListener,
      DataListener {

    /**
     * The SolverWorldConnection object that this handler is used by.
     */
    private final SolverWorldConnection parent;

    /**
     * Creates a new Handler with the provided SolverWorldConnection as its
     * parent.
     * 
     * @param parent
     *          the connection that this handler will interact with.
     */
    public Handler(final SolverWorldConnection parent) {
      super();
      this.parent = parent;
    }

    /**
     * Notifies the connection of the connection interruption.
     */
    @Override
    public void connectionInterrupted(SolverWorldModelInterface worldModel) {
      this.parent.connectionInterrupted(worldModel);
    }

    /**
     * Notifies the connection of the connection failure.
     */
    @Override
    public void connectionEnded(SolverWorldModelInterface worldModel) {
      this.parent.connectionEnded(worldModel);
    }

    /**
     * Notifies the connection of the connection establishment.
     */
    @Override
    public void connectionEstablished(SolverWorldModelInterface worldModel) {
      this.parent.connectionEstablished(worldModel);
    }

    /**
     * Notifies the connection that the world model has sent a transient request
     * message.
     */
    @Override
    public void startOnDemandReceived(SolverWorldModelInterface worldModel,
        StartOnDemandMessage message) {
      this.parent.startOnDemandReceived(worldModel, message);
    }

    /**
     * Notifies the connection that a specific transient request should be
     * stopped.
     */
    @Override
    public void stopOnDemandReceived(SolverWorldModelInterface worldModel,
        StopOnDemandMessage message) {
      this.parent.stopOnDemand(worldModel, message);
    }

    /**
     * Notifies the connection that the type specification messages have been
     * sent to the world model.
     */
    @Override
    public void attributeSpecificationsSent(
        SolverWorldModelInterface worldModel, AttributeAnnounceMessage message) {
      this.parent.attributeSpecificationsSent(worldModel, message);
    }

  }

  /**
   * Logger for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(SolverWorldConnection.class);

  /**
   * Handler for this object.
   */
  private final Handler handler = new Handler(this);

  /**
   * Low-level world model interface for this connection object.
   */
  private final SolverWorldModelInterface wmi = new SolverWorldModelInterface();

  /**
   * Flag to indicate that the world model is ready to accept solutions. This
   * typically means that the handshake messages have been exchanged.
   */
  private volatile boolean canSend = false;

  /**
   * Flag to indicate the the connection has been terminated.
   */
  private volatile boolean terminated = false;

  /**
   * A queue of solutions that should be sent to the world model the next time
   * the connection is available.
   */
  private final LinkedBlockingQueue<Attribute> attributeBuffer = new LinkedBlockingQueue<Attribute>(
      1000);

  /**
   * Creates a new {@code SolverWorldConnection} with the following parameters:
   * <ul>
   * <li>Connection retry delay: 5 seconds</li>
   * <li>Connection timeout: 5 seconds</li>
   * <li>Auto-create Identifiers: {@code true}</li>
   * <li>Disconnect on exception: {@code true}</li>
   * <li>Stay connected: {@code true}</li>
   * </ul>
   */
  public SolverWorldConnection() {
    super();
    this.wmi.setConnectionRetryDelay(5000l);
    this.wmi.setConnectionTimeout(5000l);
    this.wmi.setCreateIds(true);
    this.wmi.setDisconnectOnException(true);
    this.wmi.setStayConnected(true);

    this.wmi.addConnectionListener(this.handler);
    this.wmi.addDataListener(this.handler);
  }

  /**
   * Set the world model host or IP address.
   * 
   * @param wmHost
   *          the new world model hostname or IP address.
   */
  public void setHost(final String wmHost) {
    this.wmi.setHost(wmHost);
  }

  /**
   * Set the world model port number.
   * 
   * @param wmPort
   *          the new port number.
   */
  public void setPort(final int wmPort) {
    this.wmi.setPort(wmPort);
  }

  /**
   * Connects to the world model at the configured host and port. Returns
   * immediately if the connection fails. If the connection succeeds, automatic
   * reconnect will be in effect until {@link #disconnect()} is called.
   * 
   * @param timeout
   *          how long to wait for the connection, in milliseconds. If 0, the
   *          configured timeout value will be used.
   * 
   * @return {@code true} if the connection succeeds, else {@code false}.
   */
  public boolean connect(long timeout) {
    if (this.wmi.connect(timeout)) {
      this.wmi.setStayConnected(true);
      return true;
    }
    return false;
  }

  /**
   * Disconnects from the world model. Automatic reconnection will not occur.
   */
  public void disconnect() {
    this.wmi.disconnect();
  }

  @Override
  public String toString() {
    return "World Model (S) @ " + this.wmi.getHost() + ":" + this.wmi.getPort();
  }

  /**
   * Sends a single attribute value update to the world model, or buffers it to
   * be sent later if the World Model is not connected.
   * 
   * @param attribute
   *          the Attribute value to send.
   * @return {@code true} if the solution was sent immediately or buffered, and
   *         {@code false} if it was unable to be sent or buffered.
   * @throws IllegalStateException
   *           if this method is called once the world model connection has been
   *           destroyed.
   */
  public boolean updateAttribute(Attribute attribute)
      throws IllegalStateException {
    if (this.terminated) {
      throw new IllegalStateException(
          "Cannot send solutions to the World Model once the connection has been destroyed.");
    }

    if (this.canSend) {
      return this.wmi.updateAttribute(attribute);
    }
    return this.attributeBuffer.offer(attribute);
  }

  /**
   * Sends a collection of updated Attribute values to the world model, or
   * buffers them to be sent later if the World Model is not connected.
   * 
   * @param attributes
   *          the Attribute values to send.
   * @return {@code true} if the solutions were able to be sent immediately, and
   *         {@code false} if one or more were unable to be sent or were
   *         buffered for later transmission.
   * @throws IllegalStateException
   *           if this method is called once the world model connection has been
   *           destroyed.
   */
  public boolean updateAttributes(Collection<Attribute> attributes)
      throws IllegalStateException {
    if (this.terminated) {
      throw new IllegalStateException(
          "Cannot send solutions to the World Model once the connection has been destroyed.");
    }

    if (this.canSend) {
      return this.wmi.updateAttributes(attributes);
    }
    for (Attribute a : attributes) {
      if (!this.attributeBuffer.offer(a)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Adds the Attribute specification to the world model connection.
   * 
   * @param spec
   *          the Attribute specification to add to the world model connection.
   */
  public void addAttribute(AttributeSpecification spec) {
    this.wmi.addAttribute(spec);
  }

  /**
   * Sets the origin string value for this world model connection. The origin
   * string uniquely identifies a solver to the world model.
   * 
   * @param origin
   *          the origin string for this world model connection.
   */
  public void setOriginString(final String origin) {
    this.wmi.setOriginString(origin);
  }

  /**
   * Creates the specified Identifier in the world model, returning {@code true}
   * on success.
   * 
   * @param identifier
   *          the Identifier to create in the world model.
   * @return {@code true} if the command succeeds, else {@code false}.
   */
  public boolean createId(final String identifier) {
    return this.wmi.createId(identifier);
  }

  /**
   * Expires an Identifier, or one or more attributes of that Identifier. If
   * Attributes are specified, then they will be expired instead of the
   * Identifier.
   * 
   * @param identifier
   *          the Identifier to expire, or the Identifier of the attributes to
   *          expire.
   * @param timestamp
   *          the time at which the values are expired, in milliseconds since the UNIX epoch.
   * @param attributes
   *          one or more attribute names to expire. If none are specified, then
   *          the Identifier itself is expired.
   * @return {@code true} if all expirations are successful, else {@code false}
   *         .
   */
  public boolean expire(final String identifier, final long timestamp,
      final String... attributes) {
    
    if (attributes == null || attributes.length == 0) {
      return this.wmi.expireId(identifier, timestamp);
    }

    boolean retVal = true;
    for (String attribute : attributes) {
      retVal = retVal && this.wmi.expireAttribute(identifier, attribute, timestamp);
    }
    return retVal;
  }
  
  /**
   * Expires an Identifier, or one or more attributes of that Identifier. If
   * Attributes are specified, then they will be expired instead of the
   * Identifier.  The expiration time will be the current local time.
   * @param identifier
   *          the Identifier to expire, or the Identifier of the attributes to
   *          expire.
   * @param attributes
   *          one or more attribute names to expire. If none are specified, then
   *          the Identifier itself is expired.
   * @return {@code true} if all expirations are successful, else {@code false}
   */
  public boolean expire(final String identifier, final String... attributes){
    return this.expire(identifier, System.currentTimeMillis(),attributes);
  }

  /**
   * Deletes the specified Identifier or Attributes. If {@code attributes} is
   * null or of length 0, then the Identifier is deleted, otherwise the
   * specified attributes are deleted for the Identifier.
   * 
   * @param identifier
   *          the Identifier to delete, or the Identifier for the attributes to
   *          delete.
   * @param attributes
   *          one or more attributes to delete. If none are specified, then the
   *          Identifier itself is deleted.
   * @return {@code true} if all deletions are successful, else {@code false}.
   */
  public boolean delete(final String identifier, final String... attributes) {
    if (attributes == null || attributes.length == 0) {
      return this.wmi.deleteId(identifier);
    }
    boolean retVal = true;
    for (String attribute : attributes) {
      retVal = retVal && this.wmi.deleteAttribute(identifier, attribute);
    }
    return retVal;
  }

  /**
   * Sends any buffered Attribute values to the world model.
   */
  private void sendBufferedValues() {
    ArrayList<Attribute> attributesToSend = new ArrayList<Attribute>();
    int num = 0;
    while (!this.attributeBuffer.isEmpty()) {
      num += this.attributeBuffer.drainTo(attributesToSend);
    }
    if (num > 0) {
      this.wmi.updateAttributes(attributesToSend);
      log.info("Sent {} buffered attribute updates.", Integer.valueOf(num));
    }
  }

  /**
   * Marks the {@code canSend} flag to false.
   * 
   * @param worldModel
   *          the connection to the world model.
   */
  void connectionInterrupted(SolverWorldModelInterface worldModel) {
    this.canSend = false;
  }

  /**
   * Marks this connection as terminated, {@code canSend} as false.
   * 
   * @param worldModel
   *          the connection to the world model.
   */
  void connectionEnded(SolverWorldModelInterface worldModel) {
    this.terminated = true;
    this.canSend = false;
  }

  /**
   * Marks this connection as non-terminated, but {@code canSend} remains
   * {@code false}.
   * 
   * @param worldModel
   *          the connection to the world model.
   */
  void connectionEstablished(SolverWorldModelInterface worldModel) {
    this.terminated = false;
  }

  /**
   * Does nothing for now.
   * 
   * @param worldModel
   * @param message
   */
  void startOnDemandReceived(SolverWorldModelInterface worldModel,
      StartOnDemandMessage message) {
    // Nothing to do.

  }

  /**
   * Does nothing for now.
   * 
   * @param worldModel
   * @param message
   */
  void stopOnDemand(SolverWorldModelInterface worldModel,
      StopOnDemandMessage message) {
    // Nothing to do

  }

  /**
   * Marks {@code canSend} as true, sends any buffered Attribute updates.
   * 
   * @param worldModel
   *          the connection to the world model.
   * @param message
   *          the Attribute Specification message that was sent.
   */
  void attributeSpecificationsSent(SolverWorldModelInterface worldModel,
      AttributeAnnounceMessage message) {
    this.canSend = true;
    this.sendBufferedValues();
  }

  /**
   * Returns {@code true} if handshakes have been exchanged on an active
   * connection, else {@code false}.
   * 
   * @return {@code true} if the connection is ready for message exchange.
   */
  public boolean isConnectionLive() {
    return this.wmi.isReady();
  }
}
