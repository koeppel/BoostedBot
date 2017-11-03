package listeners;

import commands.commandGameChannels;
import core.gameHandler;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.Serializable;
import java.util.HashMap;

public class readyListener extends ListenerAdapter implements Serializable {
    private HashMap<TextChannel, Guild> gameChannels = new HashMap<>();

    public void onReady(ReadyEvent event) {
        System.out.println("Bot Ready!");

        commandGameChannels.loadChannels(event.getJDA());

        this.gameChannels = commandGameChannels.getGameChannels();

        for (Guild g : event.getJDA().getGuilds()) {
            gameChannels.keySet().stream()
                .filter(gameChannel -> gameChannels.get(gameChannel).equals(g))
                .forEach(gameChannel -> {
                    new gameHandler(event.getJDA(), gameChannel).run();
                });
        }
    }
}
