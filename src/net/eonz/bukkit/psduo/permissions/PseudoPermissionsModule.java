package net.eonz.bukkit.psduo.permissions;

import java.util.Iterator;
import java.util.List;

public class PseudoPermissionsModule implements PermissionsInterface {
	private final List<String> allowList;
	
	public PseudoPermissionsModule(List<String> allowedPlayers) {
		this.allowList = allowedPlayers;
	}
	
	public boolean has(String player, String permission, String world) {
		Iterator<String> players = allowList.iterator();
		while (players.hasNext()) {
			if (player.equalsIgnoreCase(players.next())) {
				return true;
			}
		}
		return false;
	}

	public boolean inGroup(String player, String group, String world) {
		return false;
	}

	@Override
	public String getName() {
		return "allow-list permissions";
	}
}
