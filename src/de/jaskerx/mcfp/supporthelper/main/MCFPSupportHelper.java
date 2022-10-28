package de.jaskerx.mcfp.supporthelper.main;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import de.jaskerx.mcfp.supporthelper.listeners.SelectMenuListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class MCFPSupportHelper
{
public static JDA builder;
public static String token = "";
public static String stopCmnd = "";
public static int höchstesTicket = 0;
private static Connection con;
private static String url;
private static String username;
private static String password;
public static String guildId;
	

    public static void main(String[] args) throws LoginException, IllegalArgumentException
    {
    	
    	readConfig();
    	con = connectToDb();
    	loadTickets();
		
		Status.Start();
	}
    
    
    public static void log(Object obj) {
    	
    	String datum = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.now());
    	String uhrzeit = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now());
    	
    	System.out.println("[" + uhrzeit +", " + datum + "]   " + obj);
    }
    
    private static void readConfig() {
		
		try(FileReader reader = new FileReader("config_mcfp.config")) {
			Properties properties = new Properties();
			properties.load(reader);
			
			token = properties.getProperty("token");
			stopCmnd = properties.getProperty("stopCommand");
			url = properties.getProperty("db_url");
			username = properties.getProperty("db_username");
			password = properties.getProperty("db_password");
			guildId = properties.getProperty("guildId");
			
		} catch(Exception e) {e.printStackTrace();}
	}
    
    
    public static Connection connectToDb() {
    	
    	try {	
    		Class.forName("org.mariadb.jdbc.Driver");
			
			return DriverManager.getConnection(url, username, password);
			
		} catch (Exception e) {e.printStackTrace();}
		
		return null;
    }
    
    private static void loadTickets() {
    	
    	try {			
			Statement insertStatement = con.createStatement();
			ResultSet rs = insertStatement.executeQuery("SELECT * FROM open_tickets");
			while(rs.next()) {
				Timestamp ts = rs.getTimestamp("creationTimestamp");
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(ts.getTime());
				String time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
				String date = c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);
				Ticket t = new Ticket(rs.getString("creatorId"), rs.getString("category"), rs.getString("thema"), time, date, "", "", "", rs.getString("updateMessageId"), rs.getString("channelId"));
				t.setNumber(rs.getInt(1));
				SelectMenuListener.tickets.put(rs.getInt(1), t);
			}
			
			String[] urlSplit = url.split("/");
			insertStatement = con.createStatement();
			rs = insertStatement.executeQuery("SELECT `AUTO_INCREMENT` FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='" + urlSplit[urlSplit.length - 1] + "' AND TABLE_NAME='open_tickets'");
			if(rs.next()) höchstesTicket = rs.getInt(1) - 1;
			
		} catch (Exception e) {e.printStackTrace();}
    }
    
    public static void addTicketToDb(Ticket ticket) {
    	
    	try {
    		Calendar c = Calendar.getInstance(Locale.GERMAN);
    		int[] date = new int[3];
    		int[] time = new int[3];
    		for(int i = 0; i < date.length; i++) {
    			date[i] = Integer.valueOf(ticket.creationDate.split("\\.")[i]);
    		}
    		for(int i = 0; i < time.length; i++) {
    			time[i] = Integer.valueOf(ticket.creationTime.split(":")[i]);
    		}
    		c.set(date[2], (date[1] +11) % 12, date[0], time[0], time[1], time[2]);
    		
			PreparedStatement insertStatement = con.prepareStatement("INSERT INTO open_tickets (creatorId, category, thema, creationTimestamp, updateMessageId, channelId) VALUES ('" + ticket.creator + "', '" + ticket.category + "', '" + ticket.thema + "', '" + /*ticket.creationTime + "', '" + ticket.creationDate*/ new Timestamp(c.getTimeInMillis()) + "', '" + ticket.updateMessageId + "', '" + ticket.channelId + "')", Statement.RETURN_GENERATED_KEYS);
			insertStatement.executeUpdate();
			
			ResultSet rs = insertStatement.getGeneratedKeys();
			if(rs.next()) {
				höchstesTicket = (rs.getInt(1));
			}
			
		} catch (Exception e) {e.printStackTrace();}
    }
    
    public static void deleteTicket(int id) {
    	
    	try {
			PreparedStatement insertStatement = con.prepareStatement("DELETE FROM open_tickets WHERE id=" + id);
			insertStatement.executeUpdate();
			
		} catch (Exception e) {e.printStackTrace();}
    }
    
    public static void setTicketInfo(SlashCommandInteraction event, String id, String value) {
    	
    	try {
			PreparedStatement insertStatement = con.prepareStatement("REPLACE INTO tickets_info (id, value) VALUES ('" + id + "', '" + value + "')", Statement.RETURN_GENERATED_KEYS);
			
			if(insertStatement.executeUpdate() != 0) {
				event.getHook().editOriginal(id + " wurde auf " + value + " gesetzt.").queue();
			}
			
		} catch (Exception e) {e.printStackTrace();}
    }
    
    public static String getTicketsInfo(String id) {
    	
    	try {			
			Statement insertStatement = con.createStatement();
			ResultSet rs = insertStatement.executeQuery("SELECT value FROM tickets_info WHERE id='" + id + "'");
			if(rs.next()) {
				return rs.getString("value");
			}
			
		} catch (Exception e) {e.printStackTrace();}
    	
    	return null;
    }
    
}
