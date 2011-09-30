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

import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class CtimeSign extends PSSign {

	protected void triggersign(TriggerType type, Object args) {
		int ctime = (int) this.getWorld().getTime();
		
		boolean ncurrent = false;

		if (ctime <= highTime && ctime >= lowTime) {
			ncurrent = betweenState;
		} else {
			ncurrent = !betweenState;
		}

		if (ncurrent) {
			this.setOutput(true);
			current = true;
		} else {
			this.setOutput(false);
			current = false;
		}

	}

	public String getData() {
		// This sign does not use data.
		return "";
	}

	protected void setData(String data) {
		// This sign does not use data.
	}

	boolean betweenState;
	int lowTime, highTime;
	boolean current = false;

	protected void declare(boolean reload, SignChangeEvent event) {
		int l1, l2;

		String[] lines = this.getLines(event);

		if (lines[1].equals("") && lines[2].equals("")) {

			lowTime = 0;
			l1 = lowTime;
			highTime = 12000;
			l2 = highTime;
			betweenState = true;

		} else {

			l1 = fixTime(parseTime(lines[1]));
			l2 = fixTime(parseTime(lines[2]));

			if (l1 <= l2) {

				betweenState = true;
				highTime = l2;
				lowTime = l1;

			} else {

				betweenState = false;
				highTime = l1;
				lowTime = l2;

			}

		}

		if (!reload) {
			this.clearArgLines(event);
			this.setLine(1, "" + l1, event);
			this.setLine(2, "" + l2, event);
		}

		main.sgc.register(this, TriggerType.TIMER_SECOND);
		if (!reload) {
			this.init("ctime sign accepted.");
		}
		
		this.triggersign(null, null);
	}

	private int parseTime(String ts) {

		if (isInteger(ts)) {
			return Integer.parseInt(ts);
		} else {
			return 0;
		}

	}

	public boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private int fixTime(int time) {
		if (time > 24000) {
			time = 24000;
		}
		if (time < 0) {
			time = 0;
		}
		return time;
	}

}
