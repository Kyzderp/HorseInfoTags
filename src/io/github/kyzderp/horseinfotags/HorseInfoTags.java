package io.github.kyzderp.horseinfotags;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Kyzeragon 3/4/2016
 * Added configuration 3/5/2016 
 *
 */

public class HorseInfoTags extends JavaPlugin 
{
	private Settings settings;
	private PlayerListener playerListener;

	@Override
	public void onEnable()
	{
		final File configFile = new File(this.getDataFolder(), "config.yml");
		if (!configFile.exists())
		{
			this.saveDefaultConfig();
			this.reloadConfig();
		}
		this.settings = new Settings(this);
		this.settings.loadSettings();

		this.playerListener = new PlayerListener(this, this.settings);
		this.getServer().getPluginManager().registerEvents(this.playerListener, this);
	}

	@Override
	public void onDisable()
	{
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (cmd.getName().equalsIgnoreCase("horseinfotags"))
		{
			if (args.length != 1 || !args[0].equalsIgnoreCase("reload"))
				return false;
			
			this.reloadConfig();
			this.settings.loadSettings();
			sender.sendMessage("HorseInfoTags configuration reloaded.");
			return true;
		}
		return false;
	}
}
