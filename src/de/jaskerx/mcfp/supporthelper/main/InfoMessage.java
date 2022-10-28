package de.jaskerx.mcfp.supporthelper.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

public class InfoMessage {
	
	String creator;
	String category;
	String thema;
	String channel;
	
	public Message getInfoMessage() {
		
		MessageBuilder mes = new MessageBuilder();
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Informationen über das Ticket");
		eb.addField("Ersteller", MCFPSupportHelper.builder.getUserById(creator).getAsTag(), false);
		eb.addField("Kategorie", category, false);
		eb.addField("Genaues Thema", thema, false);
		
		mes.setEmbeds(eb.build());
		
		return mes.build();
	}
	
	public void setCreator(String id) {
		creator = id;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public void setThema(String thema) {
		this.thema = thema;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public String getCreator() {
		return creator;
	}
	public String getCategory() {
		return category;
	}
	public String getThema() {
		return thema;
	}
	public String getChannel() {
		return channel;
	}
	
}
