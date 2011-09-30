package net.eonz.bukkit.psduo;

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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PSCommand implements CommandExecutor {

	private final PailStone main;

	public PSCommand(PailStone pailStone) {
		this.main = pailStone;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("msg") && sender instanceof Player) {
				Player p = (Player) sender;

				if (args.length > 1) {
					String newMsg = combine(args, 1);
					this.main.alert(p.getName(), "Saved: \"" + newMsg + "\"");
					PSPlayer psp = this.main.players.safelyGet(p.getName(), this.main);
					psp.setMessage(newMsg);
					return true;
				} else {
					PSPlayer psp = this.main.players.safelyGet(p.getName(), this.main);
					if (psp.message != null) {
						this.main.alert(p.getName(), "Stored message: \"" + psp.message + "\"");
					} else {
						this.main.alert(p.getName(), "You have not stored a message. " + org.bukkit.ChatColor.AQUA + "/ps msg <message>");
					}
					return true;
				}
			}
		}

		return false;
	}

	public static String combine(String[] args, int from) {
		String out = "";
		for (int i = from; i < args.length; i++) {
			if (i != from) {
				out += " ";
			}
			out += args[i];
		}
		return out;
	}

}
