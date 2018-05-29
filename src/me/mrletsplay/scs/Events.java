package me.mrletsplay.scs;



import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e){
		for(Player p : Bukkit.getOnlinePlayers()){
			String player = Config.use_uuids?p.getUniqueId().toString():p.getName();
			if(Config.getCommandSpy(player) && !p.hasPermission("simplecommandspy.commandspy")){
				Config.setCommandSpy(player, false);
			}
			
			if(Config.getCommandSpy(player)){
				String cmdName = e.getMessage().split(" ")[0];
				if(!p.getName().equals(e.getPlayer().getName()) && !Config.getFilteredCommandsForPlayer(player).contains(cmdName) && !Config.getGlobalFilteredCommands().contains(cmdName)){
//					p.sendMessage("§8[§6CmdSpy§8] §7"+e.getPlayer().getName()+": §8"+e.getMessage());
					p.sendMessage(Config.getMessage("commandspy-command", "player", e.getPlayer().getName(), "command", e.getMessage()));
				}
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(e.getPlayer().hasPermission("simplecommandspy.notify-update")){
			if(Config.update_check && Config.update_check_on_join){
				UpdateChecker.sendUpdateMessage(UpdateChecker.checkForUpdate(), e.getPlayer());
			}
		}
	}
	
}