package net.eonz.bukkit.psduo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;

public class AreaManager {
	private static File areaDirectory = new File(PailStone.dataPath + "areas/");

	static {
		if (!areaDirectory.exists()) {
			areaDirectory.mkdir();
		}
	}

	private HashMap<String, Area> areas;

	private final PailStone main;

	public AreaManager(PailStone main) {
		this.main = main;
		areas = new HashMap<String, Area>();
		
		loadAreas();
	}

	public void loadAreas() {
		this.areas.clear();

		File[] areas = areaDirectory.listFiles();
		for (File f : areas) {
			String name = f.getName().substring(0, f.getName().length() - 5);
			String extension = f.getName().substring(f.getName().length() - 4);

			if (extension.equals("area")) {
				this.areas.put(name, new Area(name));
			}
		}
	}

	public void saveAreas() {
		Iterator<Area> i = this.areas.values().iterator();
		while (i.hasNext()) {
			Area a = i.next();
			try {
				if (main.cfgDebug)
					main.c("Saving area '" + a.getName() + "'.");
				a.save(main);
			} catch (FileNotFoundException e) {
				main.e(e.getMessage() + " while saving the '" + a.getName() + "' area.");
				if (main.cfgDebug)
					e.printStackTrace();
			} catch (IOException e) {
				main.e(e.getMessage() + " while saving the '" + a.getName() + "' area.");
				if (main.cfgDebug)
					e.printStackTrace();
			}
		}
	}

	public List<Area> getPlayerAreas(String player) {
		ArrayList<Area> pAreas = new ArrayList<Area>();
		Iterator<Area> allAreas = this.areas.values().iterator();
		while (allAreas.hasNext()) {
			Area nArea = allAreas.next();
			if (nArea.getOwnerName().equalsIgnoreCase(player)) {
				pAreas.add(nArea);
			}
		}
		return pAreas;
	}
	
	public Area getArea(String name) {
		return this.areas.get(name);
	}

	public void defineArea(String areaName, String playerName, Location l1, Location l2) {
		this.areas.put(areaName, Area.newArea(l1, l2, areaName, playerName));
	}
}
