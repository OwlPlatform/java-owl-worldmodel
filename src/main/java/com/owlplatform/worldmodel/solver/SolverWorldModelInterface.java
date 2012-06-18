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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.worldmodel.solver.WorldModelIoHandler;
import com.owlplatform.worldmodel.solver.listeners.ConnectionListener;
import com.owlplatform.worldmodel.solver.listeners.DataListener;
import com.owlplatform.worldmodel.solver.protocol.codec.WorldModelSolverProtocolCodecFactory;
import com.owlplatform.worldmodel.solver.protocol.messages.CreateURIMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DataTransferMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DeleteAttributeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DeleteURIMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.ExpireAttributeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.ExpireURIMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.HandshakeMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.KeepAliveMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StartTransientMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StopTransientMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.TypeAnnounceMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DataTransferMessage.Solution;
import com.owlplatform.worldmodel.solver.protocol.messages.TypeAnnounceMessage.TypeSpecification;

/**
 * Handles low-level network interaction with the World Model for solvers.
 * 
 * @author Robert Moore
 * 
 */
public class SolverWorldModelInterface implements SolverIoAdapter {

  /**
   * Logging facility for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(SolverWorldModelInterface.class);

  /**
   * Timeout value in seconds.
   */
  private static final int TIMEOUT_PERIOD = 60;

  /**
   * Host where the World Model is hosted.
   */
  private String host = null;

  /**
   * Port on which the World Model is listening for solver connections.
   */
  private int port = 7012;

  /**
   * The IoSession used to connect to the World Model
   */
  private IoSession session = null;

  /**
   * How long to wait (in milliseconds) on socket operations (open, close,
   * etc.).
   */
  private long connectionTimeout = 5000l;

  /**
   * How long to wait (in milliseconds) before reconnecting to the World Model.
   */
  private long connectionRetryDelay = 10000l;

  /**
   * Whether or not to disconnect from the World Model if an exception is
   * caught.
   */
  private boolean disconnectOnException = true;

  /**
   * Whether or not to reconnect to the World Model after the interface has
   * disconnected.
   */
  private boolean stayConnected = true;

  /**
   * The Handshake message sent to the World Model.
   */
  private HandshakeMessage sentHandshake = null;

  /**
   * The Handshake received from the World Model.
   */
  private HandshakeMessage receivedHandshake = null;

  /**
   * Flag to indicate that the connection is ready for solvers to interact with
   * the World Model.
   */
  private boolean connectionReady = false;

  /**
   * Indicates whether or not this World Model Interface is ready for solvers to
   * interact with the World Model. Specifically, it will return true when the
   * local handshake has been sent and the remote (World Model) handshake has
   * been received.
   * 
   * @return {@code true} if the connection is ready for interaction, else
   *         {@code false}.
   */
  public boolean isReady() {
    return this.connectionReady;
  }

  private NioSocketConnector connector = null;

  private WorldModelIoHandler ioHandler = new WorldModelIoHandler(this);

  private final ExecutorFilter executors = new ExecutorFilter(Runtime
      .getRuntime().availableProcessors());

  private final ConcurrentHashMap<String, Integer> solutionTypeAliases = new ConcurrentHashMap<String, Integer>();

  /**
   * Number of times the receiving side of the connection has become idle
   * (.5*TIMEOUT_PERIOD).
   */
  private volatile int receiveIdleTimes = 0;

  /**
   * Queue of interfaces that are interested in connection status events.
   */
  private final ConcurrentLinkedQueue<ConnectionListener> connectionListeners = new ConcurrentLinkedQueue<ConnectionListener>();

  /**
   * Queue of interfaces that are interested in data-related events.
   */
  private final ConcurrentLinkedQueue<DataListener> dataListeners = new ConcurrentLinkedQueue<DataListener>();

  /**
   * List of solution types to be sent to the World Model after handshaking.
   */
  private final ConcurrentLinkedQueue<TypeSpecification> solutions = new ConcurrentLinkedQueue<TypeSpecification>();

  /**
   * Origin String for the solver, sent to the World Model.
   */
  private String originString = null;

  /**
   * Whether or not to create target URI values if they don't already exist in
   * the World Model.
   */
  private boolean createUris = true;

  public boolean isCreateUris() {
    return createUris;
  }

  public void setCreateUris(boolean createUris) {
    this.createUris = createUris;
  }

  private boolean sentTypeSpecifications = false;

  public void addConnectionListener(final ConnectionListener listener) {
    this.connectionListeners.add(listener);
  }

  public void removeConnectionListener(final ConnectionListener listener) {
    this.connectionListeners.remove(listener);
  }

  public void addDataListener(final DataListener listener) {
    this.dataListeners.add(listener);
  }

  public void removeDataListener(final DataListener listener) {
    this.dataListeners.remove(listener);
  }

  @Override
  public void exceptionCaught(IoSession session, Throwable cause) {
    log.warn("Exception caught for {}: {}", this, cause);
    cause.printStackTrace();
    if (this.disconnectOnException) {
      this.disconnect();
    }
  }

  protected boolean setConnector() {
    if (this.host == null) {
      log.error("No host value set, cannot set up socket connector.");
      return false;
    }
    if (this.port < 0 || this.port > 65535) {
      log.error("Port value is invalid {}.", this.port);
      return false;
    }

    connector = new NioSocketConnector();
    this.connector.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE,
        SolverWorldModelInterface.TIMEOUT_PERIOD / 2);
    if (!connector.getFilterChain().contains(
        WorldModelSolverProtocolCodecFactory.CODEC_NAME)) {
      connector.getFilterChain().addLast(
          WorldModelSolverProtocolCodecFactory.CODEC_NAME,
          new ProtocolCodecFilter(
              new WorldModelSolverProtocolCodecFactory(true)));
    }
    connector.getFilterChain().addLast("ExecutorPool", this.executors);
    connector.setHandler(this.ioHandler);
    log.debug("Connector set up successful.");
    return true;
  }

  /**
   * Initiates a connection to the World Model (if it is not yet connected).
   * 
   * @return true if the connection is established.
   */
  public boolean doConnectionSetup() {
    if (this.connector == null) {
      if (!this.setConnector()) {
        log.error("Unable to set up connection to the World Model.");
        return false;
      }
    }

    if (this.session != null) {
      log.error("Already connected!");
      return false;
    }

    do {
      if (this.connect()) {
        log.debug("Connection succeeded!");
        return true;
      }

      if (this.stayConnected) {
        try {
          log.warn(String
              .format(
                  "Connection to World Model at %s:%d failed, waiting %dms before retrying.",
                  this.host, this.port, this.connectionRetryDelay));
          Thread.sleep(this.connectionRetryDelay);
        } catch (InterruptedException ie) {
          // Ignored
        }
      }
    } while (this.stayConnected);

    this.disconnect();
    this.finishConnection();

    return false;
  }

  void finishConnection() {
    this.connector.dispose();
    this.connector = null;
    for (ConnectionListener listener : this.connectionListeners) {
      listener.connectionEnded(this);
    }
    this.executors.destroy();
  }

  public void doConnectionTearDown() {
    // Make sure we don't automatically reconnect
    this.stayConnected = false;
    this.disconnect();
  }

  protected boolean connect() {

    ConnectFuture connFuture = this.connector.connect(new InetSocketAddress(
        this.host, this.port));
    if (!connFuture.awaitUninterruptibly(connectionTimeout)) {
      return false;
    }
    if (!connFuture.isConnected()) {
      return false;
    }

    try {
      log.debug("Attempting connection to {}:{}.", this.host, this.port);
      this.session = connFuture.getSession();
    } catch (RuntimeIoException ioe) {
      log.error(String.format("Could not create session to World Model %s:%d.",
          this.host, this.port), ioe);
      return false;
    }
    return true;
  }

  protected void disconnect() {
    if (this.session != null) {
      log.debug(
          "Closing connection to World Model (solver) at {} (waiting {}ms).",
          this.session.getRemoteAddress(), this.connectionTimeout);
      this.session.close(false).awaitUninterruptibly(connectionTimeout);
      this.session = null;
      this.sentHandshake = null;
      this.receivedHandshake = null;
      this.receiveIdleTimes = 0;
      this.sentTypeSpecifications = false;
      for (ConnectionListener listener : this.connectionListeners) {
        listener.connectionInterrupted(this);
      }
    }
  }

  @Override
  public void sessionIdle(IoSession session, IdleStatus status) {
    if (status.equals(IdleStatus.WRITER_IDLE)) {
      log.debug("Writing Keep-Alive message to World Model at {}",
          this.session.getRemoteAddress());
      this.session.write(KeepAliveMessage.MESSAGE);
    }
  }

  @Override
  public void connectionOpened(IoSession session) {
    if (this.session == null) {
      log.warn("Session was not correctly stored during connection set-up.");
      this.session = session;
    }

    log.info("Connected to World Model (solver) at {}.",
        session.getRemoteAddress());

    for (ConnectionListener listener : this.connectionListeners) {
      listener.connectionEstablished(this);
    }

    log.debug("Attempting to write handshake.");
    this.session.write(HandshakeMessage.getDefaultMessage());
  }

  @Override
  public void connectionClosed(IoSession session) {
    this.connectionReady = false;
    this.disconnect();
    if (this.stayConnected) {
      log.info("Reconnecting to World Model (solver) at {}:{}", this.host,
          this.port);

      if (this.doConnectionSetup()) {
        return;
      }
      SolverWorldModelInterface.this.finishConnection();

    } else {
      this.finishConnection();
    }
  }

  @Override
  public void handshakeReceived(IoSession session, HandshakeMessage message) {
    this.receiveIdleTimes = 0;
    log.debug("Received {}", message);
    this.receivedHandshake = message;
    Boolean handshakeCheck = this.checkHandshake();
    if (handshakeCheck == null) {
      return;
    }

    if (Boolean.FALSE.equals(handshakeCheck)) {
      log.warn("Handshakes did not match.");
      this.disconnect();
    }
    if (Boolean.TRUE.equals(handshakeCheck)) {
      log.debug("Handshakes matched with {}.", this);
    }
  }

  protected Boolean checkHandshake() {
    if (this.sentHandshake == null) {
      log.debug("Sent handshake is null, not checking.");
      return null;
    }
    if (this.receivedHandshake == null) {
      log.debug("Received handshake is null, not checking.");
      return null;
    }

    if (!this.sentHandshake.equals(this.receivedHandshake)) {
      log.error(
          "Handshakes do not match.  Closing connection to world model at {}.",
          this.session.getRemoteAddress());
      boolean prevValue = this.stayConnected;
      this.stayConnected = false;
      this.disconnect();
      this.stayConnected = prevValue;
      return Boolean.FALSE;
    }
    this.connectionReady = true;
    this.announceTypes();
    return Boolean.TRUE;
  }

  protected void announceTypes() {
    if (this.originString == null) {
      log.error("Unable to announce solution types, no Origin String set.");
      return;
    }
    this.sentTypeSpecifications = true;
    if (this.solutions.size() > 0) {
      TypeAnnounceMessage message = new TypeAnnounceMessage();

      message.setOrigin(this.originString);

      ArrayList<TypeSpecification> specificationList = new ArrayList<TypeSpecification>();
      specificationList.addAll(this.solutions);
      TypeSpecification[] specs = new TypeSpecification[specificationList
          .size()];
      int specAlias = 0;
      for (TypeSpecification spec : specificationList) {
        specs[specAlias] = spec;
        spec.setTypeAlias(specAlias++);
        this.solutionTypeAliases.put(spec.getUriName(),
            Integer.valueOf(spec.getTypeAlias()));
      }
      message.setTypeSpecifications(specs);
      this.session.write(message);
      this.sentTypeSpecifications = true;
    }
  }

  @Override
  public void keepAliveReceived(IoSession session, KeepAliveMessage message) {
    this.receiveIdleTimes = 0;
    log.debug("Received Keep-Alive message.");
  }

  @Override
  public void typeAnnounceReceived(IoSession session,
      TypeAnnounceMessage message) {
    log.error("World Model should not send type announce messages to the solver.");
    this.disconnect();
  }

  @Override
  public void startTransientReceived(IoSession session,
      StartTransientMessage message) {
    log.debug("Received Start Transient message from world model.");
    for (DataListener listener : this.dataListeners) {
      listener.startTransientReceived(this, message);
    }
  }

  @Override
  public void stopTransientReceived(IoSession session,
      StopTransientMessage message) {
    log.debug("Received Stop Transient message from world model.");
    for (DataListener listener : this.dataListeners) {
      listener.stopTransientReceived(this, message);
    }
  }

  @Override
  public void dataTransferReceived(IoSession session,
      DataTransferMessage message) {
    log.error("World Model should not send Data Transfer messages to solvers.");
    this.disconnect();
  }

  @Override
  public void createUriReceived(IoSession session, CreateURIMessage message) {
    log.error("World Model should not send Create URI messages to solvers.");
    this.disconnect();
  }

  @Override
  public void expireUriReceived(IoSession session, ExpireURIMessage message) {
    log.error("World Model should not send Expire URI messages to solvers.");
    this.disconnect();
  }

  @Override
  public void deleteUriReceived(IoSession session, DeleteURIMessage message) {
    log.error("World Model should not send Delete URI messages to solvers.");
    this.disconnect();
  }

  @Override
  public void expireAttributeReceived(IoSession session,
      ExpireAttributeMessage message) {
    log.error("World Model should not send Expire Attribute messages to solvers.");
    this.disconnect();
  }

  @Override
  public void deleteAttributeReceived(IoSession session,
      DeleteAttributeMessage message) {
    log.error("World Model should ot send Delete Attribute messages to solvers.");
    this.disconnect();
  }

  @Override
  public void handshakeSent(IoSession session, HandshakeMessage message) {
    log.debug("Sent {}", message);
    this.sentHandshake = message;
    Boolean handshakeCheck = this.checkHandshake();
    if (handshakeCheck == null) {
      return;
    }

    if (Boolean.FALSE.equals(handshakeCheck)) {
      log.warn("Handshakes did not match.");
      this.disconnect();
    }
    if (Boolean.TRUE.equals(handshakeCheck)) {
      log.debug("Handshakes matched with {}.", this);
    }
  }

  @Override
  public void keepAliveSent(IoSession session, KeepAliveMessage message) {
    log.debug("Sending Keep-Alive message.");
  }

  @Override
  public void typeAnnounceSent(IoSession session, TypeAnnounceMessage message) {
    log.debug("Sent Type Announce message to {}: {}", this, message);
    for (DataListener listener : this.dataListeners) {
      listener.typeSpecificationsSent(this, message);
    }
  }

  @Override
  public void startTransientSent(IoSession session,
      StartTransientMessage message) {
    log.error("Solver should not send Start Transient messages to the World Model.");
    this.disconnect();
  }

  @Override
  public void stopTransientSent(IoSession session, StopTransientMessage message) {
    log.error("Solver should not send Stop Transient messages to the World Model.");
    this.disconnect();
  }

  @Override
  public void dataTransferSent(IoSession session, DataTransferMessage message) {
    log.debug("Sent Data Transfer to {}: {}", this, message);
  }

  @Override
  public void createUriSent(IoSession session, CreateURIMessage message) {
    log.debug("Sent Create URI to {}: {}", this, message);
  }

  @Override
  public void expireUriSent(IoSession session, ExpireURIMessage message) {
    log.debug("Sent Expire URI to {}: {}", this, message);
  }

  @Override
  public void deleteUriSent(IoSession session, DeleteURIMessage message) {
    log.debug("Sent Delete URI to {}: {}", this, message);
  }

  @Override
  public void expireAttributeSent(IoSession session,
      ExpireAttributeMessage message) {
    log.debug("Sent Expire Attribute to {}: {}", this, message);
  }

  @Override
  public void deleteAttributeSent(IoSession session,
      DeleteAttributeMessage message) {
    log.debug("Sent Delete Attribute to {}: {}", this, message);
  }

  public boolean sendSolution(final Solution solution) {
    if (!this.sentTypeSpecifications) {
      log.error("Haven't sent type specifications yet, can't send solutions.");
      return false;
    }

    DataTransferMessage message = new DataTransferMessage();

    message.setCreateUri(this.createUris);
    message.setSolutions(new Solution[] { solution });

    Integer solutionTypeAlias = this.solutionTypeAliases.get(solution
        .getAttributeName());
    if (solutionTypeAlias == null) {
      log.error("Cannot send solution: Unregistered attribute type: {}",
          solution.getAttributeName());
      return false;
    }

    solution.setAttributeNameAlias(solutionTypeAlias.intValue());

    this.session.write(message);
    log.debug("Sent {} to {}", message, this);

    return true;
  }

  public boolean sendSolutions(final Collection<Solution> solutions) {
    if (!this.sentTypeSpecifications) {
      log.error("Haven't sent type specifications yet, can't send solutions.");
      return false;
    }
    for (Iterator<Solution> iter = solutions.iterator(); iter.hasNext();) {
      Solution soln = iter.next();
      Integer solutionTypeAlias = this.solutionTypeAliases.get(soln
          .getAttributeName());
      if (solutionTypeAlias == null) {
        log.error("Cannot send solution: Unregistered attribute type: {}",
            soln.getAttributeName());
        iter.remove();
        continue;
      }
      soln.setAttributeNameAlias(solutionTypeAlias.intValue());
    }

    DataTransferMessage message = new DataTransferMessage();

    message.setCreateUri(this.createUris);

    message.setSolutions(solutions.toArray(new Solution[] {}));

    this.session.write(message);

    return true;
  }

  public void addType(TypeSpecification specification) {
    synchronized (this.solutions) {
      if (!this.solutions.contains(specification)) {
        this.solutions.add(specification);

        if (this.sentTypeSpecifications) {
          this.announceTypes();
        }
      }
    }
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public long getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(long connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public long getConnectionRetryDelay() {
    return connectionRetryDelay;
  }

  public void setConnectionRetryDelay(long connectionRetryDelay) {
    this.connectionRetryDelay = connectionRetryDelay;
  }

  public boolean isDisconnectOnException() {
    return disconnectOnException;
  }

  public void setDisconnectOnException(boolean disconnectOnException) {
    this.disconnectOnException = disconnectOnException;
  }

  public boolean isStayConnected() {
    return stayConnected;
  }

  public void setStayConnected(boolean stayConnected) {
    this.stayConnected = stayConnected;
  }

  public String getOriginString() {
    return originString;
  }

  public void setOriginString(String originString) {
    this.originString = originString;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("Solver-World Model Interface");
    if (this.host != null) {
      sb.append(" (").append(this.host);
      if (this.port > 0) {
        sb.append(":").append(this.port);
      }
      sb.append(")");
    }
    return sb.toString();

  }

  public boolean expireUri(final String uri, final long expirationTime) {

    if (uri == null) {
      log.error("Unable to expire a null URI value.");
      return false;
    }

    if (this.originString == null) {
      log.error("Origin has not been set.  Cannot expire URIs without a valid origin.");
      return false;
    }
    ExpireURIMessage message = new ExpireURIMessage();
    message.setOrigin(this.originString);
    message.setUri(uri);
    message.setExpirationTime(expirationTime);

    this.session.write(message);
    log.debug("Sent {}", message);

    return true;
  }

  public boolean expireAttribute(final String uri, final String attribute,
      final long expirationTime) {
    if (uri == null) {
      log.error("Unable to expire an attribute with a null URI value.");
      return false;
    }

    if (attribute == null) {
      log.error("Unable to expire a null attribute.");
      return false;
    }

    if (this.originString == null) {
      log.error("Origin has not been set.  Cannot expire attributes without a valid origin.");
      return false;
    }

    ExpireAttributeMessage message = new ExpireAttributeMessage();

    message.setUri(uri);
    message.setAttributeName(attribute);
    message.setExpirationTime(expirationTime);
    message.setOrigin(this.originString);

    this.session.write(message);
    log.debug("Sent {}", message);

    return true;
  }

  public boolean deleteUri(final String uri) {
    if (uri == null) {
      log.error("Unable to delete a null URI value.");
      return false;
    }

    if (this.originString == null) {
      log.error("Origin has not been set.  Cannot delete URIs without a valid origin.");
      return false;
    }

    DeleteURIMessage message = new DeleteURIMessage();
    message.setOrigin(this.originString);
    message.setUri(uri);

    this.session.write(message);
    log.debug("Sent {}", message);

    return true;
  }

  public boolean deleteAttribute(final String uri, final String attribute) {
    if (uri == null) {
      log.error("Unable to delete an attribute with a null URI value.");
      return false;
    }

    if (attribute == null) {
      log.error("Unable to delete a null attribute.");
      return false;
    }

    if (this.originString == null) {
      log.error("Origin has not been set.  Cannot delete attributes without a valid origin.");
      return false;
    }

    DeleteAttributeMessage message = new DeleteAttributeMessage();
    message.setOrigin(this.originString);
    message.setUri(uri);
    message.setAttributeName(attribute);

    this.session.write(message);
    log.debug("Sent {}", message);

    return true;
  }

  public boolean createUri(final String uri) {

    if (uri == null) {
      log.error("Unable to create a null URI.");
      return false;
    }

    if (this.originString == null) {
      log.error("Origin has not been set.  Cannot create URIs without a valid origin.");
      return false;
    }

    CreateURIMessage message = new CreateURIMessage();
    message.setCreationTime(System.currentTimeMillis());
    message.setOrigin(this.originString);
    message.setUri(uri);

    this.session.write(message);
    log.debug("Sent {}", message);

    return true;
  }

  public int getCachedWrites() {
    return this.session.getScheduledWriteMessages();
  }
}
