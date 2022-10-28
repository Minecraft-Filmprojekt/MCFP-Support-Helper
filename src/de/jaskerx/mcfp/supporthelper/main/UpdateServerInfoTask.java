package de.jaskerx.mcfp.supporthelper.main;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.TimerTask;

import org.json.JSONObject;
import org.json.JSONTokener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class UpdateServerInfoTask extends TimerTask{

	@Override
	public void run() {
		try {
			
			char[] buffer = new char[2048];
			HttpURLConnection httpcon = (HttpURLConnection) new URL("https://api.mcsrvstat.us/2/mcfp.gq").openConnection();
			httpcon.setRequestMethod("GET");
			InputStreamReader reader = new InputStreamReader(httpcon.getInputStream());
			reader.read(buffer);
			reader.close();
			System.out.println(new String(buffer));
			System.out.println(new String(buffer).trim());
			if(!new String(buffer).trim().endsWith("}") || MCFPSupportHelper.builder.getTextChannelById("1014099220531265606") == null) return;
			
			JSONObject obj = new JSONObject(new String(buffer).trim());
			
			String status = obj.getBoolean("online") ? "online" : "offline";
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Server Infos");
			eb.addBlankField(false);
			eb.addField("IP", "mcfp.gq", true);
			eb.addBlankField(true);
			eb.addField("Status", status, true);
			
			if(obj.getBoolean("online")) {
				String players = obj.getJSONObject("players").getInt("online") + "/" + obj.getJSONObject("players").getInt("max");
				String version = obj.getString("version");
				
				eb.addBlankField(false);
				eb.addField("Version", version, true);
				eb.addBlankField(true);
				eb.addField("Spieler", players, true);
			}
			
			System.out.println(MCFPSupportHelper.builder);
			System.out.println(MCFPSupportHelper.builder.getTextChannelById("1014099220531265606"));
			System.out.println(MCFPSupportHelper.builder.getTextChannelById("1014099220531265606").retrievePinnedMessages().complete());
			List<Message> pinnedMessages = MCFPSupportHelper.builder.getTextChannelById("1014099220531265606").retrievePinnedMessages().complete();
			if(pinnedMessages.size() == 0) {
				MCFPSupportHelper.builder.getTextChannelById("1014099220531265606").sendMessageEmbeds(eb.build()).queue(mes -> mes.pin().queue());
			} else {
				pinnedMessages.get(0).editMessageEmbeds(eb.build()).queue();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}