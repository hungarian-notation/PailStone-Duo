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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.eonz.bukkit.psduo.permissions.PermissionsInterface;
import net.eonz.bukkit.psduo.permissions.PermissionsSetup;
import net.eonz.bukkit.psduo.signs.PSSignCommand;
import net.eonz.bukkit.psduo.signs.SignController;
import net.eonz.bukkit.psduo.signs.TriggerType;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;

public class PailStone extends JavaPlugin {

	/**
	 * The global sign controller.
	 */
	public SignController sgc;

	public PSTickControl tickctrl;

	/**
	 * The player info tracker.
	 */
	public PSPlayers players = new PSPlayers();
	
	public AreaManager areas;

	/**
	 * The block listener for signs
	 */
	private PSBlockListener blockListener = new PSBlockListener(this);

	/**
	 * Listener for player events.
	 */
	private PSPlayerListener playerListener = new PSPlayerListener(this, players);

	/**
	 * The place to store all pailstone data.
	 */
	public static String dataPath = "./plugins/pail/stone/";

	public static File configFile = new File(dataPath + "config.txt");

	public PailConfigFile cfg;

	// CONFIG KEYS -------------

	public boolean cfgDebug;
	public boolean cfgWipeProtection;
	public int cfgMaxCuboid;
	public List<Integer> blockList;
	public boolean useWhiteList;
	public List<String> allowList;

	// END CONFIG --------------

	public void saveData() {
		sgc.save();
		cfg.save();
		areas.saveAreas();
	}
	
	public void onDisable() {
		saveData();
		this.c("Finished unloading. (" + this.getDescription().getVersion() + ")");
	}

	public void onEnable() {
		areas = new AreaManager(this);
		
		PluginDescriptionFile pdf = this.getDescription();

		this.c("Loading PailStone");
		this.c("Copyright (c) 2011 Chris Bode");
		this.c("Author contact: Chris@Eonz.net");
		this.c("PailStone is licensed under the");
		this.c("RECIPROCAL PUBLIC LICENSE 1.1");

		cfg = new PailConfigFile(this, configFile);
		cfg.load();
		
		ArrayList<String> devList = new ArrayList<String>();
		devList.add("Hafnium");
		devList.add("Thulinma");
		devList.add("imjake9");

		cfgDebug = cfg.getBoolean("debug", false);
		cfgWipeProtection = cfg.getBoolean("wipe-protection", true);
		cfgMaxCuboid = cfg.getInt("max-cuboid-area", 400);
		blockList = cfg.getIntegerList("block-blacklist", new ArrayList<Integer>());
		useWhiteList = cfg.getBoolean("use-blacklist-as-whitelist", false);
		allowList = cfg.getCSV("allow-list", devList);

		cfg.announce();

		cfg.save();

		setupPermissions();

		sgc = new SignController(this);
		tickctrl = new PSTickControl(this);

		sgc.load();

		PluginManager pm = this.getServer().getPluginManager();

		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);

		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new PSTickUpdate(TriggerType.TIMER_SECOND, sgc), 10, 20);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new PSTickUpdate(TriggerType.TIMER_HALF_SECOND, sgc), 10, 10);

		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, tickctrl, 10, 1);

		this.getCommand("trigger").setExecutor(new PSSignCommand(sgc, TriggerType.TRIGGER_COMMAND));
		this.getCommand("pailstone").setExecutor(new PSCommand(this));

		this.c("Finished Loading. (" + pdf.getVersion() + ")");
	}

	Logger log = Logger.getLogger("PailStone");

	public void c(String message) {
		log.log(Level.INFO, "[PailStone] " + message);
	}

	public void e(String message) {
		log.log(Level.SEVERE, "[PailStone] [ERROR] " + message);
	}

	public void d(String message) {
		c("[*DEBUG*] " + message);
	}

	public static void alert(CommandSender sender, String message) {
		if (sender instanceof Player) {
			message = ChatColor.GOLD + "[PailStone] " + message;
			for (String line : wrap(message, 54)) {
				sender.sendMessage(line);
			}
		} else {
			sender.sendMessage("[PS] " + message);
		}
	}

	// Not going to deprecate this, but avoid if possible.
	public boolean alert(String playerName, String message) {
		Player p = this.getServer().getPlayer(playerName);
		if (p != null && p.isOnline()) {
			message = ChatColor.GOLD + "[PailStone] " + message;
			for (String line : wrap(message, 54)) {
				p.sendMessage(line);
			}
			return true;
		}
		return false;
	}

	/**
	 * This method standardizes a lines array, removing all color codes and
	 * adding blank lines to the array if they don't exist.
	 * 
	 * @param lines
	 * @return
	 */
	public static String[] formatLines(String[] lines) {
		String[] newLines = new String[4];
		for (int i = 0; i < 4; i++) {
			if (lines.length > i) {
				newLines[i] = org.bukkit.ChatColor.stripColor(lines[i]);
			} else {
				newLines[i] = "";
			}
		}
		return newLines;
	}

	private PermissionsInterface permissionHandler;

	private void setupPermissions() {
		permissionHandler = PermissionsSetup.getBestPermissions(this);
		if (permissionHandler == null) {
			this.e("No compatable permissions detected. All players will be able to use all PailStone features. You can add players to allow-list so that only they will be able to use pailstone's features.");
		} else {
			this.c("using " + permissionHandler.getName() + ".");
		}
	}

	public boolean hasPermission(Player p, String permission, String world) {
		// Have to check all three due to a peculiarity in bukkit's built-in
		// permissions.
		return hasPermission(p.getName(), permission, world);
	}

	public boolean hasPermission(String p, String permission, String world) {
		// Have to check all three due to a peculiarity in bukkit's built-in
		// permissions.
		if (this.permissionHandler == null)
			return true;

		return this.permissionHandler.has(p, "pailstone." + permission, world) || this.permissionHandler.has(p, "pailstone.*", world) || this.permissionHandler.has(p, "*", world);
	}

	public boolean inGroup(Player p, String world, String group) {
		return this.inGroup(p.getName(), world, group);
	}

	public boolean inGroup(String p, String world, String group) {
		if (this.permissionHandler == null)
			return false;
		return this.permissionHandler.inGroup(p, group, world);
	}

	public static String[] wrap(String message, int lineLength) {
		ArrayList<String> lines = new ArrayList<String>();

		int cursor = 0;

		ChatColor last = ChatColor.GOLD;

		while (cursor < message.length() && message.length() >= 0) {
			char next = message.charAt(cursor);
			if (next == '\n') {
				lines.add(message.substring(0, cursor).trim());
				message = last + message.substring(cursor + 1).trim();
				cursor = 0;
			} else if (next == '\u00A7') {
				cursor += 2;
				last = ChatColor.getByCode(Integer.parseInt("" + message.charAt(cursor - 1), 16));
			} else if (cursor >= lineLength) {
				while (message.charAt(cursor) != ' ') {
					cursor--;
					if (cursor < 0) {
						cursor = lineLength - 1;
						break;
					}
				}
				lines.add(message.substring(0, cursor).trim());
				message = last + message.substring(cursor).trim();
				cursor = 0;
			} else {
				cursor++;
			}
		}

		lines.add(message.trim());

		return lines.toArray(new String[0]);
	}
}