package listeners;

import commands.CommandKey;
import keystoneManager.KeystoneHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.ChannelManager;
import net.dv8tion.jda.core.managers.ChannelManagerUpdatable;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.*;

import java.util.Collection;
import java.util.List;

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
