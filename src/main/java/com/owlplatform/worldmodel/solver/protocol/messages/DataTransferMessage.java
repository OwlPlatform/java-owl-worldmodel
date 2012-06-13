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
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.common.util.NumericUtils;

/**
 * This message contains data sent by the solver to the World Model.
 */
public class DataTransferMessage {

	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(DataTransferMessage.class);

	/**
	 * Message type value identifying Data Response messages.
	 */
	public static final byte MESSAGE_TYPE = 4;

	/**
	 * 1-byte boolean value (0 for false, 1 for true) that indicates whether
	 * URIs should be created when a solution is for a URI that is not present
	 * in the world model.
	 */
	private boolean createUri = false;

	/**
	 * The attributes associated with this URI, if any.
	 */
	private Solution[] solutions;

	public int getMessageLength() {
		// Message Type, createUri
		int length = 1 + 1;

		// Number of solutions
		length += 4;

		// Each solution
		if (this.solutions != null) {
			for (Solution soln : this.solutions) {
				length += soln.getLength();
			}
		}

		return length;
	}

	public static class Solution {
		/**
		 * Logging facility for this class.
		 */
		private static final Logger log = LoggerFactory
				.getLogger(Solution.class);

		/**
		 * 32 bit unsigned integer specifying an alias for this attribute's
		 * name.
		 */
		private int attributeNameAlias = -1;

		/**
		 * The actual UTF-16 representation of the attribute name. This is only
		 * used internally and not transmitted over the network.
		 */
		private String attributeName;

		/**
		 * 8-byte integer indicating the time of the solution generation in
		 * milliseconds since midnight, January 1, 1970 UTC. This does not need
		 * to be the current time. For instance, if this result is based upon
		 * historic data the time field might match the historic time rather
		 * than the current time.
		 */
		private long time;

		/**
		 * A UTF16 string that indicates the name of the target of this result.
		 */
		private String targetName;

		/**
		 * A buffer of the specified length that contains this attribute's data.
		 * The content of this buffer is specified by the attribute's name.
		 */
		private byte[] data = null;

		public int getLength() {
			// Attribute type alias, time, target name length,
			int length = 4 + 8 + 4;

			if (this.targetName != null) {
				try {
					length += this.targetName.getBytes("UTF-16BE").length;
				} catch (UnsupportedEncodingException e) {
					log.error("Unable to encode to UTF-16BE.");
				}
			}
			// Data length
			length += 4;

			// Data
			if (this.data != null) {
				length += this.data.length;
			}
			return length;
		}

		public int getAttributeNameAlias() {
			return attributeNameAlias;
		}

		public void setAttributeNameAlias(int attributeNameAlias) {
			this.attributeNameAlias = attributeNameAlias;
		}

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

		public String getAttributeName() {
			return attributeName;
		}

		public void setAttributeName(String attributeName) {
			this.attributeName = attributeName;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public String getTargetName() {
			return targetName;
		}

		public void setTargetName(String targetName) {
			this.targetName = targetName;
		}
		
		public String toString(){
			StringBuffer sb = new StringBuffer("Solution ");
			if(this.targetName != null){
				sb.append('(').append(this.targetName).append(')');
			}
			else{
				sb.append("[NULL]");
			}
			sb.append(" ");
			
			
			
			if(this.attributeName != null){
				sb.append(this.attributeName);
			}
			else{
				sb.append('(').append(this.attributeNameAlias).append(')');
			}
			
			sb.append('@');
			
			sb.append(new Date(this.time));
			if(this.data != null){
				sb.append(": ").append(NumericUtils.toHexString(this.data));
			}
			
			return sb.toString();
		}
	}

	public boolean getCreateUri() {
		return createUri;
	}

	public void setCreateUri(boolean createUri) {
		this.createUri = createUri;
	}

	public Solution[] getSolutions() {
		return solutions;
	}

	public void setSolutions(Solution[] solutions) {
		this.solutions = solutions;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer("Data Transfer");
		if(this.createUri){sb.append("[Auto-Create]");}
		sb.append('\n');
		
		if(this.solutions != null){
			for(Solution sol : this.solutions){
				sb.append(sol);
			}

		}
		return sb.toString();
	}
}
