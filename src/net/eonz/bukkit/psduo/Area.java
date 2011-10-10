package net.eonz.bukkit.psduo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.Location;
import org.bukkit.World;

public class Area {
	public Area(String name) {
		this.areaName = name;
	}

	public String getName() {
		return areaName;
	}

	public boolean ready(PailStone plugin) {
		if (!isLoaded) {
			try {
				load(plugin);
				return true;
			} catch (FileNotFoundException e) {
				plugin.e(e.getMessage() + " while loading the '" + this.getName() + "' area.");
				if (plugin.cfgDebug)
					e.printStackTrace();
			} catch (IOException e) {
				plugin.e(e.getMessage() + " while loading the '" + this.getName() + "' area.");
				if (plugin.cfgDebug)
					e.printStackTrace();
			}

			return false;
		} else {
			return true;
		}
	}
	
	public int getSize() {
		return xsize * ysize * zsize;
	}

	public String getOwnerName() {
		if (ownerName == null) {
			ownerName = peekOwnerName();
		}
		return ownerName;
	}

	private String peekOwnerName() {
		File f = getFile();

		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new FileInputStream(f));
			in.readUTF();
			String owner = in.readUTF();
			in.close();

			return owner;
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}

		return "";
	}

	private boolean isLoaded = false;
	private boolean isModified = false;
	private String areaName;
	private String ownerName = null;
	private Location loc;
	private int xsize, ysize, zsize;
	private byte[] block;
	private byte[] data;

	public void load(PailStone plugin) throws FileNotFoundException, IOException {
		if (isLoaded && isModified)
			save(plugin);

		File f = getFile();

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));

		this.areaName = in.readUTF();
		this.ownerName = in.readUTF();

		World w = plugin.getServer().getWorld(in.readUTF());
		int x1 = in.readInt();
		int y1 = in.readInt();
		int z1 = in.readInt();

		this.loc = new Location(w, x1, y1, z1);

		this.xsize = in.readInt();
		this.ysize = in.readInt();
		this.zsize = in.readInt();

		this.block = new byte[xsize * ysize * zsize];
		this.data = new byte[xsize * ysize * zsize];

		in.read(block);
		in.read(data);

		in.close();

		this.isLoaded = true;
		this.isModified = false;
	}

	public void save(PailStone plugin) throws FileNotFoundException, IOException {
		if (!isLoaded)
			return;

		File f = getFile();

		f.delete();
		f.createNewFile();

		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));

		out.writeUTF(this.areaName);
		out.writeUTF(this.ownerName);

		out.writeUTF(loc.getWorld().getName());

		out.writeInt(loc.getBlockX());
		out.writeInt(loc.getBlockY());
		out.writeInt(loc.getBlockZ());

		out.writeInt(xsize);
		out.writeInt(ysize);
		out.writeInt(zsize);

		out.write(block);
		out.write(data);

		out.close();
	}

	private File getFile() {
		return new File(PailStone.dataPath + "areas/" + areaName + ".area");
	}

	protected byte getBlock(int x, int y, int z) {
		return block[x + xsize * (y + ysize * z)];
	}

	protected byte getData(int x, int y, int z) {
		return data[x + xsize * (y + ysize * z)];
	}

	protected void setBlock(byte id, int x, int y, int z) {
		block[x + xsize * (y + ysize * z)] = id;
		isModified = true;
	}

	protected void setData(byte d, int x, int y, int z) {
		data[x + xsize * (y + ysize * z)] = d;
		isModified = true;
	}

	protected void loadFromWorld(int ux1, int uy1, int uz1, int ux2, int uy2, int uz2, World w, String ownerName) {
		int x1 = Math.min(ux1, ux2);
		int y1 = Math.min(uy1, uy2);
		int z1 = Math.min(uz1, uz2);

		int x2 = Math.max(ux1, ux2);
		int y2 = Math.max(uy1, uy2);
		int z2 = Math.max(uz1, uz2);

		this.xsize = x2 - x1 + 1;
		this.ysize = y2 - y1 + 1;
		this.zsize = z2 - z1 + 1;

		block = new byte[xsize * ysize * zsize];
		data = new byte[xsize * ysize * zsize];

		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				for (int z = z1; z <= z2; z++) {
					this.setBlock((byte) w.getBlockAt(x, y, z).getTypeId(), x - x1, y - y1, z - z1);
					this.setData((byte) w.getBlockAt(x, y, z).getData(), x - x1, y - y1, z - z1);
				}
			}
		}

		loc = new Location(w, x1, y1, z1);

		this.ownerName = ownerName;
		this.isLoaded = true;
		this.isModified = true;
	}

	public void draw(PailStone plugin) {
		try {
			if (!isLoaded)
				load(plugin);

			int x1 = loc.getBlockX();
			int y1 = loc.getBlockY();
			int z1 = loc.getBlockZ();
			
			for (int x = x1; x < x1 + xsize; x++) {
				for (int y = y1; y < y1 + ysize; y++) {
					for (int z = z1; z < z1 + zsize; z++) {
						CuboidUtil.drawBlock(this.getBlock(x - x1, y - y1, z - z1), this.getData(x - x1, y - y1, z - z1), loc.getWorld(), x, y, z);
					}
				}
			}
		} catch (Exception e) {
			plugin.e(e.getMessage() + " while trying to draw area '" + this.getName() + "'.");
		}
	}

	public static Area newArea(Location pos1, Location pos2, String areaName, String ownerName) {
		Area a = new Area(areaName);
		a.loadFromWorld(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ(), pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ(), pos1.getWorld(), ownerName);
		return a;
	}
}
