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
import org.bukkit.DyeColor;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Colorable;

import net.eonz.bukkit.psduo.Direction;
import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

/*
 * WARNING: 
 * This class contains sloppy code from PailStone Classic. Do not use as a reference.
 */

public class SpawnSign extends PSSign {

	protected void triggersign(TriggerType type, Object args) {
		
		if (this.getInputId((BlockRedstoneEvent)args) != 1 || this.getInput(1, (BlockRedstoneEvent) args) != InputState.HIGH) {
			return;
		}

		Location spawnLoc = null;
		
		for (int i = 1; i <= 10; i++) {
			spawnLoc = Direction.shift(this.getHostLocation(), spawnDir, i);
			if ((this.getWorld().getBlockAt(spawnLoc).getType() == Material.AIR) && (this.getWorld().isChunkLoaded(this.getWorld().getBlockAt(spawnLoc).getChunk()))) {

				spawnLoc.setX(spawnLoc.getX() + .5);
				spawnLoc.setZ(spawnLoc.getZ() + .5);

				LivingEntity c = this.getWorld().spawnCreature(spawnLoc, creature);
				
				if (arg != null) {
					if (c instanceof Wolf) {
						if (arg.equalsIgnoreCase("a")) {
							Wolf w = (Wolf) c;
							w.setAngry(true);
						}
					} else if (c instanceof Colorable && arg.length() >= 1) {
						int color = 0;
						try {
							color = Integer.parseInt(arg, 16);
						} catch (Exception e) {
							color = 0;
						}
						Colorable cle = (Colorable) c;
						cle.setColor(DyeColor.getByData((byte) color));
					} else if (c instanceof Slime) {
						Slime s = (Slime) c;
						int size = 0;
						try {
							size = Integer.parseInt(arg, 16);
						} catch (Exception e) {
							size = 0;
						}
						if (size < 1)
							size = 1;
						if (size > 8)
							size = 8;
						s.setSize(size);
					}
				}

				break;

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

	private CreatureType creature;
	private Direction spawnDir;
	private String arg;

	protected void declare(boolean reload, SignChangeEvent event) {

		boolean ctypeError = false;

		String cline = this.getLines(event)[1];

		String[] csplit = cline.split("\\:");

		try {
			creature = CreatureType.fromName(csplit[0]);
		} catch (Exception e) {
			ctypeError = true;
		}

		try {
			if (csplit.length > 1) {
				arg = csplit[1];
			} else {
				arg = null;
			}
		} catch (Exception e) {
			if (!reload)
				this.main.alert(this.getOwnerName(), "Error in parsing creature argument. Sign still valid.");
		}

		if (ctypeError || (creature == null)) {
			if (!reload)
				this.main.alert(this.getOwnerName(), "Unknown creature type. Please try again.");
			event.setCancelled(true);
			return;
		}

		String dirLine = this.getLines(event)[2];

		if (dirLine.equals("") || (dirLine == null)) {
			spawnDir = Direction.UP;
		} else {
			spawnDir = Direction.fromString(dirLine.toUpperCase());
			if ((spawnDir == Direction.ERROR) || (spawnDir == null)) {
				spawnDir = Direction.UP;
			}
		}

		if (!reload)
			this.clearArgLines();

		String ncline = creature.getName();
		if (arg != null) {
			ncline += ":" + arg;
		}

		if (!reload) {
			this.setLine(1, ncline, event);
			this.setLine(2, spawnDir.toString(), event);
		}

		main.sgc.register(this, TriggerType.REDSTONE_CHANGE);
		if (!reload) {
			this.init("Spawn sign accepted.");
		}
	}

}
