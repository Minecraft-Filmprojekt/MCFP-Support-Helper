package de.jaskerx.mcfp.supporthelper.main;


import java.util.List;
import java.util.Timer;

import javax.security.auth.login.LoginException;

import de.jaskerx.mcfp.supporthelper.listeners.MessageListener;
import de.jaskerx.mcfp.supporthelper.listeners.ModalListener;
import de.jaskerx.mcfp.supporthelper.listeners.RawGatewayListener;
import de.jaskerx.mcfp.supporthelper.listeners.RoleChangeListener;
import de.jaskerx.mcfp.supporthelper.listeners.SelectMenuListener;
import de.jaskerx.mcfp.supporthelper.listeners.SlashCommandListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Status extends ListenerAdapter
{
	
	private static Timer t = new Timer();
	
	public static void Start() throws LoginException
	{

		MCFPSupportHelper.builder = JDABuilder.create(MCFPSupportHelper.token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
			
			.disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE)
		    .addEventListeners(new MessageListener(), new SelectMenuListener(), new ModalListener(), new SlashCommandListener())
		    .setRawEventsEnabled(false)
		    .setStatus(OnlineStatus.ONLINE)
		    .build();

	
		try {	
			MCFPSupportHelper.builder.awaitReady();
			MCFPSupportHelper.log("MCFP Support Helper ist online.");
			
			MCFPSupportHelper.builder.addEventListener(new RoleChangeListener());
			t.schedule(new UpdateServerInfoTask(), 0, 300000);
			//upsertCommands();
			MCFPSupportHelper.builder.getGuildById(MCFPSupportHelper.guildId).retrieveCommands().queue(commands -> {
				commands.forEach((command) -> {
					System.out.println(command.getName() + " | id:" + command.getId());
				});
			});
					    
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}
	
	public static void Stop()
	{
		
		MCFPSupportHelper.builder.getPresence().setStatus(OnlineStatus.OFFLINE);
		MCFPSupportHelper.log("MCFP Support Helper ist offline.");
		MCFPSupportHelper.builder.shutdown();
		System.exit(0);
	}
	
	
	
	private static void upsertCommands() {
		
		MCFPSupportHelper.builder.getGuildById(MCFPSupportHelper.guildId).upsertCommand(
				
				Commands.slash("ticket-ping", "Nimmt oder gibt dir die @Ticket-Support Rolle")
				
		).queue();
		
		MCFPSupportHelper.builder.getGuildById(MCFPSupportHelper.guildId).upsertCommand(
				
				Commands.slash("set", "Lege Ids fest").addSubcommands(
					new SubcommandData("tickets", "Lege Ids für den Ticket-Support fest")
						.addOption(OptionType.CHANNEL, "updates-log", "Channel, in dem über neue Tickets informiert wird.", false)
						.addOption(OptionType.ROLE, "rolle", "Support-Rolle, die bei einem neuen Ticket gepingt wird.", false)
				)
				
		).queue();
		
		MCFPSupportHelper.builder.getGuildById(MCFPSupportHelper.guildId).upsertCommand(
				
				Commands.slash("tickets", "Commands, die Tickets betreffen.").addSubcommands(
					new SubcommandData("menü", "Sendet das Auswahlmenü zum Erstellen eines Tickets.")
						.addOption(OptionType.CHANNEL, "channel", "Channel, in den das Menü geschickt werden soll.", false)
				)
				
		).queue();
	}
	
}
