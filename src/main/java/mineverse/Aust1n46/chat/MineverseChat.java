/*
 * VentureChat plugin for Minecraft servers running Bukkit or Spigot software.
 * @author Aust1n46
 */
package mineverse.Aust1n46.chat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import mineverse.Aust1n46.chat.json.JsonFormatInfo;
import mineverse.Aust1n46.chat.listeners.CommandListener;
import mineverse.Aust1n46.chat.listeners.LoginListener;
import mineverse.Aust1n46.chat.listeners.ChatListener;
import mineverse.Aust1n46.chat.listeners.PacketListener;
import mineverse.Aust1n46.chat.listeners.SignListener;
import mineverse.Aust1n46.chat.localization.Localization;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
//import mineverse.Aust1n46.chat.alias.Alias;
import mineverse.Aust1n46.chat.alias.AliasInfo;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
//import mineverse.Aust1n46.chat.command.CCommand;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.command.MineverseCommandExecutor;
import mineverse.Aust1n46.chat.command.chat.Broadcast;
import mineverse.Aust1n46.chat.command.chat.BungeeToggle;
import mineverse.Aust1n46.chat.command.chat.Channel;
import mineverse.Aust1n46.chat.command.chat.Channelinfo;
import mineverse.Aust1n46.chat.command.chat.Chatinfo;
import mineverse.Aust1n46.chat.command.chat.Chatreload;
import mineverse.Aust1n46.chat.command.chat.Chlist;
import mineverse.Aust1n46.chat.command.chat.Chwho;
import mineverse.Aust1n46.chat.command.chat.Clearchat;
import mineverse.Aust1n46.chat.command.chat.Commandblock;
import mineverse.Aust1n46.chat.command.chat.Commandspy;
import mineverse.Aust1n46.chat.command.chat.Config;
import mineverse.Aust1n46.chat.command.chat.Edit;
import mineverse.Aust1n46.chat.command.chat.Filter;
import mineverse.Aust1n46.chat.command.chat.Force;
import mineverse.Aust1n46.chat.command.chat.Forceall;
import mineverse.Aust1n46.chat.command.chat.Kickchannel;
import mineverse.Aust1n46.chat.command.chat.Kickchannelall;
import mineverse.Aust1n46.chat.command.chat.Leave;
import mineverse.Aust1n46.chat.command.chat.Listen;
import mineverse.Aust1n46.chat.command.chat.Me;
import mineverse.Aust1n46.chat.command.chat.Nick;
import mineverse.Aust1n46.chat.command.chat.Party;
import mineverse.Aust1n46.chat.command.chat.RangedSpy;
import mineverse.Aust1n46.chat.command.chat.Removemessage;
import mineverse.Aust1n46.chat.command.chat.Setchannel;
import mineverse.Aust1n46.chat.command.chat.Setchannelall;
import mineverse.Aust1n46.chat.command.chat.VentureChatGui;
import mineverse.Aust1n46.chat.command.chat.Venturechat;
import mineverse.Aust1n46.chat.command.message.Ignore;
import mineverse.Aust1n46.chat.command.message.Message;
import mineverse.Aust1n46.chat.command.message.MessageToggle;
import mineverse.Aust1n46.chat.command.message.Notifications;
import mineverse.Aust1n46.chat.command.message.Reply;
import mineverse.Aust1n46.chat.command.message.Spy;
import mineverse.Aust1n46.chat.command.mute.Mute;
import mineverse.Aust1n46.chat.command.mute.Muteall;
import mineverse.Aust1n46.chat.command.mute.Unmute;
import mineverse.Aust1n46.chat.command.mute.Unmuteall;
import mineverse.Aust1n46.chat.database.Database;
import mineverse.Aust1n46.chat.database.MySQL;
import mineverse.Aust1n46.chat.database.PlayerData;
import mineverse.Aust1n46.chat.gui.GuiSlotInfo;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.versions.V1_8;
import mineverse.Aust1n46.chat.versions.VersionHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.Sound;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.clip.placeholderapi.PlaceholderAPI;

public class MineverseChat extends JavaPlugin implements PluginMessageListener {
	// Listeners --------------------------------
	private ChatListener chatListener;
	private LoginListener loginListener;
	private SignListener signListener;
	private CommandListener commandListener;
	private Channel channelListener;
	public static String[] playerlist;
	public static String playerlist_server;
	public boolean ircListen;
	public static ChatMessage lastChatMessage;
	public static String lastJson;
	public static Method messageMethod;
	public static Field posField;
	public static Class<?> chatMessageType;
	private static Field commandMap;
	private static Field knownCommands;

	// Executors --------------------------------
	private MineverseCommandExecutor commandExecutor;
	private Map<String, MineverseCommand> commands = new HashMap<String, MineverseCommand>();

	// Database ------------------------------------
	public Database db = null;

	// Misc --------------------------------
	public static AliasInfo aaInfo;
	public static JsonFormatInfo jfInfo;
	public static GuiSlotInfo gsInfo;
	public boolean quickchat = true;
	private static final Logger log = Logger.getLogger("Minecraft");
	public static Set<MineverseChatPlayer> players = new HashSet<MineverseChatPlayer>();
	public static Set<MineverseChatPlayer> onlinePlayers = new HashSet<MineverseChatPlayer>();
	public static HashMap<String, String> networkPlayers = new HashMap<String, String>();
	private boolean firstRun = true;
	
	// Plugin Messaging Channel
	public static final String PLUGIN_MESSAGING_CHANNEL = "venturechat:data";
	
	// Event constants
	public static final boolean ASYNC = true;
	public static final boolean SYNC = false;

	// Vault --------------------------------
	public static Permission permission = null;
	public static Chat chat = null;
	public static CommandMap cmap;

	// Offline data ----------------------------
	public Map<String, String> mutes = new HashMap<String, String>();

	private LogLevels curLogLevel;

	public long LINELENGTH = 40;
	
	// DiscordSRV backwards compatibility
	@Deprecated
	public static ChatChannelInfo ccInfo;
	
	public static void main(String[] args) {}
	
	@Override
	public void onEnable() {
		ccInfo = new ChatChannelInfo();
		try {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Initializing..."));
			if(!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}
			File file = new File(getDataFolder(), "config.yml");
			if(!file.exists()) {
				Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Config not found! Generating file."));
				saveDefaultConfig();
			}
			else {
				Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Config found! Loading file."));
			}

			saveResource("defaultconfig.yml", true);
		}
		catch(Exception ex) {
			log.severe(String.format("[" + String.format("VentureChat") + "]" + " - Could not load configuration!\n " + ex, getDescription().getName()));
		}
		
		this.setLogLevel(this.getConfig().getString("loglevel", "INFO").toUpperCase());
		ChatChannel.initialize();
		
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Checking for Vault..."));
		// Set up Vault
		if(!this.setupPermissions()) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - &cCould not find Vault dependency, disabling."));
			Bukkit.getPluginManager().disablePlugin(this);
		}
		this.setupChat();
		// Log completion of initialization
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabled Successfully"));
		// Get config and handle
		// Configuration

		Localization.initialize();
		
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Registering Listeners"));
		// Channel information reference
		aaInfo = new AliasInfo(this);
		jfInfo = new JsonFormatInfo(this);
		gsInfo = new GuiSlotInfo();
		
		PlayerData.initialize();
		if(this.firstRun) {
			for(String uuidString : PlayerData.getPlayerData().getConfigurationSection("players").getKeys(false)) {
				UUID uuid = UUID.fromString(uuidString);
				String name = PlayerData.getPlayerData().getConfigurationSection("players." + uuid).getString("name");
				String currentChannelName = PlayerData.getPlayerData().getConfigurationSection("players." + uuid).getString("current");
				ChatChannel currentChannel = ChatChannel.isChannel(currentChannelName) ? ChatChannel.getChannel(currentChannelName) : ChatChannel.getDefaultChannel();
				Set<UUID> ignores = new HashSet<UUID>();
				StringTokenizer i = new StringTokenizer(PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("ignores"), ",");
				while(i.hasMoreTokens()) {
					ignores.add(UUID.fromString(i.nextToken()));
				}
				Set<String> listening = new HashSet<String>();
				StringTokenizer l = new StringTokenizer(PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("listen"), ",");
				while(l.hasMoreTokens()) {
					String channel = l.nextToken();
					if(ChatChannel.isChannel(channel)) {
						listening.add(channel);
					}
				}
				HashMap<String, Integer> mutes = new HashMap<String, Integer>();
				StringTokenizer m = new StringTokenizer(PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("mutes"), ",");
				while(m.hasMoreTokens()) {
					String[] parts = m.nextToken().split(":");
					if(ChatChannel.isChannel(parts[0])) {
						if(parts[1].equals("null")) {
							log.info("[VentureChat] Null Mute Time: " + parts[0] + " " + name);
							continue;
						}
						mutes.put(ChatChannel.getChannel(parts[0]).getName(), Integer.parseInt(parts[1]));
					}
				}
				Set<String> blockedCommands = new HashSet<String>();
				StringTokenizer b = new StringTokenizer(PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("blockedcommands"), ",");
				while(b.hasMoreTokens()) {
					blockedCommands.add(b.nextToken());
				}
				boolean host = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("host");
				UUID party = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("party").length() > 0 ? UUID.fromString(PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("party")) : null;
				boolean filter = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("filter");
				boolean notifications = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("notifications");
				String nickname = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getString("nickname");
				String jsonFormat = "Default";
				boolean spy = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("spy", false);
				boolean commandSpy = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("commandspy", false);
				boolean rangedSpy = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("rangedspy", false);
				boolean messageToggle = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("messagetoggle", true);
				boolean bungeeToggle = PlayerData.getPlayerData().getConfigurationSection("players." + uuidString).getBoolean("bungeetoggle", true);
				players.add(new MineverseChatPlayer(uuid, name, currentChannel, ignores, listening, mutes, blockedCommands, host, party, filter, notifications, nickname, jsonFormat, spy, commandSpy, rangedSpy, messageToggle, bungeeToggle));
			}
		}
		for(Player p : this.getServer().getOnlinePlayers()) {
			MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(p);
			mcp.setName(p.getName());
			mcp.setOnline(true);
			mcp.setHasPlayed(false);
			mcp.setJsonFormat();
			onlinePlayers.add(mcp);
		}

		FileConfiguration config = getConfig();
		ConfigurationSection mysqlConfig = config.getConfigurationSection("mysql");
		if (this.getConfig().getConfigurationSection("mysql").getBoolean("enabled")) {
			String host = mysqlConfig.getString("host");
			int port = mysqlConfig.getInt("port");
			String database = mysqlConfig.getString("database");
			String user = mysqlConfig.getString("user");
			String password = mysqlConfig.getString("password");
			db = new MySQL(host, port, database, user, password);
		}

		commands.put("broadcast", new Broadcast("broadcast"));
		commands.put("channel", new Channel("channel"));
		commands.put("join", new Channel("join"));
		commands.put("channelinfo", new Channelinfo("channelinfo"));
		commands.put("chatinfo", new Chatinfo("chatinfo"));
		commands.put("chatreload", new Chatreload("chatreload"));
		commands.put("chlist", new Chlist("chlist"));
		commands.put("chwho", new Chwho("chwho"));
		commands.put("clearchat", new Clearchat("clearchat"));
		commands.put("commandblock", new Commandblock("commandblock"));
		commands.put("commandspy", new Commandspy("commandspy"));
		commands.put("config", new Config("config"));
		commands.put("edit", new Edit("edit"));
		commands.put("filter", new Filter("filter"));
		commands.put("force", new Force("force"));
		commands.put("forceall", new Forceall("forceall"));
		commands.put("ignore", new Ignore("ignore"));
		commands.put("kickchannel", new Kickchannel("kickchannel"));
		commands.put("kickchannelall", new Kickchannelall("kickchannelall"));
		commands.put("leave", new Leave("leave"));
		commands.put("listen", new Listen("listen"));
		commands.put("me", new Me("me"));
		commands.put("message", new Message("message"));
		commands.put("tell", new Message("tell"));
		commands.put("whisper", new Message("whisper"));
		commands.put("venturechat", new Venturechat("venturechat"));
		commands.put("mute", new Mute("mute"));
		commands.put("muteall", new Muteall("muteall"));
		commands.put("nick", new Nick("nick"));
		commands.put("notifications", new Notifications("notifications"));
		commands.put("party", new Party("party"));
		commands.put("rangedspy", new RangedSpy("rangedspy"));
		commands.put("removemessage", new Removemessage("removemessage"));
		commands.put("reply", new Reply("reply"));
		commands.put("setchannel", new Setchannel("setchannel"));
		commands.put("setchannelall", new Setchannelall("setchannelall"));
		commands.put("spy", new Spy("spy"));
		commands.put("unmute", new Unmute("unmute"));
		commands.put("unmuteall", new Unmuteall("unmuteall"));
		commands.put("venturechatgui", new VentureChatGui("venturechatgui"));
		commands.put("messagetoggle", new MessageToggle("messagetoggle"));
		commands.put("bungeetoggle", new BungeeToggle("bungeetoggle"));
		commandExecutor = new MineverseCommandExecutor(commands);
		for(String command : commands.keySet()) {
			this.getCommand(command).setExecutor(commandExecutor);
		}

		channelListener = new Channel();
		signListener = new SignListener();
		chatListener = new ChatListener();
		commandListener = new CommandListener(aaInfo);

		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(channelListener, this);
		pluginManager.registerEvents(chatListener, this);
		pluginManager.registerEvents(signListener, this);
		pluginManager.registerEvents(commandListener, this);
		loginListener = new LoginListener();
		pluginManager.registerEvents(loginListener, this);
		this.registerPacketListeners();
		this.loadNMS();
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Attaching to Executors"));
		try {
			// if(VersionHandler.is1_7_9()) cmap = V1_7_9.v1_7_9();
			// if(VersionHandler.is1_7_10()) cmap = V1_7_10.v1_7_10();
			if(VersionHandler.is1_8()) cmap = V1_8.v1_8();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		this.quickchat = false;
		if(cmap == null) {
			this.quickchat = false;
			// log.info(String.format("[" + String.format("VentureChat" + "]" +
			// " - Unrecognized server version, Quickchat commands not
			// registering",
			// getDescription().getName())));
			// log.info(String.format("[" + String.format("VentureChat" + "]" +
			// " - Unrecognized server version, Alias commands not registering",
			// getDescription().getName())));
		}
		else {
			/*
			 * Don't run this code right now for(ChatChannel c :
			 * ccInfo.getChannelsInfo()) { CCommand cmd = new
			 * CCommand(c.getAlias()); cmap.register("", cmd);
			 * cmd.setExecutor(commandListener); } for(Alias a :
			 * aaInfo.getAliases()) { CCommand cmd = new CCommand(a.getName());
			 * cmap.register("", cmd); }
			 * Bukkit.getConsoleSender().sendMessage(Format.
			 * FormatStringAll("&8[&eVentureChat&8]&e - Registering Alias commands"
			 * )); Bukkit.getConsoleSender().sendMessage(Format.
			 * FormatStringAll("&8[&eVentureChat&8]&e - Registering Quickchat commands"
			 * ));
			 */
		}
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Establishing BungeeCord"));
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL);
		Bukkit.getMessenger().registerIncomingPluginChannel(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, this);
		if(pluginManager.isPluginEnabled("Towny")) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabling Towny Formatting"));
		}
		if(pluginManager.isPluginEnabled("Jobs")) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabling Jobs Formatting"));
		}
		if(pluginManager.isPluginEnabled("Factions")) {
			String version = pluginManager.getPlugin("Factions").getDescription().getVersion();
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabling Factions Formatting version " + version));
		}
		if(pluginManager.isPluginEnabled("Heroes")) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabling Heroes Formatting"));
		}
		if(pluginManager.isPluginEnabled("PlaceholderAPI")) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Enabling PlaceholderAPI Hook"));
		}
		boolean hooked = PlaceholderAPI.registerPlaceholderHook("venturechat", new VentureChatPlaceholders());
		if(hooked) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Added placeholders to PlaceholderAPI!"));
		}
		else {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - &cPlaceholders were not added to PlaceholderAPI!"));
		}
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Loading player data"));
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				PlayerData.savePlayerData();
				if(getConfig().getString("loglevel", "info").equals("debug")) {
					Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Saving Player Data"));
				}
			}
		}, 0L, getConfig().getInt("saveinterval") * 1200);
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for(MineverseChatPlayer p : MineverseChat.players) {
					int time = (int) (System.currentTimeMillis() / 60000);

					for(String c : p.getMutes().keySet()) {
						ChatChannel channel = ChatChannel.getChannel(c);
						int timemark = p.getMutes().get(channel.getName());
						if(timemark == 0) return;
						// System.out.println(time + " " + timemark);
						if(time > timemark) {
							p.removeMute(channel.getName());
							if(p.isOnline()) p.getPlayer().sendMessage(ChatColor.RED + "You have just been unmuted in: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
							else p.setModified(true);
						}
					}
				}
				if(getConfig().getString("loglevel", "info").equals("debug")) {
					Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Updating Player Mutes"));
				}
			}
		}, 0L, 20L);
		this.firstRun = false;
	}

	@SuppressWarnings("unchecked")
	public void unregister(String name) {
		try {
			((Map<String, Command>) knownCommands.get((SimpleCommandMap) commandMap.get(Bukkit.getServer()))).remove(name);
		}
		catch(Exception e) {
		}
	}

	@SuppressWarnings("unused")
	private void loadCommandMap() {
		try {
			commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMap.setAccessible(true);
			knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
			knownCommands.setAccessible(true);
		}
		catch(Exception e) {
		}
	}

	public CommandMap getCommandMap() {
		return cmap;
	}

	public static MineverseChat getInstance() {
		return getPlugin(MineverseChat.class);
		
	}

	private void registerPacketListeners() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListener());
	}

	private void loadNMS() {	
		try {
			MineverseChat.posField = MinecraftReflection.getMinecraftClass("PacketPlayOutChat").getDeclaredField("b");
			MineverseChat.posField.setAccessible(true);
			
			
			//MineverseChat.messageMethod = MinecraftReflection.getMinecraftClass("ChatBaseComponent").getDeclaredMethod("getString");
			//MineverseChat.messageMethod.setAccessible(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(!VersionHandler.is1_7_10() && !VersionHandler.is1_8() && !VersionHandler.is1_9() && !VersionHandler.is1_10() && !VersionHandler.is1_11()) {
			try {
				MineverseChat.chatMessageType = getNMSClass("ChatMessageType");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Class<?> getNMSClass(String name) {
		try {
			return Class.forName("net.minecraft.server." + getVersion() + "." + name);
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if(permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return(permission != null);
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if(chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		return(chat != null);
	}

	public long getLineLength() {
		return LINELENGTH;
	}

	@Override
	public void onDisable() {
		PlayerData.savePlayerData();
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Disabling..."));
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Disabled Successfully"));
	}

	public void setLogLevel(String loglevel) {
		if(LogLevels.valueOf(loglevel) != null) {
			curLogLevel = LogLevels.valueOf(loglevel);
		}
		else {
			curLogLevel = LogLevels.INFO;
		}
	}

	public void logme(LogLevels level, String location, String logline) {
		if(level.ordinal() >= curLogLevel.ordinal()) {
			log.log(Level.INFO, "[VentureChat]: {0}:{1} : {2}", new Object[] { level.toString(), location, logline });
		}
	}

	public void synchronize(MineverseChatPlayer mcp, boolean changes) {
		// System.out.println("Sync started...");
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(outstream);
		try {
			out.writeUTF("Sync");
			if(!changes) {
				out.writeUTF("Receive");
				// System.out.println(mcp.getPlayer().getServer().getServerName());
				// out.writeUTF(mcp.getPlayer().getServer().getServerName());
				out.writeUTF(mcp.getUUID().toString());
			}
			else {
				out.writeUTF("Update");
				out.writeUTF(mcp.getUUID().toString());
				// out.writeUTF("Channels");
				int channelCount = 0;
				for(String c : mcp.getListening()) {
					ChatChannel channel = ChatChannel.getChannel(c);
					if(channel.getBungee()) {
						channelCount++;
					}
				}
				out.write(channelCount);
				for(String c : mcp.getListening()) {
					ChatChannel channel = ChatChannel.getChannel(c);
					if(channel.getBungee()) {
						out.writeUTF(channel.getName());
					}
				}
				// out.writeUTF("Mutes");
				int muteCount = 0;
				for(String c : mcp.getMutes().keySet()) {
					ChatChannel channel = ChatChannel.getChannel(c);
					if(channel.getBungee()) {
						muteCount++;
					}
				}
				// System.out.println(muteCount + " mutes");
				out.write(muteCount);
				for(String c : mcp.getMutes().keySet()) {
					ChatChannel channel = ChatChannel.getChannel(c);
					if(channel.getBungee()) {
						out.writeUTF(channel.getName());
					}
				}
				int ignoreCount = 0;
				for(@SuppressWarnings("unused")
				UUID c : mcp.getIgnores()) {
					ignoreCount++;
				}
				out.write(ignoreCount);
				for(UUID c : mcp.getIgnores()) {
					out.writeUTF(c.toString());
				}
				out.writeBoolean(mcp.isSpy());
				out.writeBoolean(mcp.getMessageToggle());
			}
			for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
				p.getPlayer().sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
				break;
			}
			// System.out.println("Sync start bottom...");
			out.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void updatePlayerList(MineverseChatPlayer mcp, boolean request) {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(outstream);
		try {
			out.writeUTF("Sync");
			if(request) {
				out.writeUTF("PlayersReceive");
				// System.out.println(mcp.getPlayer().getServer().getServerName());
				out.writeUTF(this.getServer().getName());
			}
			else {
				out.writeUTF("PlayersUpdate");
				// System.out.println(networkPlayers.keySet().size());
				out.write(networkPlayers.keySet().size());
				for(String p : networkPlayers.keySet()) {
					out.writeUTF(p + "," + networkPlayers.get(p));
				}
			}
			mcp.getPlayer().sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, outstream.toByteArray());
			// System.out.println("Sync start bottom...");
			out.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendDiscordSRVPluginMessage(String chatChannel, String message) {
		if(onlinePlayers.size() == 0) {
			return;
		}
		Player host = onlinePlayers.iterator().next().getPlayer();
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutStream);
		try {
			out.writeUTF("DiscordSRV");
			out.writeUTF(chatChannel);
			out.writeUTF(message);
			host.sendPluginMessage(MineverseChat.getInstance(), MineverseChat.PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] inputStream) {
		if(!channel.equals(MineverseChat.PLUGIN_MESSAGING_CHANNEL)) {
			return;
		}
		try {
			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(inputStream));
			if(this.getConfig().getString("loglevel", "info").equals("debug")) {
				System.out.println(msgin.available() + " size on receiving end");
			}
			String subchannel = msgin.readUTF();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(stream);
			if(subchannel.equals("Chat")) {
				String server = msgin.readUTF();
				String chatchannel = msgin.readUTF();
				String senderName = msgin.readUTF();
				UUID senderUUID = UUID.fromString(msgin.readUTF());
				int hash = msgin.readInt();
				String format = msgin.readUTF();
				String chat = msgin.readUTF();
				String consoleChat = format + chat;
				String globalJSON = msgin.readUTF();
				String primaryGroup = msgin.readUTF();
				String nickname = msgin.readUTF();
				
				if(!ChatChannel.isChannel(chatchannel)) {
					return;
				}
				ChatChannel chatChannelObject = ChatChannel.getChannel(chatchannel);
				
				if(!chatChannelObject.getBungee()) {
					return;
				}
				
				Set<Player> recipients = new HashSet<Player>();
				for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
					if(p.isListening(chatChannelObject.getName())) {
						recipients.add(p.getPlayer());
					}
				}
				
				Bukkit.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
					@Override
					public void run() {
						//Create VentureChatEvent
						VentureChatEvent ventureChatEvent = new VentureChatEvent(null, senderName, nickname, primaryGroup, chatChannelObject, recipients, recipients.size(), format, chat, globalJSON, hash, false);
						//Fire event and wait for other plugin listeners to act on it
						Bukkit.getServer().getPluginManager().callEvent(ventureChatEvent);
					}
				});
				
				Bukkit.getConsoleSender().sendMessage(consoleChat);
				
				if(db != null) {
					Calendar currentDate = Calendar.getInstance();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String date = formatter.format(currentDate.getTime());
					db.writeVentureChat(date, senderUUID.toString(), senderName, server, chatchannel, chat.replace("'", "''"), "Chat");
				}
				
				for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
					if(p.isListening(chatChannelObject.getName())) {
						if(!p.getBungeeToggle() && MineverseChatAPI.getOnlineMineverseChatPlayer(senderName) == null) {
							continue;
						}
						
						String json = Format.formatModerationGUI(globalJSON, p.getPlayer(), senderName, chatchannel, hash);
						WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(json);
						PacketContainer packet = Format.createPacketPlayOutChat(chatComponent);
						
						if(this.getConfig().getBoolean("ignorechat", false)) {
							if(!p.getIgnores().contains(senderUUID)) {
								// System.out.println("Chat sent");
								Format.sendPacketPlayOutChat(p.getPlayer(), packet);							
							}
							continue;
						}
						Format.sendPacketPlayOutChat(p.getPlayer(), packet);	
					}
				}
			}
			if(subchannel.equals("DiscordSRV")) {
				String chatchannel = msgin.readUTF();
				String message = msgin.readUTF();
				if(ChatChannel.isChannel(chatchannel) && ChatChannel.getChannel(chatchannel).getBungee()) {
					for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
						p.getPlayer().sendMessage(Format.FormatStringAll(message));
					}
				}
			}
			if(subchannel.equals("Chwho")) {
				String identifier = msgin.readUTF();
				if(identifier.equals("Get")) {
					String sender = msgin.readUTF();
					String name = msgin.readUTF();
					String chatchannel = msgin.readUTF();
					List<String> listening = new ArrayList<String>();
					if(ChatChannel.isChannel(chatchannel)) {
						for(MineverseChatPlayer mcp : onlinePlayers) {
							if(mcp.isListening(chatchannel)) {
								String entry = "&f" + mcp.getName();
								if(mcp.isMuted(chatchannel)) {
									entry = "&c" + mcp.getName();
								}
								listening.add(entry);
							}
						}
					}
					out.writeUTF("Chwho");
					out.writeUTF("Receive");
					out.writeUTF(sender);
					out.writeUTF(name);
					out.writeUTF(chatchannel);
					out.writeInt(listening.size());
					for(String s : listening) {
						out.writeUTF(s);
					}
					player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
				}
				if(identifier.equals("Receive")) {
					String sender = msgin.readUTF();
					String stringchannel = msgin.readUTF();
					MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(UUID.fromString(sender));
					ChatChannel chatchannel = ChatChannel.getChannel(stringchannel);
					String playerList = "";
					int size = msgin.readInt();
					for(int a = 0; a < size; a++) {
						playerList += msgin.readUTF() + ChatColor.WHITE + ", ";
					}
					if(playerList.length() > 2) {
						playerList = playerList.substring(0, playerList.length() - 2);
					}
					mcp.getPlayer().sendMessage(LocalizedMessage.CHANNEL_PLAYER_LIST_HEADER.toString()
							.replace("{channel_color}", (ChatColor.valueOf(chatchannel.getColor().toUpperCase())).toString())
							.replace("{channel_name}", chatchannel.getName()));
					mcp.getPlayer().sendMessage(Format.FormatStringAll(playerList));
				}
			}
			if(subchannel.equals("RemoveMessage")) {
				String hash = msgin.readUTF();
				this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "removemessage " + hash);
			}
			if(subchannel.equals("PlayersUpdate")) {
				networkPlayers.clear();
				int size = msgin.read();
				for(int a = 1; a <= size; a++) {
					String p = msgin.readUTF();
					String[] parts = p.split(",");
					networkPlayers.put(parts[0], parts[1]);
					System.out.print(p);
				}
			}
			if(subchannel.equals("Sync")) {
				if(this.getConfig().getString("loglevel", "info").equals("debug")) {
					Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Received update..."));
				}
				String uuid = msgin.readUTF();
				MineverseChatPlayer p = MineverseChatAPI.getMineverseChatPlayer(UUID.fromString(uuid));
				for(Object ch : p.getListening().toArray()) {
					String c = ch.toString();
					ChatChannel cha = ChatChannel.getChannel(c);
					if(cha.getBungee()) {
						p.removeListening(c);
					}
				}
				int size = msgin.read();
				// System.out.println(size);
				for(int a = 0; a < size; a++) {
					String ch = msgin.readUTF();
					if(ChatChannel.isChannel(ch)) {
						ChatChannel cha = ChatChannel.getChannel(ch);
						if(cha.hasPermission() && p.getPlayer().hasPermission(cha.getPermission())) {
							p.addListening(ch);
						}
					}
				}
				for(Object o : p.getMutes().keySet().toArray()) {
					ChatChannel ch = ChatChannel.getChannel((String) o);
					if(ch.getBungee()) {
						p.removeMute(ch.getName());
					}
				}
				int sizeB = msgin.read();
				// System.out.println(sizeB + " mute size");
				for(int b = 0; b < sizeB; b++) {
					String ch = msgin.readUTF();
					// System.out.println(ch);
					if(ChatChannel.isChannel(ch)) {
						p.addMute(ch, 0);
					}
				}
				// System.out.println(msgin.available() + " available before");
				p.setSpy(msgin.readBoolean());
				p.setMessageToggle(msgin.readBoolean());
				// System.out.println(msgin.available() + " available after");
				for(Object o : p.getIgnores().toArray()) {
					p.removeIgnore((UUID) o);
				}
				int sizeC = msgin.read();
				// System.out.println(sizeC + " ignore size");
				for(int c = 0; c < sizeC; c++) {
					String i = msgin.readUTF();
					// System.out.println(i);
					p.addIgnore(UUID.fromString(i));
				}
				if(!p.hasPlayed()) {
					for(ChatChannel ch : ChatChannel.getAutojoinList()) {
						if(ch.hasPermission()) {
							if(p.getPlayer().hasPermission(ch.getPermission())) {
								p.addListening(ch.getName());
							}
						}
						else {
							p.addListening(ch.getName());
						}
					}
					p.setHasPlayed(true);
					this.synchronize(p, true);
				}
			}
			if(subchannel.equals("Ignore")) {
				String identifier = msgin.readUTF();
				if(identifier.equals("Send")) {
					String server = msgin.readUTF();
					String receiver = msgin.readUTF();
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(receiver);
					UUID sender = UUID.fromString(msgin.readUTF());
					if(!this.getConfig().getBoolean("bungeecordmessaging", true) || p == null || !p.isOnline()) {
						out.writeUTF("Ignore");
						out.writeUTF("Offline");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
						return;
					}
					out.writeUTF("Ignore");
					out.writeUTF("Echo");
					out.writeUTF(server);
					out.writeUTF(p.getUUID().toString());
					out.writeUTF(sender.toString());
					player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
					return;
				}
				if(identifier.equals("Offline")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
							.replace("{args}", receiver));
				}
				if(identifier.equals("Echo")) {
					UUID receiver = UUID.fromString(msgin.readUTF());
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					MineverseChatPlayer r = MineverseChatAPI.getMineverseChatPlayer(receiver);
					String rName = receiver.toString();
					if(r != null) {
						rName = Format.FormatStringAll(r.getNickname());
					}
					
					if(p.getIgnores().contains(receiver)) {
						p.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer ignoring player: " + ChatColor.RED + rName);
						p.removeIgnore(receiver);
						this.synchronize(p, true);
						return;
					}
					
					p.addIgnore(receiver);
					p.getPlayer().sendMessage(LocalizedMessage.IGNORE_PLAYER_ON.toString()
							.replace("{player}", rName));
					this.synchronize(p, true);
				}
			}
			if(subchannel.equals("Message")) {
				String identifier = msgin.readUTF();
				if(identifier.equals("Send")) {
					String server = msgin.readUTF();
					String receiver = msgin.readUTF();
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(receiver);
					UUID sender = UUID.fromString(msgin.readUTF());
					String sName = msgin.readUTF();
					MineverseChatPlayer s = MineverseChatAPI.getMineverseChatPlayer(sender);
					String msg = msgin.readUTF();
					String echo = msgin.readUTF();
					String spy = msgin.readUTF();
					// System.out.println((p == null) + " null");
					if(p != null) {
						// System.out.println(p.isOnline() + " online");
					}
					if(!this.getConfig().getBoolean("bungeecordmessaging", true) || p == null || !p.isOnline()) {
						out.writeUTF("Message");
						out.writeUTF("Offline");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
						return;
					}
					if(p.getIgnores().contains(sender)) {
						out.writeUTF("Message");
						out.writeUTF("Ignore");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
						return;
					}
					if(!p.getMessageToggle()) {
						out.writeUTF("Message");
						out.writeUTF("Blocked");
						out.writeUTF(server);
						out.writeUTF(receiver);
						out.writeUTF(sender.toString());
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
						return;
					}
					if(s != null) {
						sName = Format.FormatStringAll(s.getNickname());
					}
					else {
						UUID uuid = sender;
						String name = sName;
						ChatChannel current = ChatChannel.getDefaultChannel();
						Set<UUID> ignores = new HashSet<UUID>();
						Set<String> listening = new HashSet<String>();
						listening.add(current.getName());
						HashMap<String, Integer> mutes = new HashMap<String, Integer>();
						Set<String> blockedCommands = new HashSet<String>();
						String jsonFormat = "Default";
						s = new MineverseChatPlayer(uuid, name, current, ignores, listening, mutes, blockedCommands, false, null, true, true, name, jsonFormat, false, false, false, true, true);
						MineverseChat.players.add(s);
					}
					p.getPlayer().sendMessage(msg.replace("{playerfrom}", sName).replace("{playerto}", Format.FormatStringAll(p.getNickname())));
					if(p.hasNotifications()) {
						if(VersionHandler.is1_8() || VersionHandler.is1_7_10() || VersionHandler.is1_7_2() || VersionHandler.is1_7_9()) {
							p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.valueOf("LEVEL_UP"), 1, 0);
						}
						else {
							p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 0);
						}
					}
					p.setReplyPlayer(sender);
					out.writeUTF("Message");
					out.writeUTF("Echo");
					out.writeUTF(server);
					out.writeUTF(p.getNickname());
					out.writeUTF(sender.toString());
					out.writeUTF(sName);
					out.writeUTF(echo);
					out.writeUTF(spy);
					player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
					return;
				}
				if(identifier.equals("Offline")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
							.replace("{args}", receiver));
					p.setReplyPlayer(null);
				}
				if(identifier.equals("Ignore")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.IGNORING_MESSAGE.toString()
							.replace("{player}", receiver));
				}
				if(identifier.equals("Blocked")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					p.getPlayer().sendMessage(LocalizedMessage.BLOCKING_MESSAGE.toString()
							.replace("{player}", receiver));
				}
				if(identifier.equals("Echo")) {
					String receiver = msgin.readUTF();
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					MineverseChatPlayer r = MineverseChatAPI.getMineverseChatPlayer(receiver);
					String echo = msgin.readUTF();
					String rName = Format.FormatStringAll(receiver);
					if(r != null) {
						rName = Format.FormatStringAll(r.getNickname());
						p.setReplyPlayer(r.getUUID());
					}
					p.getPlayer().sendMessage(echo.replace("{playerfrom}", Format.FormatStringAll(p.getNickname())).replace("{playerto}", rName));
				}
				if(identifier.equals("Spy")) {
					String receiver = msgin.readUTF();
					MineverseChatPlayer r = MineverseChatAPI.getMineverseChatPlayer(receiver);
					UUID sender = UUID.fromString(msgin.readUTF());
					MineverseChatPlayer p = MineverseChatAPI.getOnlineMineverseChatPlayer(sender);
					String sName = msgin.readUTF();
					String spy = msgin.readUTF();
					String rName = receiver;
					if(r != null) {
						rName = Format.FormatStringAll(r.getNickname());
					}
					if(p != null) {
						sName = Format.FormatStringAll(p.getNickname());
					}
					for(MineverseChatPlayer pl : onlinePlayers) {
						if(pl.isSpy() && !pl.getName().equals(sName) && !pl.getName().equals(rName)) {
							pl.getPlayer().sendMessage(spy.replace("{playerto}", rName).replace("{playerfrom}", sName));
						}
					}
				}
			}
			if(subchannel.equals("Mute")) {
				String sendplayer = msgin.readUTF();
				String mutePlayer = msgin.readUTF();
				String chatchannel = msgin.readUTF();
				String server = msgin.readUTF();
				String time = msgin.readUTF();
				int numtime = 0;
				MineverseChatPlayer p = MineverseChatAPI.getMineverseChatPlayer(mutePlayer);
				ChatChannel cc = ChatChannel.getChannel(chatchannel);
				if(cc == null) {
					try {
						out.writeUTF("Mute");
						out.writeUTF("Channel");
						out.writeUTF(sendplayer);
						out.writeUTF(chatchannel);
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(p == null) {
					try {
						out.writeUTF("Mute");
						out.writeUTF("Player");
						out.writeUTF(sendplayer);
						out.writeUTF(mutePlayer);
						out.writeUTF(server);
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(!cc.isMutable()) {
					try {
						out.writeUTF("Mute");
						out.writeUTF("Mutable");
						out.writeUTF(sendplayer);
						out.writeUTF(cc.getName());
						out.writeUTF(cc.getColor());
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(p.isMuted(cc.getName())) {
					try {
						out.writeUTF("Mute");
						out.writeUTF("Already");
						out.writeUTF(sendplayer);
						out.writeUTF(mutePlayer);
						out.writeUTF(cc.getName());
						out.writeUTF(cc.getColor());
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(!time.equals("None\n")) {
					try {
						numtime = Integer.parseInt(time);
						if(numtime > 0) {
							Calendar currentDate = Calendar.getInstance();
							SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm:ss");
							String date = formatter.format(currentDate.getTime());
							String[] datearray = date.split(":");
							int datetime = (Integer.parseInt(datearray[0]) * 1440) + (Integer.parseInt(datearray[1]) * 60) + (Integer.parseInt(datearray[2]));
							p.addMute(cc.getName(), datetime + numtime);
							String keyword = "minutes";
							if(numtime == 1) keyword = "minute";
							if(p.isOnline()) p.getPlayer().sendMessage(ChatColor.RED + "You have just been muted in: " + ChatColor.valueOf(cc.getColor().toUpperCase()) + cc.getName() + ChatColor.RED + " for " + time + " " + keyword);
							else p.setModified(true);
							if(cc.getBungee()) {
								MineverseChat.getInstance().synchronize(p, true);
							}
							try {
								out.writeUTF("Mute");
								out.writeUTF("Valid");
								out.writeUTF(sendplayer);
								out.writeUTF(mutePlayer);
								out.writeUTF(cc.getName());
								out.writeUTF(cc.getColor());
								out.writeUTF(time);
								player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
							}
							catch(Exception e) {
								e.printStackTrace();
							}
							return;
						}
						try {
							out.writeUTF("Mute");
							out.writeUTF("Time");
							out.writeUTF(sendplayer);
							out.writeUTF(time);
							player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						return;
					}
					catch(Exception e) {
						try {
							out.writeUTF("Mute");
							out.writeUTF("Time");
							out.writeUTF(sendplayer);
							out.writeUTF(time);
							player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
						}
						catch(Exception e1) {
							e1.printStackTrace();
						}
						return;
					}
				}
				p.addMute(cc.getName(), 0);
				if(p.isOnline()) p.getPlayer().sendMessage(ChatColor.RED + "You have just been muted in: " + ChatColor.valueOf(cc.getColor().toUpperCase()) + cc.getName());
				else p.setModified(true);
				if(cc.getBungee()) {
					MineverseChat.getInstance().synchronize(p, true);
				}
				try {
					out.writeUTF("Mute");
					out.writeUTF("Valid");
					out.writeUTF(sendplayer);
					out.writeUTF(mutePlayer);
					out.writeUTF(cc.getName());
					out.writeUTF(cc.getColor());
					out.writeUTF(time);
					player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if(subchannel.equals("Muteall")) {
				String sendplayer = msgin.readUTF();
				String muteplayer = msgin.readUTF();
				String server = msgin.readUTF();
				Player mp = Bukkit.getPlayer(muteplayer);
				MineverseChatPlayer p = MineverseChatAPI.getMineverseChatPlayer(mp);
				if(mp == null) {
					try {
						out.writeUTF("Muteall");
						out.writeUTF("Player");
						out.writeUTF(sendplayer);
						out.writeUTF(muteplayer);
						out.writeUTF(server);
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				boolean bungee = false;
				for(ChatChannel c : ChatChannel.getChannels()) {
					if(c.isMutable()) {
						p.addMute(c.getName(), 0);
						if(c.getBungee()) {
							bungee = true;
						}
					}
				}
				if(bungee) {
					MineverseChat.getInstance().synchronize(p, true);
				}
				if(p.isOnline()) {
					p.getPlayer().sendMessage(ChatColor.RED + "You have just been muted in all channels.");
				}
				else p.setModified(true);
				try {
					out.writeUTF("Muteall");
					out.writeUTF("Valid");
					out.writeUTF(sendplayer);
					out.writeUTF(muteplayer);
					player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if(subchannel.equals("Unmuteall")) {
				String sendplayer = msgin.readUTF();
				String muteplayer = msgin.readUTF();
				String server = msgin.readUTF();
				Player mp = Bukkit.getPlayer(muteplayer);
				MineverseChatPlayer p = MineverseChatAPI.getMineverseChatPlayer(mp);
				if(mp == null) {
					try {
						out.writeUTF("Unmuteall");
						out.writeUTF("Player");
						out.writeUTF(sendplayer);
						out.writeUTF(muteplayer);
						out.writeUTF(server);
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				boolean bungee = false;
				for(ChatChannel c : ChatChannel.getChannels()) {
					p.removeMute(c.getName());
					if(c.getBungee()) {
						bungee = true;
					}
				}
				if(bungee) {
					MineverseChat.getInstance().synchronize(p, true);
				}
				if(p.isOnline()) {
					p.getPlayer().sendMessage(ChatColor.RED + "You have just been unmuted in all channels.");
				}
				else p.setModified(true);
				try {
					out.writeUTF("Unmuteall");
					out.writeUTF("Valid");
					out.writeUTF(sendplayer);
					out.writeUTF(muteplayer);
					player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if(subchannel.equals("Unmute")) {
				String sendplayer = msgin.readUTF();
				String mutePlayer = msgin.readUTF();
				String chatchannel = msgin.readUTF();
				String server = msgin.readUTF();
				MineverseChatPlayer p = MineverseChatAPI.getMineverseChatPlayer(mutePlayer);
				ChatChannel cc = ChatChannel.getChannel(chatchannel);
				if(cc == null) {
					try {
						out.writeUTF("Unmute");
						out.writeUTF("Channel");
						out.writeUTF(sendplayer);
						out.writeUTF(chatchannel);
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(p == null) {
					try {
						out.writeUTF("Unmute");
						out.writeUTF("Player");
						out.writeUTF(sendplayer);
						out.writeUTF(mutePlayer);
						out.writeUTF(server);
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				if(!p.isMuted(cc.getName())) {
					try {
						out.writeUTF("Unmute");
						out.writeUTF("Already");
						out.writeUTF(sendplayer);
						out.writeUTF(mutePlayer);
						out.writeUTF(cc.getName());
						out.writeUTF(cc.getColor());
						player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				p.removeMute(cc.getName());
				if(p.isOnline()) p.getPlayer().sendMessage(ChatColor.RED + "You have just been unmuted in: " + ChatColor.valueOf(cc.getColor().toUpperCase()) + cc.getName());
				else p.setModified(true);
				if(cc.getBungee()) {
					MineverseChat.getInstance().synchronize(p, true);
				}
				try {
					out.writeUTF("Unmute");
					out.writeUTF("Valid");
					out.writeUTF(sendplayer);
					out.writeUTF(mutePlayer);
					out.writeUTF(cc.getName());
					out.writeUTF(cc.getColor());
					player.sendPluginMessage(this, MineverseChat.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}