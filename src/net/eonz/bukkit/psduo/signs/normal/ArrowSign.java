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
import org.bukkit.entity.Arrow;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.Vector;

import net.eonz.bukkit.psduo.Direction;
import net.eonz.bukkit.psduo.controllers.PSPlayer;
import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class ArrowSign extends PSSign {

	private boolean lastState = false;

	protected void triggersign(TriggerType type, Object args) {
		InputState is = this.getInput(1, (BlockRedstoneEvent) args);

		if (is == InputState.HIGH && !lastState) {
			lastState = true;

			Vector v = new Vector(to.getBlockX() - from.getBlockX(), to.getBlockY() - from.getBlockY(), to.getBlockZ() - from.getBlockZ());

			v.normalize();

			v.multiply(speed);
			
			while (from.getBlock().getType() != Material.AIR) {
				from = from.toVector().add(v.clone().normalize().multiply(.2)).toLocation(this.getWorld());
			}
			
			for (int i = 0; i < arrows; i++) {
				
				Arrow a = this.getWorld().spawn(from, Arrow.class);
				a.setVelocity(v.clone().add(getVariance(variance)));
				this.main.cleaner.register(a, 5000);

			}
			
			this.getWorld().playEffect(from, org.bukkit.Effect.BOW_FIRE, 0);
		} else if ((is == InputState.LOW || is == InputState.DISCONNECTED) && lastState) {
			lastState = false;
		} else {
			return;
		}
	}

	private Vector getVariance(float variance) {
		Vector v = new Vector((Math.random() * 2 - 1), (Math.random() * 2 - 1), (Math.random() * 2 - 1)).normalize().multiply(Math.random() * variance);
		return (v);
		
	}

	public String getData() {
		// This sign does not use data.
		return "";
	}

	protected void setData(String data) {
		if (data == null)
			return;
		// This sign does not use data.
	}

	Location from, to;
	int arrows = 1;
	float speed = 1;
	float variance = .1f;

	protected void declare(boolean reload, SignChangeEvent event) {
		PSPlayer psp = this.main.players.safelyGet(this.getOwnerName(), main);

		String[] lines = this.getLines(event);

		if (lines[1].trim().equals("") && lines[2].trim().equals("")) {
			if (psp.l1 && (psp.loc2 != null)) {
				from = psp.loc1;
				to = psp.loc2;
			} else {
				this.main.alert(this.getOwnerName(), "You did not supply enough location arguments. You must either designate the from and to locations on lines two and three, or set them by right clicking with a piece of lightstone dust.");
				if (!reload) {
					event.setCancelled(true);
				}
				return;
			}
		} else {
			String[] split1 = lines[1].split(" ");
			String[] split2 = lines[2].split(" ");

			try {

				int x1 = Integer.parseInt(split1[0]);
				int y1 = Integer.parseInt(split1[1]);
				int z1 = Integer.parseInt(split1[2]);

				int x2 = Integer.parseInt(split2[0]);
				int y2 = Integer.parseInt(split2[1]);
				int z2 = Integer.parseInt(split2[2]);

				Location h = this.getHostLocation();

				from = new Location(this.getWorld(), h.getBlockX() + x1, h.getBlockY() + y1, h.getBlockZ() + z1);
				to = new Location(this.getWorld(), h.getBlockX() + x2, h.getBlockY() + y2, h.getBlockZ() + z2);

			} catch (Exception e) {

				this.main.alert(this.getOwnerName(), "The coordinates you specified are either invalid or not formatted properly.");
				if (!reload) {
					event.setCancelled(true);
				}
				return;

			}
		}

		Direction.center(from);
		Direction.center(to);
		
		System.out.println(from.getX() + " " + from.getY() + " " + from.getZ());

		String modLine = lines[3];

		if (!modLine.trim().equals("")) {
			String[] modArgs = modLine.split(" ");
			try {
				arrows = Integer.parseInt(modArgs[0]);

				if (modArgs.length >= 2) {
					speed = Float.parseFloat(modArgs[1]);
				}

				if (modArgs.length >= 3) {
					variance = Float.parseFloat(modArgs[2]);
				}
			} catch (Exception e) {
				this.main.alert(this.getOwnerName(), "Error parsing line 3.");
			}
		}

		if (!reload) {
			this.clearArgLines(event);
			this.setLine(1, coordLine(from, this.getHostLocation()), event);
			this.setLine(2, coordLine(to, this.getHostLocation()), event);
			this.setLine(3, arrows + " " + speed + " " + variance, event);
		}

		main.sgc.register(this, TriggerType.REDSTONE_CHANGE);
		if (!reload) {
			this.init("Arrow sign accepted.");
		}
	}

	private String coordLine(Location coord, Location from) {
		return (coord.getBlockX() - from.getBlockX()) + " " + (coord.getBlockY() - from.getBlockY()) + " " + (coord.getBlockZ() - from.getBlockZ());
	}
}
