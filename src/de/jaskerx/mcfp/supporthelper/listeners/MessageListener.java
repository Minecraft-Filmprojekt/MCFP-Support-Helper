package de.jaskerx.mcfp.supporthelper.listeners;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.List;

import de.jaskerx.mcfp.supporthelper.main.InfoMessage;
import de.jaskerx.mcfp.supporthelper.main.MCFPSupportHelper;
import de.jaskerx.mcfp.supporthelper.main.Status;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

public class MessageListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		if (event.getChannelType().equals(ChannelType.TEXT)) {
			
			Member member = event.getMember();
			User user = event.getAuthor();
			String content = event.getMessage().getContentDisplay();
			TextChannel channel = event.getTextChannel();
			
			if (content.equals(MCFPSupportHelper.stopCmnd) && user.getId().equals("511297223901052949")) {
				Status.Stop();
			}
			
			if (content.equals("!close") && channel.getName().startsWith("ticket-") && Character.isDigit(channel.getName().split("ticket-")[1].charAt(0))) {
				MCFPSupportHelper.log("close");
				channel.delete().complete();
				closeTicket(Integer.valueOf(channel.getName().split("ticket-")[1]), user, event.getGuild());
			}
			
			if(content.equals("!ping")) {
				OffsetDateTime timeCreated = event.getMessage().getTimeCreated();
				channel.sendMessage("Pong!\n" + ChronoUnit.SECONDS.between(timeCreated, OffsetDateTime.now()) + "," + ChronoUnit.MILLIS.between(timeCreated, OffsetDateTime.now()) + " Sekunden").queue();
			}
		
		}
	}
	
	private void closeTicket(int ticketNum, User closer, Guild guild) {
		
		SelectMenuListener.tickets.get(ticketNum).close(closer);
		SelectMenuListener.tickets.get(ticketNum).sendClosingMessagePrivat();
		SelectMenuListener.tickets.get(ticketNum).editUpdateMessage();
		MCFPSupportHelper.deleteTicket(ticketNum);
		SelectMenuListener.tickets.remove(ticketNum);
	}
	
}
