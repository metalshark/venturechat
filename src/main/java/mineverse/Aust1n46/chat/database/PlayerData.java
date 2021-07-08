package mineverse.Aust1n46.chat.database;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

//This class writes player data to the PlayerData.yml file in preparation for saving and shutting down the server.
public class PlayerData {
	private static FileConfiguration playerData;
	private static File playerDataFile;
	private static MineverseChat plugin;
	
	public static void initialize() {
		plugin = MineverseChat.getInstance();
		playerDataFile = new File(plugin.getDataFolder().getAbsolutePath(), "Players.yml");
		if(!playerDataFile.isFile()) {
			plugin.saveResource("Players.yml", true);		
		}
		playerData = YamlConfiguration.loadConfiguration(playerDataFile);
	}
	
	public static FileConfiguration getPlayerData() {		
		return playerData;
	}
	
	public static void savePlayerData() {
		try {
			for(MineverseChatPlayer p : MineverseChat.players) {
				if(p.wasModified() || p.isOnline()) {
					ConfigurationSection cs = playerData.getConfigurationSection("players." + p.getUUID().toString());
					String nickname = p.getNickname();
					if(cs == null) {
						ConfigurationSection ps = playerData.getConfigurationSection("players");
						if(ps == null) {
							cs = playerData.createSection("players");
						}
						cs = playerData.createSection("players." + p.getUUID().toString());
					}
					cs.set("name", p.getName());
					cs.set("current", p.getCurrentChannel().getName());
					String ignores = "";
					for(UUID s : p.getIgnores()) {
						ignores += s.toString() + ",";
					}
					cs.set("ignores", ignores);
					String listening = "";
					for(String channel : p.getListening()) {
						ChatChannel c = ChatChannel.getChannel(channel);
						listening += c.getName() + ",";
					}
					String mutes = "";
					for(String channel : p.getMutes().keySet()) {		
						ChatChannel c = ChatChannel.getChannel(channel);
						mutes += c.getName() + ":" + p.getMutes().get(c.getName()) + ",";
					}
					String blockedCommands = "";
					for(String s : p.getBlockedCommands()) {
						blockedCommands += s + ",";
					}
					if(listening.length() > 0) {
						listening = listening.substring(0, listening.length() - 1);
					}
					cs.set("listen", listening);
					if(mutes.length() > 0) {
						mutes = mutes.substring(0, mutes.length() - 1);
					}
					cs.set("mutes", mutes);
					if(blockedCommands.length() > 0) {
						blockedCommands = blockedCommands.substring(0, blockedCommands.length() - 1);
					}
					cs.set("blockedcommands", blockedCommands);
					cs.set("host", p.isHost());
					cs.set("party", p.hasParty() ? p.getParty().toString() : "");
					cs.set("filter", p.hasFilter());
					cs.set("notifications", p.hasNotifications());
					cs.set("nickname", nickname);
					cs.set("spy", p.isSpy());
					cs.set("commandspy", p.hasCommandSpy());
					cs.set("rangedspy", p.getRangedSpy());
					cs.set("messagetoggle", p.getMessageToggle());
					cs.set("bungeetoggle", p.getBungeeToggle());
					Calendar currentDate = Calendar.getInstance();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss");
					String dateNow = formatter.format(currentDate.getTime());
					cs.set("date", dateNow);
					p.setModified(false);
				}
			}
			playerData.save(playerDataFile);
		}
		catch(IOException e) {			
			e.printStackTrace();
		}
	}
}