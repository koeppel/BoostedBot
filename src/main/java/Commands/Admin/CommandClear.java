package Commands.Admin;

import Commands.Command;
import Utils.CONFIG;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import Utils.UTILS;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CommandClear implements Command {

    EmbedBuilder error = new EmbedBuilder().setColor(Color.RED);
    EmbedBuilder sucess = new EmbedBuilder().setColor(Color.GREEN);

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (args.length < 1) {
            Message msg = event.getTextChannel().sendMessage(
                    error.setDescription("Please enter a number of messages you want to delete!").build()
            ).complete();
            UTILS.clearMessage(msg, 5000);
        }
        else if(UTILS.isAdmin(event.getMember())){
            int numb = UTILS.getInt(args[0]);
            if (numb > 1 && numb <= 100) {
                try {
                    MessageHistory history = new MessageHistory(event.getTextChannel());
                    List<Message> msgs;

                    msgs = history.retrievePast(numb).complete();
                    event.getTextChannel().deleteMessages(msgs).queue();

                    Message msg = event.getTextChannel().sendMessage(
                            sucess.setDescription("Deleted " + args[0] + " messages!").build()
                    ).complete();

                    UTILS.clearMessage(msg, 3000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Message msg = event.getTextChannel().sendMessage(
                        error.setDescription("Please enter a number between 2 and 100").build()
                ).complete();
                UTILS.clearMessage(msg, 3000);
            }
        }
        else {
            event.getTextChannel().sendMessage(
                    error.setDescription("No admin Role!").build()
            ).queue();
        }
        UTILS.clearMessage(event.getMessage(), 3000);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[INFO] Command '!clear' was executed!");
    }

    @Override
    public String help() {
        return null;
    }
}
