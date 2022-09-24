package de.jaskerx.main;


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
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import de.jaskerx.listeners.SelectMenuListener;
import net.dv8tion.jda.api.JDA;

public class MCFPSupportHelper
{
public static JDA builder;
public static String token = "";
public static String stopCmnd = "";
public static int höchstesTicket = 0;
private static int n = 0;
private static String fileTagsSplitter = "_&-_";
private static String fileArgsSplitter = "%&/&";
//private static Connection con;
	
    public static void main(String[] args) throws LoginException, IllegalArgumentException
    {
    	
    	//con = connectToDb();
    	readTokenFromConfig();
    	readStopCmndFromConfig();
    	readTicketNumberFromConfig();
		readTicketsFromConfig();
		Status.Start();
		//addTicketToDb(new Ticket("cr-id", "cat", "them", "creTime", "creDate", "clTime", "clDate", "cl-id"));
	}
    
    
    public static void log(Object obj) {
    	
    	String datum = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.now());
    	String uhrzeit = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now());
    	
    	System.out.println("[" + uhrzeit +", " + datum + "]   " + obj);
    }
    
    /*public static Connection connectToDb() {
    	
    	try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://127.0.0.1:3306/mcfp_support_helper";
			String username = "DiscordBot";
			String password = "dcdb";
			Class.forName(driver);
			
			return DriverManager.getConnection(url, username, password);
			
		} catch (Exception e) {e.printStackTrace();}
		
		return null;
    }
    
    public static void addTicketToDb(Ticket ticket) {
    	
    	try {
    		//Time time = new Time(Calendar.getInstance().getTimeInMillis());
    		//Date date = new Date(Calendar.getInstance().getTimeInMillis());
			PreparedStatement insertStatement = con.prepareStatement("INSERT INTO open_tickets (creatorId, category, thema, creationTime, creationDate, updateMessageId) VALUES ('" + ticket.creator + "', '" + ticket.category + "', '" + ticket.thema + "', '" + ticket.creationTime + "', '" + ticket.creationDate + "', '" + ticket.updateMessageId + "')");
			insertStatement.executeUpdate();
			
		} catch (Exception e) {e.printStackTrace();}
    }*/
    
    public static void readStopCmndFromConfig() {
		
		File file = new File("stopCmnd.txt");
    	FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			stopCmnd = br.readLine();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public static void readTokenFromConfig() {
		
		File file = new File("token.txt");
    	FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			token = br.readLine();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public static void readTicketNumberFromConfig() {
    	
    	File file = new File("ticketInfo.txt");
    	FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			höchstesTicket = Integer.valueOf(br.readLine().split("hoechstesTicket: ")[1]);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void refreshTicketNumberInConfig() {
    	
    	File file = new File("ticketInfo.txt");
    	FileWriter fw;
		try {
			fw = new FileWriter(file, false);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.flush();
			bw.write("hoechstesTicket: " + höchstesTicket);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void readTicketsFromConfig() {
    	
    	File file = new File("tickets.txt");
    	FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			List<String> lines = br.lines().collect(Collectors.toList());
			for (String line : lines) {
				System.out.println(line);
				SelectMenuListener.tickets.put(Integer.valueOf(line.split(fileTagsSplitter)[0]), new Ticket(
						line.split(fileTagsSplitter)[1].split(fileArgsSplitter)[0],
						line.split(fileTagsSplitter)[1].split(fileArgsSplitter)[1],
						line.split(fileTagsSplitter)[1].split(fileArgsSplitter)[2],
						line.split(fileTagsSplitter)[1].split(fileArgsSplitter)[3],
						line.split(fileTagsSplitter)[1].split(fileArgsSplitter)[4],
						line.split(fileTagsSplitter)[1].split(fileArgsSplitter)[5],
						line.split(fileTagsSplitter)[1].split(fileArgsSplitter)[6],
						line.split(fileTagsSplitter)[1].split(fileArgsSplitter)[7]));
				SelectMenuListener.tickets.get(Integer.valueOf(line.split(fileTagsSplitter)[0])).setNumber(Integer.valueOf(line.split(fileTagsSplitter)[0]));
				SelectMenuListener.tickets.get(Integer.valueOf(line.split(fileTagsSplitter)[0])).setUpdateMessageId(line.split(fileTagsSplitter)[1].split(fileArgsSplitter)[8]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public static void refreshTicketsInConfig() {
		
		File file = new File("tickets.txt");
		FileWriter fw;
		try {
			fw = new FileWriter(file, false);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.flush();
			SelectMenuListener.tickets.forEach((key, value) -> {
				if (n > 0) {
					try {
						bw.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					bw.write(key + fileTagsSplitter + value.creator + fileArgsSplitter + value.category + fileArgsSplitter + value.thema + fileArgsSplitter + value.creationTime + fileArgsSplitter + value.creationDate + fileArgsSplitter + value.closingTime + fileArgsSplitter + value.closingDate + fileArgsSplitter + value.closer + fileArgsSplitter + value.updateMessageId);
				} catch (IOException e) {
					e.printStackTrace();
				}
				n++;
			});
			bw.close();
			n = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
