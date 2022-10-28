package de.jaskerx.mcfp.supporthelper.listeners;

import java.util.List;

import de.jaskerx.mcfp.supporthelper.main.MCFPSupportHelper;
import de.jaskerx.mcfp.supporthelper.main.Ticket;
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
	
	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		
		Guild guild = event.getGuild();
		String cId = MCFPSupportHelper.getTicketsInfo("updates-log");
		
		guild.getTextChannelById(cId).getParentCategory().createTextChannel("ticket-" + (MCFPSupportHelper.höchstesTicket + 1)).queue(channel -> {
			channel.upsertPermissionOverride(guild.getRoleById("949642462345973831")).deny(Permission.ALL_PERMISSIONS).queue();	
			channel.upsertPermissionOverride(guild.getRoleById("950346767239643186")).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.MESSAGE_MANAGE).queue();
			channel.upsertPermissionOverride(guild.getRoleById("949643338401857576")).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
			channel.upsertPermissionOverride(guild.getRoleById("949647081872691250")).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
			channel.upsertPermissionOverride(guild.getRoleById("984047393366491156")).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
			channel.upsertPermissionOverride(event.getMember()).deny(Permission.ALL_PERMISSIONS).setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY).queue();
			channel.sendMessage("!close zum Schließen").queue();
			SelectMenuListener.messages.get(event.getUser()).setThema(event.getValue("anliegen").getAsString());
			channel.sendMessage(SelectMenuListener.messages.get(event.getUser()).getInfoMessage()).queue();
			event.reply("Dein Ticket wurde erstellt. Du findest es hier: " + channel.getAsMention()).setEphemeral(true).queue();
			SelectMenuListener.tickets.put(MCFPSupportHelper.höchstesTicket + 1, new Ticket(SelectMenuListener.messages.get(event.getUser())));
			SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket + 1).setNumber(MCFPSupportHelper.höchstesTicket + 1);

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Neues Ticket " + (MCFPSupportHelper.höchstesTicket + 1));
			eb.addField("Ersteller:", event.getUser().getAsTag() + " / " + event.getUser().getId(), false);
			eb.addField("Kanal:", channel.getAsMention(), false);
			eb.addField("Kategorie:", SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket + 1).getCategory(), false);
			eb.addField("Genaues Thema:", SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket + 1).getThema(), false);
			MessageBuilder mb = new MessageBuilder(guild.getRoleById(MCFPSupportHelper.getTicketsInfo("rolle")).getAsMention()).setEmbeds(eb.build());
			
			guild.getTextChannelById(cId).sendMessage(mb.build()).queue((Message mes) -> {
				MCFPSupportHelper.höchstesTicket++;
				SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket).setUpdateMessageId(mes.getId());
				MCFPSupportHelper.addTicketToDb(SelectMenuListener.tickets.get(MCFPSupportHelper.höchstesTicket));
			});
			
			SelectMenuListener.messages.remove(event.getUser());
		});
	}
	
}
