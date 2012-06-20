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
package com.owlplatform.worldmodel.solver.listeners;

import com.owlplatform.worldmodel.solver.SolverWorldModelInterface;
import com.owlplatform.worldmodel.solver.protocol.messages.StartOnDemandMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.StopOnDemandMessage;
import com.owlplatform.worldmodel.solver.protocol.messages.AttributeAnnounceMessage;

public interface DataListener {
	public void startTransientReceived(SolverWorldModelInterface worldModel, StartOnDemandMessage message);
	
	public void stopTransientReceived(SolverWorldModelInterface worldModel, StopOnDemandMessage message);
	
	public void typeSpecificationsSent(SolverWorldModelInterface worldModel, AttributeAnnounceMessage message);
}
