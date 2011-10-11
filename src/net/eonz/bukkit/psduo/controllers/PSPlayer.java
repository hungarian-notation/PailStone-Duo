package net.eonz.bukkit.psduo.controllers;

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

import net.eonz.bukkit.psduo.PailStone;

import org.bukkit.Location;
import org.bukkit.entity.Player;


public class PSPlayer {

	public final String name;
	public Location loc1, loc2, coord;
	public boolean l1 = true;
	public String message = null, lastChannel = null, lastBand;
	private final PailStone main;
	
	public PSPlayer(String name, PailStone main) {
		this.name = name;
		this.lastBand = name;
		this.main = main;
		this.main.c("Created new player tracker for " + name + ".");
	}
	
	public String getName() {
		return name;
	}
	
	public Player getPlayer() {
		return this.main.getServer().getPlayer(name);
	}
	
	public boolean isOnline() {
		return getPlayer() != null && getPlayer().isOnline();
	}
	
	public boolean coordMode = false;
	
	public void coordMode() {
		coordMode = true;
	}
	
	public void setLoc1(Location l) {
		this.main.alert(this.getName(), "Set point one.");
		loc1 = l;
		l1 = false;
	}
	
	public void setLoc2(Location l) {
		this.main.alert(this.getName(), "Set point two.");
		loc2 = l;
		l1 = true;
	}
	
	public void setCoord(Location l) {
		this.coord = l;
		this.coordMode = false;
		this.main.alert(this.getName(), "Coordinate set.");
	}
	
	public boolean validatePoints() {
		return (loc1.getWorld() == loc2.getWorld());
	}
	
	public void setLoc(Location l) {
		if (coordMode) {
			setCoord(l);
		} else if (l1) {
			setLoc1(l);
		} else {
			setLoc2(l);
		}
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setChannel(String channel) {
		this.lastChannel = channel;
	}
	
	public void setBand(String band) {
		this.lastBand = band;
	}
	
	public String getChannel() {
		return lastChannel;
	}
	
	public String getBand() {
		return lastBand;
	}
}

