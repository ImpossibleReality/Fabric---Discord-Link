package fr.arthurbambou.fblink;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.arthurbambou.fblink.discordstuff.DiscordBot;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FBLink implements DedicatedServerModInitializer {

	private static ConfigManager configManager;
	private static DiscordBot discordBot;

	@Override
	public void onInitializeServer() {
		configManager = new ConfigManager();
		discordBot = new DiscordBot(configManager.init(), configManager.config);
		configManager.config.token = "";
	}

	public static void regenConfig() {
		configManager.regenConfig();
		discordBot = new DiscordBot(configManager.init(), configManager.config);
		configManager.config.token = "";
	}

	public static DiscordBot getDiscordBot() {
		return discordBot;
	}

	protected class ConfigManager {
		private File CONFIG_PATH = FabricLoader.INSTANCE.getConfigDirectory();

		private final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().create();

		private File configFile;
		private String configFilename = "fblink";
		private Gson gson = DEFAULT_GSON;
		private Config DefaultConfig = new Config();

		private Config config;

		protected String init() {
			configFile = new File(CONFIG_PATH, configFilename + (configFilename.endsWith(".json") ? "" : ".json"));
			if (!configFile.exists()) {
				return saveConfig(DefaultConfig);
			}
			return loadConfig();
		}

		private String saveConfig(Config instanceConfig) {
			try (FileWriter fileWriter = new FileWriter(configFile)) {
				fileWriter.write(gson.toJson(instanceConfig));
				config = instanceConfig;
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (instanceConfig.token != DefaultConfig.token) {
				return instanceConfig.token;
			}
			return DefaultConfig.token;
		}

		public String regenConfig() {
			try (FileWriter fileWriter = new FileWriter(configFile)) {
				fileWriter.write(gson.toJson(DefaultConfig));
				config = DefaultConfig;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return DefaultConfig.token;
		}

		public String loadConfig() {
			try (FileReader fileReader = new FileReader(configFile)) {
				config = gson.fromJson(fileReader, Config.class);
				if (config.token == null) {
					config.token = DefaultConfig.token;
				}
				if (config.chatChannels == null) {
					config.chatChannels = DefaultConfig.chatChannels;
				}
				if (config.logChannels == null) {
					config.logChannels = DefaultConfig.logChannels;
				}
				if (config.discordToMinecraft == null) {
					config.discordToMinecraft = DefaultConfig.discordToMinecraft;
				}
				if (config.minecraftToDiscordMessage == null) {
					config.minecraftToDiscordMessage = DefaultConfig.minecraftToDiscordMessage;
				}
				if (config.minecraftToDiscordMessage.serverStarted == null) {
					config.minecraftToDiscordMessage.serverStarted = DefaultConfig.minecraftToDiscordMessage.serverStarted;
				}
				if (config.minecraftToDiscordMessage.serverStarting == null) {
					config.minecraftToDiscordMessage.serverStarting = DefaultConfig.minecraftToDiscordMessage.serverStarting;
				}
				if (config.minecraftToDiscordMessage.serverStopped == null) {
					config.minecraftToDiscordMessage.serverStopped = DefaultConfig.minecraftToDiscordMessage.serverStopped;
				}
				return saveConfig(config);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public class Config {
		private String token = "";
		public String discordToMinecraft = "[%player] %message";
		public List<String> chatChannels = new ArrayList<String>();
		public List<String> logChannels = new ArrayList<String>();
		public boolean ignoreBots = true;
		public MinecraftToDiscordMessage minecraftToDiscordMessage = new MinecraftToDiscordMessage();

		public boolean getIgnoreBots() {
			return ignoreBots;
		}

		public class MinecraftToDiscordMessage {
			public String serverStarting = "Server is starting !";
			public String serverStarted = "Server Started";
			public String serverStopped = "Server Stopped";
		}
	}
}
