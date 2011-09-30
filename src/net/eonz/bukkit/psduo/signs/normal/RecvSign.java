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

import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;
import net.eonz.bukkit.psduo.signs.WirelessPacket;

public class RecvSign extends PSSign {

	protected void triggersign(TriggerType type, Object args) {
		if (type == TriggerType.SEND_DATA) {

			WirelessPacket p = (WirelessPacket) args;

			if (this.p.sameChannel(p)) {
				this.setOutput(p.getState());
			}

		}
	}

	public String getData() {
		// This sign does not use data.
		return "";
	}

	protected void setData(String data) {
		// This sign does not use data.
	}

	private WirelessPacket p;

	protected void declare(boolean reload, SignChangeEvent event) {

		String band = this.getLines()[2];
		String channel = this.getLines()[1];
		
		if (!reload) {

			if (band.trim().equals("")) {
				band = this.getOwnerName();
			} else if (!band.equalsIgnoreCase(this.getOwnerName())) {
				if (band.charAt(0) == '~') {
					// GOOD!
				} else {
					band = "~" + band;
				}
			}

			this.clearArgLines(event);
			this.setLine(1, channel, event);
			this.setLine(2, band, event);

		}

		p = new WirelessPacket(band, channel, false);

		main.sgc.register(this, TriggerType.SEND_DATA);
		if (!reload) {
			this.init("Recv sign accepted.");
		}
	}

}
