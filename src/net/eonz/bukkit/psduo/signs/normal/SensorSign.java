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
import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.PSPlayer;
import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class SensorSign extends PSSign {
	
	boolean lastState = false;

	protected void triggersign(TriggerType type, Object args) {
		boolean tripped = false;

		int xmin, xmax, ymin, ymax, zmin, zmax;
		xmin = Math.min(x1, x2);
		xmax = Math.max(x1, x2);
		
		ymin = Math.min(y1, y2);
		ymax = Math.max(y1, y2);
		
		zmin = Math.min(z1, z2);
		zmax = Math.max(z1, z2);

		boolean x, y, z;
		
		Location l;
		
		for (Player p : this.main.getServer().getOnlinePlayers()) {

			l = p.getLocation();

			x = (xmin <= l.getBlockX()) && (l.getBlockX() <= xmax);
			y = (ymin <= l.getBlockY()) && (l.getBlockY() <= ymax);
			z = (zmin <= l.getBlockZ()) && (l.getBlockZ() <= zmax);

			if (x && y && z) {
				tripped = true;
				break;
			}

		}

		if (tripped != lastState) {
			lastState = tripped;
			this.setOutput(tripped);
		}
	}

	public String getData() {
		// This sign does not use data.
		return "";
	}

	protected void setData(String data) {
		// This sign does not use data.
	}

	private int x1, y1, z1, x2, y2, z2;

	protected void declare(boolean reload, SignChangeEvent event) {

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

					this.main.alert(this.getOwnerName(), "You did not supply any arguments. You must either designate the sensor area on lines two and three, or set the cuboid area by right clicking with a piece of lightstone dust.");
					if (!reload) {
						event.setCancelled(true);
					}
					return;

				}

			} else {

				this.main.alert(this.getOwnerName(), "You did not supply any arguments. You must either designate the sensor area on lines two and three, or set the cuboid area by right clicking with a piece of lightstone dust.");
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

		main.sgc.register(this, TriggerType.TIMER_HALF_SECOND);
		if (!reload) {
			this.init("Sensor sign accepted.");
		}
	}

}
