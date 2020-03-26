package mineverse.Aust1n46.chat.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;

import com.zaxxer.hikari.HikariDataSource;

import mineverse.Aust1n46.chat.MineverseChat;

//Parent class for both the MySQL and SQLite database classes.
public abstract class Database {

	protected HikariDataSource dataSource = null;

	public abstract void init();

	public void writeVentureChat(String time, String uuid, String name, String server, String channel, String text, String type) {
		final MineverseChat plugin = MineverseChat.getInstance();
		final String SQL_INSERT_VENTURE_CHAT = "INSERT INTO VentureChat " +
			"(ChatTime, UUID, Name, Server, Channel, Text, Type) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?)";
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try (final Connection conn = dataSource.getConnection();
				 final PreparedStatement statement = conn.prepareStatement(SQL_INSERT_VENTURE_CHAT)) {
				statement.setString(1, time);
				statement.setString(2, uuid);
				statement.setString(3, name);
				statement.setString(4, server);
				statement.setString(5, channel);
				statement.setString(6, text);
				statement.setString(7, type);
				statement.executeUpdate();
			} catch(SQLException e) {
				throw new RuntimeException(e);
			}
		});
	}
}