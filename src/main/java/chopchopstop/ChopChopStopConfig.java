package chopchopstop;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("chopchopstop")
public interface ChopChopStopConfig extends Config
{
	@ConfigItem(
		keyName = "muteAxes",
		name = "Hide Axe Specs",
		description = "Hides the 'Chop chop!' messages of dragon, infernal, and crystal axe specs."
	)
	default boolean muteAxes()
	{
		return true;
	}

	@ConfigItem(
			keyName = "mutePickaxes",
			name = "Hide Pickaxe Specs",
			description = "Hides the 'Smashing!' messages of dragon, infernal, and crystal pickaxe specs."
	)
	default boolean mutePickaxes()
	{
		return true;
	}

	@ConfigItem(
			keyName = "muteHarpoons",
			name = "Hide Harpoon Specs",
			description = "Hides the 'Here fishy fishies!' messages of dragon, infernal, and crystal harpoon specs."
	)
	default boolean muteHarpoons()
	{
		return true;
	}
}