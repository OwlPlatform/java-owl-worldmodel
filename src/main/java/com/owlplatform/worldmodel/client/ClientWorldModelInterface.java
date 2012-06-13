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

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.worldmodel.client.listeners.ConnectionListener;
import com.owlplatform.worldmodel.client.listeners.DataListener;
import com.owlplatform.worldmodel.client.protocol.codec.WorldModelClientProtocolCodecFactory;
import com.owlplatform.worldmodel.client.protocol.messages.AbstractRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.Attribute;
import com.owlplatform.worldmodel.client.protocol.messages.AttributeAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.CancelRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.DataResponseMessage;
import com.owlplatform.worldmodel.client.protocol.messages.HandshakeMessage;
import com.owlplatform.worldmodel.client.protocol.messages.KeepAliveMessage;
import com.owlplatform.worldmodel.client.protocol.messages.OriginAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.OriginPreferenceMessage;
import com.owlplatform.worldmodel.client.protocol.messages.RangeRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.RequestCompleteMessage;
import com.owlplatform.worldmodel.client.protocol.messages.SnapshotRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.StreamRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.URISearchMessage;
import com.owlplatform.worldmodel.client.protocol.messages.URISearchResponseMessage;
import com.owlplatform.worldmodel.client.protocol.messages.AttributeAliasMessage.AttributeAlias;
import com.owlplatform.worldmodel.client.protocol.messages.OriginAliasMessage.OriginAlias;

/**
 * Handles low-level network interaction with the World Model for client
 * applications.
 * 
 * @author Robert Moore
 * 
 */
public class ClientWorldModelInterface implements ClientIoAdapter {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(ClientWorldModelInterface.class);

	/**
	 * Timeout value in seconds.
	 */
	private static final int TIMEOUT_PERIOD = 60;

	/**
	 * Host where the World Model is hosted.
	 */
	private String host = "localhost";

	/**
	 * Port on which the World Model is listening for client connections.
	 */
	private int port = 7013;

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
	 * How long to wait (in milliseconds) before reconnecting to the World
	 * Model.
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
	 * Session-based mapping of Attribute names to 32-bit unsigned integer
	 * values.
	 */
	private final ConcurrentHashMap<Integer, String> attributeAliasValues = new ConcurrentHashMap<Integer, String>();

	/**
	 * Session-based mapping of Origin values to 32-bit unsigned integer values.
	 */
	private final ConcurrentHashMap<Integer, String> originAliasValues = new ConcurrentHashMap<Integer, String>();

	/**
	 * Queue of interfaces that are interested in connection status events.
	 */
	private final ConcurrentLinkedQueue<ConnectionListener> connectionListeners = new ConcurrentLinkedQueue<ConnectionListener>();

	/**
	 * Queue of interfaces that are interested in data messages.
	 */
	private final ConcurrentLinkedQueue<DataListener> dataListeners = new ConcurrentLinkedQueue<DataListener>();

	private NioSocketConnector connector = null;

	private WorldModelIoHandler ioHandler = new WorldModelIoHandler(this);

	// private final ExecutorService workers = new
	// OrderedThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
	private final ExecutorFilter executors = new ExecutorFilter(Runtime
			.getRuntime().availableProcessors());

	private final AtomicBoolean sentUriSearch = new AtomicBoolean(false);

	/**
	 * Number of times the receiving side of the connection has become idle
	 * (.5*TIMEOUT_PERIOD).
	 */
	private volatile int receiveIdleTimes = 0;

	/**
	 * The next available ticket number for this World Model interface.
	 */
	private volatile int nextTicketNumber = 1;

	/**
	 * Requests sent to the World Model that have received Request Tickets but
	 * have not yet completed. This would include all stream requests.
	 */
	private final ConcurrentHashMap<Long, AbstractRequestMessage> outstandingRequests = new ConcurrentHashMap<Long, AbstractRequestMessage>();

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
				ClientWorldModelInterface.TIMEOUT_PERIOD / 2);
		this.connector.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE,
        (int)(ClientWorldModelInterface.TIMEOUT_PERIOD *1.1f));
		if (!connector.getFilterChain().contains(
				WorldModelClientProtocolCodecFactory.CODEC_NAME)) {
			connector.getFilterChain().addLast(
					WorldModelClientProtocolCodecFactory.CODEC_NAME,
					new ProtocolCodecFilter(
							new WorldModelClientProtocolCodecFactory(true)));
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
							.format("Connection to World Model at %s:%d failed, waiting %dms before retrying.",
									this.host, this.port,
									this.connectionRetryDelay));
					Thread.sleep(this.connectionRetryDelay);
				} catch (InterruptedException ie) {
					// Ignored
				}
			}
		} while (this.stayConnected);

		this.disconnect();
		// this.finishConnection();

		return false;
	}

	void finishConnection() {
		this.connector.dispose();
		this.connector = null;
		for (ConnectionListener listener : this.connectionListeners) {
			listener.connectionEnded(this);
		}
		this.executors.destroy();
		// this.workers.shutdown(true);
	}

	public void doConnectionTearDown() {
		// Make sure we don't automatically reconnect
		this.stayConnected = false;
		this.disconnect();
	}

	protected boolean connect() {
	  log.debug("Attempting connection...");
		ConnectFuture connFuture = this.connector
				.connect(new InetSocketAddress(this.host, this.port));
		if (!connFuture.awaitUninterruptibly(connectionTimeout)) {
			log.warn("Unable to connect to world model after {}ms.",
					Long.valueOf(this.connectionTimeout));
			return false;
		}
		if (!connFuture.isConnected()) {
			log.debug("Failed to connect.");
			return false;
		}

		try {
			log.debug("Attempting connection to {}:{}.", this.host, this.port);
			this.session = connFuture.getSession();
		} catch (RuntimeIoException ioe) {
			log.error(String.format(
					"Could not create session to World Model %s:%d.",
					this.host, this.port), ioe);
			return false;
		}
		return true;
	}

	protected void disconnect() {
		if (this.session != null) {
			log.debug(
					"Closing connection to World Model (client) at {} (waiting {}ms).",
					this.session.getRemoteAddress(), this.connectionTimeout);
			this.session.close(false).awaitUninterruptibly(connectionTimeout);
			this.session = null;
			this.sentHandshake = null;
			this.receivedHandshake = null;
			this.receiveIdleTimes = 0;
			this.attributeAliasValues.clear();
			this.originAliasValues.clear();
			synchronized (this.sentUriSearch) {
				this.sentUriSearch.set(false);
			}
			for (ConnectionListener listener : this.connectionListeners) {
				listener.connectionInterrupted(this);
			}
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
	  if(status.equals(IdleStatus.READER_IDLE)){
	    log.error("World Model timed-out. Disconnecting.");
	    this.disconnect();
	    return;
	  }
		if (status.equals(IdleStatus.WRITER_IDLE)
				|| status.equals(IdleStatus.BOTH_IDLE)) {
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

		log.info("Connected to World Model (client) at {}.",
				session.getRemoteAddress());

		log.debug("Attempting to write handshake.");
		this.session.write(HandshakeMessage.getDefaultMessage());
	}

	@Override
	public void connectionClosed(IoSession session) {
		this.disconnect();
		if (this.stayConnected) {
			log.info("Reconnecting to World Model (Client) at {}:{}",
					this.host, this.port);
			Thread reconnectThread =

			new Thread("Reconnect Thread") {

				public void run() {
					if (ClientWorldModelInterface.this.doConnectionSetup()) {
						return;
					}
					ClientWorldModelInterface.this.finishConnection();
				}
			};
			reconnectThread.start();
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
			for (ConnectionListener listener : this.connectionListeners) {
				listener.connectionEstablished(this);
			}
			this.sendSearchRequests();
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
		return Boolean.TRUE;
	}

	@Override
	public void keepAliveReceived(IoSession session, KeepAliveMessage message) {
		this.receiveIdleTimes = 0;
		log.debug("Received Keep-Alive message.");
	}

	@Override
	public void snapshotRequestReceived(IoSession session,
			SnapshotRequestMessage message) {
		log.error(
				"Client should not receive snapshot requests from the World Model: {}",
				message);
		this.disconnect();

	}

	@Override
	public void rangeRequestReceived(IoSession session,
			RangeRequestMessage message) {
		log.error(
				"Client should not receive snapshot requests from the World Model: {}",
				message);
		this.disconnect();
	}

	@Override
	public void streamRequestReceived(IoSession session,
			StreamRequestMessage message) {
		log.error(
				"Client should not received stream requests from the World Model: {}",
				message);
		this.disconnect();
	}

	@Override
	public void attributeAliasReceived(IoSession session,
			AttributeAliasMessage message) {
		this.receiveIdleTimes = 0;
		log.debug("Received Attribute Aliases.");
		AttributeAlias[] aliases = message.getAliases();
		if (aliases == null) {
			log.warn("Attribute aliases were null!");
			return;
		} else {
			for (AttributeAlias alias : aliases) {
				this.attributeAliasValues.put(
						Integer.valueOf(alias.aliasNumber), alias.aliasName);
				log.debug("Attribute ({})->{}", alias.aliasName,
						alias.aliasNumber);
			}
		}

		for (DataListener listener : this.dataListeners) {
			listener.attributeAliasesReceived(this, message);
		}
	}

	@Override
	public void originAliasReceived(IoSession session,
			OriginAliasMessage message) {
		this.receiveIdleTimes = 0;
		log.debug("Received Origin Aliases.");
		OriginAlias[] aliases = message.getAliases();
		if (aliases == null) {
			log.warn("Origin aliases were null!");
			return;
		} else {
			for (OriginAlias alias : aliases) {
				this.originAliasValues.put(Integer.valueOf(alias.aliasNumber),
						alias.aliasName);
				log.debug("Origin ({})->{}", alias.aliasName, alias.aliasNumber);
			}
		}

		for (DataListener listener : this.dataListeners) {
			listener.originAliasesReceived(this, message);
		}
	}

	@Override
	public void requestCompleteReceived(IoSession session,
			RequestCompleteMessage message) {
		this.receiveIdleTimes = 0;
		Long ticketNumber = Long.valueOf(message.getTicketNumber());
		log.debug("Request {} has completed.", ticketNumber);

		AbstractRequestMessage request = this.outstandingRequests
				.get(ticketNumber);
		if (request == null) {
			log.error("Unable to retrieve request for ticket {}.", ticketNumber);
			this.disconnect();
			return;
		}
		for (DataListener listener : this.dataListeners) {
			listener.requestCompleted(this, request);
		}

	}

	@Override
	public void cancelRequestReceived(IoSession session,
			CancelRequestMessage message) {
		log.error("Client should not receive Cancel Request messages from the World Model.");
		this.disconnect();
	}

	@Override
	public void dataResponseReceived(IoSession session,
			DataResponseMessage message) {
		this.receiveIdleTimes = 0;

		if (message.getAttributes() != null) {
			for (Attribute attr : message.getAttributes()) {
				String attributeName = this.attributeAliasValues.get(Integer
						.valueOf(attr.getAttributeNameAlias()));
				if (attributeName == null) {
					log.error("World Model sent unknown Attribute Alias {}.",
							Integer.valueOf(attr.getAttributeNameAlias()));
					this.disconnect();
					return;
				}
				attr.setAttributeName(attributeName);

				String originName = this.originAliasValues.get(Integer
						.valueOf(attr.getOriginNameAlias()));
				if (originName == null) {
					log.error("World Model sent unknown Origin Alias {}.",
							Integer.valueOf(attr.getOriginNameAlias()));
					this.disconnect();
					return;
				}
				attr.setOriginName(originName);
			}
		}

		log.debug("Received data response from {}: {}", this, message);

		for (DataListener listener : this.dataListeners) {
			listener.dataResponseReceived(this, message);
		}
	}

	public synchronized long sendMessage(AbstractRequestMessage message) {
		log.debug("Sending {} to {}", message, this);
		message.setTicketNumber(this.nextTicketNumber++);
		this.outstandingRequests.put(Long.valueOf(message.getTicketNumber()),
				message);
		this.session.write(message);
		return message.getTicketNumber();
	}

	public void cancelRequest(long ticketNumber) {
		if (this.session != null && this.outstandingRequests.containsKey(Long.valueOf(ticketNumber))) {
			CancelRequestMessage message = new CancelRequestMessage();
			message.setTicketNumber(ticketNumber);
			this.session.write(message);
		} else {
			log.warn("Tried to cancel unknown request for ticket number {}.",
					Long.valueOf(ticketNumber));
		}
	}

	@Override
	public void URISearchReceived(IoSession session, URISearchMessage message) {
		log.error("Client should not receive URI search messages from the World Model.");
		this.disconnect();
	}

	@Override
	public void URISearchResponseReceived(IoSession session,
			URISearchResponseMessage message) {
		this.receiveIdleTimes = 0;
		log.debug("Received URI search response from {}: {}", this, message);
		for (DataListener listener : this.dataListeners) {
			listener.uriSearchResponseReceived(this, message);
		}

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
			for (ConnectionListener listener : this.connectionListeners) {
				listener.connectionEstablished(this);
			}
			this.sendSearchRequests();
		}
	}

	@Override
	public void keepAliveSent(IoSession session, KeepAliveMessage message) {
		log.debug("Sending Keep-Alive message.");
	}

	@Override
	public void snapshotRequestSent(IoSession session,
			SnapshotRequestMessage message) {
		log.debug("Sent Snapshot request to {}: {}", this, message);
	}

	@Override
	public void rangeRequestSent(IoSession session, RangeRequestMessage message) {
		log.debug("Sent Range request to {}: {}", this, message);
	}

	@Override
	public void streamRequestSent(IoSession session,
			StreamRequestMessage message) {
		log.debug("Sent Stream request to {}: {}", this, message);
	}

	@Override
	public void attributeAliasSent(IoSession session,
			AttributeAliasMessage message) {
		log.error("Client should not send Attribute Alias messages to the World Model.");
		this.disconnect();
	}

	@Override
	public void originAliasSent(IoSession session, OriginAliasMessage message) {
		log.error("Client should not send Origin Alias messages to the World Model.");
		this.disconnect();
	}

	@Override
	public void requestCompleteSent(IoSession session,
			RequestCompleteMessage message) {
		log.error("Client should not send Request Complete messages to the World Model.");
		this.disconnect();
	}

	@Override
	public void cancelRequestSent(IoSession session,
			CancelRequestMessage message) {
		log.debug("Sent Cancel Request to {}: {}", this, message);
	}

	@Override
	public void dataResponseSent(IoSession session, DataResponseMessage message) {
		log.error("Client should not send Data Respones messages to the World Model.");
		this.disconnect();
	}

	@Override
	public void URISearchSent(IoSession session, URISearchMessage message) {
		log.debug("Sent URI Search message to {}: {}", this, message);
	}

	@Override
	public void URISearchResponseSent(IoSession session,
			URISearchResponseMessage message) {
		log.error("Client should not send URI Search Responses to the World Model.");
		this.disconnect();
	}

	private final ConcurrentLinkedQueue<String> registeredSearchUris = new ConcurrentLinkedQueue<String>();

	public void registerSearchRequest(final String searchUri) {
		this.registeredSearchUris.add(searchUri);

		synchronized (this.sentUriSearch) {
			if (this.sentUriSearch.get()) {
				URISearchMessage message = new URISearchMessage();
				message.setUriRegex(searchUri);
				this.session.write(message);
			}
		}
	}

	protected void sendSearchRequests() {
		synchronized (this.sentUriSearch) {
			this.sentUriSearch.set(true);
			this.sentUriSearch.notifyAll();
		}
		for (String uri : this.registeredSearchUris) {
			URISearchMessage message = new URISearchMessage();
			message.setUriRegex(uri);
			this.session.write(message);
		}
	}

	public boolean searchURIRegex(final String uriRegex) {
		if (uriRegex == null) {
			log.error("Unable to search for a null URI regex.");
			return false;
		}

		synchronized (this.sentUriSearch) {
			if (!this.sentUriSearch.get()) {
				try {
					this.sentUriSearch.wait();
				} catch (InterruptedException e) {
					// Ignored
				}
			}
			URISearchMessage message = new URISearchMessage();
			message.setUriRegex(uriRegex);
			this.session.write(message);
			log.debug("Sent {}", message);
		}
		return true;
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

	public void setDisconnectOnException(boolean disconectOnException) {
		this.disconnectOnException = disconectOnException;
	}

	public boolean isStayConnected() {
		return stayConnected;
	}

	public void setStayConnected(boolean stayConnected) {
		this.stayConnected = stayConnected;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("Client-World Model Interface");
		if (this.host != null) {
			sb.append(" (").append(this.host);
			if (this.port > 0) {
				sb.append(":").append(this.port);
			}
			sb.append(")");
		}
		return sb.toString();

	}

	@Override
	public void originPreferenceReceived(IoSession session,
			OriginPreferenceMessage message) {
		log.error("Should not receive an origin preference message from the world model.");
		this.disconnect();
	}

	@Override
	public void OriginPreferenceSent(IoSession session,
			OriginPreferenceMessage message) {
		log.debug("Sent {}", message);

		for (DataListener listener : this.dataListeners) {
			listener.originPreferenceSent(this, message);
		}

	}
}
