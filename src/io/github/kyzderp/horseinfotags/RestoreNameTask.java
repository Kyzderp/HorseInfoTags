package io.github.kyzderp.horseinfotags;

import org.bukkit.entity.Horse;

public class RestoreNameTask extends org.bukkit.scheduler.BukkitRunnable
{
	String originalName;
	Horse horse;
	PlayerListener playerListener;

	public RestoreNameTask(String originalName, Horse horse, PlayerListener playerListener)
	{
		this.horse = horse;
		this.originalName = originalName;
		this.playerListener = playerListener;
	}


	@Override
	public void run() 
	{
		if (this.originalName != null)
			this.horse.setCustomName(this.originalName);
		else
		{
			this.horse.setCustomName(null);
			this.horse.setCustomNameVisible(false);
		}
		
		this.playerListener.removeHorse(this.horse);
	}
}
