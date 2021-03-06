package me.aov.commands;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.aov.PotionsMain;

public class Speed implements CommandExecutor {
	private PotionsMain plugin;
	private static HashMap<String, Long> cds = new HashMap<String, Long>();

	public Speed() {
	}

	public Speed(PotionsMain instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		p = p.getPlayer();
		// Messages
		final String speedOn = ChatColor.translateAlternateColorCodes('&',
				plugin.getConfig().getString("Potions.Messages.SpeedOn"));
		final String speedOff = ChatColor.translateAlternateColorCodes('&',
				plugin.getConfig().getString("Potions.Messages.SpeedOff"));
		final String prefix = plugin.getPrefix();

		int highestLevel = plugin.getConfig().getInt("Potions.Levels.Speed");

		// Gets the highest level of speed available
		for (PermissionAttachmentInfo perms : p.getEffectivePermissions()) {
			if (perms.getPermission().startsWith("pot.speed.")) {
				String permission = perms.getPermission().replaceAll("pot.speed.", "");
				if (Integer.parseInt(permission) > highestLevel) {
					highestLevel = Integer.parseInt(permission);
				}
			}
		}
		// End of loop
		if (command.getName().equalsIgnoreCase("speedp")) {
			int cd = plugin.getConfig().getInt("Potions.Cooldown") * 1000;
			if (!cds.containsKey(p.getName()) || System.currentTimeMillis() - cd >= cds.get(p.getName())) {
				if (args.length == 0) {

					if (!p.hasPotionEffect(PotionEffectType.SPEED)) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, highestLevel - 1), true);
						p.sendMessage(prefix + speedOn);
						cds.put(p.getName(), System.currentTimeMillis());

					} else {
						p.removePotionEffect(PotionEffectType.SPEED);
						p.sendMessage(prefix + speedOff);

					}
					return true;
				}

				else if (args.length > 0 && Integer.parseInt(args[0]) > 0) {

					if (Integer.parseInt(args[0]) <= highestLevel && !p.hasPotionEffect(PotionEffectType.SPEED)) {
						p.addPotionEffect(
								new PotionEffect(PotionEffectType.SPEED, 1000000, Integer.parseInt(args[0]) - 1), true);
						p.sendMessage(prefix + speedOn);
						cds.put(p.getName(), System.currentTimeMillis());

					} else if (p.hasPotionEffect(PotionEffectType.SPEED)) {
						p.removePotionEffect(PotionEffectType.SPEED);
						p.sendMessage(prefix + speedOff);
					} else {
						p.sendMessage(prefix + ChatColor.GRAY + "You do not have permission for this level!");
					}
					return true;
				}
			} else {
				String send = Long
						.toString((Math.abs((System.currentTimeMillis() - cd) - cds.get(p.getName()))) / 1000);
				p.sendMessage(prefix + ChatColor.GRAY + "You're still on cooldown! Please wait " + send
						+ " seconds before trying again");
				return true;
			}
		}
		return false;
	}
}
