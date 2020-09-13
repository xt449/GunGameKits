package com.github.xt449.gungamekits;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author xt449
 * Copyright BinaryBanana/xt449 2019
 * All Rights Reserved
 */
final class MainListener implements Listener {

	final List<PlayerTeleportEvent.TeleportCause> teleportCauses = Arrays.asList(PlayerTeleportEvent.TeleportCause.COMMAND, PlayerTeleportEvent.TeleportCause.PLUGIN, PlayerTeleportEvent.TeleportCause.SPECTATE);

	@EventHandler(priority = EventPriority.MONITOR)
	public final void onPlayerTeleport(PlayerTeleportEvent event) {
		if(teleportCauses.contains(event.getCause())) {
			GunGameKits.leavePvP(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public final void onPlayerDeath(PlayerDeathEvent event) {
		final EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
		if(damageEvent instanceof EntityDamageByEntityEvent) {
			final Entity entity = ((EntityDamageByEntityEvent) damageEvent).getDamager();
			if(entity instanceof Player) {
				GunGameKits.promoteKit((Player) entity);
			} else if(entity instanceof Projectile) {
				final ProjectileSource source = ((Projectile) entity).getShooter();
				if(source instanceof Player) {
					GunGameKits.promoteKit((Player) source);
				}
			}
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(GunGameKits.getPlugin(GunGameKits.class), () -> event.getEntity().spigot().respawn());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public final void onPlayerEditBook(PlayerEditBookEvent event) {
		GunGameKits.joinPvP(event.getPlayer());
	}

	private final Random random = new Random();

	@EventHandler(priority = EventPriority.HIGHEST)
	public final void onPlayerReaspawn(PlayerRespawnEvent event) {
		if(GunGameKits.inPvP(event.getPlayer())) {
			Location location;
			final World world = event.getRespawnLocation().getWorld();
			boolean safe = false;

			do {
				final int radius = random.nextInt(200);
				final double angle = random.nextDouble() * 2 * Math.PI;
				location = world.getHighestBlockAt((int) (Math.cos(angle) * radius), (int) (Math.sin(angle) * radius)).getLocation();
				if(location.getY() > 0 && location.getY() < 72) {
					safe = true;
				}
			} while(!safe);

			event.setRespawnLocation(location);
		}
	}
}
