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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class PailConfigFile {

	private final PailStone main;

	private File cfg;

	ArrayList<PCfgKey> keys = new ArrayList<PCfgKey>();

	public PailConfigFile(PailStone main, File cfg) {
		this.main = main;
		this.cfg = cfg;
	}

	public void load() {
		keys.clear();

		if (cfg.isDirectory()) {
			cfg = new File(cfg.getAbsolutePath() + "config.txt");
		}

		if (!cfg.exists()) {
			try {
				cfg.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				this.main.e("Error while creating config file.");
			}
		} else {
			try {
				BufferedReader in = new BufferedReader(new FileReader(cfg));
				String line;
				while ((line = in.readLine()) != null) {
					line = line.trim();
					if (line.charAt(0) != '#') {
						if (line.contains("=")) {
							String[] args = line.split("=");
							String key = args[0].trim();
							String value = (args.length >= 2) ? args[1].trim() : "";
							keys.add(new PCfgKey(key, value));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.main.e("Error while updating config file.");
			}
		}
	}

	public void save() {
		boolean changes = false;
		for (int i = 0; i < keys.size(); i++) {
			if (keys.get(i).isChanged()) {
				changes = true;
			}
		}
		if (changes) {
			try {
				PrintWriter out = new PrintWriter(new FileOutputStream(cfg));
				for (int i = 0; i < keys.size(); i++) {
					String line = "";
					PCfgKey cc = keys.get(i);
					line += cc.key;
					line += " = ";
					line += cc.value;
					out.write(line + "\n");
				}
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				this.main.e("Error while updating config file.");
			}
		}
	}

	public void announce() {
		for (int i = 0; i < keys.size(); i++) {
			PCfgKey k = keys.get(i);
			this.main.c(k.getKey() + " is set to '" + k.getValue() + "'");
		}
	}

	public String getString(String key, String defaultValue) {
		for (int i = 0; i < keys.size(); i++) {
			PCfgKey k = keys.get(i);
			if (key.equals(k.getKey())) {
				return k.getValue();
			}
		}
		PCfgKey nk = new PCfgKey(key, defaultValue);
		nk.dirty();
		keys.add(nk);
		return nk.getValue();
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		String val = this.getString(key, Boolean.toString(defaultValue));
		// Requires no checking. parseBoolean returns false on faulty input.
		return Boolean.parseBoolean(val);
	}

	public List<Integer> getIntegerList(String key, List<Integer> defaultValue) {
		String defaultList = "";
		for (int i = 0; i < defaultValue.size(); i++) {
			if (i != 0)
				defaultList += ", ";
			defaultList += defaultValue.get(i);
		}

		String valString = this.getString(key, defaultList);

		if (valString == null || valString.equals(""))
			return defaultValue;
		List<Integer> val = new ArrayList<Integer>();
		String[] split = valString.replaceAll(" ", "").split(",");
		for (String s : split) {
			s = s.trim();
			if (!s.equals("")) {
				try {
					Integer nextVal = new Integer(s);
					val.add(nextVal);
				} catch (NumberFormatException e) {
					main.e("Found \"" + s + "\" as an item in " + key + ". " + key + " is required to be a list of integers.");
					continue;
				}
			}
		}
		return val;
	}

	public List<String> getCSV(String key, List<String> defaultValue) {
		String defaultList = "";

		for (int i = 0; i < defaultValue.size(); i++) {
			if (i != 0)
				defaultList += ", ";
			defaultList += defaultValue.get(i);
		}

		String valString = this.getString(key, defaultList);

		List<String> csv = new ArrayList<String>();
		String[] split = valString.split(",");
		for (String s : split) {
			s = s.trim();
			if (!s.equals(""))
				csv.add(s);
		}

		return csv;
	}

	public int getInt(String key, int defaultValue) {
		String val = this.getString(key, Integer.toString(defaultValue));
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			main.e("Found \"" + val + "\" as the value of " + key + ". " + key + " is required to be an integer.");
			return defaultValue;
		}
	}

	private static class PCfgKey {
		private final String key;
		private String value;

		private boolean changed;

		public PCfgKey(String key, String value) {
			this.key = key;
			this.value = value;
			changed = false;
		}

		public void dirty() {
			changed = true;
		}

		public String getValue() {
			return value;
		}

		public String getKey() {
			return key;
		}

		public boolean isChanged() {
			return changed;
		}
	}

}
