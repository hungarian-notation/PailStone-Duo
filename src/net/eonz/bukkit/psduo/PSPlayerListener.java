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

import net.eonz.bukkit.psduo.signs.TriggerType;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class PSPlayerListener extends PlayerListener {

	private final PailStone main;
	private final PSPlayers players;

	public PSPlayerListener(PailStone main, PSPlayers players) {
		this.main = main;
		this.players = players;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		this.main.sgc.trigger(TriggerType.PLAYER_INTERACT, event);
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			if (event.getPlayer().getItemInHand().getType() == Material.GLOWSTONE_DUST ) {
				
				if( players.exists(event.getPlayer().getName())) {
					
					players.get(event.getPlayer().getName()).setLoc(event.getClickedBlock().getLocation());
					
				} else {
					
					players.safelyGet(event.getPlayer().getName(), this.main).setLoc(event.getClickedBlock().getLocation());
					
				}
				
			} /*else if (event.getPlayer().getItemInHand().getType() == Material.SLIME_BALL) {
				
				// TODO: Slime ball slime ball yummy yummy slime balls debug code is good for you so you should implement it.
				
				
				RightClickWrapper signCheck =  new RightClickWrapper(event, false);
				
				signs.update(UpdateType.DEBUG_CHECK, signCheck);
				
				if ((!signCheck.handled) && (event.getClickedBlock().getType() == Material.WALL_SIGN)) {
					Sign thisSign = (Sign)event.getClickedBlock().getState();
					signs.addSign(event.getClickedBlock(), thisSign.getLines(), event.getPlayer().getName(), false);
					
					signCheck =  new RightClickWrapper(event, true);
					signs.update(UpdateType.DEBUG_CHECK, signCheck);
					
					if (signCheck.handled) {
						event.getPlayer().sendMessage(ChatColor.DARK_GREEN.toString() + "[PailStone] Restored PailStone sign.");
					}
					
				}
				
				
			}*/
			
		}
		
	}
	
}
