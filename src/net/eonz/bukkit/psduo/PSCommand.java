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

import net.eonz.bukkit.psduo.signs.SignType;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import net.eonz.bukkit.psduo.signs.PSSign;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public class PSCommand implements CommandExecutor {

  private final PailStone main;

  public PSCommand(PailStone pailStone) {
    this.main = pailStone;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("msg") && isPlayer(sender)) {
				setMessage(sender, command, label, args);
				return true;
			}

			if (args[0].equalsIgnoreCase("listsigns")) {
				listSigns(sender, command, label, args);
				return true;
			}
			
      if (args[0].equalsIgnoreCase("reloadsigns")) {
        reloadSigns(sender, command, label, args);
        return true;
      }
		}

		sender.sendMessage("Malformed command.");

		return true;
	}

	/**
	 * Sets the player's message for announce, disp, etc.
	 * 
	 * @param sender
	 * @param command
	 * @param label
	 * @param args
	 */
	private void setMessage(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;

		if (args.length > 1) {
			String newMsg = combine(args, 1);
			PailStone.alert(p, "Saved: \"" + newMsg + "\"");
			PSPlayer psp = this.main.players.safelyGet(p.getName(), this.main);
			psp.setMessage(newMsg);
		} else {
			PSPlayer psp = this.main.players.safelyGet(p.getName(), this.main);
			if (psp.message != null) {
				PailStone.alert(p, "Stored message: \"" + psp.message + "\"");
			} else {
				PailStone.alert(p, "You have not stored a message. " + org.bukkit.ChatColor.AQUA + "/ps msg <message>");
			}
		}
	}

  /**
   * Reloads signs in given world or all worlds, re-creating them as the given player if they do not exist already.
   * 
   * @param sender
   * @param command
   * @param label
   * @param args
   */
  public void reloadSigns(CommandSender sender, Command command, String label, String[] args){
    if (!main.hasPermission((Player) sender, "pailstone.recreate", ((Player)sender).getWorld().getName())){
      sender.sendMessage("You do not have permission to run this command.");
      return;
    }
    sender.sendMessage("Searching for all currently loaded signs...");
    Integer signCount = 0;
    ArrayList<PSSign> allsigns = this.main.sgc.getAllSigns();
    for (PSSign sign : allsigns){
      if (sign.isLoaded()){
        this.main.sgc.invalidate(sign, "Reloading sign");
      }
    }
    for (World w : this.main.getServer().getWorlds()){
      for (Chunk c : w.getLoadedChunks()){
        for (BlockState b : c.getTileEntities()){
          if (b.getBlock().getState() instanceof Sign){
            SignChangeEvent event = new SignChangeEvent(b.getBlock(), (Player)sender, ((Sign)b).getLines());
            this.main.getServer().getPluginManager().callEvent(event);
            signCount++;
          }
        }
      }
    }
    sender.sendMessage("Found "+signCount+" signs!");
  }
	
	/**
	 * Parse the listsigns command.
	 * 
	 * @param sender
	 * @param command
	 * @param label
	 * @param args
	 */
	private void listSigns(CommandSender sender, Command command, String label, String[] args) {
		if (args.length >= 2) {
			Player p = main.getServer().getPlayer(args[1]);
			if (p != null) {
				if (args.length >= 3) {
					if (main.getServer().getWorld(args[2]) != null) {
						listSigns(p, sender, args[2]);
					} else {
						PailStone.alert(sender, "There is no \"" + args[2] + "\".");
					}
				} else {
					listSigns(p, sender, null);
				}
			} else {
				PailStone.alert(sender, args[1] + " could not be found.");
				PailStone.alert(sender, "Please note that " + args[1] + " must be logged in for this command to work.");
			}
		} else if (sender instanceof Player) {
			listSigns((Player) sender, sender, null);
		} else {
			PailStone.alert(sender, "This command must be called by or on a player.");
		}
	}

	/**
	 * List what signs 'p' can use on 'world' to 'sender'.
	 * 
	 * @param p
	 * @param sender
	 * @param world
	 */
	private void listSigns(Player p, CommandSender sender, String world) {
		String message = p.getName() + " can use: " + ChatColor.WHITE;

		String actualWorld = ((world == null) ? p.getWorld().getName() : world);

		for (SignType sign : SignType.values()) {
			if (main.hasPermission(p, sign.name().toLowerCase(), actualWorld)) {
				message += sign.name() + " ";
			}
		}
		message += ChatColor.GOLD + "on world \"" + actualWorld + "\".";
		PailStone.alert(sender, message);
	}

	/**
	 * Returns true if the sender is a player, sends a message to the sender and
	 * returns false if not.
	 * 
	 * @param sender
	 * @return
	 */
	public boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player) {
			return true;
		} else {
			sender.sendMessage("This command can only be used by a player.");
			return false;
		}
	}

  public static String combine(String[] args, int from) {
    String out = "";
    for (int i = from; i < args.length; i++) {
      if (i != from) {
        out += " ";
      }
      out += args[i];
    }
    return out;
  }

}
