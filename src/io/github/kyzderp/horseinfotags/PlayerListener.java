package io.github.kyzderp.horseinfotags;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
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
	private HashSet<Horse> displaying;

	public PlayerListener(JavaPlugin plugin, Settings settings)
	{
		this.plugin = plugin;
		this.settings = settings;
		this.displaying = new HashSet<Horse>();
	}

	@EventHandler
	public void onPlayerRightClickHorse(PlayerInteractEntityEvent event)
	{
		if ((event.getPlayer().getInventory().getItemInMainHand() == null
				|| event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR)
				&& (event.getPlayer().getInventory().getItemInOffHand() == null
				|| event.getPlayer().getInventory().getItemInOffHand().getType() != Material.NAME_TAG))
		{			
			if (!(event.getRightClicked() instanceof Horse))
				return;
			Horse horse = (Horse) event.getRightClicked();

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
			if (this.settings.isChatEnabled() && !this.displaying.contains(horse)
					&& event.getPlayer().hasPermission("horseinfotags.chat"))
			{
				List<String> lines = this.settings.getChatFormat();
				String[] toDisplay = new String[lines.size()];
				for (int i = 0; i < lines.size(); i++)
				{
					String info = lines.get(i).replaceAll("&", "\u00A7");
					info = info.replaceAll("\\{name\\}", name == null ? "" : name);
					info = info.replaceAll("\\{maxhealthcolor\\}", healthColor);
					info = info.replaceAll("\\{maxhealth\\}", health + "");
					info = info.replaceAll("\\{currenthealthcolor\\}", currentHealthColor);
					info = info.replaceAll("\\{currenthealth\\}", currentHealth + "");
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
			if (this.settings.isTagEnabled() && !this.displaying.contains(horse)
					&& event.getPlayer().hasPermission("horseinfotags.tag"))
			{
				String info = this.settings.getTagFormat();
				info = info.replaceAll("\\{name\\}", name == null ? "" : name);
				info = info.replaceAll("\\{maxhealthcolor\\}", healthColor);
				info = info.replaceAll("\\{maxhealth\\}", health + "");
				info = info.replaceAll("\\{currenthealthcolor\\}", currentHealthColor);
				info = info.replaceAll("\\{currenthealth\\}", currentHealth + "");
				info = info.replaceAll("\\{speedcolor\\}", speedColor);
				info = info.replaceAll("\\{speed\\}", df.format(speed));
				info = info.replaceAll("\\{jumpcolor\\}", jumpColor);
				info = info.replaceAll("\\{jump\\}", df.format(jump));
				info = info.replaceAll("\\{ratingcolor\\}", ratingColor);
				info = info.replaceAll("\\{rating\\}", df.format(rating));

				horse.setCustomName(info);
				horse.setCustomNameVisible(true);
				this.displaying.add(horse);

				new RestoreNameTask(name, horse, this).runTaskLater(this.plugin, 200);
			}
		}
	}

	private String compareAverage(double current, double average)
	{
		if (current == average)
			return "\u00A7" + this.settings.getAvgColor();
		if (current < average)
			return "\u00A7" + this.settings.getBelowAvgColor();
		return "\u00A7" + this.settings.getAboveAvgColor();
	}

	public void removeHorse(Horse horse)
	{
		this.displaying.remove(horse);
	}
}