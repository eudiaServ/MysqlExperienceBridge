package net.craftersland.bridge.exp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import net.craftersland.bridge.exp.Exp;

public class DatabaseManagerMysql {
	
	public Connection conn = null;
	private String tableName = "meb_experience";
	
	// Hostname
		  private String dbHost;
		 
		  // Port -- Standard: 3306
		  private String dbPort;
		 
		  // Databankname
		  private String database;
		 
		  // Databank username
		  private String dbUser;
		 
		  // Databank password
		  private String dbPassword;

		private Exp exp;
		
		public DatabaseManagerMysql(Exp exp) {
			this.exp = exp;
			
			setupDatabase();
		}
		
		
		public boolean setupDatabase() {
			try {
	       	 	//Load Drivers
	            Class.forName("com.mysql.jdbc.Driver");
	            
	            dbHost = exp.getConfigHandler().getString("database.mysql.host");
	            dbPort = exp.getConfigHandler().getString("database.mysql.port");
	            database = exp.getConfigHandler().getString("database.mysql.databaseName");
	            dbUser = exp.getConfigHandler().getString("database.mysql.user");
	            dbPassword = exp.getConfigHandler().getString("database.mysql.password");
	            
	            String passFix = dbPassword.replaceAll("%", "%25");
	            String passFix2 = passFix.replaceAll("\\+", "%2B");
	            
	            //Connect to database
	            conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + database + "?" + "user=" + dbUser + "&" + "password=" + passFix2);
	           
	          } catch (ClassNotFoundException e) {
	        	  Exp.log.severe("Could not locate drivers for mysql!");
	            return false;
	          } catch (SQLException e) {
	        	  Exp.log.severe("Could not connect to mysql database!");
	            return false;
	          }
			
			//Create tables if needed
		      Statement query;
		      try {
		        query = conn.createStatement();
		        tableName = exp.getConfigHandler().getString("database.mysql.tableName");
		        
		        String accounts = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (id int(10) AUTO_INCREMENT, player_uuid varchar(50) NOT NULL UNIQUE, player_name varchar(50) NOT NULL, exp float(10,10) NOT NULL, exp_to_level int(10) NOT NULL, total_exp int(20) NOT NULL, exp_lvl int(10) NOT NULL, last_seen varchar(30) NOT NULL, PRIMARY KEY(id));";
		        query.executeUpdate(accounts);
		      } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		      }
		    Exp.log.info("Mysql has been set up!");
			return true;
		}
		
		public Connection getConnection() {
			checkConnection();
			return conn;
		}
		
		
		public boolean closeDatabase() {
			try {
				conn.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		public boolean checkConnection() {
			try {
				if (conn == null) {
					Exp.log.warning("Connection failed. Reconnecting...");
					if (reConnect() == true) return true;
					return false;
				}
				if (!conn.isValid(3)) {
					Exp.log.warning("Connection is idle or terminated. Reconnecting...");
					if (reConnect() == true) return true;
					return false;
				}
				if (conn.isClosed() == true) {
					Exp.log.warning("Connection is closed. Reconnecting...");
					if (reConnect() == true) return true;
					return false;
				}
				return true;
			} catch (Exception e) {
				Exp.log.severe("Could not reconnect to Database!");
			}
			return true;
		}
		
		public boolean reConnect() {
			try {
				dbHost = exp.getConfigHandler().getString("database.mysql.host");
	            dbPort = exp.getConfigHandler().getString("database.mysql.port");
	            database = exp.getConfigHandler().getString("database.mysql.databaseName");
	            dbUser = exp.getConfigHandler().getString("database.mysql.user");
	            dbPassword = exp.getConfigHandler().getString("database.mysql.password");
	            
	            String passFix = dbPassword.replaceAll("%", "%25");
	            String passFix2 = passFix.replaceAll("\\+", "%2B");
	            
	            long start = 0;
				long end = 0;
				
			    start = System.currentTimeMillis();
			    Exp.log.info("Attempting to establish a connection to the MySQL server!");
			    Class.forName("com.mysql.jdbc.Driver");
			    conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + database + "?" + "user=" + dbUser + "&" + "password=" + passFix2);
			    end = System.currentTimeMillis();
			    Exp.log.info("Connection to MySQL server established!");
			    Exp.log.info("Connection took " + ((end - start)) + "ms!");
	            return true;
			} catch (Exception e) {
				Exp.log.severe("Could not connect to MySQL server! because: " + e.getMessage());
				return false;
			}
		}

}
