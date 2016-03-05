package io.github.kyzderp.horseinfotags;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


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
	{ // TODO: reload
		return false;
	}
}
