package fi.flexplex.pvp.menus;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class Menu implements Listener {

	private final ClickAction[] actions;
	final Inventory inventory;
	private final Consumer<Player> onClose;
	private final Player player;


	public Menu(final Player player, final int size, final String nameKey, final Consumer<Player> onClose) {
		this.inventory = Bukkit.createInventory(null, size, Language.getMessage(player, nameKey));
		this.onClose = onClose;
		this.player = player;
		this.actions = new ClickAction[size];
	}

	protected void open() {
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		this.player.openInventory(this.inventory);
	}

	public void setItem(final ItemStack item, final int slot, final ClickAction action) {
		this.inventory.setItem(slot, item);

		if (action != null) {
			actions[slot] = action;
		}
	}

	@EventHandler
	public void onInventoryClose(final InventoryCloseEvent event) {
		if (event.getInventory() == this.inventory) {
			if (event.getReason() == InventoryCloseEvent.Reason.PLAYER) {
				if (event.getPlayer() == this.player) {
					if (this.onClose != null) {
						this.onClose.accept(this.player);
					}
				}
			}
			this.player.updateInventory();
			HandlerList.unregisterAll(this);
		}
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		if (event.getInventory() == this.inventory) {
			event.setCancelled(true);
			if (event.getWhoClicked() == this.player) {
				if (event.getClickedInventory() == this.inventory) {
					if (actions[event.getSlot()] != null) {
						actions[event.getSlot()].clickAction(event.getClick());
					}
				}
			}
		}
	}

	public Player getPlayer() {
		return this.player;
	}
}
