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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener
{
	private Settings settings;
	private JavaPlugin plugin;
	private final NumberFormat df = new DecimalFormat("#0.00");
	private HashMap<Horse, String> displaying;

	public PlayerListener(JavaPlugin plugin, Settings settings)
	{
		this.plugin = plugin;
		this.settings = settings;
		this.displaying = new HashMap<Horse, String>();
	}

	/**
	 * When a player right clicks a horse, we should do something
	 * @param event
	 */
	@EventHandler
	public void onPlayerRightClickHorse(PlayerInteractEntityEvent event)
	{
		if (!(event.getRightClicked() instanceof Horse))
			return;
		
		Horse horse = (Horse) event.getRightClicked();
		
		// The player is trying to rename the horse but it's currently displaying info in name tag
		if (this.displaying.containsKey(horse)
				&& (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NAME_TAG
				&& event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
				|| (event.getPlayer().getInventory().getItemInOffHand().getType() == Material.NAME_TAG
				&& event.getPlayer().getInventory().getItemInOffHand().getItemMeta().hasDisplayName()))
		{
			event.getPlayer().sendMessage(this.settings.getCannotRenameMessage());
			event.setCancelled(true);
			return;
		}
		
		// Clicking with empty hand = show stats
		if ((event.getPlayer().getInventory().getItemInMainHand() == null
				|| event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR)
				&& (event.getPlayer().getInventory().getItemInOffHand() == null
				|| event.getPlayer().getInventory().getItemInOffHand().getType() != Material.NAME_TAG))
		{			
			String name = horse.getCustomName();
			double health = horse.getMaxHealth();
			String healthColor = this.compareAverage(health, 22.5);
			double currentHealth = horse.getHealth();
			String currentHealthColor = this.compareAverage(currentHealth, (int)(health * 0.75));
			double rawSpeed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();
			String speedColor = this.compareAverage(rawSpeed, 0.225);
			double speed = rawSpeed * 43;
			double rawJump = horse.getJumpStrength();
			double jump = -0.1817584952 * Math.pow(rawJump, 3) 
					+ 3.689713992 * rawJump * rawJump 
					+ 2.128599134 * rawJump - 0.343930367;
			String jumpColor = this.compareAverage(rawJump, 0.7);
			double rating = health * this.settings.getHealthWeight()
					+ speed * this.settings.getSpeedWeight()
					+ jump * this.settings.getJumpWeight();
			String ratingColor = this.compareAverage(rating, this.settings.getAverageRating());

			// Send as chat message
			if (this.settings.isChatEnabled() && !this.displaying.containsKey(horse)
					&& event.getPlayer().hasPermission("horseinfotags.chat"))
			{
				List<String> lines = this.settings.getChatFormat();
				String[] toDisplay = new String[lines.size()];
				for (int i = 0; i < lines.size(); i++)
				{
					String info = lines.get(i).replaceAll("&", "\u00A7");
					info = info.replaceAll("\\{name\\}", name == null ? "" : name);
					info = info.replaceAll("\\{maxhealthcolor\\}", healthColor);
					info = info.replaceAll("\\{maxhealth\\}", df.format(health) + "");
					info = info.replaceAll("\\{currenthealthcolor\\}", currentHealthColor);
					info = info.replaceAll("\\{currenthealth\\}", df.format(currentHealth) + "");
					info = info.replaceAll("\\{speedcolor\\}", speedColor);
					info = info.replaceAll("\\{speed\\}", df.format(speed));
					info = info.replaceAll("\\{jumpcolor\\}", jumpColor);
					info = info.replaceAll("\\{jump\\}", df.format(jump));
					info = info.replaceAll("\\{ratingcolor\\}", ratingColor);
					info = info.replaceAll("\\{rating\\}", df.format(rating));
					toDisplay[i] = info;
				}

				event.getPlayer().sendMessage(toDisplay);
			}

			// Display as tag over horse
			if (this.settings.isTagEnabled() && !this.displaying.containsKey(horse)
					&& event.getPlayer().hasPermission("horseinfotags.tag"))
			{
				this.displaying.put(horse, name == null ? "" : name);

				String info = this.settings.getTagFormat();
				info = info.replaceAll("\\{name\\}", name == null ? "" : name);
				info = info.replaceAll("\\{maxhealthcolor\\}", healthColor);
				info = info.replaceAll("\\{maxhealth\\}", df.format(health) + "");
				info = info.replaceAll("\\{currenthealthcolor\\}", currentHealthColor);
				info = info.replaceAll("\\{currenthealth\\}", df.format(currentHealth) + "");
				info = info.replaceAll("\\{speedcolor\\}", speedColor);
				info = info.replaceAll("\\{speed\\}", df.format(speed));
				info = info.replaceAll("\\{jumpcolor\\}", jumpColor);
				info = info.replaceAll("\\{jump\\}", df.format(jump));
				info = info.replaceAll("\\{ratingcolor\\}", ratingColor);
				info = info.replaceAll("\\{rating\\}", df.format(rating));

				horse.setCustomName(info);
				horse.setCustomNameVisible(true);

				// Remember to restore afterwards!
				new RestoreNameTask(horse, this).runTaskLater(this.plugin, this.settings.getTagDuration());
			}
		}
	}

	/**
	 * Compares the current stat to the average stat as specified
	 * then returns the appropriate format color
	 * @param current
	 * @param average
	 * @return
	 */
	private String compareAverage(double current, double average)
	{
		if (current == average)
			return "\u00A7" + this.settings.getAvgColor();
		if (current < average)
			return "\u00A7" + this.settings.getBelowAvgColor();
		return "\u00A7" + this.settings.getAboveAvgColor();
	}

	/**
	 * For restoring nametag
	 * @param horse
	 * @return
	 */
	public String removeAndGetHorse(Horse horse)
	{
		return this.displaying.remove(horse);
	}
}