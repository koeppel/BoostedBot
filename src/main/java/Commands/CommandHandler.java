package Commands;

import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CommandHandler {

    public static final CommandParser parse = new CommandParser();
    public static HashMap<String, Command> commands = new HashMap<>();

    public static void handleCommand (CommandParser.commandContainer cmd) {
        if (commands.containsKey(cmd.invoke)) {
            boolean safe = commands.get(cmd.invoke).called(cmd.args, cmd.event);

            if (!safe) {
                commands.get(cmd.invoke).action(cmd.args, cmd.event);
                commands.get(cmd.invoke).executed(safe, cmd.event);
            }
            else {
                commands.get(cmd.invoke).executed(safe, cmd.event);
            }

        } else {
            Message msg = cmd.event.getTextChannel().sendMessage("Command not found!").complete();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    msg.delete().queue();
                }
            }, 3000);
        }
    }

}
