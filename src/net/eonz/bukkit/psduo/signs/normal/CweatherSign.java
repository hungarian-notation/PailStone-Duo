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

import org.bukkit.World;
import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class CweatherSign extends PSSign {
	
	protected void triggersign(TriggerType type, Object args) {
		boolean state = false;
		World w = this.getWorld();
		switch (this.type) {
		case SUNNY:
			state = !(w.hasStorm());
			break;
		case STORMY:
			state = w.hasStorm();
			break;
		}
		this.setOutput(state);
	}

	public String getData() {
		// This sign does not use data.
		return "";
	}

	protected void setData(String data) {
		// This sign does not use data.
	}

	private static enum WeatherType {
		SUNNY,
		STORMY;
	}
	
	private WeatherType type = WeatherType.SUNNY;
	
	protected void declare(boolean reload, SignChangeEvent event) {
		String typeLine = this.getLines(event)[1];
		
		for (WeatherType t : WeatherType.values()) {
			if (t.name().toUpperCase().equals(typeLine.toUpperCase())) {
				type = t;
			}
		}
		
		if (!reload) {
			this.setLine(1, type.name(), event);
		}
		
		main.sgc.register(this, TriggerType.TIMER_SECOND);
		if (!reload) {
			this.init("cweather sign accepted.");
		}
	}

}
