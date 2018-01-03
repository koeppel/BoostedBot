package Listeners;

import AutoChannelManager.AutoChannelHandler;
import Commands.Admin.CommandAutoChannel;
import Commands.Everyone.CommandKey;
import KeystoneManager.KeystoneHandler;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.Serializable;

public class ReadyListener extends ListenerAdapter implements Serializable {
    //private HashMap<TextChannel, Guild> gameChannels = new HashMap<>();

    public void onReady(ReadyEvent event) {
        System.out.println("Bot Ready!");

        for (Guild g : event.getJDA().getGuilds()) {
            loadKeystoneManager(g);
            loadAutoChannelManager(g);
        }

//        CommandGameChannels.loadChannels(event.getJDA());
//
//        this.gameChannels = CommandGameChannels.getGameChannels();
//
//        for (Guild g : event.getJDA().getGuilds()) {
//            gameChannels.keySet().stream()
//                .filter(gameChannel -> gameChannels.get(gameChannel).equals(g))
//                .forEach(gameChannel -> {
//                    new gameHandler(event.getJDA(), gameChannel).run();
//                });
//        }

//        for (Guild g : event.getJDA().getGuilds()) {
//
//        }
    }

    private void loadKeystoneManager(Guild guild) {
            KeystoneHandler ksh = new KeystoneHandler(guild);
            ksh.loadKeystonesFromFile();
            ksh.checkGuildRoles();

            CommandKey.addKeystoneHandler(guild, ksh);
    }

    private void loadAutoChannelManager(Guild guild) {
        AutoChannelHandler ach = new AutoChannelHandler(guild);
        ach.loadAutoChannelsFromFile();
        CommandAutoChannel.addAutoChannelHandler(guild, ach);
    }
}
