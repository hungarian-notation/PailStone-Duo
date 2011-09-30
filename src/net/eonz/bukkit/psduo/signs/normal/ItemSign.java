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
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import net.eonz.bukkit.psduo.Direction;
import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class ItemSign extends PSSign {

	protected void triggersign(TriggerType type, Object args) {
		if (this.getInputId((BlockRedstoneEvent) args) != 1 || this.getInput(1, (BlockRedstoneEvent) args) != InputState.HIGH) {
			return;
		}

		Location spawnLoc = null;

		for (int i = 1; i <= 10; i++) {
			spawnLoc = Direction.shift(this.getHostLocation(), spawnDir, i);
			if ((this.getWorld().getBlockAt(spawnLoc).getType() == Material.AIR) && (this.getWorld().isChunkLoaded(this.getWorld().getBlockAt(spawnLoc).getChunk()))) {
				ItemStack toSpawn = new ItemStack(itemId, amount, (short)damage);
				this.getWorld().dropItemNaturally(spawnLoc, toSpawn);
				return;
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

	Direction spawnDir;
	int itemId;
	int damage;
	int amount;
	
	protected void declare(boolean reload, SignChangeEvent event) {
		String[] itemLine = this.getLines(event)[1].split(" ");
		
		itemId = 1;
		damage = 0;
		amount = 1;
		
		if (itemLine.length == 0) {
			if (!reload) {
				this.main.alert(this.getOwnerName(), "You MUST specify an Item ID.");
				event.setCancelled(true);
				return;
			}
		} else {
			if (itemLine[0].contains(":")) {
				String[] itemData = itemLine[0].split(":");
				try {
					itemId = Integer.parseInt(itemData[0]);
					damage = Integer.parseInt(itemData[1]);
				} catch (Exception e) {
					if (!reload) {
						this.main.alert(this.getOwnerName(), "Error parsing item or data value.");
						event.setCancelled(true);
					}
					return;
				}
			} else {
				try {
					itemId = Integer.parseInt(itemLine[0]);
					damage = 0;
				} catch (Exception e) {
					if (!reload) {
						this.main.alert(this.getOwnerName(), "Error parsing item value.");
						event.setCancelled(true);
					}
					return;
				}
			}
			
			if (itemLine.length == 2) {
				try {
					amount = Integer.parseInt(itemLine[1]);
				} catch (Exception e) {
					if (!reload) {
						this.main.alert(this.getOwnerName(), "Error parsing amount.");
						event.setCancelled(true);
					}
					return;
				}
			}
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
		
		if (!reload) {
			this.clearArgLines();
			this.setLine(2, spawnDir.toString(), event);
			String newItemLine = "" + itemId;
			if (damage != 0) newItemLine += ":" + damage;
			if (amount != 1) newItemLine += " " + amount;
			this.setLine(1, newItemLine, event);
		}
		
		main.sgc.register(this, TriggerType.REDSTONE_CHANGE);
		if (!reload) {
			this.init("Item sign accepted.");
		}
	}

}
