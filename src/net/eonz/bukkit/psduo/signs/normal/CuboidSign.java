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

import org.bukkit.Material;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.PSPlayer;
import net.eonz.bukkit.psduo.PailStone;
import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class CuboidSign extends PSSign {
	
	protected void triggersign(TriggerType type, Object args) {
		
		int mat = lmat;
		byte dat = ldat;

		BlockRedstoneEvent event = (BlockRedstoneEvent) args;
		InputState is = this.getInput(1, event);

		if (is == InputState.HIGH) {
			mat = hmat;
			dat = hdat;
		}

		if (mat != -1) {
			int csize = Math.abs((x1 - x2 + 1) * (y1 - y2 + 1) * (z1 - z2 + 1));
			if (csize > this.main.cfgMaxCuboid) {
				this.main.alert(this.getOwnerName(), "The cuboid you specified was " + csize + " blocks big. The maximum acceptable area is " + this.main.cfgMaxCuboid + " blocks.");
				return;
			} else {
				if (dat != -1) {
					PailStone.drawCuboid(mat, dat, this.getWorld(), x1, y1, z1, x2, y2, z2);
				} else {
					PailStone.drawCuboid(mat, this.getWorld(), x1, y1, z1, x2, y2, z2);
				}
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

	int x1, y1, z1, x2, y2, z2, lmat, hmat;
	byte ldat, hdat;
	boolean has_ldat, has_hdat;

	protected void declare(boolean reload, SignChangeEvent event) {

		if (this.getLines(event)[1].equals("") || this.getLines(event)[2].equals("")) {

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

					this.main.alert(this.getOwnerName(), "You did not supply any arguments. You must either designate the cuboid area on lines two and three, or set the cuboid area by right clicking with a piece of lightstone dust.");
					if (!reload) {
						event.setCancelled(true);
					}
					return;

				}

			} else {

				this.main.alert(this.getOwnerName(), "You did not supply any arguments. You must either designate the cuboid area on lines two and three, or set the cuboid area by right clicking with a piece of lightstone dust.");
				if (!reload) {
					event.setCancelled(true);
				}
				return;

			}

		} else {

			String[] split1, split2;
			split1 = this.getLines(event)[1].split(" ");
			split2 = this.getLines(event)[2].split(" ");

			try {

				x1 = Integer.parseInt(split1[0]);
				y1 = Integer.parseInt(split1[1]);
				z1 = Integer.parseInt(split1[2]);

				x2 = Integer.parseInt(split2[0]);
				y2 = Integer.parseInt(split2[1]);
				z2 = Integer.parseInt(split2[2]);

			} catch (Exception e) {

				this.main.alert(this.getOwnerName(), "The coordinates you specified are either invalid or not formatted properly.");
				if (!reload) {
					event.setCancelled(true);
				}
				return;

			}

		}

		if (!reload) {
			this.setLine(1, x1 + " " + y1 + " " + z1, event);
			this.setLine(2, x2 + " " + y2 + " " + z2, event);
		}

		x1 += this.getHostLocation().getBlockX();
		y1 += this.getHostLocation().getBlockY();
		z1 += this.getHostLocation().getBlockZ();

		x2 += this.getHostLocation().getBlockX();
		y2 += this.getHostLocation().getBlockY();
		z2 += this.getHostLocation().getBlockZ();

		int csize = Math.abs((Math.abs(x1 - x2) + 1) * (Math.abs(y1 - y2) + 1) * (Math.abs(z1 - z2) + 1));

		if (csize > this.main.cfgMaxCuboid) {
			this.main.alert(this.getOwnerName(), "The cuboid you specified was " + csize + " blocks big. The maximum acceptable area is " + this.main.cfgMaxCuboid + " blocks.");
			if (!reload) {
				event.setCancelled(true);
			}
			return;
		}

		int matHigh, matLow;
		matHigh = -1;
		matLow = -1;

		String matLine = this.getLines(event)[3];
		String matArgs[] = matLine.split(" ");

		try {
			matLow = Integer.parseInt(matArgs[1].split(":")[0]);
			matLow = validate(matLow);
		} catch (Exception e) {
			matLow = -1;
		}

		try {
			matHigh = Integer.parseInt(matArgs[0].split(":")[0]);
			matHigh = validate(matHigh);
		} catch (Exception e) {
			matHigh = -1;
		}

		try {
			ldat = Byte.parseByte(matArgs[1].split(":")[1]);
		} catch (Exception e) {
			ldat = -1;
		}

		try {
			hdat = Byte.parseByte(matArgs[0].split(":")[1]);
		} catch (Exception e) {
			hdat = -1;
		}

		lmat = matLow;
		hmat = matHigh;
                
                // Check if block is allowed:
                if ((main.useWhiteList ? (!main.blockList.contains(lmat) || !main.blockList.contains(hmat)) : (main.blockList.contains(lmat) || main.blockList.contains(hmat))) && !main.hasPermission(this.getOwner(), "blocklist", main.getServer().getPlayer(this.getOwnerName()).getWorld().getName())) {
                    main.alert(this.getOwnerName(), "You are not allowed to use that block value.");
                    if (!reload) {
                        event.setCancelled(true);
                    }
                    return;
                }

		matLine = "" + matHigh + ":" + hdat + " " + matLow + ":" + ldat;

		if (!reload) {
			this.setLine(3, matLine, event);
		}

		main.sgc.register(this, TriggerType.REDSTONE_CHANGE);
		if (!reload) {
			this.init("Cuboid sign accepted.");
		}
	}

	private int validate(int id) {
		if (id == -1 || isValidID(id)) {
			return id;
		} else {
			return -1;
		}
	}

	public static boolean isValidID(int id) {

		boolean isValid = false;

		Material[] ms = Material.values();
		for (Material m : ms) {
			if (m.getId() == id && m.isBlock()) {
				isValid = true;
			}
		}

		return isValid;

	}

}
