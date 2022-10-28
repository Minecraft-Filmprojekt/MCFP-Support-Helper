package de.jaskerx.mcfp.supporthelper.main;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nullable;

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
	String channelId;
	int number;
	
	public Ticket (InfoMessage infoMessage) {
		
		creator = infoMessage.getCreator();
		category = infoMessage.getCategory();
		thema = infoMessage.getThema();
		creationDate = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.now());
		creationTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now());
		channelId = infoMessage.getChannel();
	}
	
	public Ticket(String creator, String category, String thema, String creationTime, String creationDate, String closingTime, String closingDate, String closer, @Nullable String updateMessageId, String channelId) {
		
		this.creator = creator;
		this.category = category;
		this.thema = thema;
		this.creationTime = creationTime;
		this.creationDate = creationDate;
		this.closingTime = closingTime;
		this.closingDate = closingDate;
		this.closer = closer;
		this.updateMessageId = updateMessageId;
		this.channelId = channelId;
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
	
	public void editUpdateMessage() {
		
		MCFPSupportHelper.builder.getGuildById(MCFPSupportHelper.guildId).getTextChannelById(channelId).editMessageEmbedsById(updateMessageId, getClosingMessage()).queue();
	}
	
	
	public void setNumber(int number) {
		this.number = number;
	}
	public void setUpdateMessageId(String updateMessageId) {
		this.updateMessageId = updateMessageId;
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
	
	public String getChannelId() {
		return channelId;
	}
	
}
