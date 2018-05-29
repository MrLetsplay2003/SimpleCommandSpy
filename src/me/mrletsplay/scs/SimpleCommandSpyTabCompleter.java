package me.mrletsplay.scs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class SimpleCommandSpyTabCompleter implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> tabCompletions = new ArrayList<>();
		if(sender instanceof Player){
			Player p = (Player)sender;
			if(command.getName().equalsIgnoreCase("commandspy")){
				if(args.length==1){
					if(p.hasPermission("simplecommandspy.commandspy")){
						if("on".startsWith(args[0])){
							tabCompletions.add("on");
						}
						
						if("off".startsWith(args[0])){
							tabCompletions.add("off");
						}
					}
					
					if(p.hasPermission("simplecommandspy.filter")){
						if("filter".startsWith(args[0])){
							tabCompletions.add("filter");
						}
						
						if("addtofilter".startsWith(args[0])){
							tabCompletions.add("addtofilter");
						}
						
						if("removefromfilter".startsWith(args[0])){
							tabCompletions.add("removefromfilter");
						}
						
						if("clearfilter".startsWith(args[0])){
							tabCompletions.add("clearfilter");
						}
					}
					

					if(p.hasPermission("simplecommandspy.globalfilter")){
						if("globalfilter".startsWith(args[0])){
							tabCompletions.add("globalfilter");
						}
					}
					
					if(p.hasPermission("simplecommandspy.version") && "version".startsWith(args[0])){
						tabCompletions.add("version");
					}
				}else if(args.length == 2){
					if(args[0].equalsIgnoreCase("removefromfilter")){
						String player = Config.use_uuids?p.getUniqueId().toString():p.getName();
						for(String s : Config.getFilteredCommandsForPlayer(player)){
							if(s.startsWith(args[1])){
								tabCompletions.add(s);
							}
						}
					}else if(args[0].equalsIgnoreCase("filter")){
						if(p.hasPermission("simplecommandspy.filter.other")){
							for(Player pl : Bukkit.getOnlinePlayers()){
								if(pl.getName().startsWith(args[1])){
									tabCompletions.add(pl.getName());
								}
							}
						}
					}
				}
			}
		}
		return tabCompletions;
	}
	
}
