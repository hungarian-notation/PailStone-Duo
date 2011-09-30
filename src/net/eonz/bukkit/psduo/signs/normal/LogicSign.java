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

public class LogicSign extends PSSign {

	protected void triggersign(TriggerType type, Object args) {

		if (true) {

			int inputs = 0;

			BlockRedstoneEvent event = (BlockRedstoneEvent) args;
			
			boolean input[] = new boolean[3];

			for (int i = 0; i < 3; i++) {

				InputState is = this.getInput(i, event);

				if (is != InputState.DISCONNECTED) {

					boolean ninput = false;
					if (is == InputState.HIGH)
						ninput = true;
					input[inputs] = ninput;
					inputs++;

				}
			}

			if (inputs > 1) {

				Type t = Type.getType(this.getLines()[1]);

				if (!(t == Type.ERROR)) {
					boolean out = false;
					if (t == Type.OR) {
						out = false;
						for (int i = 0; i < inputs; i++) {
							if (input[i]) {
								out = true;
							}
						}
					} else if (t == Type.NOR) {
						out = true;
						for (int i = 0; i < inputs; i++) {
							if (input[i]) {
								out = false;
							}
						}
					} else if (t == Type.AND) {
						out = true;
						for (int i = 0; i < inputs; i++) {
							if (!input[i]) {
								out = false;
							}
						}
					} else if (t == Type.NAND) {
						out = false;
						for (int i = 0; i < inputs; i++) {
							if (!input[i]) {
								out = true;
							}
						}
					} else if (t == Type.XNOR) {
						boolean ffalse = false;
						boolean ftrue = false;
						for (int i = 0; i < inputs; i++) {
							if (input[i]) {
								ftrue = true;
							} else {
								ffalse = true;
							}
						}
						if (ftrue && ffalse) {
							out = false;
						} else {
							out = true;
						}
					} else if (t == Type.XOR) {
						int high = 0;
						for (int i = 0; i < inputs; i++) {
							if (input[i]) {
								high++;
							}
						}
						if (high == 1) {
							out = true;
						} else {
							out = false;
						}
					}

					this.setOutput(out);

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

	protected void declare(boolean reload, SignChangeEvent event) {
		main.sgc.register(this, TriggerType.REDSTONE_CHANGE);
		if (!reload) {
			this.init("Logic sign accepted.");
		}
	}

	/**
	 * The type of logic sign
	 */
	protected enum Type {

		// REMEMBER TO ADD TYPES TO THE STATIC ARRAY!

		NAND(new String[] { "nand", "no and", "!and", "!+", "!&", "-" }), AND(new String[] { "and", "+", "&", "&&" }), OR(new String[] { "or", "|", "||" }), NOR(new String[] { "nor", "!|", "x" }), XOR(new String[] { "xor", "eor", "exor", "!=", "<>", "#" }), XNOR(new String[] { "xnor", "enor",
				"exnor", "=", "==" }), ERROR(new String[0]);

		final String aliases[];

		static Type TYPES[] = new Type[] { NAND, AND, XNOR, XOR, OR, NOR };

		Type(String[] a) {
			aliases = a;
		}

		String[] getAliases() {
			return aliases;
		}

		static Type getType(String s) {

			Type tm = Type.ERROR;

			for (Type t : TYPES) {

				for (String a : t.getAliases()) {

					if (s.equalsIgnoreCase(a)) {

						tm = t;

					}

				}

			}

			return tm;

		}

	}

}
