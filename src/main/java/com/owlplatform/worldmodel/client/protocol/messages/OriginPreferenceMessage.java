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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the World Model with a list of preferred origins for the client.
 * @author Robert Moore
 *
 */
public class OriginPreferenceMessage
{
    /**
     * Logging facility for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(OriginPreferenceMessage.class);
    
    /**
     * The message type for the Origin Preference message.
     */
    public static final byte MESSAGE_TYPE = 11;
    
    /**
     * Mapping of origin strings to weight values.
     */
    private final HashMap<String,Integer> weights = new HashMap<String, Integer>();
    
    public int getMessageLength(){
        // Message ID
        int length = 1;
        
        for(String origin : this.weights.keySet()){
            // String prefix, weight
            length += 8;
            try {
                length += origin.getBytes("UTF-16BE").length;
            }
            catch(UnsupportedEncodingException uee){
                log.error("Unable to encode to UTF-16BE.");
            }
        }
        
        return length;
    }

    public HashMap<String, Integer> getWeights()
    {
        return weights;
    }
}
