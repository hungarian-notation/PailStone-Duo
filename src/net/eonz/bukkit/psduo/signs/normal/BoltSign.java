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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class BoltSign extends PSSign {

	private boolean lastState = false;
	
	protected void triggersign(TriggerType type, Object args) {
		InputState is = this.getInput(1, (BlockRedstoneEvent) args);
		
		if (is != InputState.HIGH) {
			lastState = false;
			return;
		} else {
			if (lastState == true) {
				return;
			}
			lastState = true;
		}
		
		Location strikeTarget = this.getHostLocation().clone();
		strikeTarget.setY(127);
		
		for (int y = 127; y > 0; y--) {
			strikeTarget.setY(y);
			if (strikeTarget.getBlock().getType() != Material.AIR) {
				strikeTarget.setY(y + 1);
				break;
			}
		}
		
		if (!isEffect) {
			this.getWorld().strikeLightning(strikeTarget);
		} else {
			this.getWorld().strikeLightningEffect(strikeTarget);
		}
	}

	public String getData() {
		// This sign does not use data.
		return "";
	}
	
	protected void setData(String data) {
		// This sign does not use data.
	}

	boolean isEffect = true;
	
	protected void declare(boolean reload, SignChangeEvent event) {
		
		String type = this.getLines(event)[1];
		
		if (type.equalsIgnoreCase("REAL")) {
			isEffect = false;
		} else if (type.equalsIgnoreCase("FAKE")) {
			isEffect = true;
		}
		
		if (!reload) {
			this.clearArgLines(event);
			if (isEffect) {
				this.setLine(1, "FAKE", event);
			} else {
				this.setLine(1, "REAL", event);
			}
		}
		
		main.sgc.register(this, TriggerType.REDSTONE_CHANGE);
		if (!reload) {
			this.init("Bolt sign accepted.");
		}
	}

}
