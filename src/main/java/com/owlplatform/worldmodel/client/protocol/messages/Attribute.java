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
package com.owlplatform.worldmodel.client.protocol.messages;

import java.util.Date;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.common.util.NumericUtils;
import com.owlplatform.worldmodel.types.DataConverter;

public class Attribute {
	/**
	 * Logging facility for this class.
	 */
	private static final Logger log = LoggerFactory
			.getLogger(Attribute.class);

	/**
	 * 32 bit unsigned integer specifying an alias for this attribute's
	 * name.
	 */
	private int attributeNameAlias;

	/**
	 * The actual UTF-16 representation of the attribute name. This is only
	 * used internally and not transmitted over the network.
	 */
	private String attributeName;

	/**
	 * A signed 64 bit integer specifying an offset from the epoch, 1970,
	 * when this attribute was created.
	 */
	private long creationDate;

	/**
	 * A signed 64 bit integer specifying an offset from the epoch, 1970,
	 * when this attribute expired. This is 0 if the attribute has not
	 * expired.
	 */
	private long expirationDate;

	/**
	 * 32 bit unsigned integer specifying an alias for this the origin of
	 * this attribute.
	 */
	private int originNameAlias;

	/**
	 * The actual UTF-16 representation of the data origin. This value is
	 * only used internally and not transmitted over the network.
	 */
	private String originName;

	/**
	 * A buffer of the specified length that contains this attribute's data.
	 * The content of this buffer is specified by the attribute's name.
	 */
	private byte[] data = null;

	public int getLength() {
		// Attribute name alias, creation date, expiration date, Origin name
		// alias, data length
		int length = 4 + 8 + 8 + 4 + 4;
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

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(long expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getOriginNameAlias() {
		return originNameAlias;
	}

	public void setOriginNameAlias(int originNameAlias) {
		this.originNameAlias = originNameAlias;
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

	public String getOriginName() {
		return originName;
	}

	public void setOriginName(String originName) {
		this.originName = originName;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (this.attributeName == null) {
			sb.append('(').append(this.attributeNameAlias).append(") ");
		} else {
			sb.append(this.attributeName).append(' ');
		}
		sb.append(new Date(this.creationDate)).append('-')
				.append(new Date(this.expirationDate)).append(" from ");
		if (this.originName == null) {
			sb.append('(').append(this.originNameAlias).append(")");
		} else {
			sb.append(this.originName);
		}
		sb.append(":");
		if (this.data != null) {
			if(this.attributeName != null && DataConverter.hasConverterForURI(this.attributeName)){
				sb.append(DataConverter.asString(this.attributeName, this.data));
			}else{
				sb.append(NumericUtils.toHexString(this.data));
			}
		}else{
			sb.append("NULL");
		}
		
		return sb.toString();
	}
}