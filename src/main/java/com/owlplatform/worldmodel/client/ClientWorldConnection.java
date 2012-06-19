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

package com.owlplatform.worldmodel.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.worldmodel.Attribute;
import com.owlplatform.worldmodel.client.listeners.ConnectionListener;
import com.owlplatform.worldmodel.client.listeners.DataListener;
import com.owlplatform.worldmodel.client.protocol.messages.AbstractRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.AttributeAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.DataResponseMessage;
import com.owlplatform.worldmodel.client.protocol.messages.OriginAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.OriginPreferenceMessage;
import com.owlplatform.worldmodel.client.protocol.messages.RangeRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.SnapshotRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.StreamRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.IdSearchResponseMessage;

/**
 * A simple class for clients or solvers that need to request data from the
 * World Model.
 * 
 * @author Robert Moore
 * 
 */
public class ClientWorldConnection {

  /**
   * Simple nested class to hide the listener methods for the World Model
   * interface.
   * 
   * @author Robert Moore
   * 
   */
  private static final class Handler implements ConnectionListener,
      DataListener {

    private final ClientWorldConnection client;

    public Handler(final ClientWorldConnection client) {
      this.client = client;
    }

    @Override
    public void requestCompleted(ClientWorldModelInterface worldModel,
        AbstractRequestMessage message) {
      this.client.requestCompleted(worldModel, message);

    }

    @Override
    public void dataResponseReceived(ClientWorldModelInterface worldModel,
        DataResponseMessage message) {
      this.client.dataResponseReceived(worldModel, message);
    }

    @Override
    public void idSearchResponseReceived(ClientWorldModelInterface worldModel,
        IdSearchResponseMessage message) {
      this.client.uriSearchResponseReceived(worldModel, message);
    }

    @Override
    public void attributeAliasesReceived(ClientWorldModelInterface worldModel,
        AttributeAliasMessage message) {
      this.client.attributeAliasesReceived(worldModel, message);
    }

    @Override
    public void originAliasesReceived(ClientWorldModelInterface worldModel,
        OriginAliasMessage message) {
      this.client.originAliasesReceived(worldModel, message);
    }

    @Override
    public void originPreferenceSent(ClientWorldModelInterface worldModel,
        OriginPreferenceMessage message) {
      this.client.originPreferenceSent(worldModel, message);
    }

    @Override
    public void connectionInterrupted(ClientWorldModelInterface worldModel) {
      this.client.connectionInterrupted(worldModel);
    }

    @Override
    public void connectionEnded(ClientWorldModelInterface worldModel) {
      this.client.connectionEnded(worldModel);
    }

    @Override
    public void connectionEstablished(ClientWorldModelInterface worldModel) {
      this.client.connectionEstablished(worldModel);
    }

  }

  /**
   * Logging utility for this class.
   */
  private static final Logger log = LoggerFactory
      .getLogger(ClientWorldConnection.class);

  /**
   * A map of outstanding snapshot request responses. Used to set the World
   * State when a snapshot request completes. The key is the request ticket
   * number.
   */
  private final Map<Long, Response> outstandingSnapshots = new ConcurrentHashMap<Long, Response>();

  /**
   * A map of outstanding range and streaming request responses. Used to update
   * the World State when a range or streaming request message receives updated
   * values. The key is the request ticket number.
   */
  private final Map<Long, StepResponse> outstandingSteps = new ConcurrentHashMap<Long, StepResponse>();

  /**
   * A map of WorldState objects for Responses returned by snapshot requests.
   * The key is the request ticket number.
   */
  private final Map<Long, WorldState> outstandingStates = new ConcurrentHashMap<Long, WorldState>();

  private ClientWorldModelInterface wmi = new ClientWorldModelInterface();

  private volatile boolean isConnected = false;

  private final Handler handler = new Handler(this);

  private final LinkedBlockingQueue<String[]> uriSearchResponses = new LinkedBlockingQueue<String[]>();

  public boolean isConnected() {
    return isConnected;
  }

  public ClientWorldConnection() {
    super();
    this.wmi.setStayConnected(false);
    this.wmi.setConnectionRetryDelay(1000l);
    this.wmi.setDisconnectOnException(true);
    this.wmi.setConnectionTimeout(1000l);
    this.wmi.addConnectionListener(this.handler);
    this.wmi.addDataListener(this.handler);
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

  public void setHost(final String host) {
    this.wmi.setHost(host);
  }

  public void setPort(final int port) {
    this.wmi.setPort(port);
  }

  @Override
  public String toString() {
    return "World Model (C) @ " + this.wmi.getHost() + ":" + this.wmi.getPort();
  }

  public Response getSnapshot(final String uriRegex, final long start,
      final long end, String... attributes) {
    SnapshotRequestMessage req = new SnapshotRequestMessage();
    req.setIdRegex(uriRegex);
    req.setBeginTimestamp(start);
    req.setEndTimestamp(end);
    if (attributes != null) {
      req.setAttributeRegexes(attributes);
    }
    Response resp = new Response(this, 0);
    try {
      while (!this.isConnected) {
        log.debug("Trying to wait until connection is ready.");
        synchronized (this) {
          try {
            this.wait();
          } catch (InterruptedException ie) {
          }
        }
      }
      long reqId = this.wmi.sendMessage(req);
      resp.setTicketNumber(reqId);
      this.outstandingSnapshots.put(Long.valueOf(reqId), resp);
      WorldState ws = new WorldState();
      this.outstandingStates.put(Long.valueOf(reqId), ws);

      return resp;
    } catch (Exception e) {
      resp.setError(e);
      return resp;
    }
  }

  public Response getCurrentSnapshot(final String uriRegex,
      String... attributes) {
    return this.getSnapshot(uriRegex, 0l, 0l, attributes);
  }

  public StepResponse getRangeRequest(final String uriRegex, final long start,
      final long end, String... attributes) {
    RangeRequestMessage req = new RangeRequestMessage();
    req.setIdRegex(uriRegex);
    req.setBeginTimestamp(start);
    req.setEndTimestamp(end);
    if (attributes != null) {
      req.setAttributeRegexes(attributes);
    }
    StepResponse resp = new StepResponse(this, 0);
    try {
      while (!this.isConnected) {
        log.debug("Trying to wait until connection is ready.");
        synchronized (this) {
          try {
            this.wait();
          } catch (InterruptedException ie) {
            // Ignored
          }
        }
      }
      long reqId = this.wmi.sendMessage(req);
      resp.setTicketNumber(reqId);
      this.outstandingSteps.put(Long.valueOf(reqId), resp);

      return resp;
    } catch (Exception e) {
      resp.setError(e);
      return resp;
    }
  }

  public StepResponse getStreamRequest(final String uriRegex, final long start,
      final long interval, String... attributes) {
    StreamRequestMessage req = new StreamRequestMessage();
    req.setIdRegex(uriRegex);
    req.setBeginTimestamp(start);
    req.setUpdateInterval(interval);
    if (attributes != null) {
      req.setAttributeRegexes(attributes);
    }

    StepResponse resp = new StepResponse(this, 0);
    try {
      while (!this.isConnected) {
        log.debug("Trying to wait until connection is ready.");
        synchronized (this) {
          try {
            this.wait();
          } catch (InterruptedException ie) {
            // Ignored
          }
        }
      }
      long reqId = this.wmi.sendMessage(req);
      resp.setTicketNumber(reqId);
      this.outstandingSteps.put(Long.valueOf(reqId), resp);

      return resp;
    } catch (Exception e) {
      resp.setError(e);
      return resp;
    }
  }

  public String[] searchURI(final String uriRegex) {
    synchronized (this.uriSearchResponses) {
      if (!this.wmi.searchURIRegex(uriRegex)) {
        log.warn("Attempted to search for a null URI regex. Not sending.");
        return new String[] {};
      }
      while (this.uriSearchResponses.isEmpty()) {
        log.info("Waiting for response.");
        try {
          return this.uriSearchResponses.take();
        } catch (InterruptedException ie) {
          // Ignored
        }
      }
      log.error("Unable to retrieve matching URI values for {}.", uriRegex);
      return new String[] {};
    }

  }

  void cancelSnapshot(final long ticketNumber) {
    // Send a cancel request
    this.wmi.cancelRequest(ticketNumber);
  }

  void connectionInterrupted(ClientWorldModelInterface worldModel) {
    this.isConnected = false;
    for (Iterator<Long> iter = this.outstandingSnapshots.keySet().iterator(); iter
        .hasNext();) {
      Long tix = iter.next();
      Response resp = this.outstandingSnapshots.get(tix);
      resp.setError(new RuntimeException("Connection to "
          + worldModel.toString() + " was closed."));
      iter.remove();
    }

    this.outstandingStates.clear();

    for (Iterator<Long> iter = this.outstandingSteps.keySet().iterator(); iter
        .hasNext();) {
      Long tix = iter.next();
      StepResponse resp = this.outstandingSteps.get(tix);
      resp.setError(new RuntimeException("Connection to "
          + worldModel.toString() + " was closed."));
      iter.remove();
    }
  }

  void connectionEnded(ClientWorldModelInterface worldModel) {
    this.isConnected = false;
  }

  void connectionEstablished(ClientWorldModelInterface worldModel) {
    this.isConnected = true;
    synchronized (this) {
      this.notifyAll();
    }
  }

  void requestCompleted(ClientWorldModelInterface worldModel,
      AbstractRequestMessage message) {
    Long ticket = Long.valueOf(message.getTicketNumber());
    log.debug("Request {} completed.", ticket);

    Response resp = this.outstandingSnapshots.remove(ticket);
    // Snapshot request
    if (resp != null) {
      WorldState ws = this.outstandingStates.get(ticket);
      if (ws == null) {
        log.error("Unknown ticket number {} for request.", ticket, message);
        return;
      }
      resp.setState(ws);
      return;
    }

    StepResponse sResp = this.outstandingSteps.remove(ticket);
    // Range/Streaming request
    if (sResp != null) {
      sResp.setComplete();
      return;
    }

    log.error("Couldn't find response for ticket {}.", ticket);
  }

  void dataResponseReceived(ClientWorldModelInterface worldModel,
      DataResponseMessage message) {
    // Check for snapshot request
    WorldState ws = null;
    ws = this.outstandingStates.get(Long.valueOf(message.getTicketNumber()));
    if (ws != null) {
      log.debug("Updating data for ticket {}:\n{}",
          Long.valueOf(message.getTicketNumber()), message);
      List<Attribute> attribList = new ArrayList<Attribute>();
      for (Attribute a : message.getAttributes()) {
        attribList.add(a);
      }
      ws.addState(message.getId(), attribList);
      return;
    }

    StepResponse resp = this.outstandingSteps.get(Long.valueOf(message
        .getTicketNumber()));
    if (resp == null) {
      log.error("Unknown request ticket number {}", message.getTicketNumber());
      return;
    }
    log.debug("Updating data for ticket {}:\n{}",
        Long.valueOf(message.getTicketNumber()), message);
    ws = new WorldState();
    List<Attribute> attribList = new ArrayList<Attribute>();
    if (message.getAttributes() != null) {
      for (Attribute a : message.getAttributes()) {
        attribList.add(a);
      }
    }
    ws.addState(message.getId(), attribList);
    resp.addState(ws);
    return;
  }

  void uriSearchResponseReceived(ClientWorldModelInterface worldModel,
      IdSearchResponseMessage message) {
    log.info("Got a URI search response: {}", message);
    String[] matching = message.getMatchingIds();
    if (matching == null) {
      this.uriSearchResponses.add(new String[] {});
    } else {
      this.uriSearchResponses.add(matching);
    }
  }

  void attributeAliasesReceived(ClientWorldModelInterface worldModel,
      AttributeAliasMessage message) {
    // TODO Auto-generated method stub

  }

  void originAliasesReceived(ClientWorldModelInterface worldModel,
      OriginAliasMessage message) {
    // TODO Auto-generated method stub

  }

  void originPreferenceSent(ClientWorldModelInterface worldModel,
      OriginPreferenceMessage message) {
    // TODO Auto-generated method stub

  }
}
