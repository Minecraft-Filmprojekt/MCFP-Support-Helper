package de.jaskerx.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener extends ListenerAdapter {
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		
		if(event.getName().equals("ticket-ping")) {
			
			if(event.getMember().getRoles().contains(event.getGuild().getRoleById("984047393366491156"))) {
				
				event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("984047393366491156")).queue();
				event.reply("Die Rolle wurde entfernt.\rWenn ein Ticket erstellt wird, erhältst du keinen @Ping mehr!").setEphemeral(true).queue();
			} else {
				
				event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("984047393366491156")).queue();
				event.reply("Die Rolle wurde hinzugefügt.\rWenn ein Ticket erstellt wird, erhältst du einen @Ping!").setEphemeral(true).queue();
			}
		}
		
	}

}
