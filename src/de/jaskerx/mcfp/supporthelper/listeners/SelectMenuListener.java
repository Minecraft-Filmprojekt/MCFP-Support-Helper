package de.jaskerx.mcfp.supporthelper.listeners;

import java.awt.Menu;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.jaskerx.mcfp.supporthelper.main.InfoMessage;
import de.jaskerx.mcfp.supporthelper.main.MCFPSupportHelper;
import de.jaskerx.mcfp.supporthelper.main.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command.Option;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SelectMenuListener extends ListenerAdapter {
	
	public static HashMap<User, InfoMessage> messages = new HashMap<>();
	public static HashMap<Integer, Ticket> tickets = new HashMap<>();
	SelectOption opt11 = SelectOption.of("Probleme beim Connect/Error", "11");
	SelectOption opt12 = SelectOption.of("Allgemeine Fragen", "12");
	SelectOption opt13 = SelectOption.of("Entbannung", "13");
	SelectOption opt14 = SelectOption.of("Commands/Permissions", "14");
	SelectOption opt21 = SelectOption.of("Allgemein", "21");
	SelectOption opt22 = SelectOption.of("Voice chats", "22");
	SelectOption opt23 = SelectOption.of("Text chats", "23");
	SelectOption opt24 = SelectOption.of("Sonstige", "24");
	SelectOption opt31 = SelectOption.of("404/Errors", "31");
	SelectOption opt32 = SelectOption.of("Feedback", "32");
	SelectOption opt33 = SelectOption.of("Sonstige", "33");
	SelectOption[] options1 = new SelectOption[] {opt11, opt12, opt13, opt14};
	SelectOption[] options2 = new SelectOption[] {opt21, opt22, opt23, opt24};
	SelectOption[] options3 = new SelectOption[] {opt31, opt32, opt33};
	String cId;
	

	@Override
	public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
		
		SelectMenu menu = event.getComponent();
		
		if (menu.getId().equals("firstMenu")) {
			
			if (event.getSelectedOptions().size() != 0) {
				
				SelectMenu selMenu = SelectMenu.create("error").build();
				messages.put(event.getUser(), new InfoMessage());
				messages.get(event.getUser()).setCreator(event.getUser().getId());
				cId = MCFPSupportHelper.getTicketsInfo("updates-log");
				if(cId == null) {
					event.reply("Achtung! Es wurde kein Update-Channel festgelegt, bitte richte dich an ein Teammitglied!").setEphemeral(true).queue();
					return;
				}
				if(MCFPSupportHelper.getTicketsInfo("rolle") == null) {
					event.reply("Achtung! Es wurde keine Support-Rolle für Tickets festgelegt, bitte richte dich an ein Teammitglied!").setEphemeral(true).queue();
					return;
				}
				
				messages.get(event.getUser()).setChannel(cId);
				
				if (event.getSelectedOptions().get(0).getValue().equals("mc")) {
					
					selMenu = SelectMenu.create("menuMc")
							.setPlaceholder("Option(en) auswählen")
							.addOptions(opt11, opt12, opt13, opt14)
							.setRequiredRange(1, 4)
							.build();
					
					messages.get(event.getUser()).setCategory("Minecraft Server");
				} else if (event.getSelectedOptions().get(0).getValue().equals("dc")) {
					
					selMenu = SelectMenu.create("menuDc")
							.setPlaceholder("Option(en) auswählen")
							.addOptions(opt21, opt22, opt23, opt24)
							.setRequiredRange(1, 4)
							.build();
					
					messages.get(event.getUser()).setCategory("Discord Server");
				} else if (event.getSelectedOptions().get(0).getValue().equals("website")) {
					
					selMenu = SelectMenu.create("menuWebsite")
							.setPlaceholder("Option(en) auswählen")
							.addOptions(opt31, opt32, opt33)
							.setRequiredRange(1, 3)
							.build();
					
					messages.get(event.getUser()).setCategory("Website");
				} else {
					
					//sendModal(event.getId(), event.getToken());
					TextInput input = TextInput.create("anliegen", "Anliegen", TextInputStyle.PARAGRAPH)
								.setRequiredRange(1, 1024)
								.setPlaceholder("Bitte beschreibe dein Anliegen")
								.setRequired(true)
								.build();
					Modal modal = Modal.create("modal_sonstiges", "Ticket: Sonstiges")
								.addActionRow(input)
								.build();
					event.replyModal(modal).queue();
					messages.get(event.getUser()).setCategory("Sonstiges");
						
					return;
				}
				ActionRow r1 = ActionRow.of(selMenu);
				event.reply(new MessageBuilder("Bitte grenze das Thema weiter ein.").setActionRows(r1).build()).setEphemeral(true).queue();
			}
		} else {
			
			event.deferReply(true).queue();
			int[] options = new int[event.getSelectedOptions().size()];
			//if (menu.getId().equals("menuMc")) {
				String thema = "";
				for (int i = 0; i < event.getSelectedOptions().size(); i++) {
					options[i] = Integer.valueOf(event.getSelectedOptions().get(i).getValue());
					if (thema != "") {
						thema += ", ";
					}
					thema += event.getSelectedOptions().get(i).getLabel();
				}
				messages.get(event.getUser()).setThema(thema);
				createChannel(options, event);
		}
		
	}
	
	private void createChannel(int options[], SelectMenuInteractionEvent event) {
		
		Guild guild = event.getGuild();
		
		guild.getTextChannelById(cId).getParentCategory().createTextChannel("ticket-" + (MCFPSupportHelper.höchstesTicket + 1)).queue(channel -> {
			channel.upsertPermissionOverride(guild.getRoleById("949642462345973831")).deny(Permission.ALL_PERMISSIONS).queue();
			channel.upsertPermissionOverride(guild.getRoleById("950346767239643186")).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.MESSAGE_MANAGE).queue();
			channel.upsertPermissionOverride(guild.getRoleById("949643338401857576")).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
			channel.upsertPermissionOverride(guild.getRoleById("949647081872691250")).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
			channel.upsertPermissionOverride(guild.getRoleById("984047393366491156")).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
			channel.upsertPermissionOverride(event.getMember()).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
			channel.sendMessage("!close zum Schließen").queue();
			channel.sendMessage(messages.get(event.getUser()).getInfoMessage()).queue();
			event.getHook().editOriginal("Dein Ticket wurde erstellt. Du findest es hier: " + channel.getAsMention()).queue();
			tickets.put(MCFPSupportHelper.höchstesTicket + 1, new Ticket(messages.get(event.getUser())));
			tickets.get(MCFPSupportHelper.höchstesTicket + 1).setNumber(MCFPSupportHelper.höchstesTicket + 1);

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Neues Ticket " + (MCFPSupportHelper.höchstesTicket + 1));
			eb.addField("Ersteller:", event.getUser().getAsTag() + " / " + event.getUser().getId(), false);
			eb.addField("Kanal:", channel.getAsMention(), false);
			eb.addField("Kategorie:", tickets.get(MCFPSupportHelper.höchstesTicket + 1).getCategory(), false);
			eb.addField("Genaues Thema:", tickets.get(MCFPSupportHelper.höchstesTicket + 1).getThema(), false);
			MessageBuilder mb = new MessageBuilder(guild.getRoleById(MCFPSupportHelper.getTicketsInfo("rolle")).getAsMention()).setEmbeds(eb.build());
			
			guild.getTextChannelById(cId).sendMessage(mb.build()).queue((Message mes) -> {
				MCFPSupportHelper.höchstesTicket++;
				SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket).setUpdateMessageId(mes.getId());
				MCFPSupportHelper.addTicketToDb(SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket));});
			
			messages.remove(event.getUser());
		});
	}
	
	/*private void sendModal(String id, String token) {
		
		String response = "";
		JSONObject responseContent = new JSONObject(
				"{\"type\": 9," +
				"\"data\": {" +
				  "\"title\": \"Ticket: Sonstiges\"," +
				  "\"custom_id\": \"modal_sonstiges\"," +
				  "\"components\": [{" +
				    "\"type\": 1," +
				    "\"components\": [{" +
				      "\"type\": 4," +
				      "\"custom_id\": \"anliegen\"," +
				      "\"label\": \"Anliegen\"," +
				      "\"style\": 1," +
				      "\"min_length\": 1," +
				      "\"max_length\": 4000," +
				      "\"placeholder\": \"Bitte beschreibe dein Anliegen\"," +
				      "\"required\": true" +
				    "}]" +
				  "}]" +
				"}}");
		OkHttpClient client = new OkHttpClient();
		
		Request request = new Request.Builder()
	    		.url("https://discord.com/api/v10/interactions/" + id + "/" + token + "/callback")
	    		.addHeader("Content-Type", "application/json")
	    		.addHeader("Authorization", "Bot " + MCFPSupportHelper.token)
	    		.post(RequestBody.create(responseContent.toString(), MediaType.parse("application/json, charset=utf-8")))
	    		.build();
	
		
		try (Response responsePost = client.newCall(request).execute()) {
	        response = responsePost.body().string();
	    } catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}*/
	
}
