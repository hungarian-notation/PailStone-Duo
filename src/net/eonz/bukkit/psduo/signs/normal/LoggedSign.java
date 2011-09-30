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

import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class LoggedSign extends PSSign {

	private boolean isNew = true;
	private boolean lastState = false;
	
	protected void triggersign(TriggerType type, Object args) {
		Iterator<Player> i = this.getWorld().getPlayers().iterator();
		boolean inWorld = false;
		while (i.hasNext()) {
			if (i.next().getName().equalsIgnoreCase(playerName)) {
				inWorld = true;
			}
		}
		
		if (lastState != inWorld || isNew) {
			isNew = false;
			lastState = inWorld;
			this.setOutput(inWorld);
		}
	}

	public String getData() {
		// This sign does not use data.
		return "";
	}

	protected void setData(String data) {
		// This sign does not use data.
	}

	private String playerName;
	
	protected void declare(boolean reload, SignChangeEvent event) {
		main.sgc.register(this, TriggerType.TIMER_SECOND);
		
		String playerLine = this.getLines(event)[1];
		playerLine = playerLine.trim();
		playerLine = playerLine.substring(0, Math.min(16, playerLine.length()));
		
		if (playerLine.length() < 1) {
			playerLine = this.getOwnerName();
		}
		
		if (!reload) {
			this.clearArgLines(event);
			this.setLine(1, playerLine, event);
		}
		
		this.playerName = playerLine;
		
		if (!reload) {
			this.init("Logged sign accepted.");
		}
	}

}
