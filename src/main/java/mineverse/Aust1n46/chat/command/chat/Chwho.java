package mineverse.Aust1n46.chat.command.chat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.massivecraft.factions.entity.MPlayer;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Chwho extends MineverseCommand {
	private MineverseChat plugin = MineverseChat.getInstance();
	
	public Chwho(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		String playerlist = "";
		if(sender.hasPermission("venturechat.chwho")) {
			if(args.length > 0) {
				ChatChannel channel = ChatChannel.getChannel(args[0]);
				if(channel != null) {
					if(channel.hasPermission()) {
						if(!sender.hasPermission(channel.getPermission())) {
							MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(((Player) sender));
							mcp.removeListening(channel.getName());
							mcp.getPlayer().sendMessage(LocalizedMessage.CHANNEL_NO_PERMISSION_VIEW.toString());
							return;
						}
					}
					
					if(channel.getBungee() && sender instanceof Player) {
						MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
						ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
						DataOutputStream out = new DataOutputStream(byteOutStream);
						try {
							out.writeUTF("Chwho");
							out.writeUTF("Get");
							out.writeUTF(mcp.getUUID().toString());
							out.writeUTF(mcp.getName());
							out.writeUTF(channel.getName());
							mcp.getPlayer().sendPluginMessage(plugin, MineverseChat.PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
							out.close();
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						return;
					}
					
					PluginManager pluginManager = plugin.getServer().getPluginManager();
					long linecount = plugin.getLineLength();
					for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
						if(p.getListening().contains(channel.getName())) {
							if(sender instanceof Player) {
								if(!((Player) sender).canSee(p.getPlayer())) {
									continue;
								}
							}
							if(channel.hasDistance() && sender instanceof Player) {
								if(!this.isPlayerWithinDistance((Player) sender, p.getPlayer(), channel.getDistance())) {
									continue;
								}
							}
							if(pluginManager.isPluginEnabled("Towny") && sender instanceof Player) {
								try {
									Resident r = TownyUniverse.getDataSource().getResident(p.getName());
									Resident pp = TownyUniverse.getDataSource().getResident(((Player) sender).getName());
									if(channel.getName().equalsIgnoreCase("Town")) {
										if(!pp.hasTown()) {
											if(playerlist.length() + p.getName().length() > linecount) {
												playerlist += "\n";
												linecount = linecount + plugin.getLineLength();
											}
											if(!p.isMuted(channel.getName())) {
												playerlist += ChatColor.WHITE + p.getName();
											}
											else {
												playerlist += ChatColor.RED + p.getName();
											}
											playerlist += ChatColor.WHITE + ", ";
											break;
										}
										else if(!r.hasTown()) {
											continue;
										}
										else if(!(r.getTown().getName().equals(pp.getTown().getName()))) {
											continue;
										}
									}
									if(channel.getName().equalsIgnoreCase("Nation")) {
										if(!pp.hasNation()) {
											if(playerlist.length() + p.getName().length() > linecount) {
												playerlist += "\n";
												linecount = linecount + plugin.getLineLength();
											}
											if(!p.isMuted(channel.getName())) {
												playerlist += ChatColor.WHITE + p.getName();
											}
											else {
												playerlist += ChatColor.RED + p.getName();
											}
											playerlist += ChatColor.WHITE + ", ";
											break;
										}
										else if(!r.hasNation()) {
											continue;
										}
										else if(!(r.getTown().getNation().getName().equals(pp.getTown().getNation().getName()))) {
											continue;
										}
									}
								}
								catch(Exception ex) {
									ex.printStackTrace();
								}
							}
							if(pluginManager.isPluginEnabled("Factions") && sender instanceof Player) {
								try {
									MPlayer mplayer = MPlayer.get(p.getPlayer());
									MPlayer mplayerp = MPlayer.get((Player) sender);
									if(channel.getName().equalsIgnoreCase("Faction")) {
										if(!mplayerp.hasFaction()) {
											if(playerlist.length() + p.getName().length() > linecount) {
												playerlist += "\n";
												linecount = linecount + plugin.getLineLength();
											}
											if(!p.isMuted(channel.getName())) {
												playerlist += ChatColor.WHITE + p.getName();
											}
											else {
												playerlist += ChatColor.RED + p.getName();
											}
											playerlist += ChatColor.WHITE + ", ";
											break;
										}
										else if(!mplayerp.hasFaction()) {
											continue;
										}
										else if(!(mplayer.getFactionName().equals(mplayerp.getFactionName()))) {
											continue;
										}
									}
								}
								catch(Exception ex) {
									ex.printStackTrace();
								}
							}
							if(playerlist.length() + p.getName().length() > linecount) {
								playerlist += "\n";
								linecount = linecount + plugin.getLineLength();
							}
							if(!p.isMuted(channel.getName())) {
								playerlist += ChatColor.WHITE + p.getName();
							}
							else {
								playerlist += ChatColor.RED + p.getName();
							}
							playerlist += ChatColor.WHITE + ", ";
						}
					}
					if(playerlist.length() > 2) {
						playerlist = playerlist.substring(0, playerlist.length() - 2);
					}
					sender.sendMessage(LocalizedMessage.CHANNEL_PLAYER_LIST_HEADER.toString()
							.replace("{channel_color}", (ChatColor.valueOf(channel.getColor().toUpperCase())).toString())
							.replace("{channel_name}", channel.getName()));
					sender.sendMessage(playerlist);
					return;
				}
				else {
					sender.sendMessage(LocalizedMessage.INVALID_CHANNEL.toString()
							.replace("{args}", args[0]));
					return;
				}
			}
			else {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
						.replace("{command}", "/chwho")
						.replace("{args}", "[channel]"));
				return;
			}
		}
		else {
			sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
			return;
		}
	}

	private boolean isPlayerWithinDistance(Player p1, Player p2, double Distance) {
		Double chDistance = Distance;
		Location locreceip;
		Location locsender = p1.getLocation();
		Location diff;
		if(chDistance > (double) 0) {
			locreceip = p2.getLocation();
			if(locreceip.getWorld() == p1.getWorld()) {
				diff = locreceip.subtract(locsender);
				if(Math.abs(diff.getX()) > chDistance || Math.abs(diff.getZ()) > chDistance) {
					return false;
				}
			}
			else {
				return false;
			}
		}
		return true;
	}
}