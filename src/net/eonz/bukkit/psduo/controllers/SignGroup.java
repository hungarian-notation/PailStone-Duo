package net.eonz.bukkit.psduo.controllers;

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

import java.util.ArrayList;

import org.bukkit.block.Sign;

import net.eonz.bukkit.psduo.PailStone;
import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;
import net.eonz.bukkit.psduo.signs.PSSign.ValidationState;

public class SignGroup {

	private final TriggerType type;
	private final ArrayList<PSSign> signs;
	private final PailStone main;

	public SignGroup(TriggerType type, PailStone main) {
		this.type = type;
		this.signs = new ArrayList<PSSign>();
		this.main = main;
	}

	public TriggerType getType() {
		return type;
	}

	public boolean isType(TriggerType ctype) {
		return (getType() == ctype);
	}

	public void add(PSSign sign) {
		signs.add(sign);
	}

	public void invalidate(PSSign sign) {
		signs.remove(sign);
	}
	
	public ArrayList<PSSign> getSigns() {
		return signs;
	}
	
	public void trigger(Object args) {
		for (int i = 0; i < signs.size(); i++) {
			PSSign toTrigger = signs.get(i);
			ValidationState valid = toTrigger.isValid();
			
			// CLEAR messages should be sent to valid and invalid signs alike.
			if (valid == ValidationState.VALID || this.type == TriggerType.CLEAR) {
				toTrigger.trigger(this.type, args);
			} else {
				if (valid == ValidationState.INVALID) {
					main.sgc.invalidate(toTrigger, "Sign was either gone or had different text.");
				} else if (valid == ValidationState.BLANK) {
					if (main.cfgWipeProtection) {
						
						if (this.type == TriggerType.PING)
							return; // Don't deal with wiped signs on pings.
						
						Sign s = (Sign)(toTrigger.getBlock().getState());
						String[] knownLines = toTrigger.getLines();
						for (int j = 0; j < 4; j++) {
							s.setLine(j, knownLines[j]);
						}
						s.update();
						
					} else {
						main.sgc.invalidate(toTrigger, "Sign was blank. (wipe-protection is off)");
					}
				}
			}
		}
	}

}
