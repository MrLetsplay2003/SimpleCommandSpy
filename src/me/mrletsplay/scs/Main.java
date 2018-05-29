package me.mrletsplay.scs;



import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.mrletsplay.scs.UpdateChecker.Result;

public class Main extends JavaPlugin
{
	
	public static Plugin plugin;
	public static final String PLUGIN_VERSION = "2.0.4";
	
	private static List<Player> toConfirm = new ArrayList<>();

	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		plugin = this;
		initConfig();
		getLogger().info("SimpleCommandSpy is "+(Config.use_uuids?"NOT ":"")+"using UUIDs");
		getCommand("commandspy").setTabCompleter(new SimpleCommandSpyTabCompleter());
		if(Config.update_check){
			getLogger().info("Checking for update...");
			List<Player> pls = new ArrayList<>();
			for(Player pl : Bukkit.getOnlinePlayers()){
				if(pl.hasPermission("simplecommandspy.notify-update")){
					pls.add(pl);
				}
			}
			Result r = UpdateChecker.checkForUpdate();
			if(r.updAvailable) {
				getLogger().info("There's an update available for SimpleCommandSpy");
				getLogger().info(PLUGIN_VERSION + " -> "+r.updVer);
				r.updChlog.forEach(getLogger()::info);
			}
			UpdateChecker.sendUpdateMessage(r, pls.toArray(new Player[pls.size()]));
			getLogger().info("Finished!");
		}
		getLogger().info("Enabled");
	}
	
	public void onDisable(){
		getLogger().info("Disabled");
	}
	
	private void initConfig(){
		Config.init();
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(command.getName().equalsIgnoreCase("commandspy")){
			if(sender instanceof Player){
				Player p = (Player)sender;
				String player = Config.use_uuids?p.getUniqueId().toString():p.getName();
				if(p.hasPermission("simplecommandspy.commandspy")){
					if(args.length==1){
						if(args[0].equalsIgnoreCase("on")){
							Config.setCommandSpy(player, true);
							p.sendMessage("§8[§6CmdSpy§8] §aCommandSpy enabled");
						}else if(args[0].equalsIgnoreCase("off")){
							Config.setCommandSpy(player, false);
							p.sendMessage("§8[§6CmdSpy§8] §cCommandSpy disabled");
						}else if(args[0].equalsIgnoreCase("version")){
							if(p.hasPermission("simplecommandspy.version")){
								p.sendMessage("Current SimpleCommandSpy version: §7"+PLUGIN_VERSION);
								if(Config.update_check && Config.update_check_on_command){
									UpdateChecker.sendUpdateMessage(UpdateChecker.checkForUpdate(), p);
								}
							}
						}else if(args[0].equalsIgnoreCase("filter")){
							if(p.hasPermission("simplecommandspy.filter")){
								p.sendMessage("§8[§6CmdSpy§8] §6Your personal CommandSpy filter:");
								List<String> filter = Config.getFilteredCommandsForPlayer(player);
								if(!filter.isEmpty()){
								for(String s : filter){
										p.sendMessage("§8- §7"+s);
									}
								}else{
									p.sendMessage("§7Your filter is empty!");
								}
							}else{
								p.sendMessage("§cNope");
							}
						}else if(args[0].equalsIgnoreCase("clearfilter")){
							if(p.hasPermission("simplecommandspy.filter")){
								if(!toConfirm.contains(p)){
									toConfirm.add(p);
									p.sendMessage("§8[§6CmdSpy§8] §6Type in the command again within the next 6 seconds to confirm");
									p.sendMessage("§8[§6CmdSpy§8] §c§lNote: This will clear your entire personal CommandSpy filter!");
									Bukkit.getScheduler().runTaskLater(this, new Runnable() {
										
										@Override
										public void run() {
											if(toConfirm.contains(p)){
												toConfirm.remove(p);
												if(p.isOnline()){
													p.sendMessage("§8[§6CmdSpy§8] §cConfirmation timed out");
												}
											}
										}
									}, 20*6);
								}else{
									toConfirm.remove(p);
									Config.clearFilteredCommandsForPlayer(player);
									p.sendMessage("§8[§6CmdSpy§8] §aCleared your personal CommandSpy filter");
								}
							}else{
								p.sendMessage("§cNope");
							}
						}else if(args[0].equalsIgnoreCase("globalfilter")){
							if(p.hasPermission("simplecommandspy.globalfilter")){
								p.sendMessage("§8[§6CmdSpy§8] §6Global CommandSpy filter:");
								List<String> filter = Config.getGlobalFilteredCommands();
								if(!filter.isEmpty()){
								for(String s : filter){
										p.sendMessage("§8- §7"+s);
									}
								}else{
									p.sendMessage("§7The global filter is empty!");
								}
							}else{
								p.sendMessage("§cNope");
							}
						}else{
							sendCommandHelp(p);
						}
					}else if(args.length == 2){
						if(args[0].equalsIgnoreCase("addtofilter")){
							if(p.hasPermission("simplecommandspy.filter")){
								String toAdd = args[1];
								if(!toAdd.startsWith("/")){
									toAdd = "/"+toAdd;
								}
								if(!Config.getFilteredCommandsForPlayer(player).contains(toAdd)){
									Config.addFilteredCommandForPlayer(player, toAdd);
									p.sendMessage("§8[§6CmdSpy§8] §aAdded §6\""+toAdd+"\" §ato your personal CommandSpy filter");
								}else{
									p.sendMessage("§8[§6CmdSpy§8] §6\""+toAdd+"\" §cis already on your personal CommandSpy filter");
								}
							}else{
								p.sendMessage("§cNope");
							}
						}else if(args[0].equalsIgnoreCase("removefromfilter")){
							if(p.hasPermission("simplecommandspy.filter")){
								String toRemove = args[1];
								if(!toRemove.startsWith("/")){
									toRemove = "/"+toRemove;
								}
								if(Config.getFilteredCommandsForPlayer(player).contains(toRemove)){
									Config.removeFilteredCommandForPlayer(player, toRemove);
									p.sendMessage("§8[§6CmdSpy§8] §aRemoved §6\""+toRemove+"\" §afrom your personal CommandSpy filter");
								}else{
									p.sendMessage("§8[§6CmdSpy§8] §6\""+toRemove+"\" §cis not on your personal CommandSpy filter");
								}
							}else{
								p.sendMessage("§cNope");
							}
						}else if(args[0].equalsIgnoreCase("filter")){
							if(p.hasPermission("simplecommandspy.filter.other")){
								OfflinePlayer oPl = Bukkit.getOfflinePlayer(args[1]);
								if(oPl!=null) {
									p.sendMessage("§8[§6CmdSpy§8] §6"+args[1]+"'s personal CommandSpy filter:");
									String oPlayer = Config.use_uuids?oPl.getUniqueId().toString():oPl.getName();
									List<String> filter = Config.getFilteredCommandsForPlayer(oPlayer);
									if(!filter.isEmpty()){
									for(String s : filter){
											p.sendMessage("§8- §7"+s);
										}
									}else{
										p.sendMessage("§7"+args[1]+"'s filter is empty!");
									}
								}else {
									p.sendMessage("§8[§6CmdSpy§8] §cThat is not a valid player");
								}
							}else{
								p.sendMessage("§cNope");
							}
						}else{
							sendCommandHelp(p);
						}
					}else{
						sendCommandHelp(p);
					}
				}else{
					p.sendMessage("§cNope");
				}
			}else{
				sender.sendMessage("§cThe console can't do that");
			}
		}
		return false;
	}
	
	private void sendCommandHelp(Player p) {
		p.sendMessage("§8[§6CmdSpy§8] §cHelp");
		p.sendMessage("§7/commandspy on/off §8- Turns CommandSpy on/off");
		if(p.hasPermission("simplecommandspy.filter")){
			p.sendMessage("§7/commandspy filter "+(p.hasPermission("commandspy.filter.other")?"[Player] ":"")+"§8- Shows your"+(p.hasPermission("commandspy.filter.other")?" (or another player's)":"")+" personal CommandSpy filter");
			p.sendMessage("§7/commandspy addtofilter <CmdName> §8- Add a command to your personal CommandSpy filter");
			p.sendMessage("§7/commandspy removefromfilter <CmdName> §8- Remove a command from your personal CommandSpy filter");
			p.sendMessage("§7/commandspy clearfilter §8- Clear your personal CommandSpy filter");
		}
		if(p.hasPermission("simplecommandspy.globalfilter")){
			p.sendMessage("§7/commandspy globalfilter §8- Shows the global CommandSpy filter");
		}
		if(p.hasPermission("simplecommandspy.version")){
			p.sendMessage("§7/commandspy version §8- Shows the CommandSpy version and checks for an update (if enabled)");
		}
	}
	
}