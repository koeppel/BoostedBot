package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CommandClear implements Command {

    EmbedBuilder error = new EmbedBuilder().setColor(Color.RED);
    EmbedBuilder sucess = new EmbedBuilder().setColor(Color.GREEN);

    private int getInt (String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        int numb = getInt(args[0]);

        if (args.length < 1) {
            event.getTextChannel().sendMessage(
                    error.setDescription("Please enter a number of messages you want to delete!").build()
            ).queue();
        }
        else if (numb > 1 && numb <= 100) {
            try {
                MessageHistory history = new MessageHistory(event.getTextChannel());
                List<Message> msgs;

                msgs = history.retrievePast(numb).complete();
                event.getTextChannel().deleteMessages(msgs).queue();

                Message msg = event.getTextChannel().sendMessage(
                        sucess.setDescription("Deleted " + args[0] + " messages!").build()
                ).complete();

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        msg.delete().queue();
                    }
                }, 3000);

            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        else {
            event.getTextChannel().sendMessage(
                    error.setDescription("Please enter a number between 2 and 100").build()
            ).queue();
        }
    }

    @Override
    public void executed(boolean sucess, MessageReceivedEvent event) {
        System.out.println("[INFO] Command '!bbclear' was executed!");
    }

    @Override
    public String help() {
        return null;
    }
}
