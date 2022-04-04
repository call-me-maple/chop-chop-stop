package chopchopstop;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@PluginDescriptor(
		name = "Chop Chop Stop"
)
public class ChopChopStopPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ChopChopStopConfig config;

	static final String CONFIG_GROUP = "chopchopstop";

	private static final String AXE_SPECIAL_TEXT = "Chop chop!";
	private static final String PICKAXE_SPECIAL_TEXT = "Smashing!";
	private static final String HARPOON_SPECIAL_TEXT = "Here fishy fishies!";

	private Set<String> activeMutes;

	@Override
	protected void startUp() throws Exception
	{
		activeMutes = new HashSet<>();
		readConfig();
	}

	private void readConfig()
	{
		activeMutes.clear();
		if (config.muteAxes())
		{
			activeMutes.add(AXE_SPECIAL_TEXT);
		}
		if (config.mutePickaxes())
		{
			activeMutes.add(PICKAXE_SPECIAL_TEXT);
		}
		if (config.muteHarpoons())
		{
			activeMutes.add(HARPOON_SPECIAL_TEXT);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		activeMutes.clear();
	}

	@Subscribe(priority = -2) // Lower priority to go after Chat Filter plugin
	public void onOverheadTextChanged(OverheadTextChanged event)
	{
		if (!(event.getActor() instanceof Player))
		{
			return;
		}
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null || localPlayer.getName() == null || localPlayer.getName().equals(event.getActor().getName()))
		{
			return;
		}

		if (activeMutes.contains(event.getOverheadText()))
		{
			event.getActor().setOverheadText(" ");
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		Player localPlayer = client.getLocalPlayer();
		if (chatMessage.getType() != ChatMessageType.PUBLICCHAT)
		{
			return;
		}
		if (localPlayer == null || localPlayer.getName() == null || !localPlayer.getName().equals(chatMessage.getName()))
		{
			return;
		}

		final ChatLineBuffer lineBuffer = client.getChatLineMap().get(ChatMessageType.PUBLICCHAT.getType());
		if (lineBuffer == null)
		{
			return;
		}

		if (activeMutes.contains(chatMessage.getMessage()))
		{
			lineBuffer.removeMessageNode(chatMessage.getMessageNode());
			clientThread.invoke(() -> client.runScript(ScriptID.BUILD_CHATBOX));
			localPlayer.setOverheadText(" ");
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(CONFIG_GROUP))
		{
			readConfig();
		}
	}

	@Provides
	ChopChopStopConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ChopChopStopConfig.class);
	}
}
