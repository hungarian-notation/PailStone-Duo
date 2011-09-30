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

import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class PSBlockListener extends BlockListener {

	private final PailStone main;

	public PSBlockListener(PailStone main) {
		this.main = main;
	}

	public void onSignChange(SignChangeEvent event) {
		
		if (event.getPlayer() == null) return; // Non player change events are ignored.
		
		if (event.getBlock().getState() instanceof Sign) {
			if (event.getLines().length > 0) {
				this.main.sgc.trigger(TriggerType.PING, null);
				Direction d = PSSign.getDirection((Sign) (event.getBlock().getState()));
				PSSign.signFactory(event.getLines(), event.getPlayer().getName(), null, event.getBlock().getWorld().getName(), event.getBlock().getLocation(), d, false, event, main);
			}
		} else {
			this.main.c("SignChangeEvent contains a non-sign block. Please send a list of plugins you are running to chris@eonz.net");
		}
	}

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		this.main.sgc.trigger(TriggerType.REDSTONE_CHANGE, event);
	}

}
