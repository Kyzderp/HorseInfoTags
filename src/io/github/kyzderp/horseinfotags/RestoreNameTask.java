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

import org.bukkit.entity.Horse;

public class RestoreNameTask extends org.bukkit.scheduler.BukkitRunnable
{
	Horse horse;
	PlayerListener playerListener;

	public RestoreNameTask(Horse horse, PlayerListener playerListener)
	{
		this.horse = horse;
		this.playerListener = playerListener;
	}


	@Override
	public void run() 
	{
		// Gets original name that it needs to be restored to
		String originalName = this.playerListener.removeAndGetHorse(this.horse);
		
		if (originalName != null && !originalName.equals(""))
			this.horse.setCustomName(originalName);
		else
		{
			// Horse didn't have a name previously, hide the name.
			this.horse.setCustomName(null);
			this.horse.setCustomNameVisible(false);
		}
	}
}
