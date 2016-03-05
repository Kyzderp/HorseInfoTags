package io.github.kyzderp.horseinfotags;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Settings 
{
	private JavaPlugin plugin;
	
	public Settings(JavaPlugin plugin)
	{
		this.plugin = plugin;
	}

	public void loadSettings()
	{
		FileConfiguration config = plugin.getConfig();
		config.options().copyDefaults(true);
	}
}
