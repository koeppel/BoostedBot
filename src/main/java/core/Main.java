package core;

import commands.commandClear;
import commands.commandGameChannels;
import commands.commandLevelWorker;
import listeners.commandListener;
import listeners.joinListener;
import listeners.readyListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import utils.CONFIG;
import utils.SECRETS;

import javax.security.auth.login.LoginException;

public class Main {

    public static JDABuilder builder;

    public static void main(String[] Args)
    {
        builder = new JDABuilder(AccountType.BOT);

        builder.setToken(SECRETS.TOKEN);
        builder.setAutoReconnect(CONFIG.RECONNECT);

        builder.setStatus(OnlineStatus.ONLINE);
        builder.setGame(Game.of("Boosted Idle Game"));

        loadListeners(builder);
        loadCommands();

        try {
            JDA jda = builder.buildBlocking();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
    }

    private static void loadCommands() {
        commandHandler.commands.put("clear", new commandClear());
        commandHandler.commands.put("gameChannels", new commandGameChannels());
        commandHandler.commands.put("lvlWorker", new commandLevelWorker());
    }

    private static void loadListeners(JDABuilder builder) {
        builder.addEventListener(new readyListener());
        builder.addEventListener(new joinListener());
        builder.addEventListener(new commandListener());
    }
}
