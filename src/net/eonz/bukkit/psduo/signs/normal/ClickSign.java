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
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.eonz.bukkit.psduo.controllers.PSPlayer;
import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class ClickSign extends PSSign {

	private static final int PULSE_TIME = 10;
	
	private boolean ticking = false;
	private int timer;
	
	
	protected void triggersign(TriggerType type, Object args) {
		PlayerInteractEvent e = (PlayerInteractEvent)args;
		if ((e.getAction() == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK && left) || (e.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK && right)) {	
			Location l = e.getClickedBlock().getLocation();
			boolean x, y, z;
			x = (xmin <= l.getBlockX()) && (l.getBlockX() <= xmax);
			y = (ymin <= l.getBlockY()) && (l.getBlockY() <= ymax);
			z = (zmin <= l.getBlockZ()) && (l.getBlockZ() <= zmax);

			if (x && y && z) {
				timer = PULSE_TIME;
				if (!ticking) {
					this.startTicking();
					ticking = true;
					this.setOutput(true);
				}
			}
		}
	}

	public boolean tick() {
		timer--;
		if (timer <= 0) {
			ticking = false;
			this.setOutput(false);
		}
		return ticking;
	}
	
	public String getData() {
		// This sign does not use data.
		return "";
	}

	protected void setData(String data) {
		if (data == null) return;
		// This sign does not use data.
	}

	private int x1, y1, z1, x2, y2, z2;
	private int xmin, xmax, ymin, ymax, zmin, zmax;
	
	boolean left;
	boolean right;
	
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

					this.main.alert(this.getOwnerName(), "You did not supply any arguments. You must either designate the click area on lines two and three, or set the cuboid area by right clicking with a piece of lightstone dust.");
					if (!reload) {
						event.setCancelled(true);
					}
					return;

				}

			} else {

				this.main.alert(this.getOwnerName(), "You did not supply any arguments. You must either designate the click area on lines two and three, or set the cuboid area by right clicking with a piece of lightstone dust.");
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

		String typeLine = this.getLines(event)[3].toUpperCase();
		if (typeLine.equals("LEFT")) {
			left = true;
			right = false;
		} else if (typeLine.equals("RIGHT")) {
			right = true;
			left = false;
		} else {
			typeLine = "BOTH";
			left = true;
			right = true;
		}
		
		if (!reload) {
			this.clearArgLines(event);
			this.setLine(1, x1 + " " + y1 + " " + z1, event);
			this.setLine(2, x2 + " " + y2 + " " + z2, event);
			this.setLine(3, typeLine, event);
		}

		x1 += this.getHostLocation().getBlockX();
		y1 += this.getHostLocation().getBlockY();
		z1 += this.getHostLocation().getBlockZ();

		x2 += this.getHostLocation().getBlockX();
		y2 += this.getHostLocation().getBlockY();
		z2 += this.getHostLocation().getBlockZ();
		
		main.sgc.register(this, TriggerType.PLAYER_INTERACT);
		if (!reload) {
			this.init("Click sign accepted.");
		}
		
		xmin = Math.min(x1, x2);
		xmax = Math.max(x1, x2);
		
		ymin = Math.min(y1, y2);
		ymax = Math.max(y1, y2);
		
		zmin = Math.min(z1, z2);
		zmax = Math.max(z1, z2);
	}

}
