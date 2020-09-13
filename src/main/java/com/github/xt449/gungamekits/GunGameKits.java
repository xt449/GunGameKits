package com.github.xt449.gungamekits;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author xt449
 * Copyright BinaryBanana/xt449 2019
 * All Rights Reserved
 */
public final class GunGameKits extends JavaPlugin {

	private static final Map<UUID, Integer> playerRanks = new HashMap<>();

	private static MainConfiguration mainConfiguration;

	@Override
	public final void onLoad() {
		ConfigurationSerialization.registerClass(MainConfiguration.KitData.class, "KitData");
	}

	@Override
	public final void onEnable() {
		mainConfiguration = new MainConfiguration(this);
		mainConfiguration.initialize();

		Bukkit.getPluginManager().registerEvents(new MainListener(), this);
	}

	@Override
	public final void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
	}

	private static final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();

	private static void saveInventory(final Player player) {
		savedInventories.put(player.getUniqueId(), player.getInventory().getContents());
	}

	private static void loadInventory(final Player player) {
		if(savedInventories.containsKey(player.getUniqueId())) {
			player.getInventory().setContents(savedInventories.get(player.getUniqueId()));
		}
	}

	static void joinPvP(final Player player) {
		final UUID uuid = player.getUniqueId();

		if(!playerRanks.containsKey(uuid)) {// TODO
			saveInventory(player);

			playerRanks.put(uuid, 0);

			mainConfiguration.getKits().get(0).apply(player);
		}
	}

	static void leavePvP(final Player player) {
		loadInventory(player);

		playerRanks.remove(player.getUniqueId());
	}

	static boolean inPvP(final Player player) {
		return playerRanks.containsKey(player.getUniqueId());
	}

	static void promoteKit(final Player player) {
		final UUID uuid = player.getUniqueId();
		int rank;
		if(playerRanks.containsKey(uuid)) {
			rank = playerRanks.get(player.getUniqueId());
		} else {
			rank = 0;
		}
		playerRanks.put(uuid, ++rank);

		mainConfiguration.getKits().get(rank).apply(player);

		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
	}

	static String serializeItemData(final ItemStack itemStack) {
		final StringBuilder builder = new StringBuilder(itemStack.getType().name());
		builder.append(' ').append(itemStack.getAmount());
		for(Map.Entry<Enchantment, Integer> enchantment : itemStack.getEnchantments().entrySet()) {
			builder.append(' ').append(enchantment.getKey().getKey().getKey()).append(':').append(enchantment.getValue());
		}
		if(itemStack.getItemMeta().hasAttributeModifiers()) {
			for(Map.Entry<Attribute, AttributeModifier> attribute : itemStack.getItemMeta().getAttributeModifiers().entries()) {
				builder.append(' ').append(attribute.getKey().name()).append(':').append(attribute.getValue().getAmount());
			}
		}
		return builder.toString();
	}

	static ItemStack deserializeItemData(final String string) {
		/*final String[] parts = string.toUpperCase().split(" ");
		final Material material = Material.getMaterial(parts[0]);
		if(material == null) {
			throw new IllegalArgumentException(parts[0] + " is not a valid material name");
		}

		final ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(material);
		itemMeta.setUnbreakable(true);

		int amount = 0;
		if(parts.length > 1) {
			amount = Integer.parseInt(parts[1]);
			if(parts.length > 2) {
				Arrays.stream(parts).skip(2).filter(s -> s.contains(":")).forEach(modifier -> {
					final String[] segments = modifier.split(":");
					if(segments.length == 2) {
						Attribute attribute = null;
						Enchantment enchantment;
						try {
							try {
								attribute = Attribute.valueOf(segments[0]);
							} catch(IllegalArgumentException exc) {
								// continue
							}
							if(attribute != null) {
								EquipmentSlot equipmentSlot = EquipmentSlot.OFF_HAND;
								final String materialName = material.getKey().getKey().toLowerCase();
								if(materialName.contains("sword")) {
									equipmentSlot = EquipmentSlot.HAND;
								} else if(materialName.contains("helmet")) {
									equipmentSlot = EquipmentSlot.HEAD;
								} else if(materialName.contains("chestplate")) {
									equipmentSlot = EquipmentSlot.CHEST;
								} else if(materialName.contains("leggings")) {
									equipmentSlot = EquipmentSlot.LEGS;
								} else if(materialName.contains("boots")) {
									equipmentSlot = EquipmentSlot.FEET;
								}
								itemMeta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attribute.toString(), Double.parseDouble(segments[1]), AttributeModifier.Operation.ADD_NUMBER, equipmentSlot));
							} else {
								System.out.println("debug 3");
								enchantment = Enchantment.getByKey(new NamespacedKey(NamespacedKey.MINECRAFT, segments[0].toLowerCase()));
								if(enchantment == null) {
									enchantment = Enchantment.getByName(segments[0].toUpperCase());
								}
								if(enchantment != null) {
									itemMeta.addEnchant(enchantment, Integer.parseInt(segments[1]), true);
								} else {
									Bukkit.getLogger().warning("Invalid modifier flag name \"" + segments[0] + "\"!");
								}
							}
						} catch(NumberFormatException exc) {
							Bukkit.getLogger().warning("Invalid modifier value \"" + segments[1] + "\"!");
						}
					} else {
						Bukkit.getLogger().warning("Modifier flags must contain exactly 1 ':' not at an end of the 'word'!");
					}
				});
			}
		}

		return new ItemStack(material, amount);*/
		final String[] parts = string.toUpperCase().split(" ");
		final Material material = Material.getMaterial(parts[0]);
		if(material == null) {
			throw new IllegalArgumentException(parts[0] + " is not a valid material name");
		}
		int count = 0;
		if(parts.length > 1) {
			count = Integer.parseInt(parts[1]);
		}
		final ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(material);
		itemMeta.setUnbreakable(true);
		if(parts.length > 2) {
			Arrays.stream(parts).skip(2).filter(s -> s.contains(":")).forEach(modifier -> {
//			    System.out.println(modifier);
				final String[] segments = modifier.split(":");
				if(segments.length == 2) {
//					System.out.println("debug 1");
					Attribute attribute = null;
					Enchantment enchantment;
					try {
						try {
							attribute = Attribute.valueOf(segments[0]);
						} catch(IllegalArgumentException exc) {
							// continue
						}
						if(attribute != null) {
//							System.out.println("debug 2");
							EquipmentSlot equipmentSlot = EquipmentSlot.OFF_HAND;
							final String materialName = material.getKey().getKey().toLowerCase();
							if(materialName.contains("sword")) {
								equipmentSlot = EquipmentSlot.HAND;
							} else if(materialName.contains("helmet")) {
								equipmentSlot = EquipmentSlot.HEAD;
							} else if(materialName.contains("chestplate")) {
								equipmentSlot = EquipmentSlot.CHEST;
							} else if(materialName.contains("leggings")) {
								equipmentSlot = EquipmentSlot.LEGS;
							} else if(materialName.contains("boots")) {
								equipmentSlot = EquipmentSlot.FEET;
							}
							itemMeta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attribute.toString(), Double.parseDouble(segments[1]), AttributeModifier.Operation.ADD_NUMBER, equipmentSlot));
//							System.out.println(attribute);
//							if(itemMeta.hasAttributeModifiers()) {
//								System.out.println("WOAH! value=" + itemMeta.getAttributeModifiers(attribute).iterator().next().getAmount());
//							}
						} else {
							System.out.println("debug 3");
							enchantment = Enchantment.getByKey(new NamespacedKey(NamespacedKey.MINECRAFT, segments[0].toLowerCase()));
							if(enchantment == null) {
								enchantment = Enchantment.getByName(segments[0].toUpperCase());
							}
							if(enchantment != null) {
//								System.out.println("debug 4");
								itemMeta.addEnchant(enchantment, Integer.parseInt(segments[1]), true);
							} else {
//								System.out.println("debug 5");
								Bukkit.getLogger().warning("Invalid modifier flag name \"" + segments[0] + "\"!");
							}
						}
					} catch(NumberFormatException exc) {
						Bukkit.getLogger().warning("Invalid modifier value \"" + segments[1] + "\"!");
					}
				} else {
					Bukkit.getLogger().warning("Modifier flags must contain exactly 1 ':' not at an end of the 'word'!");
				}
			});
		}
//		System.out.println("0: Unbreakable transfered? : " + itemMeta.isUnbreakable());
//		System.out.println("0: Attributes transfered? : " + itemMeta.hasAttributeModifiers());
//		System.out.println("0: Enchantments transfered? : " + itemMeta.hasEnchants());

		final ItemStack itemStack = new ItemStack(material, count);
		final boolean success = itemStack.setItemMeta(itemMeta);
//		System.out.println("!!! - Item Meta applied successfully? : " + success);
//		System.out.println("1: Unbreakable transfered? : " + itemStack.getItemMeta().isUnbreakable());
//		System.out.println("1: Attributes transfered? : " + itemStack.getItemMeta().hasAttributeModifiers());
//		System.out.println("1: Enchantments transfered? : " + itemStack.getItemMeta().hasEnchants());
		return itemStack;
	}
}
