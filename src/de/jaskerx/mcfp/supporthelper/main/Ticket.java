package de.jaskerx.main;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import net.dv8tion.jda.api.entities.Invite.Channel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Ticket {

	String creator;
	String category;
	String thema;
	String creationTime;
	String creationDate;
	String closingTime;
	String closingDate;
	String closer;
	String updateMessageId;
	int number;
	
	public Ticket (InfoMessage infoMessage) {
		
		creator = infoMessage.getCreator();
		category = infoMessage.getCategory();
		thema = infoMessage.getThema();
		creationDate = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.now());
		creationTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now());
	}
	
	public Ticket(String creator, String category, String thema, String creationTime, String creationDate, String closingTime, String closingDate, String closer) {
		
		this.creator = creator;
		this.category = category;
		this.thema = thema;
		this.creationTime = creationTime;
		this.creationDate = creationDate;
		this.closingTime = closingTime;
		this.closingDate = closingDate;
		this.closer = closer;
	}
	
	public void close(User closer) {
		closingDate = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.now());
		closingTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now());
		this.closer = closer.getId();
	}
	
	public MessageEmbed getClosingMessage() {

		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Ticket " + number + " wurde geschlossen");
		eb.addField("Kategorie", category, false);
		eb.addField("Genaues Thema", thema, false);
		eb.addField("Erstellt:", "von: " + MCFPSupportHelper.builder.getUserById(creator).getAsTag() + " / " + MCFPSupportHelper.builder.getUserById(creator).getId()
				+ "\num: " + creationTime + " Uhr"
				+ "\nam: " + creationDate, true);
		eb.addField("Geschlossen:", "von: " + MCFPSupportHelper.builder.getUserById(closer).getAsTag() + " / " + MCFPSupportHelper.builder.getUserById(closer).getId()
				+ "\num: " + closingTime + " Uhr"
				+ "\nam: " + closingDate, true);
		
		return eb.build();
	}
	
	public void sendClosingMessagePrivat() {

		MCFPSupportHelper.builder.getUserById(creator).openPrivateChannel().queue(channel ->
				channel.sendMessageEmbeds(getClosingMessage()).queue());
	}
	
	public void editUpdateMessage(MessageChannel channel) {
		
		channel.editMessageEmbedsById(updateMessageId, getClosingMessage()).queue();
	}
	
	
	public void setNumber(int number) {
		this.number = number;
	}
	public void setUpdateMessageId(String updateMessageId) {
		this.updateMessageId = updateMessageId;
		MCFPSupportHelper.refreshTicketsInConfig();
	}
	
	public String getCategory() {
		return category;
	}
	public String getThema() {
		return thema;
	}
	public String getUpdateMessageId() {
		return updateMessageId;
	}
	
}
