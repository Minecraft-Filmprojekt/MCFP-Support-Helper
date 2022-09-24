package de.jaskerx.listeners;

import java.util.List;

import de.jaskerx.main.MCFPSupportHelper;
import de.jaskerx.main.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

public class ModalListener extends ListenerAdapter {

	private static boolean doesChannelExist = false;
	
	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		
		Guild guild = event.getGuild();
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
				channel.upsertPermissionOverride(guild.getRoleById("949647081872691250")).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
				channel.upsertPermissionOverride(guild.getRoleById("984047393366491156")).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
				channel.upsertPermissionOverride(event.getMember()).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
				channel.sendMessage("!close zum Schließen").queue();
				SelectMenuListener.messages.get(event.getUser()).setThema(event.getValue("anliegen").getAsString());
				channel.sendMessage(SelectMenuListener.messages.get(event.getUser()).getInfoMessage()).queue();
				event.reply("Dein Ticket wurde erstellt. Du findest es hier: " + guild.getTextChannelsByName(channelName, false).get(0).getAsMention()).setEphemeral(true).queue();
				SelectMenuListener.tickets.put(MCFPSupportHelper.höchstesTicket + 1, new Ticket(SelectMenuListener.messages.get(event.getUser())));
				SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket + 1).setNumber(MCFPSupportHelper.höchstesTicket + 1);
				doesChannelExist = false;
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Neues Ticket " + (MCFPSupportHelper.höchstesTicket + 1));
				eb.addField("Ersteller:", event.getUser().getAsTag() + " / " + event.getUser().getId(), false);
				eb.addField("Kanal:", channel.getAsMention(), false);
				eb.addField("Kategorie:", SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket + 1).getCategory(), false);
				eb.addField("Genaues Thema:", SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket + 1).getThema(), false);
				MessageBuilder mb;
				if (guild.getId().equals("949642462345973831")) { //MCFP
					mb = new MessageBuilder(guild.getRoleById("984047393366491156").getAsMention()).setEmbeds(eb.build());
				} else if (guild.getId().equals("904443699256242227")) {
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
				SelectMenuListener.messages.remove(event.getUser());
				MCFPSupportHelper.refreshTicketsInConfig();
				});
	}
	
}
