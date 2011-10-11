package net.eonz.bukkit.psduo.controllers;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Entity;

public class ManagedEntity implements Delayed {

	private long killTime;
	private final Entity e;
	
	public ManagedEntity(Entity e, long life) {
		this.killTime = System.currentTimeMillis() + life;
		this.e = e;
	}

	public int compareTo(Delayed d) {
		return (int) Math.signum(this.getDelay(TimeUnit.MILLISECONDS) - d.getDelay(TimeUnit.MILLISECONDS));
	}

	public long getDelay(TimeUnit unit) {
		return unit.convert(msLeft(), TimeUnit.MILLISECONDS);
	}
	
	public long msLeft() {
		return killTime - System.currentTimeMillis();
	}
	
	public void clean() {
		this.e.eject();
		this.e.remove();
	}
}
