package net.eonz.bukkit.psduo.signs;

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

/*
 * Note that the text used to define these enum fields is the name that must be used in-game, although not case sensitively, to define the sign. 
 */

public enum SignType {
	TEST(false, false),
	
	RAND(true, true),
	
	LOGIC(true, true), DELAY(true, true), TOGGLE(true, true), PULSE(true, true),
	
	SEND(true, true), RECV(true, true), TRIGGER(true, true), CLICK(true, true),
	
	COUNT(true, true), CLOCK(true, true),
	
	CTIME(true, true), CWEATHER(true, true), LOGGED(true, true), SENSOR(true, true),
	
	DISP(true, false), ANNOUNCE(true, false),
	
	CUBOID(false, true), SPAWN(false, true), AREA(false, true), ITEM(false, true), BOLT(false, true);
	
	public final boolean defaultPermission;
	public final boolean reloadable;
	private SignType(boolean def, boolean reloadable) {
		this.defaultPermission = def;
		this.reloadable = reloadable;
	}
}
