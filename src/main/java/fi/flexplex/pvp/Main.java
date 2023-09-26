package fi.flexplex.pvp;

import fi.flexplex.core.api.Language;

import java.io.File;
import java.util.List;

import fi.flexplex.pvp.command.CSTCmd;
import fi.flexplex.pvp.command.DuelInviteCmd;
import fi.flexplex.pvp.command.DuelsAcceptCmd;
import fi.flexplex.pvp.command.RematchCmd;
import fi.flexplex.pvp.command.SpawnCmd;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.command.FfaCmd;
import fi.flexplex.pvp.listener.DamageListener;
import fi.flexplex.pvp.listener.FlexDeathMessageListener;
import fi.flexplex.pvp.listener.PvPListener;
import fi.flexplex.pvp.listener.JoinAndLeaveListener;
import fi.flexplex.pvp.listener.WorldProtectListener;
import fi.flexplex.pvp.loader.ArenaLoader;
import fi.flexplex.pvp.loader.ConfigLoader;
import fi.flexplex.pvp.loader.KitLoader;
import fi.flexplex.pvp.command.LocationToolCmd;
import fi.flexplex.pvp.misc.world.WorldUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public final class Main extends JavaPlugin {

	private static Main instance;

	@Override
	public void onEnable() {
		instance = this;

		try {
			final File directory = new File("allocations");

			directory.mkdirs();

			for (File file : directory.listFiles()) {
				if (file.isDirectory()) {
					WorldUtils.deleteDirectory(file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		saveDefaultConfig();
		createCustomConfigFiles();
		loadLanguages();
		KitLoader.LoadAllKits();
		ArenaLoader.loadAllArenas();
		ArenaManager.createAllDuelArenas();

		this.getCommand("location").setExecutor(new LocationToolCmd());

		this.getCommand("cst").setExecutor(new CSTCmd());
		this.getCommand("cst").setTabCompleter(new CSTCmd());

		this.getCommand("ffa").setExecutor(new FfaCmd());
		this.getCommand("ffa").setTabCompleter(new FfaCmd());

		this.getCommand("duelsinvite").setExecutor(new DuelInviteCmd());
		this.getCommand("duelsinvite").setTabCompleter(new DuelInviteCmd());

		this.getCommand("advancedduelsinvite").setExecutor(new DuelInviteCmd());
		this.getCommand("advancedduelsinvite").setTabCompleter(new DuelInviteCmd());

		this.getCommand("duelsaccept").setExecutor(new DuelsAcceptCmd());

		this.getCommand("spawn").setExecutor(new SpawnCmd());
		this.getCommand("spawn").setTabCompleter(new SpawnCmd());

		this.getCommand("rematch").setExecutor(new RematchCmd());
		this.getCommand("rematch").setTabCompleter(new RematchCmd());

		this.getServer().getPluginManager().registerEvents(new FlexDeathMessageListener(), this);
		this.getServer().getPluginManager().registerEvents(new JoinAndLeaveListener(), this);
		this.getServer().getPluginManager().registerEvents(new PvPListener(), this);
		this.getServer().getPluginManager().registerEvents(new DamageListener(), this);
		this.getServer().getPluginManager().registerEvents(new WorldProtectListener(), this);
	}

	@Override
	public void onDisable() {
		ArenaManager.onDisable();
	}

	public static Main getInstance() {
		return instance;
	}


	private void loadLanguages() {
		final File parent = new File(this.getDataFolder().getPath() + "/lang");
		final File[] files = parent.listFiles();
		for (int i = 0; i < files.length; i++) {
			final File file = files[i];
			final YamlConfiguration yamlConf = ConfigLoader.loadConfig(file);
			if (yamlConf == null) {
				continue;
			}
			final String language = file.getName().split("\\.")[0];
			for (final String key : yamlConf.getKeys(false)) {
				Language.setKey(language, key, yamlConf.getString(key));
			}
		}
	}

	private void createCustomConfigFiles() {
		final List<String> paths = List.of("lang/fi_FI.yml", "lang/en_US.yml");
		new File(getDataFolder(), "/kits").mkdirs();
		new File(getDataFolder(), "/arenas").mkdirs();
		for (final String path : paths) {
			final File file = new File(getDataFolder(), path);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				saveResource(path, false);
			}
		}

	}
}
