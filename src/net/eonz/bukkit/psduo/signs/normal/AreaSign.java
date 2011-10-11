package net.eonz.bukkit.psduo.signs.normal;

/*
 * This code is Copyright (C) 2011 Chris Bode, Some Rights Reserved.
 *
 * Copyright (C) 1999-2002 Technical Pursuit Inc., All Rights Reserved. Patent 
 * Pending, Technical Pursuit Inc.
 *
 * Unless explicitly acquired and licensed from Licensor under the Technical 
 * Pursuit License ("TPL") Version 1.0 or greater, the contents of this file are 
 * subject to the Reciprocal Public License ("RPL") Version 1.1, or subsequent 
 * versions as allowed by the RPL, and You may not copy or use this file in 
 * either source code or executable form, except in compliance with the terms and 
 * conditions of the RPL.
 *
 * You may obtain a copy of both the TPL and the RPL (the "Licenses") from 
 * Technical Pursuit Inc. at http://www.technicalpursuit.com.
 *
 * All software distributed under the Licenses is provided strictly on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND TECHNICAL
 * PURSUIT INC. HEREBY DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT 
 * LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE, QUIET ENJOYMENT, OR NON-INFRINGEMENT. See the Licenses for specific 
 * language governing rights and limitations under the Licenses. 
 */

import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.controllers.Area;
import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class AreaSign extends PSSign {

	private boolean lastState = false;

	protected void triggersign(TriggerType type, Object args) {
		InputState is = this.getInput(1, (BlockRedstoneEvent) args);

		if (is == InputState.HIGH && !lastState) {
			lastState = true;

			Area a = this.main.areas.getArea(areaName);
			
			if (a.getSize() > this.main.cfgMaxCuboid && !this.main.hasPermission(this.getOwnerName(), "ignoremaxsize", this.getWorld().getName())) {
				this.main.alert(this.getOwnerName(), "The toggle area you specified is " + a.getSize() + " blocks big. The maximum acceptable area is " + this.main.cfgMaxCuboid + " blocks.");
				return;
			} else {
				a.draw(this.main);
			}
		} else if ((is == InputState.LOW || is == InputState.DISCONNECTED) && lastState) {
			lastState = false;
		} else {
			return;
		}
	}

	public String getData() {
		// This sign does not use data.
		return "";
	}

	protected void setData(String data) {
		if (data == null)
			return;
	}

	String areaName;

	protected void declare(boolean reload, SignChangeEvent event) {
		areaName = this.getLines(event)[1];

		if (!reload) {
			Area a = this.main.areas.getArea(areaName);
			
			if (a == null) {
				this.main.alert(this.getOwnerName(), "There is no area of that name available to you.");
				return;
			}
			
			a.ready(this.main);

			if (a.getOwnerName().equals(this.getOwnerName())) {
				this.clearArgLines(event);
				this.setLine(1, areaName, event);
			} else {
				this.main.alert(this.getOwnerName(), "There is no area of that name available to you.");
				return;
			}
		}

		main.sgc.register(this, TriggerType.REDSTONE_CHANGE);
		if (!reload) {
			this.init("Area sign accepted.");
		}
	}

}
