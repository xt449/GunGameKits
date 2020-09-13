package com.github.xt449.gungamekits;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import com.github.xt449.bukkitutilitylibrary.AbstractConfiguration;

import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xt449
 * Copyright BinaryBanana/xt449 2019
 * All Rights Reserved
 */
final class MainConfiguration extends AbstractConfiguration {

	MainConfiguration(Plugin plugin) {
		super(plugin, "config.yml");
	}

	private List<KitData> kits;

	@Override
	protected void setDefaults() {
		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(filePath))));
	}

	@Override
	protected void getValues() {
		kits = ((List<KitData>) config.getList("kits")).stream().sorted(Comparator.comparingInt(kit -> (Integer) kit.rank)).collect(Collectors.toList());
	}

	List<KitData> getKits() {
		return kits;
	}

/*	@SerializableAs("KitData")
	static final class KitData implements ConfigurationSerializable {

		final int rank;
		final List<ItemStack> items;

		private KitData(final int rank, final List<ItemStack> items) {
			this.rank = rank;
			this.items = items;

//			this.items.forEach(itemStack -> {
//				System.out.println("3: Unbreakable transfered? : " + itemStack.getItemMeta().isUnbreakable());
//				System.out.println("3: Attributes transfered? : " + itemStack.getItemMeta().hasAttributeModifiers());
//				System.out.println("3: Enchantments transfered? : " + itemStack.getItemMeta().hasEnchants());
//			});
//			System.out.println("size(): " + items.size());
		}

		@Override
		
		public final Map<String, Object> serialize() {
			final Map<String, Object> map = new HashMap<>();
			map.put("rank", rank);
			map.put("items", items.stream().map(item -> {
				final StringBuilder builder = new StringBuilder(item.getType().name());
				builder.append(' ').append(item.getAmount());
				for(Map.Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
					builder.append(' ').append(enchantment.getKey().getKey().getKey()).append(':').append(enchantment.getValue());
				}
				if(item.getItemMeta().hasAttributeModifiers()) {
					for(Map.Entry<Attribute, AttributeModifier> attribute : item.getItemMeta().getAttributeModifiers().entries()) {
						builder.append(' ').append(attribute.getKey().name()).append(':').append(attribute.getValue().getAmount());
					}
				}
				return builder.toString();
			}).collect(Collectors.toList()));
			return map;
		}

		public static KitData deserialize(final Map<String, Object> map) {
//			System.out.println(map.entrySet().stream().map(kvp -> kvp.getKey() + ": " + kvp.getValue().toString()).collect(Collectors.joining("\n")));

			final List<ItemStack> items = ((List<String>) map.get("items")).stream().map(item -> {
				final String[] parts = item.toUpperCase().split(" ");
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
//						System.out.println(modifier);
						final String[] segments = modifier.split(":");
						if(segments.length == 2) {
//							System.out.println("debug 1");
							Attribute attribute = null;
							Enchantment enchantment;
							try {
								try {
									attribute = Attribute.valueOf(segments[0]);
								} catch(IllegalArgumentException exc) {
									// continue
								}
								if(attribute != null) {
//									System.out.println("debug 2");
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
//									System.out.println(attribute);
//									if(itemMeta.hasAttributeModifiers()) {
//										System.out.println("WOAH! value=" + itemMeta.getAttributeModifiers(attribute).iterator().next().getAmount());
//									}
								} else {
									System.out.println("debug 3");
									enchantment = Enchantment.getByKey(new NamespacedKey(NamespacedKey.MINECRAFT, segments[0].toLowerCase()));
									if(enchantment == null) {
										enchantment = Enchantment.getByName(segments[0].toUpperCase());
									}
									if(enchantment != null) {
//										System.out.println("debug 4");
										itemMeta.addEnchant(enchantment, Integer.parseInt(segments[1]), true);
									} else {
//										System.out.println("debug 5");
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
//				System.out.println("0: Unbreakable transfered? : " + itemMeta.isUnbreakable());
//				System.out.println("0: Attributes transfered? : " + itemMeta.hasAttributeModifiers());
//				System.out.println("0: Enchantments transfered? : " + itemMeta.hasEnchants());

				final ItemStack itemStack = new ItemStack(material, count);
				final boolean success = itemStack.setItemMeta(itemMeta);
//				System.out.println("!!! - Item Meta applied successfully? : " + success);
//				System.out.println("1: Unbreakable transfered? : " + itemStack.getItemMeta().isUnbreakable());
//				System.out.println("1: Attributes transfered? : " + itemStack.getItemMeta().hasAttributeModifiers());
//				System.out.println("1: Enchantments transfered? : " + itemStack.getItemMeta().hasEnchants());
				return itemStack;
			}).collect(Collectors.toList());

//			items.forEach(itemStack -> {
//				System.out.println("2: Unbreakable transfered? : " + itemStack.getItemMeta().isUnbreakable());
//				System.out.println("2: Attributes transfered? : " + itemStack.getItemMeta().hasAttributeModifiers());
//				System.out.println("2: Enchantments transfered? : " + itemStack.getItemMeta().hasEnchants());
//			});

//			System.out.println("size(): " + items.size());

			return new KitData((Integer) map.get("rank"), items);
		}
	}*/

	@SerializableAs("KitData")
	public static final class KitData implements ConfigurationSerializable {

		final int rank;
		final ItemStack helmet;
		final ItemStack chestplate;
		final ItemStack leggings;
		final ItemStack boots;
		final List<ItemStack> items;

		private KitData(final int rank, final ItemStack helmet, final ItemStack chestplate, final ItemStack leggings, final ItemStack boots, final List<ItemStack> items) {
			this.rank = rank;
			this.helmet = helmet;
			this.chestplate = chestplate;
			this.leggings = leggings;
			this.boots = boots;
			this.items = items;
		}

		final void apply(final Player player) {
			final PlayerInventory inventory = player.getInventory();
			// TODO: unneeded? - inventory.clear();
			inventory.setContents(items.toArray(new ItemStack[0]));
			inventory.setHelmet(helmet);
			inventory.setChestplate(chestplate);
			inventory.setLeggings(leggings);
			inventory.setBoots(boots);
		}

		@Override
		
		public final Map<String, Object> serialize() {
			final Map<String, Object> map = new HashMap<>();
			map.put("rank", rank);
			if(helmet != null) {
				map.put("helmet", GunGameKits.serializeItemData(helmet));
			}
			if(chestplate != null) {
				map.put("chestplate", GunGameKits.serializeItemData(chestplate));
			}
			if(leggings != null) {
				map.put("leggings", GunGameKits.serializeItemData(leggings));
			}
			if(boots != null) {
				map.put("boots", GunGameKits.serializeItemData(boots));
			}
			map.put("items", items.stream().map(GunGameKits::serializeItemData).collect(Collectors.toList()));
			return map;
		}

		public static KitData deserialize(final Map<String, Object> map) {
			final String helmetString = (String) map.get("helmet");
			ItemStack helmet = null;
			if(helmetString != null) {
				helmet = GunGameKits.deserializeItemData(helmetString);
			}
			final String chestplateString = (String) map.get("chestplate");
			ItemStack chestplate = null;
			if(chestplateString != null) {
				chestplate = GunGameKits.deserializeItemData(chestplateString);
			}
			final String leggingsString = (String) map.get("leggings");
			ItemStack leggings = null;
			if(leggingsString != null) {
				leggings = GunGameKits.deserializeItemData(leggingsString);
			}
			final String bootsString = (String) map.get("boots");
			ItemStack boots = null;
			if(bootsString != null) {
				boots = GunGameKits.deserializeItemData(bootsString);
			}
			final List<ItemStack> items = ((List<String>) map.get("items")).stream().map(GunGameKits::deserializeItemData).collect(Collectors.toList());
			return new KitData((Integer) map.get("rank"), helmet, chestplate, leggings, boots, items);
		}
	}
}
