package io.github.kyzderp.horseinfotags;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;

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
		if (event.getPlayer().getInventory().getItemInMainHand() == null
				|| event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR)
		{			
			if (!(event.getRightClicked() instanceof Horse))
				return;
			Horse horse = (Horse) event.getRightClicked();
			if (this.displaying.contains(horse))
				return;

			String name = horse.getCustomName();
			String info = "\u00A77Health: \u00A7";
			double health = horse.getMaxHealth();
			info += this.compareAverage(health, 22.5) + health + " \u00A7f- \u00A77Speed: \u00A7"; 
			double speed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();
			info += this.compareAverage(speed, 0.225) + this.df.format(speed * 43) + " \u00A7f- ";

			double rawJump = horse.getJumpStrength();
			double jump = -0.1817584952 * Math.pow(rawJump, 3) 
					+ 3.689713992 * rawJump * rawJump 
					+ 2.128599134 * rawJump - 0.343930367;
			info += "\u00A77Jump: \u00A7" + this.compareAverage(rawJump, 0.7) + this.df.format(jump);

			horse.setCustomName(info);
			horse.setCustomNameVisible(true);
			this.displaying.add(horse);

			new RestoreNameTask(name, horse, this).runTaskLater(this.plugin, 200);
		}
	}

	private String compareAverage(double current, double average)
	{
		if (current == average)
			return "e";
		if (current < average)
			return "c";
		return "a";
	}

	public void removeHorse(Horse horse)
	{
		this.displaying.remove(horse);
	}
}