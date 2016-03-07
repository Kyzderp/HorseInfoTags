/**
 * This file is part of HorseInfoTags.
 * 
 * HorseInfoTags is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * HorseInfoTags is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with HorseInfoTags.  If not, see <http://www.gnu.org/licenses/>.
 * */

package io.github.kyzderp.horseinfotags;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Kyzeragon 3/4/2016
 * Added configuration and stuff 3/5/2016 
 * Fixed df for health 3/6/2016 (1.0.1)
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
			this.saveDefaultConfig();
		this.reloadConfig();
		
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
