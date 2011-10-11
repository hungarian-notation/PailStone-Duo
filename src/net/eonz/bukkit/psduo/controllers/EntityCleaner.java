package net.eonz.bukkit.psduo.controllers;

import java.util.concurrent.DelayQueue;

import org.bukkit.entity.Entity;

public class EntityCleaner implements Runnable {

	private DelayQueue<ManagedEntity> cleanQueue;
	public EntityCleaner() {
		cleanQueue = new DelayQueue<ManagedEntity>();
	}
	
	public void run() {
		ManagedEntity toClean;
		while ((toClean = cleanQueue.poll()) != null) {
			toClean.clean();
		}
	}
 	
	public void register(Entity e, long lifespan) {
		cleanQueue.add(new ManagedEntity(e, lifespan));
	}
}
