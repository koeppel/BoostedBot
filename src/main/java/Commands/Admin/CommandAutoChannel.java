package Commands.Admin;

import AutoChannelManager.AutoChannelHandler;
import Commands.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import Utils.UTILS;

import java.util.HashMap;

public class CommandAutoChannel implements Command {

    private static HashMap<Guild, AutoChannelHandler> autoChannelHandlerHashMap = new HashMap<>();

    public static void addAutoChannelHandler(Guild guild, AutoChannelHandler ach) {
        if (!(autoChannelHandlerHashMap.containsKey(guild) || autoChannelHandlerHashMap.containsValue(ach))) {
            autoChannelHandlerHashMap.put(guild, ach);
        }
    }

    public static AutoChannelHandler getAutoChannelHandler(Guild guild) {
        return autoChannelHandlerHashMap.get(guild);
    }

    private void setAutoChannel(String[] args, MessageReceivedEvent event) {
        if(!(args.length < 2)) {
            try {
                AutoChannelHandler ach = getAutoChannelHandler(event.getGuild());
                VoiceChannel vc = event.getGuild().getVoiceChannelById(args[1]);

                ach.addAutoChannel(vc);

            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        else {

        }
    }

    private void unsetAutoChannel(String[] args, MessageReceivedEvent event) {
        if(!(args.length < 2)) {
            try {
                AutoChannelHandler ach = getAutoChannelHandler(event.getGuild());
                VoiceChannel vc = event.getGuild().getVoiceChannelById(args[1]);

                ach.dropAutoChannel(vc);

            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        else {

        }
    }

    private void listAutoChannel(MessageReceivedEvent event) {
        try {
            AutoChannelHandler ach = getAutoChannelHandler(event.getGuild());
            for (VoiceChannel vc : ach.getAutoChannelHashMap().values()) {
                System.out.println(vc.getName());
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(!(args.length < 1)) {
            if(UTILS.isAdmin(event.getMember())){
                switch(args[0]) {
                    case("set"):
                    case("add"):
                        setAutoChannel(args, event);
                        break;
                    case("unset"):
                    case("remove"):
                        unsetAutoChannel(args, event);
                        break;
                    case("list"):
                        listAutoChannel(event);
                        break;
                    default:
                        // TODO: ADD a better Error-Message with List of arguments
                        break;
                }
            }
            else {
                // TODO: ADD a better Error-Message with List of arguments
                Message msg = event.getTextChannel().sendMessage("NO ADMIN!").complete();
                UTILS.clearMessage(msg, 3000);
            }
        }
        else {
            // TODO: ADD a better Error-Message with List of arguments
            Message msg = event.getTextChannel().sendMessage("Missing arguements!").complete();
            UTILS.clearMessage(msg, 3000);
        }
        UTILS.clearMessage(event.getMessage(), 3000);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
