package me.mrletsplay.scs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import me.mrletsplay.mrcore.bukkitimpl.BukkitCustomConfig;
import me.mrletsplay.mrcore.config.CustomConfig;
import me.mrletsplay.mrcore.config.LocaleProvider;
import me.mrletsplay.mrcore.config.LocaleProvider.CustomLocaleProvider;
import me.mrletsplay.mrcore.config.LocaleProvider.Locale;
import me.mrletsplay.mrcore.config.LocaleProvider.LocaleNullMode;

public class Config {

	private static File playerSettingsFile = new File(Main.plugin.getDataFolder(), "PlayerSettings.yml");
	private static BukkitCustomConfig playerSettings = (BukkitCustomConfig) new BukkitCustomConfig(playerSettingsFile).loadConfigSafely();
	private static BukkitCustomConfig config = new BukkitCustomConfig(new File(Main.plugin.getDataFolder(), "config.yml"));
	
	private static Locale locale;
	
	private static LocaleProvider localeProvider;
	
	public static boolean
			use_uuids = Bukkit.getServer().getOnlineMode(),
			update_check,
			update_check_on_join,
			update_check_on_command;
	
	public static void init(){
		List<String> cmds = new ArrayList<>();
		cmds.add("/spawn");
		cmds.add("/hub");
		config.addDefault("filtered-cmds", cmds);
		config.addDefault("enable-update-check", true);
		config.addDefault("update-check-on-join", true);
		config.addDefault("update-check-on-command", true);
		config.addDefault("use-uuids", true);
		config.addDefault("language", "en");
		config.applyDefaults(true);
		
		initLocale();
		
		use_uuids = use_uuids && config.getBoolean("use-uuids");
	}
	
	private static void initLocale() {
		localeProvider = new LocaleProvider(new File(Main.plugin.getDataFolder(), "lang"));
		localeProvider.setCustomLocaleProvider(new CustomLocaleProvider(new File(Main.plugin.getDataFolder(), "lang")));
		localeProvider.setParameterFormat("%", "%");
		localeProvider.useNullMode(LocaleNullMode.USE_PATH);
		
		CustomConfig en = new CustomConfig((File) null);
		
		en.set("commandspy-command", "%prefix% §7%player%: §8%command%");
		
		localeProvider.registerLocale("en", en);
		localeProvider.setDefaultLocale("en");
		
		locale = localeProvider.getLocale(config.getString("language"));
	}
	
	public static String getMessage(String path, String... params) {
		return locale.getMessage(path, concat(new String[] {"prefix", locale.getMessage("prefix")}, params));
	}
	
	private static String[] concat(String[] a, String[] b) {
		String[] newA = new String[a.length + b.length];
		System.arraycopy(a, 0, newA, 0, a.length);
		System.arraycopy(b, 0, newA, a.length, b.length);
		return newA;
	}
	
	public static void setCommandSpy(String player, boolean on){
		playerSettings.set(player+".command-spy-enabled", on);
		playerSettings.saveConfigSafely();
	}
	
	public static boolean getCommandSpy(String player){
		return playerSettings.getBoolean(player+".command-spy-enabled");
	}
	
	public static void addFilteredCommandForPlayer(String player, String cmd){
		List<String> cmds = playerSettings.getStringList(player+".filtered-cmds");
		cmds.add(cmd);
		playerSettings.set(player+".filtered-cmds", cmds);
		playerSettings.saveConfigSafely();
	}
	
	public static void clearFilteredCommandsForPlayer(String player){
		playerSettings.unset(player+".filtered-cmds", true);
		playerSettings.saveConfigSafely();
	}
	
	public static void removeFilteredCommandForPlayer(String player, String cmd){
		List<String> cmds = playerSettings.getStringList(player+".filtered-cmds");
		cmds.remove(cmd);
		playerSettings.set(player+".filtered-cmds", cmds);
		playerSettings.saveConfigSafely();
	}
	
	public static List<String> getFilteredCommandsForPlayer(String player){
		return playerSettings.getStringList(player+".filtered-cmds");
	}
	
	public static List<String> getGlobalFilteredCommands(){
		return config.getStringList("filtered-cmds");
	}
	
}
