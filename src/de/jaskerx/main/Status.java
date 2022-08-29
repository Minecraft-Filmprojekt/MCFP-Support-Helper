package de.jaskerx.main;


import javax.security.auth.login.LoginException;

import de.jaskerx.listeners.MessageListener;
import de.jaskerx.listeners.ModalListener;
import de.jaskerx.listeners.RawGatewayListener;
import de.jaskerx.listeners.RoleChangeListener;
import de.jaskerx.listeners.SelectMenuListener;
import de.jaskerx.listeners.SlashCommandListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Status extends ListenerAdapter
{
	
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
			//upsertCommands();
					    
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
		
		MCFPSupportHelper.builder.getGuildById("949642462345973831").upsertCommand(
				
				Commands.slash("ticket-ping", "Nimmt oder gibt dir die @Ticket-Support Rolle")
				
		).queue();
	}
	
}
