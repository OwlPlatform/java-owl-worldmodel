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

public abstract class AbstractRequestMessage {

    /**
     * The client-assigned ticket number for the request.  Used by the 
     * World Model to notify the client when a request is finished (via
     * {@link RequestCompleteMessage}) or by the client to cancel a request
     * (via {@link CancelRequestMessage}).
     */
    protected long ticketNumber = 0;

    public long getTicketNumber()
    {
        return ticketNumber;
    }

    public void setTicketNumber(long ticketNumber)
    {
        this.ticketNumber = ticketNumber&0xFFFFFFFF;
    }
    
}
