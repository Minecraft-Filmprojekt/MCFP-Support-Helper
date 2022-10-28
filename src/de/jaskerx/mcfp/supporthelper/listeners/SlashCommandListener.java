package de.jaskerx.mcfp.supporthelper.listeners;

import de.jaskerx.mcfp.supporthelper.main.MCFPSupportHelper;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.commands.Command.Option;

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
		
		if(event.getName().equals("tickets")) {
			
			if(event.getSubcommandName().equals("menü")) {
				
				SelectMenu selMenu = SelectMenu.create("firstMenu")
						.setRequiredRange(0, 1)
						.setPlaceholder("Thema auswählen")
						.addOption("Minecraft Server", "mc")
						.addOption("Discord Server", "dc")
						.addOption("Website", "website")
						.addOption("Sonstige", "other")
						.build();
				ActionRow actionRow = ActionRow.of(selMenu);
				
				if(event.getOptions().size() > 0) {
					if(event.getOption("channel").getChannelType().equals(ChannelType.TEXT)) {
						event.getOption("channel").getAsTextChannel().sendMessage(new MessageBuilder().setContent("Willkommen beim Ticket-Support des MCFP! Bitte wähle aus, bei welchem Thema wird dir helfen können.").setActionRows(actionRow).build()).queue();
						event.reply("Das Menü wurde gesendet.").setEphemeral(true).queue();
					} else {
						event.reply("Bitte wähle einen TextChannel aus!").setEphemeral(true).queue();
					}
				} else {
					event.getTextChannel().sendMessage(new MessageBuilder().setContent("Willkommen beim Ticket-Support des MCFP! Bitte wähle aus, bei welchem Thema wird dir helfen können.").setActionRows(actionRow).build()).queue();
					event.reply("Das Menü wurde gesendet.").setEphemeral(true).queue();
				}
			}
		}
		
		if(event.getName().equals("set")) {
			event.deferReply(true).queue();
			
			if(event.getSubcommandName().equals("tickets")) {
				
				for(OptionMapping option : event.getOptions()) {
					
					if(option.getType().equals(OptionType.CHANNEL) && !option.getChannelType().equals(ChannelType.TEXT)) {
						event.getHook().editOriginal("Bitte wähle einen TextChannel aus: " + option.getName()).queue();
					} else {
						
						if(option.getType().equals(OptionType.CHANNEL)) {
							MCFPSupportHelper.setTicketInfo(event, option.getName(), option.getAsGuildChannel().getId());
						} else if(option.getType().equals(OptionType.ROLE)) {
							MCFPSupportHelper.setTicketInfo(event, option.getName(), option.getAsRole().getId());
						}
					}
				}
			}
			
		}
		
	}

}
