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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.worldmodel.solver.listeners.ConnectionListener;
import com.owlplatform.worldmodel.solver.listeners.DataListener;
import com.owlplatform.worldmodel.solver.protocol.messages.StartOnDemandMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StopOnDemandMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.AttributeAnnounceMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.AttributeUpdateMessage.Solution;
import com.owlplatform.worldmodel.solver.protocol.messages.AttributeAnnounceMessage.AttributeSpecification;

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
    public void startTransientReceived(SolverWorldModelInterface worldModel,
        StartOnDemandMessage message) {
      this.parent.startTransientReceived(worldModel, message);
    }

    /**
     * Notifies the connection that a specific transient request should be
     * stopped.
     */
    @Override
    public void stopTransientReceived(SolverWorldModelInterface worldModel,
        StopOnDemandMessage message) {
      this.parent.stopTransientReceived(worldModel, message);
    }

    /**
     * Notifies the connection that the type specification messages have been
     * sent to the world model.
     */
    @Override
    public void typeSpecificationsSent(SolverWorldModelInterface worldModel,
        AttributeAnnounceMessage message) {
      this.parent.typeSpecificationsSent(worldModel, message);
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
  private final LinkedBlockingQueue<Solution> solutionBuffer = new LinkedBlockingQueue<Solution>(
      1000);

  public SolverWorldConnection() {
    super();
    this.wmi.setConnectionRetryDelay(5000l);
    this.wmi.setConnectionTimeout(5000l);
    this.wmi.setCreateUris(true);
    this.wmi.setDisconnectOnException(true);
    this.wmi.setStayConnected(false);

    this.wmi.addConnectionListener(this.handler);
    this.wmi.addDataListener(this.handler);
  }

  public void setHost(final String wmHost) {
    this.wmi.setHost(wmHost);
  }

  public void setPort(final int wmPort) {
    this.wmi.setPort(wmPort);
  }

  /**
   * Connects to the world model at the configured host and port.
   * 
   * @return {@code true} if the connection succeeds, else {@code false}.
   */
  public boolean connect() {
    if (this.wmi.doConnectionSetup()) {
      this.wmi.setStayConnected(true);
      return true;
    }
    return false;
  }

  /**
   * Permanently disconnects from the world model.
   */
  public void disconnect() {
    this.wmi.doConnectionTearDown();
  }

  @Override
  public String toString() {
    return "World Model (S) @ " + this.wmi.getHost() + ":" + this.wmi.getPort();
  }

  /**
   * Sends a single solution to the world model, or buffers it to be sent later
   * if the World Model is not connected.
   * 
   * @param solution
   *          the solution to send.
   * @return {@code true} if the solution was sent immediately or bufffered, and
   *         {@code false} if it was unable to be sent or buffered.
   * @throws IllegalStateException
   *           if this method is called once the world model connection has been
   *           destroyed.
   */
  public boolean sendSolution(Solution solution) throws IllegalStateException {
    if (this.terminated) {
      throw new IllegalStateException(
          "Cannot send solutions to the World Model once the connection has been destroyed.");
    }

    if (this.canSend) {
      return this.wmi.sendSolution(solution);
    }
    return this.solutionBuffer.offer(solution);
  }

  /**
   * Sends a collection of solutions to the world model, or buffers them to be
   * sent later if the World Model is not connected.
   * 
   * @param solutions
   *          the solutions to send.
   * @return {@code true} if the solutions were able to be sent immediately, and
   *         {@code false} if one or more were unable to be sent or were
   *         buffered for later transmission.
   * @throws IllegalStateException
   *           if this method is called once the world model connection has been
   *           destroyed.
   */
  public boolean sendSolutions(Collection<Solution> solutions)
      throws IllegalStateException {
    if (this.terminated) {
      throw new IllegalStateException(
          "Cannot send solutions to the World Model once the connection has been destroyed.");
    }

    if (this.canSend) {
      return this.wmi.sendSolutions(solutions);
    } else {
      for (Solution s : solutions) {
        if (!this.solutionBuffer.offer(s)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Adds the type specification to the world model interface.
   * 
   * @param spec
   *          the type specification to add to the world model interface.
   */
  public void addSolutionType(AttributeSpecification spec) {
    this.wmi.addType(spec);
  }

  /**
   * Sets the origin string value for this world model interface. The origin
   * string uniquely identifies a solver to the world model.
   * 
   * @param origin
   *          the origin string for this world model interface.
   */
  public void setOriginString(final String origin) {
    this.wmi.setOriginString(origin);
  }

  /**
   * Creates the specified URI in the world model, returning {@code true} on
   * success.
   * 
   * @param uri
   *          the URI to create in the world model.
   * @return {@code true} if the command succeeds, else {@code false}.
   */
  public boolean createURI(final String uri) {
    return this.wmi.createUri(uri);
  }

  /**
   * Expires a URI, or one or more attributes of that URI. If attributes are
   * specified, then they will be expired instead of the URI.
   * 
   * @param uri
   *          the URI to expire, or the URI of the attributes to expire.
   * @param attributes
   *          one or more attribute names to expire. If none are specified, then
   *          the URI itself is expired.
   * @returns {@code true} if all expirations are successful, else {@code false}
   *          .
   */
  public boolean expire(final String uri, final String... attributes) {
    long now = System.currentTimeMillis();
    if (attributes == null || attributes.length == 0) {
      return this.wmi.expireUri(uri, now);
    }

    boolean retVal = true;
    for (String attribute : attributes) {
      retVal = retVal && this.wmi.expireAttribute(uri, attribute, now);
    }
    return retVal;
  }

  /**
   * Deletes the specified URI or attributes. If {@code attributes} is null or
   * of length 0, then the URI is deleted, otherwise the specified attributes
   * are deleted for the URI.
   * 
   * @param uri
   *          the URI to delete, or the URI for the attributes to delete.
   * @param attributes
   *          one or more attributes to delete. If none are specified, then the
   *          URI itself is deleted.
   * @return {@code true} if all deletions are successful, else {@code false}.
   */
  public boolean delete(final String uri, final String... attributes) {
    if (attributes == null || attributes.length == 0) {
      return this.wmi.deleteUri(uri);
    }
    boolean retVal = true;
    for (String attribute : attributes) {
      retVal = retVal && this.wmi.deleteAttribute(uri, attribute);
    }
    return retVal;
  }

  /**
   * Sends any buffered solutions to the world model.
   */
  private void sendBufferedSolutions() {
    ArrayList<Solution> solutionsToSend = new ArrayList<Solution>();
    int num = 0;
    while (!this.solutionBuffer.isEmpty()) {
      num += this.solutionBuffer.drainTo(solutionsToSend);
    }
    if (num > 0) {
      this.wmi.sendSolutions(solutionsToSend);
      log.info("Sent {} buffered solutions.", num);
    }
  }

  void connectionInterrupted(SolverWorldModelInterface worldModel) {
    this.canSend = false;
  }

  void connectionEnded(SolverWorldModelInterface worldModel) {
    this.terminated = true;
    this.canSend = false;
  }

  void connectionEstablished(SolverWorldModelInterface worldModel) {
    this.terminated = false;
  }

  void startTransientReceived(SolverWorldModelInterface worldModel,
      StartOnDemandMessage message) {
    // TODO Auto-generated method stub

  }

  void stopTransientReceived(SolverWorldModelInterface worldModel,
      StopOnDemandMessage message) {
    // TODO Auto-generated method stub

  }

  void typeSpecificationsSent(SolverWorldModelInterface worldModel,
      AttributeAnnounceMessage message) {
    this.canSend = true;
    this.sendBufferedSolutions();
  }

  public boolean isConnectionLive() {
    return this.wmi.isReady();
  }
}
