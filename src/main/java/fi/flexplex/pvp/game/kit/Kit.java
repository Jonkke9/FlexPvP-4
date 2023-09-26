package fi.flexplex.pvp.game.kit;

import fi.flexplex.core.api.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public final class Kit {
	private final String name;
	private final String displayNameKey;
	private final List<String> descriptionLineKeys;
	private final Material iconMat;
	private final boolean ffaKit;
	private final ItemStack[] contents;
	private final int slot;

	public Kit(final String name,
			   final String displayNameKey,
			   final List<String> descriptionLineKeys,
			   final Material iconMat,
			   final boolean ffaKit,
			   final ItemStack[] contents,
			   final Collection<PotionEffect> potionEffects,
	           final int slot) {

		this.name = name;
		this.displayNameKey = displayNameKey;
		this.descriptionLineKeys = descriptionLineKeys;
		this.iconMat = iconMat;
		this.ffaKit = ffaKit;
		this.contents = contents;
		this.potionEffects = potionEffects;
		this.slot = slot;
	}

	private final Collection<PotionEffect> potionEffects;


	public void deploy(final Player player) {
		final Inventory inv = player.getInventory();
		inv.clear();
		inv.setContents(contents);
		player.addPotionEffects(potionEffects);
	}

	public ItemStack getIcon(final CommandSender sender) {
		final ItemStack icon = new ItemStack(iconMat);
		final ItemMeta iconMeta = icon.getItemMeta();
		iconMeta.displayName(Component.text("§6" + Language.getStringMessage(sender,displayNameKey)));

		final List<Component> lore = new ArrayList<>();

		for (final String s : descriptionLineKeys) {
			lore.add(Language.getMessage(sender, s));
		}

		iconMeta.lore(lore);
		icon.setItemMeta(iconMeta);
		return icon;
	}

	public ArmorStand spawnArmorStand(final Location loc) {
		 final ArmorStand armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		 armorStand.customName(Component.text("KIT_" + name));
		 armorStand.setCanMove(false);
		 armorStand.setGravity(false);
		 armorStand.setInvulnerable(true);
		 armorStand.setPersistent(true);
		 armorStand.setArms(true);

		 armorStand.setItem(EquipmentSlot.OFF_HAND, contents[40]);
		 armorStand.setItem(EquipmentSlot.HEAD, contents[39]);
		 armorStand.setItem(EquipmentSlot.CHEST, contents[38]);
		 armorStand.setItem(EquipmentSlot.LEGS, contents[37]);
		 armorStand.setItem(EquipmentSlot.FEET, contents[36]);
		 armorStand.setItem(EquipmentSlot.HAND, contents[0]);
		 return armorStand;
	}

	public String getName() {
		return name;
	}

	public String getDisplayNameKey() {
		return displayNameKey;
	}

	public int getSlot() {
		return slot;
	}

	public boolean isFfaKit() {
		return ffaKit;
	}

	public Material getDisabledMaterial() {
		return iconMat;
	}
}
