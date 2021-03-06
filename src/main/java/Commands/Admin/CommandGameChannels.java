package Commands.Admin;

import Commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import Utils.CONFIG;

import java.awt.Color;
import java.io.*;
import java.util.*;

public class CommandGameChannels implements Command, Serializable {

    private static HashMap<TextChannel, Guild> gameChannels = new HashMap<>();
    private static final String filePath = CONFIG.SAVEPATH;
    private static final String fileNameChannels = filePath + "gamechannels.dat";

    public static HashMap<TextChannel, Guild> getGameChannels() {
        return gameChannels;
    }

    private static TextChannel getTextChannel(String id, Guild g) {
        TextChannel outPut;
        try {
            outPut = g.getTextChannelById(id);
        }
        catch (NumberFormatException e) {
            outPut = g.getTextChannelsByName(id,true).get(0);
        }
        return outPut;
    }

    private static Guild getGuild(String id, JDA jda) {
        return jda.getGuildById(id);
    }

    private void error(TextChannel tc, String content) {
        tc.sendMessage(
                new EmbedBuilder().setColor(Color.RED).setDescription(content).build()
        ).queue();
    }

    private void message(TextChannel tc, String content) {
        tc.sendMessage(
                new EmbedBuilder().setColor(Color.GREEN).setDescription(content).build()
        ).queue();
    }

    private void setChannel(String id, Guild g, TextChannel tc) {
        TextChannel gameChannel = getTextChannel(id, g);

        if (gameChannel == null) {
            error(tc, "Please enter a valid channel ID:");
        }
        else if (gameChannels.containsKey(gameChannel)) {
            error(tc, String.format("Channel '%s' is already a Game Channel", gameChannel.getName()));
        }
        else {
            gameChannels.put(gameChannel, g);
            message(tc, String.format("Successfully set channel '%s' as Game Channel", gameChannel.getName()));
            saveChannels();
        }
    }

    private void unsetChannel(String id, Guild g, TextChannel tc) {
        TextChannel gameChannel = getTextChannel(id,g);

        if (gameChannel == null) {
            error(tc, "Please enter a valid channel ID:");
        }
        else if (!gameChannels.containsKey(gameChannel)) {
            error(tc, String.format("Channel '%s' is NOT a Game Channel", gameChannel.getName()));
        }
        else {
            gameChannels.remove(gameChannel, g);
            saveChannels();
            message(tc, String.format("Successfully unset Game Channel '%s'", tc.getName()));
        }
    }

    public static void unsetChannel(TextChannel gc) {
        gameChannels.remove(gc);
        saveChannels();
    }

    private void listChannels(Guild g, TextChannel tc) {
        StringBuilder sb = new StringBuilder();

        if (gameChannels.isEmpty()) {
            sb.append("No Game Channels found\nStart a new Game Channel with\n**!bbgameChannels set [CHANNEL-ID / CHANNEL-NAME]**");
        } else
        {
            sb.append("**GAME CHANNELS:\n\n**");
            gameChannels.keySet().stream()
                    .filter(gameChannel -> gameChannels.get(gameChannel).equals(g))
                    .forEach(gameChannel -> sb.append(String.format(":white_small_square: '%s' *(%s)*\n", gameChannel.getName(), gameChannel.getId())));
        }

        tc.sendMessage(
                new EmbedBuilder().setDescription(sb.toString()).build()
        ).queue();
    }

    private static void saveChannels () {
        File path = new File(filePath);

        if (!path.exists()){
            path.mkdir();
        }

        HashMap<String, String> out = new HashMap<>();
        gameChannels.forEach((gameChannel, g) -> out.put(gameChannel.getId(), g.getId()));

        try {
            FileOutputStream fos = new FileOutputStream(fileNameChannels);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadChannels (JDA jda) {
        File file = new File(fileNameChannels);

        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                HashMap<String, String> out = (HashMap<String, String>) ois.readObject();
                ois.close();

                out.forEach((tid, gid) -> {
                    Guild g = getGuild(gid, jda);
                    gameChannels.put(getTextChannel(tid, g), g);
                });
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Guild g = event.getGuild();
        TextChannel tc = event.getTextChannel();


        if (args.length < 1) {
            error(tc, help());
            return;
        }

        switch(args[0]) {
            case "list":
                listChannels(g, tc);
                break;
            case "set":
                setChannel(args[1], g, tc);
                break;
            case "unset":
                unsetChannel(args[1], g, tc);
                break;
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
