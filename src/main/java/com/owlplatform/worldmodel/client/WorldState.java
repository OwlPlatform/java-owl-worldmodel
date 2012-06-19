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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.owlplatform.worldmodel.Attribute;


public class WorldState {
	private Map<String, Collection<Attribute>> stateMap = new ConcurrentHashMap<String, Collection<Attribute>>();
	
	public void addState(final String uri, final Collection<Attribute> attributes){
		this.stateMap.put(uri,attributes);
	}
	
	public Collection<Attribute> getState(final String uri){
		return this.stateMap.get(uri);
	}
	
	/**
	 * Returns a collection containing the same URI Strings as this WorldState object.  Modifications to the
	 * returned Collection do not impact this WorldState.
	 * @return a collection containing the same URI Strings as this WorldState.
	 */
	public Collection<String> getURIs(){
		List<String> keys = new LinkedList<String>();
		keys.addAll(this.stateMap.keySet());
		return keys;
	}
}
