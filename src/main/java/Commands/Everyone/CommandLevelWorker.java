package Commands.Everyone;

import Commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class CommandLevelWorker implements Command {

    EmbedBuilder error = new EmbedBuilder().setColor(Color.RED);
    EmbedBuilder sucess = new EmbedBuilder().setColor(Color.GREEN);

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (args.length < 2) {
            event.getTextChannel().sendMessage(
                    error.setDescription("Please enter a WORKERNAME and an amount of LEVELS").build()
            ).queue();
        }
        else {
            event.getTextChannel().sendMessage("").queue();
        }
    }

    @Override
    public void executed(boolean sucess, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
