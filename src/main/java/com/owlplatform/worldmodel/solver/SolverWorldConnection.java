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
import com.owlplatform.worldmodel.solver.protocol.messages.StartTransientMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StopTransientMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.TypeAnnounceMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.DataTransferMessage.Solution;
import com.owlplatform.worldmodel.solver.protocol.messages.TypeAnnounceMessage.TypeSpecification;

/**
 * A simple class for solvers that need to push data into the World Model. This
 * interface is not suitable for solvers that produce transient data. Transient
 * solvers should use the lower-level SolverWorldModelInterface class instead.
 * 
 * @author Robert Moore
 * 
 */
public class SolverWorldConnection {

	private static final class Handler implements ConnectionListener,
			DataListener {

		private final SolverWorldConnection parent;

		public Handler(final SolverWorldConnection parent) {
			this.parent = parent;
		}

		@Override
		public void connectionInterrupted(SolverWorldModelInterface worldModel) {
			this.parent.connectionInterrupted(worldModel);
		}

		@Override
		public void connectionEnded(SolverWorldModelInterface worldModel) {
			this.parent.connectionEnded(worldModel);
		}

		@Override
		public void connectionEstablished(SolverWorldModelInterface worldModel) {
			this.parent.connectionEstablished(worldModel);
		}

		@Override
		public void startTransientReceived(
				SolverWorldModelInterface worldModel,
				StartTransientMessage message) {
			this.parent.startTransientReceived(worldModel, message);
		}

		@Override
		public void stopTransientReceived(SolverWorldModelInterface worldModel,
				StopTransientMessage message) {
			this.parent.stopTransientReceived(worldModel, message);
		}

		@Override
		public void typeSpecificationsSent(
				SolverWorldModelInterface worldModel,
				TypeAnnounceMessage message) {
			this.parent.typeSpecificationsSent(worldModel, message);
		}

	}

	private static final Logger log = LoggerFactory
			.getLogger(SolverWorldConnection.class);

	private final Handler handler = new Handler(this);

	private final SolverWorldModelInterface wmi = new SolverWorldModelInterface();

	private volatile boolean canSend = false;

	private volatile boolean terminated = false;

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
	  if(this.wmi.doConnectionSetup()){
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
		return "World Model (S) @ " + this.wmi.getHost() + ":"
				+ this.wmi.getPort();
	}

	/**
	 * Sends a single solution to the world model, or buffers it to be sent
	 * later if the World Model is not connected.
	 * 
	 * @param solution
	 *            the solution to send.
	 * @return {@code true} if the solution was sent immediately or bufffered,
	 *         and {@code false} if it was unable to be sent or buffered.
	 * @throws IllegalStateException
	 *             if this method is called once the world model connection has
	 *             been destroyed.
	 */
	public boolean sendSolution(Solution solution) throws IllegalStateException {
		if (this.terminated) {
			throw new IllegalStateException(
					"Cannot send solutions to the World Model once the connection has been destroyed.");
		}

		if (this.canSend) {
			return this.wmi.sendSolution(solution);
		} else {
			if (!this.solutionBuffer.offer(solution)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Sends a collection of solutions to the world model, or buffers them to be
	 * sent later if the World Model is not connected.
	 * 
	 * @param solutions
	 *            the solutions to send.
	 * @return {@code true} if the solutions were able to be sent immediately or
	 *         bufffered, and {@code false} if one or more were unable to be
	 *         sent or buffered.
	 * @throws IllegalStateException
	 *             if this method is called once the world model connection has
	 *             been destroyed.
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
	 *            the type specification to add to the world model interface.
	 */
	public void addSolutionType(TypeSpecification spec) {
		this.wmi.addType(spec);
	}

	/**
	 * Sets the origin string value for this world model interface. The origin
	 * string uniquely identifies a solver to the world model.
	 * 
	 * @param origin
	 *            the origin string for this world model interface.
	 */
	public void setOriginString(final String origin) {
		this.wmi.setOriginString(origin);
	}

	/**
	 * Creates the specified URI in the world model, returning {@code true} on
	 * success.
	 * 
	 * @param uri
	 *            the URI to create in the world model.
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
	 *            the URI to expire, or the URI of the attributes to expire.
	 * @param attributes
	 *            one or more attribute names to expire. If none are specified,
	 *            then the URI itself is expired.
	 * @returns {@code true} if all expirations are successful, else
	 *          {@code false}.
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
	 *            the URI to delete, or the URI for the attributes to delete.
	 * @param attributes
	 *            one or more attributes to delete. If none are specified, then
	 *            the URI itself is deleted.
	 * @return {@code true} if all deletions are successful, else {@code false}.
	 */
	public boolean delete(final String uri, final String... attributes) {
		if(attributes == null || attributes.length == 0){
			return this.wmi.deleteUri(uri);
		}
		boolean retVal = true;
		for(String attribute : attributes){
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
	}

	void startTransientReceived(SolverWorldModelInterface worldModel,
			StartTransientMessage message) {
		// TODO Auto-generated method stub

	}

	void stopTransientReceived(SolverWorldModelInterface worldModel,
			StopTransientMessage message) {
		// TODO Auto-generated method stub

	}

	void typeSpecificationsSent(SolverWorldModelInterface worldModel,
			TypeAnnounceMessage message) {
		this.canSend = true;
		this.sendBufferedSolutions();
	}

	public boolean isConnectionLive() {
		return !this.terminated;
	}
}
