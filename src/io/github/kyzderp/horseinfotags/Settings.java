package io.github.kyzderp.horseinfotags;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Settings 
{
	private JavaPlugin plugin;

	private String belowAvgColor;
	private String avgColor;
	private String aboveAvgColor;

	private boolean tagEnabled;
	private String tagFormat;
	private int tagDuration;

	private boolean chatEnabled;
	private List<String> chatFormat;

	private double healthWeight;
	private double speedWeight;
	private double jumpWeight;

	private double averageRating;

	public Settings(JavaPlugin plugin)
	{
		this.plugin = plugin;
	}

	public void loadSettings()
	{
		FileConfiguration config = this.plugin.getConfig();
		config.options().copyDefaults(true);

		this.belowAvgColor = config.getString("color.belowAverage");
		this.avgColor = config.getString("color.average");
		this.aboveAvgColor = config.getString("color.aboveAverage");

		this.tagEnabled = config.getBoolean("tag.enabled");
		this.tagFormat = config.getString("tag.format").replaceAll("&", "\u00A7");
		this.tagDuration = config.getInt("tag.duration");

		this.chatEnabled = config.getBoolean("chat.enabled");
		this.chatFormat = config.getStringList("chat.format");

		this.healthWeight = config.getDouble("ratingWeights.health");
		this.speedWeight = config.getDouble("ratingWeights.speed");
		this.jumpWeight = config.getDouble("ratingWeights.jump");

		double jump = -0.1817584952 * Math.pow(0.7, 3) 
				+ 3.689713992 * 0.7 * 0.7 
				+ 2.128599134 * 0.7 - 0.343930367;
		this.averageRating = 22.5 * this.healthWeight
				+ 0.225 * 43 * this.speedWeight
				+ jump * this.jumpWeight;

		this.plugin.getLogger().info("Configuration loaded. The average horse rating is " 
				+ new DecimalFormat("#0.00").format(this.averageRating) + ".");
	}

	public JavaPlugin getPlugin() {
		return plugin;
	}

	public String getBelowAvgColor() {
		return belowAvgColor;
	}

	public String getAvgColor() {
		return avgColor;
	}

	public String getAboveAvgColor() {
		return aboveAvgColor;
	}

	public boolean isTagEnabled() {
		return tagEnabled;
	}

	public String getTagFormat() {
		return tagFormat;
	}

	public int getTagDuration() {
		return tagDuration;
	}

	public boolean isChatEnabled() {
		return chatEnabled;
	}

	public List<String> getChatFormat() {
		return chatFormat;
	}

	public double getHealthWeight() {
		return healthWeight;
	}

	public double getSpeedWeight() {
		return speedWeight;
	}

	public double getJumpWeight() {
		return jumpWeight;
	}

	public double getAverageRating() {
		return averageRating;
	}
}
