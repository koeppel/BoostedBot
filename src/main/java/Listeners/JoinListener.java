package Listeners;

import Commands.Everyone.CommandKey;
import KeystoneManager.KeystoneHandler;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class JoinListener extends ListenerAdapter {

    public void onGuildJoin(GuildJoinEvent event) {
        System.out.println("Bot Joined Server: '" + event.getGuild().getName() + "' with ID + " + event.getGuild().getId());
        event.getGuild().getTextChannels().get(0).sendMessage(
                "Hello I just joined the Server!"
        ).queue();

        KeystoneHandler ksh = new KeystoneHandler(event.getGuild());
        ksh.checkGuildRoles();
        CommandKey.addKeystoneHandler(event.getGuild(), ksh);
    }
}
