package de.jaskerx.listeners;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import de.jaskerx.main.MCFPSupportHelper;
import de.jaskerx.main.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RawGatewayListener extends ListenerAdapter {

	private static boolean doesChannelExist = false;
	
	@Override
	public void onRawGateway(RawGatewayEvent event) {
		
		int type = -1;
		
		JSONObject interactionPackage = new JSONObject(event.getPackage().toString());
		
		if (event.getType().equals("INTERACTION_CREATE")) {
			type = interactionPackage.getJSONObject("d").getInt("type");
		}
			
		if (type == 5) {
			
			String id = interactionPackage.getJSONObject("d").getString("id");
			String token = interactionPackage.getJSONObject("d").getString("token");
			System.out.println(interactionPackage);
			System.out.println(interactionPackage.getJSONObject("d").getJSONObject("member").getJSONObject("user").getString("id"));
			User user = MCFPSupportHelper.builder.getUserById(interactionPackage.getJSONObject("d").getJSONObject("member").getJSONObject("user").getString("id"));
			Guild guild = MCFPSupportHelper.builder.getGuildById(interactionPackage.getJSONObject("d").getString("guild_id"));
			Member member = guild.getMember(user);
			String inputValue = interactionPackage.getJSONObject("d").getJSONObject("data").getJSONArray("components").getJSONObject(0).getJSONArray("components").getJSONObject(0).getString("value");
			MCFPSupportHelper.log(inputValue);
			SelectMenuListener.messages.get(user).setThema(inputValue);
			
			boolean doesCatgoryExist = false;
			
			for (Category category : guild.getCategories()) {
				if (category.getName().equals("tickets")) {
					doesCatgoryExist = true;
				}
			}
			if (!doesCatgoryExist) {
				guild.createCategory("tickets").complete();
			}
			
			List<Category> cats = guild.getCategoriesByName("tickets", false);
			
			String channelName = "ticket-" + (MCFPSupportHelper.höchstesTicket + 1);
			cats.get(0).createTextChannel(channelName).queue(channel -> {
				channel.upsertPermissionOverride(member).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
				channel.sendMessage("!close zum Schließen").queue();
				channel.sendMessage(SelectMenuListener.messages.get(user).getInfoMessage()).queue();
				
				String response = "...";
				
				OkHttpClient client = new OkHttpClient();
				
				JSONObject array = new JSONObject(
								"{\"type\": 4,\"data\": {"
								+ "\"flags\": 64,"
								+ "\"content\": \"Dein Ticket wurde erstellt. Du findest es hier: <#" + channel.getId() + ">\""
								+ "}}");
				
					
				Request request = new Request.Builder()
		        		.url("https://discord.com/api/v10/interactions/" + id + "/" + token + "/callback")
		        		.addHeader("Authorization", "Bot " + MCFPSupportHelper.token)
		        		.addHeader("Content-Type", "application/json")
		        		.post(RequestBody.create(array.toString(), MediaType.parse("application/json, charset=utf-8")))
		        		.build();
				
				try (Response responsePost = client.newCall(request).execute()) {
		            response = responsePost.body().string();
		        } catch (IOException e) {
					
					e.printStackTrace();
				}
				
				MCFPSupportHelper.log("response: " + response);
				
				SelectMenuListener.tickets.put(MCFPSupportHelper.höchstesTicket + 1, new Ticket(SelectMenuListener.messages.get(user)));
				SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket + 1).setNumber(MCFPSupportHelper.höchstesTicket + 1);
				doesChannelExist = false;
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Neues Ticket " + (MCFPSupportHelper.höchstesTicket + 1));
				eb.addField("Ersteller:", user.getAsTag() + " / " + user.getId(), false);
				eb.addField("Kanal:", channel.getAsMention(), false);
				eb.addField("Kategorie:", SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket + 1).getCategory(), false);
				eb.addField("Genaues Thema:", SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket + 1).getThema(), false);
				MessageBuilder mb;
				if (guild.getId().equals("949642462345973831")) { //MCFP
					mb = new MessageBuilder(guild.getRoleById("950704578091958303").getAsMention()).setEmbeds(eb.build());
				} else if (guild.getId().equals("904443699256242227")) { //SvVV2
					mb = new MessageBuilder(guild.getRoleById("951876302728753155").getAsMention()).setEmbeds(eb.build());
				} else {
					mb = new MessageBuilder(guild.getRoleById("758029929328672848").getAsMention()).setEmbeds(eb.build());
				}
				guild.getCategoriesByName("tickets", false).get(0).getTextChannels().forEach((TextChannel t) -> {
						if (t.getName().equals("ticket-updates")) {
							doesChannelExist = true;
							t.sendMessage(mb.build()).queue((Message mes) -> {
								MCFPSupportHelper.höchstesTicket++;
								MCFPSupportHelper.refreshTicketNumberInConfig();
								SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket).setUpdateMessageId(mes.getId());});
						}
						});
				if (!doesChannelExist) {
					guild.getCategoriesByName("tickets", false).get(0).createTextChannel("ticket-updates").queue(c -> {
							c.sendMessage(mb.build()).queue((Message mes) -> {
								MCFPSupportHelper.höchstesTicket++;
								MCFPSupportHelper.refreshTicketNumberInConfig();
								SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket).setUpdateMessageId(mes.getId());});});
				}
				SelectMenuListener.messages.remove(user);
				MCFPSupportHelper.refreshTicketsInConfig();
				});
			
			
			
		}
	}
	
}
