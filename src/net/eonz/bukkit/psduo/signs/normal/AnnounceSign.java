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
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.PSPlayer;
import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class AnnounceSign extends PSSign {

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

		for (Player p : this.main.getServer().getOnlinePlayers()) {

			Location l = p.getLocation();

			boolean x, y, z;

			x = (Math.min(x1, x2) <= l.getBlockX()) && (l.getBlockX() <= Math.max(x1, x2));
			y = (Math.min(y1, y2) <= l.getBlockY()) && (l.getBlockY() <= Math.max(y1, y2));
			z = (Math.min(z1, z2) <= l.getBlockZ()) && (l.getBlockZ() <= Math.max(z1, z2));

			if (x && y && z) {
				p.sendMessage(message);
				break;
			}

		}
	}

	public String getData() {
		return message;
	}

	protected void setData(String data) {
		if (message == null) {
			message = data;
		}
	}

	private int x1, y1, z1, x2, y2, z2;

	String message = null;

	protected void declare(boolean reload, SignChangeEvent event) {

		if (!reload) {
			message = this.main.players.safelyGet(this.getOwnerName(), this.main).message;
			if (message == null) {
				this.main.alert(this.getOwnerName(), "You must set a message first. " + org.bukkit.ChatColor.AQUA + "/ps msg <message>");
				return;
			}
		}

		if (this.getLines()[1].equals("") && this.getLines()[2].equals("")) {

			if (this.main.players.exists(this.getOwnerName())) {

				PSPlayer p = this.main.players.get(this.getOwnerName());

				if (p.l1 && (p.loc2 != null)) {

					x1 = p.loc1.getBlockX() - this.getHostLocation().getBlockX();
					y1 = p.loc1.getBlockY() - this.getHostLocation().getBlockY();
					z1 = p.loc1.getBlockZ() - this.getHostLocation().getBlockZ();

					x2 = p.loc2.getBlockX() - this.getHostLocation().getBlockX();
					y2 = p.loc2.getBlockY() - this.getHostLocation().getBlockY();
					z2 = p.loc2.getBlockZ() - this.getHostLocation().getBlockZ();

				} else {

					this.main.alert(this.getOwnerName(), "You did not supply any arguments. You must either designate the announce area on lines two and three, or set the cuboid area by right clicking with a piece of lightstone dust.");
					if (!reload) {
						event.setCancelled(true);
					}
					return;

				}

			} else {

				this.main.alert(this.getOwnerName(), "You did not supply any arguments. You must either designate the announce area on lines two and three, or set the cuboid area by right clicking with a piece of lightstone dust.");
				if (!reload) {
					event.setCancelled(true);
				}
				return;

			}

		} else {

			String[] split1, split2;
			split1 = this.getLines()[1].split(" ");
			split2 = this.getLines()[2].split(" ");

			try {

				x1 = Integer.parseInt(split1[0]);
				y1 = Integer.parseInt(split1[1]);
				z1 = Integer.parseInt(split1[2]);

				x2 = Integer.parseInt(split2[0]);
				y2 = Integer.parseInt(split2[1]);
				z2 = Integer.parseInt(split2[2]);

			} catch (Exception e) {

				this.main.alert(this.getOwnerName(), "The coordinates you specified are either invalid or formatted incorrectly.");
				if (!reload) {
					event.setCancelled(true);
				}
				return;

			}

		}
		
		if (!reload) {
			System.out.println(x1 + ", " + y1 + ", " + z1);
			this.clearArgLines(event);
			this.setLine(1, x1 + " " + y1 + " " + z1, event);
			this.setLine(2, x2 + " " + y2 + " " + z2, event);
		}
		
		x1 += this.getHostLocation().getBlockX();
		y1 += this.getHostLocation().getBlockY();
		z1 += this.getHostLocation().getBlockZ();

		x2 += this.getHostLocation().getBlockX();
		y2 += this.getHostLocation().getBlockY();
		z2 += this.getHostLocation().getBlockZ();

		main.sgc.register(this, TriggerType.REDSTONE_CHANGE);
		if (!reload) {
			this.init("Announce sign accepted.");
		}
	}

}
