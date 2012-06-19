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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A placeholder for the set of data that will be returned from the world model
 * as a result of a streaming or range request.
 * 
 * @author Robert Moore
 * 
 */
public class StepResponse {

  /**
   * Logger for this class.
   */
  private static final Logger log = LoggerFactory.getLogger(StepResponse.class);

  /**
   * Set of WorldState object representing the data returned from the world model.
   */
  private final BlockingQueue<WorldState> states = new LinkedBlockingQueue<WorldState>();

  /**
   * Special marker for the end of data.
   */
  private static final WorldState RESPONSE_COMPLETE = new WorldState();

  /**
   * Flag to indicate that the request has completed.
   */
  private volatile boolean complete = false;

  /**
   * The exception thrown by the request.
   */
  private Exception error = null;

  /**
   * The connection for this response.
   */
  private final ClientWorldConnection conn;

  /**
   * The ticket number of the request.
   */
  private long ticketNumber = 0;

  /**
   * Creates a step response with the connection and ticket number provided.
   * @param conn for cancelling the request.
   * @param ticketNumber the ticket number of the request.
   */
  StepResponse(final ClientWorldConnection conn, final long ticketNumber) {
    this.conn = conn;
    this.ticketNumber = ticketNumber;
  }

  /**
   * Returns the next {@code WorldState} provided by the world model, blocking
   * until data is available, if necessary. If an error has already occurred,
   * the {@code Exception} that was generated is thrown. If this response is
   * already completed (either normally or by a cancel request), then an
   * {@code IllegalStateException} is thrown.
   * 
   * @return the next {@code WorldState} returned by the world model.
   * @throws Exception
   *           if an exception is generated by the request, or an
   *           {@code IllegalStateException} if this {@code next()} is invoked
   *           after a request completes and has no more world states.
   */
  public synchronized WorldState next() throws Exception {
    if (this.error != null) {
      throw this.error;
    }

    // In case next() is called after completed with no states remaining
    if (this.complete && this.states.isEmpty()) {
      throw new IllegalStateException("No world states remaining.");
    }
    try {
      // Will block until something is available
      WorldState ws = this.states.take();
      if (ws != RESPONSE_COMPLETE) {
        return ws;
      }
    } catch (InterruptedException e) {
      // This should only happen once the request is complete
    }

    // If we were interrupted because of an exception, be sure to throw it
    if (this.error != null) {
      throw this.error;
    }

    // At this point, we should have an empty queue
    throw new IllegalStateException("Called next() on an empty StepResponse.");
  }

  /**
   * Adds a WorldState to the queue of states for this Response message. Adding
   * a state after this response is completed will result in an exception.
   * 
   * @param state
   *          the new WorldState to add.
   */
  void addState(final WorldState state) {
    if (this.complete) {
      throw new IllegalStateException(
          "Cannot add a World State to a completed response.");
    }
    this.states.add(state);
  }

  /**
   * Marks this StepResponse as completed. A StepResponse is completed when all
   * WorldStates have been added to it, and no others will be returned by the
   * World Model.
   */
  void setComplete() {
    this.complete = true;
    try {
      // If we haven't gotten any results back, make sure to create an
      // empty one
      if (this.states.size() == 0) {
        this.states.put(new WorldState());
      }
      // Special WorldState to indicate that the request has completed and
      // interrupt
      // any blocking threads
      else {
        this.states.put(RESPONSE_COMPLETE);
      }
    } catch (InterruptedException e) {
      log.error("Interrupted trying to indicate a completed condition.", e);
    }
  }

  /**
   * Sets the exception for this response and notifies any waiting threads. Any
   * calls to get() will have the exception thrown when an Exception is set.
   * 
   * @param error
   *          the exception for this response.
   */
  void setError(Exception error) {
    if (this.error != null) {
      throw new IllegalStateException(
          "Cannot reassign the error value of a response!");
    }
    this.error = error;
    this.complete = true;
    try {
      this.states.put(RESPONSE_COMPLETE);
    } catch (InterruptedException e) {
      log.error("Interrupted trying to indicate an error condition.", e);
    }
  }

  /**
   * Indicates if there is at least one WorldState available for retrieval from
   * this StepResponse. This method should be called before {@code next()} if
   * non-blocking operation is desired, as {@code next()} will block until data
   * is available, the request completes without additional WorldStates, or an
   * exception is generated.
   * 
   * @return {@code true} if a call to next() will return a WorldState without
   *         blocking, else {@code false}.
   */
  public boolean hasNext() {
    return (!this.states.isEmpty())
        && StepResponse.RESPONSE_COMPLETE != this.states.peek();
  }

  /**
   * A StepResponse is completed when all WorldStates have been added to it, and
   * no others will be returned by the World Model. The response to a Streaming
   * Request will only complete if it is cancelled.
   * 
   * @return {@code true} if this Response is completed, else {@code false}.
   */
  public boolean isComplete() {
    return this.complete;
  }

  /**
   * Returns the Exception generated by the request for this response message.
   * This method will return {@code null} if the response has not yet completed.
   * 
   * @return the Exception that this response's request generated, or
   *         {@code null} if no error condition is present.
   */
  public Exception getError() {
    return this.error;
  }

  /**
   * Returns {@code true} if this response has an error associated with it, else
   * {@code false}.
   * 
   * @return {@code true} if this response has an error.
   */
  public boolean isError() {
    return (this.error != null);
  }

  /**
   * Cancels the request associated with this {@code StepResponse} object.
   */
  public void cancel() {

    this.conn.cancelSnapshot(this.ticketNumber);
  }

  /**
   * Sets the request ticket number for this {@code StepResponse}. The request
   * number is used internally for canceling requests. Should only be called by
   * {@code ClientWorldConnection}.
   * 
   * @param ticketNumber
   *          the request ticket number for this response.
   */
  void setTicketNumber(long ticketNumber) {
    this.ticketNumber = ticketNumber;
  }
}
