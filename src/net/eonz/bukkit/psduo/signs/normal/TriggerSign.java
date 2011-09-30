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

import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.signs.CommandArgWrapper;
import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class TriggerSign extends PSSign {

	protected void triggersign(TriggerType type, Object args) {
		if (type == TriggerType.TRIGGER_COMMAND) {
			CommandArgWrapper command = (CommandArgWrapper) args;

			if (command.args.length >= 1) {
				String id = command.args[0];
				if (this.command.equalsIgnoreCase(id)) {
					if (this.player.equalsIgnoreCase("@")) {
						state = !state;
					} else if (command.commandSender instanceof Player) {
						Player p = (Player) command.commandSender;
						boolean isInGroup = this.player.charAt(0) == '&' && this.player.length() >= 2 && this.main.inGroup(p, this.getWorld().getName(), this.player.substring(1));
						boolean isNamed = this.player.equalsIgnoreCase(p.getName());
						if (isNamed || isInGroup) {
							state = !state;
						}
					}
				}
			}
		} 

		if (this.isLoaded()) {
			this.setOutput(state);
		}
	}

	public String getData() {
		if (state) {
			return "H";
		} else {
			return "L";
		}
	}

	protected void setData(String data) {
		if (data != null && (!data.equals(""))) {
			if (data.equals("H")) {
				this.state = true;
			}
		}
	}

	boolean state = false;
	String command, player;

	protected void declare(boolean reload, SignChangeEvent event) {

		String nullCmd = "NULL";

		command = this.getLines(event)[1].replace("/", "");
		player = this.getLines(event)[2];

		if (player == null || player.equals("")) {
			player = "@";
			if (!reload)
				this.init("Player name defaulted to \"@\" because you did not specify a name.");
			if (!reload)
				this.init("This sign will be triggerable by anyone.");
		}

		if (command == null || command.equals("")) {
			command = nullCmd;
			if (!reload)
				this.init("Trigger identifier defaulted to \"NULL\" because you did not specify your own.");
		}

		if (!reload) {
			this.clearArgLines(event);
			this.setLine(1, command, event);
			this.setLine(2, player, event);
		}

		main.sgc.register(this, TriggerType.TRIGGER_COMMAND);
		main.sgc.register(this, TriggerType.TIMER_SECOND);

		if (!reload)
			this.init("Trigger sign accepted.");

	}

}
