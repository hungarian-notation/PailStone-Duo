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

public enum TriggerType {

	/*
	 * The comments after each entry denote what is sent as the argument for the
	 * trigger.
	 */

	CLEAR, // LOCATION, clears signs from location.
	
	//CHUNK_LOAD, // ChunkLoadEvent instance
	
	PLAYER_INTERACT, // PlayerInteraceEvent instance
	
	REDSTONE_CHANGE, // BlockRedstoneEvent instance
	TIMER_SECOND, // null
	TIMER_HALF_SECOND, // null
	
	TRIGGER_COMMAND, // CommandArgWrapper instance
	
	SEND_DATA, // WirelessPacket instance
	PING; // null
}
