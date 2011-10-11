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

import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class SequenceSign extends PSSign {

	private boolean lastState[] = { false, false, false };

	private boolean empty = true;
	
	private long lastTriggered = -1;

	protected void triggersign(TriggerType type, Object args) {
		long time = this.getWorld().getFullTime();
		
		if ((lastTriggered + timeOut <= time || lastTriggered > time) && !empty) {
			this.reset();
		}

		if (args instanceof BlockRedstoneEvent) {
			int input = this.getInputId((BlockRedstoneEvent) args);

			InputState is = this.getInput(input, (BlockRedstoneEvent) args);

			if (is == InputState.HIGH && !lastState[input]) {
				lastState[input] = true;

				lastTriggered = time;
				
				enter(Entry.fromInput(input));
			} else if ((is == InputState.LOW || is == InputState.DISCONNECTED) && lastState[input]) {
				lastState[input] = false;
			} else {
				return;
			}
		}

		String dataLine = "";

		for (int i = 0; i < code.length; i++) {
			dataLine += (enteredCode[i] != null) ? enteredCode[i].id : " ";
		}

		this.setLine(3, dataLine);
		
		this.setOutput(validate());
	}

	private void enter(Entry entry) {
		System.out.println(entry.id + " " + enteredCode.length);

		for (int i = 1; i < enteredCode.length; i++) {
			enteredCode[i - 1] = enteredCode[i];
		}

		enteredCode[enteredCode.length - 1] = entry;
		
		empty = false;
	}

	private boolean validate() {
		for (int i = 0; i < code.length; i++) {
			if (code[i] != enteredCode[i]) {
				return false;
			}
		}
		return true;
	}

	private void reset() {
		enteredCode = new Entry[code.length];
		empty = true;
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

	Entry[] code;
	Entry[] enteredCode;

	int timeOut = 120;

	protected void declare(boolean reload, SignChangeEvent event) {
		String sequenceLine = this.getLines(event)[1].trim();

		if (sequenceLine.length() <= 0) {
			this.init("Please specify a code.");
			return;
		}

		code = new Entry[sequenceLine.length()];
		enteredCode = new Entry[sequenceLine.length()];

		for (int i = 0; i < sequenceLine.length(); i++) {
			code[i] = Entry.fromId(Character.toLowerCase(sequenceLine.charAt(i)));
			if (code[i] == null) {
				this.init("Invalid char, '" + sequenceLine.charAt(i) + "', in sequence.");
				return;
			}
		}

		String timeOutLine = this.getLines(event)[2].trim();

		if (timeOutLine.length() > 0) {
			try {
				timeOut = Integer.parseInt(timeOutLine);
			} catch (Exception e) {
				this.init("Could not read timeout. Defaulting to " + timeOut + ".");
			}
		}

		if (!reload) {
			this.clearArgLines(event);
			this.setLine(1, sequenceLine, event);
			this.setLine(2, timeOut + "", event);
		}

		main.sgc.register(this, TriggerType.REDSTONE_CHANGE);
		main.sgc.register(this, TriggerType.TIMER_SECOND);
		if (!reload) {
			this.init("Sequence sign accepted.");
		}
	}

	private static enum Entry {
		LEFT(0, 'a'), FRONT(1, 'b'), TOP(2, 'c');

		public final int input;
		public final char id;

		private Entry(int input, char id) {
			this.input = input;
			this.id = id;
		}

		public static Entry fromId(char id) {
			for (Entry e : values()) {
				if (e.id == id) {
					return e;
				}
			}

			return null;
		}

		public static Entry fromInput(int input) {
			for (Entry e : values()) {
				if (e.input == input) {
					return e;
				}
			}

			return null;
		}
	}
}
