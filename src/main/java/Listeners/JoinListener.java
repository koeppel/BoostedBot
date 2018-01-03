package Listeners;

import Commands.Everyone.CommandKey;
import KeystoneManager.KeystoneHandler;
import Utils.UTILS;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class JoinListener extends ListenerAdapter {

    public void onGuildJoin(GuildJoinEvent event) {
        System.out.println("Bot Joined Server: '" + event.getGuild().getName() + "' with ID + " + event.getGuild().getId());
        event.getGuild().getTextChannels().get(0).sendMessage(
                "Hello I just joined the Server!"
        ).queue();

        UTILS.loadKeystoneManager(event.getGuild());
        UTILS.loadAutoChannelManager(event.getGuild());
        UTILS.checkForAdminRole(event.getGuild());
    }
}
