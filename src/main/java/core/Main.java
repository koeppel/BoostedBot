package core;

import commands.*;
import listeners.CommandListener;
import listeners.JoinListener;
import listeners.ReadyListener;
import listeners.VoiceChannelListener;
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
        builder.setGame(Game.of("Boosted af"));

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
        CommandHandler.commands.put("clear", new CommandClear());
        CommandHandler.commands.put("gameChannels", new CommandGameChannels());
        CommandHandler.commands.put("lvlWorker", new CommandLevelWorker());
        CommandHandler.commands.put("key", new CommandKey());
        CommandHandler.commands.put("auto", new CommandAutoChannel());
    }

    private static void loadListeners(JDABuilder builder) {
        builder.addEventListener(new ReadyListener());
        builder.addEventListener(new JoinListener());
        builder.addEventListener(new CommandListener());
        builder.addEventListener(new VoiceChannelListener());
    }
}
