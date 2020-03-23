package mineverse.Aust1n46.chat.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.plugin.Plugin;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;

//This class initializes the plugins connection to the MySQL database if it's enabled.
public class MySQL extends Database {
	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;

	private static GenericObjectPool pool = null;
	private static DataSource ds = null;

	public MySQL(Plugin plugin, String hostname, String port, String database, String username, String password) {
		super(plugin);
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
	}

	private String getUrl() {
		return "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true";
	}

	@Override
	public Connection openConnection() throws SQLException, ClassNotFoundException {
		// Creates an Instance of GenericObjectPool That Holds Our Pool of Connections Object!
		pool = new GenericObjectPool();
		pool.setMaxActive(4);

		// Creates a ConnectionFactory Object Which Will Be Use by the Pool to Create the Connection Object!
		ConnectionFactory cf = new DriverManagerConnectionFactory(getUrl(), user, password);

		// Creates a PoolableConnectionFactory That Will Wraps the Connection Object Created by the ConnectionFactory to Add Object Pooling Functionality!
		PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, pool, null, null, false, true);
		ds = new PoolingDataSource(pool);

		return ds.getConnection();
	}

	@Override
	public boolean checkConnection() throws Exception {
		try {
			pool.borrowObject();
		} catch (IllegalStateException e) {
			return false;
		}
		return true;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	@Override
	public boolean closeConnection() throws Exception {
		if (!checkConnection()) return false;
		pool.close();
		return true;
	}

	@Override
	public ResultSet querySQL(String query) throws SQLException {
		Statement statement = getConnection().createStatement();
		return statement.executeQuery(query);
	}

	@Override
	public int updateSQL(String query) throws SQLException {
		Statement statement = getConnection().createStatement();
		return statement.executeUpdate(query);
	}
}