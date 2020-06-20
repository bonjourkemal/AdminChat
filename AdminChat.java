package me.bnjrkemal.AdminChat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminChat extends JavaPlugin implements Listener{
	private final Set<UUID> admins = new HashSet<>();

	private String command;
	private String permission;
	private String nopermission;
	private String consoleerror;
	private String activite;
	private String deactivite;
	private String prefix;
	private String reloaded;
	private String formatmessage;
		
	@Override
	public void onEnable() {
		saveDefaultConfig();
		command = getConfig().getString("command.command");
		permission = getConfig().getString("permission");
		nopermission = ChatColor.translateAlternateColorCodes('&', getConfig().getString("command.no-permission"));
		consoleerror = ChatColor.translateAlternateColorCodes('&', getConfig().getString("command.console-error"));
		activite = ChatColor.translateAlternateColorCodes('&', getConfig().getString("command.activite"));
		deactivite = ChatColor.translateAlternateColorCodes('&', getConfig().getString("command.deactivite"));
		prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));
		reloaded = ChatColor.translateAlternateColorCodes('&', getConfig().getString("command.reloaded"));
		formatmessage = prefix + ChatColor.translateAlternateColorCodes('&', getConfig().getString("format-message"));
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	@Override
	public void onDisable() {
		admins.clear();
	}
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {		
		if(!admins.contains(e.getPlayer().getUniqueId())) {
			return;
		}
		e.setCancelled(true);
		Bukkit.getServer().getOnlinePlayers().stream().filter(player -> player.hasPermission(permission))
		.forEach(player -> player.sendMessage(formatmessage.replace("%player%", e.getPlayer().getName()).replace("%message%", e.getMessage())));
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase(command)) {
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(consoleerror);
			return true;
		}
		if(!sender.hasPermission(permission)) {
			sender.sendMessage(nopermission);
			return true;
		}
		if(args.length == 1) {
			sender.sendMessage(reloaded);
			saveConfig();
			return true;
		}
		final UUID userUniqueId = ((Player) sender).getUniqueId();
		final boolean requiredAdd = !(admins.remove(userUniqueId));
		if(requiredAdd) {
			admins.add(userUniqueId);
		}
		sender.sendMessage(prefix + (requiredAdd ? activite : deactivite));
		return true;
	}	
}
