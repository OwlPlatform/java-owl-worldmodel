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
package com.owlplatform.worldmodel.client.listeners;

import com.owlplatform.worldmodel.client.ClientWorldModelInterface;
import com.owlplatform.worldmodel.client.protocol.messages.AbstractRequestMessage;
import com.owlplatform.worldmodel.client.protocol.messages.AttributeAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.DataResponseMessage;
import com.owlplatform.worldmodel.client.protocol.messages.OriginAliasMessage;
import com.owlplatform.worldmodel.client.protocol.messages.OriginPreferenceMessage;
import com.owlplatform.worldmodel.client.protocol.messages.URISearchResponseMessage;

public interface DataListener {
	
	public void requestCompleted(ClientWorldModelInterface worldModel, AbstractRequestMessage message);
	
	public void dataResponseReceived(ClientWorldModelInterface worldModel, DataResponseMessage message);
	
	public void uriSearchResponseReceived(ClientWorldModelInterface worldModel, URISearchResponseMessage message);

	public void attributeAliasesReceived(
			ClientWorldModelInterface worldModel,
			AttributeAliasMessage message);

	public void originAliasesReceived(
			ClientWorldModelInterface worldModel,
			OriginAliasMessage message);
	
	public void originPreferenceSent(ClientWorldModelInterface worldModel, OriginPreferenceMessage message);
}
