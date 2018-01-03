package Listeners;

import AutoChannelManager.AutoChannelHandler;
import Commands.Admin.CommandAutoChannel;
import Commands.Everyone.CommandKey;
import KeystoneManager.KeystoneHandler;
import Utils.CONFIG;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.Serializable;

public class ReadyListener extends ListenerAdapter implements Serializable {
    //private HashMap<TextChannel, Guild> gameChannels = new HashMap<>();

    public void onReady(ReadyEvent event) {
        System.out.println("Bot Ready!");

        for (Guild g : event.getJDA().getGuilds()) {
            checkForAdminRole(g);
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

    private void checkForAdminRole(Guild guild) {
        boolean addRole = true;
        for(Role role : guild.getRoles()) {
            if(role.getName().equals(CONFIG.ADMINROLE_NAME)) {
                addRole = false;
            }
        }
        if(addRole) {
            guild.getController().createRole().setName(CONFIG.ADMINROLE_NAME).queue();
        }
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
