package de.jaskerx.mcfp.supporthelper.listeners;

import java.util.ArrayList;
import java.util.List;

import de.jaskerx.mcfp.supporthelper.main.MCFPSupportHelper;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleChangeListener extends ListenerAdapter {

	List<Role> filterRoles = new ArrayList<>();
	
	public RoleChangeListener() {
		filterRoles.add(MCFPSupportHelper.builder.getGuildById("949642462345973831").getRoleById("950276926583480361")); //Community
		filterRoles.add(MCFPSupportHelper.builder.getGuildById("949642462345973831").getRoleById("951135077063090196")); //Bots
		filterRoles.add(MCFPSupportHelper.builder.getGuildById("949642462345973831").getRoleById("951135382852993174")); //Server Bot
		filterRoles.add(MCFPSupportHelper.builder.getGuildById("949642462345973831").getRoleById("950708390034808842")); //Benutzer
		filterRoles.add(MCFPSupportHelper.builder.getGuildById("949642462345973831").getRoleById("978351483961606186")); //Lawliet
		filterRoles.add(MCFPSupportHelper.builder.getGuildById("949642462345973831").getRoleById("952132237959262251")); //MCFP Support Helper
		filterRoles.add(MCFPSupportHelper.builder.getGuildById("949642462345973831").getRoleById("951747829548855309")); //Dyno
		filterRoles.add(MCFPSupportHelper.builder.getGuildById("949642462345973831").getRoleById("950629405510733877")); //MEE6
		
		filterRoles.add(MCFPSupportHelper.builder.getGuildById("949642462345973831").getRoleById("950279578042118214")); //Server Team
	}
	
	@Override
	public void onGuildMemberRoleAdd (GuildMemberRoleAddEvent event) {
		
		for(Role role : event.getRoles()) {
			if(!filterRoles.contains(role)) {
				event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("950279578042118214")).queue();
			}
		}
	}
	
	@Override
	public void onGuildMemberRoleRemove (GuildMemberRoleRemoveEvent event) {
		
		List<Role> roles = event.getMember().getRoles();
		for(Role role : roles) {
			if(!filterRoles.contains(role)) {
				return;
			}
		}
		
		event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("950279578042118214")).queue();
	}
	
}
