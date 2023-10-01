package fi.flexplex.pvp.menus;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.arena.DuelArena;
import fi.flexplex.pvp.game.duel.DuelInvite;
import fi.flexplex.pvp.game.duel.DuelSettings;
import fi.flexplex.pvp.game.duel.Duels;
import fi.flexplex.pvp.game.kit.Kit;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class DuelSettingMenu extends Menu {

	private final Player player;

	private boolean hunger = false;
	private int regenSpeed = 40;

	public DuelSettingMenu(final Player player, final Player target, final List<Kit> kits, final DuelArena arena) {
		super(player, 27, "PVP_DUELS_SETTINGS_MENU_TITLE", null);

		this.player = player;

		updateHealingSpeedDisplay();

		final ItemStack healthIn1 = setDisplayName(new ItemStack(Material.EMERALD), Language.getMessage(player, "PVP_DUELS_SETTINGS_INCREMENT_1"));
		this.setItem(healthIn1, 5, (type) -> changeHealingSpeed(2));
		final ItemStack healthIn5 = setDisplayName(new ItemStack(Material.EMERALD), Language.getMessage(player, "PVP_DUELS_SETTINGS_INCREMENT_5"));
		this.setItem(healthIn5, 6, (type) -> changeHealingSpeed(10));

		final ItemStack healthDec1 = setDisplayName(new ItemStack(Material.REDSTONE), Language.getMessage(player, "PVP_DUELS_SETTINGS_DECREMENT_1"));
		this.setItem(healthDec1, 2, (type) -> changeHealingSpeed(-2));
		final ItemStack healthDec5 = setDisplayName(new ItemStack(Material.REDSTONE), Language.getMessage(player, "PVP_DUELS_SETTINGS_DECREMENT_5"));
		this.setItem(healthDec5, 3, (type) -> changeHealingSpeed(-10));

		final ItemStack regenOff = setDisplayName(new ItemStack(Material.GOLD_INGOT), Language.getMessage(player, "PVP_DUELS_SETTINGS_HEALING_OFF"));
		this.setItem(regenOff, 8, (type) -> {
			regenSpeed = 0;
			updateHealingSpeedDisplay();
		});

		final ItemStack regenDefault = setDisplayName(new ItemStack(Material.GOLD_INGOT), Language.getMessage(player, "PVP_DUELS_SETTINGS_HEALING_DEFAULT"));
		this.setItem(regenDefault, 0, (type) -> {
			regenSpeed = 40;
			updateHealingSpeedDisplay();
		});

		final ItemStack hungerIcon = setDisplayName(new ItemStack(Material.COOKED_BEEF), Language.getMessage(player, "PVP_DUELS_SETTINGS_HUNGER", Language.getStringMessage(player, "PVP_DUELS_SETTINGS_OFF")));

		this.setItem(hungerIcon, 13, (clickType) -> {
			hunger = !hunger;
			if (hunger) {
				this.inventory.setItem(13, setDisplayName(new ItemStack(Material.ROTTEN_FLESH), Language.getMessage(player, "PVP_DUELS_SETTINGS_HUNGER", Language.getStringMessage(player, "PVP_DUELS_SETTINGS_ON"))));
			} else {
				this.inventory.setItem(13, setDisplayName(new ItemStack(Material.COOKED_BEEF), Language.getMessage(player, "PVP_DUELS_SETTINGS_HUNGER", Language.getStringMessage(player, "PVP_DUELS_SETTINGS_OFF"))));
			}
		});

		final ItemStack arrow = setDisplayName(new ItemStack(Material.ARROW), Language.getMessage(player, "PVP_DUELS_MENU_NEXT"));

		this.setItem(arrow, 26, (type) -> {
			final DuelSettings settings = new DuelSettings(hunger, regenSpeed, GameMode.ADVENTURE);
			player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
			Duels.sendInvite(new DuelInvite(player, target, kits, settings, arena));
		});
		this.open();
	}

	private ItemStack setDisplayName(final ItemStack item, final Component component) {
		final ItemMeta meta = item.getItemMeta();
		meta.displayName(component);
		item.setItemMeta(meta);
		return item;
	}

	private void changeHealingSpeed(final int amount) {
		final int num = amount + regenSpeed;
		if (num > 100) {
			regenSpeed = 0;
		} else if (num < 2) {
			regenSpeed = 2;
		} else {
			regenSpeed = num;
		}
		updateHealingSpeedDisplay();
	}

	private void updateHealingSpeedDisplay() {

		if (regenSpeed <= 0) {
			final ItemStack healthDisplay = setDisplayName(new ItemStack(Material.GOLDEN_APPLE), Language.getMessage(player, "PVP_DUELS_SETTINGS_OFF"));
			this.setItem(healthDisplay, 4, null);
		} else {
			final ItemStack healthDisplay = setDisplayName(new ItemStack(Material.GOLDEN_APPLE), Language.getMessage(player, "PVP_DUELS_SETTINGS_HEALING", String.format("%.1f", regenSpeed / 20.0d)));
			this.setItem(healthDisplay, 4, null);
		}
	}
}
