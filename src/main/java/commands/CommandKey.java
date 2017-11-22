package commands;

import keystoneManager.Keystone;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import utils.STATIC;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandKey implements Command {

    private EmbedBuilder eb = new EmbedBuilder();
    private HashMap<String, String> dungeons = STATIC.DUNGEONS;
    private HashMap<String, Keystone> keystones = new HashMap<>();

    private void setEmbed(String avatarUrl) {
        eb.setDescription("");
        eb.clearFields();
        eb.setThumbnail("http://wow.zamimg.com/images/wow/icons/large/inv_relics_hourglass.jpg");
        eb.setAuthor("Keystone Manager",avatarUrl);
    }

    private void setListOfArguments(String type) {
        if (type.equals("general")) {
            eb.addField(new MessageEmbed.Field("add [LVL] [KEY] [SEL]",
                    "Adds a Key with the given arguments:" +
                            "\n:black_small_square:  LVL - The Level of the Key +10 / +15 etc." +
                            "\n:black_small_square:  KEY - The Name of the Key DHT / BRH etc." +
                            "\n:black_small_square:  SEL - Is it a sellrun? Y/N (default N)"
                    , false));
            eb.addField(new MessageEmbed.Field("join [ID]", "Allows you to join the Key with given ID.", false));
            eb.addField(new MessageEmbed.Field("join [ID]", "Allows you to leave the Key with given ID.", false));
            eb.addField(new MessageEmbed.Field("complete [ID]", "Allows you to set a Key with given ID to completed.", false));
            eb.addField(new MessageEmbed.Field("edit [ID]", "Allows you to edit a Key with given ID.", false));
        }
        else if (type.equals("add")) {
            eb.addField(new MessageEmbed.Field("add [LVL] [KEY] [SEL]",
                    "Adds a Key with the given arguments:" +
                            "\n:black_small_square:  LVL - The Level of the Key +10 / +15 etc. (min 2)" +
                            "\n:black_small_square:  KEY - The Name of the Key DHT / BRH etc." +
                            "\n:black_small_square:  SEL - Is it a sellrun? Y/N (default N)"
                    , false));
        }
        else if (type.equals("add_level")) {
            eb.addField(new MessageEmbed.Field("add [LVL] [KEY] [SEL]",
                    "Adds a Key with the given arguments:" +
                            "\n:black_small_square:  LVL - The Level of the Key (10 / 15 etc.; min 2)"
                    , false));
        }
        else if (type.equals("add_dungeon")) {
            String dungeonPrint = "";
            eb.addField(new MessageEmbed.Field("add [LVL] [KEY] [SEL]",
                    "Adds a Key with the given arguments:" +
                            "\n:black_small_square:  KEY - The Name of the Key (DHT / BRH etc.)"
                    , false));
            for (String dungeon: dungeons.keySet()) {
                dungeonPrint += ":black_small_square:" + dungeon + " - " + dungeons.get(dungeon) + "\n";
            }
            eb.addField(new MessageEmbed.Field("Dungeons:",dungeonPrint,false));
        }
        else if (type.equals("join")) {

        }
        else if (type.equals("complete")) {

        }
        else if (type.equals("edit")) {

        }
    }

    private void inputError(String type, TextChannel textChannel) {
        if (type.equals("general")) {
            eb.setTitle(":x: Whoops, looks like something went wrong! :x:");
            eb.setDescription("You've entered an wrong argument or no argument at all. Here is a list of valid arguments:");
            setListOfArguments(type);
        }
        else if (type.equals("add")) {
            eb.setTitle(":x: Whoops, looks like something went wrong! :x:");
            eb.setDescription("You've entered an wrong argument!");
            setListOfArguments(type);
        }
        else if (type.equals("add_level")) {
            eb.setTitle(":x: Whoops, looks like something went wrong! :x:");
            eb.setDescription("You've entered an wrong argument! Key-Level is to low or not set at all!");
            setListOfArguments(type);
        }
        else if (type.equals("add_dungeon")) {
            eb.setTitle(":x: Whoops, looks like something went wrong! :x:");
            eb.setDescription("You've entered an wrong argument! Key-Dungeon was not found in the Dungeon list!");
            setListOfArguments(type);
        }
        else if (type.equals("join")) {

        }
        else if (type.equals("leave")) {

        }
        else if (type.equals("complete")) {

        }
        else if (type.equals("edit")) {

        }
        textChannel.sendMessage(eb.build()).queue();
    }

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
        setEmbed(event.getMessage().getAuthor().getAvatarUrl());

        if (args.length < 1) {
            inputError("general", event.getTextChannel());
        }
        // -- ADD
        else if (args[0].equals("add")) {
            int level = getInt(args[1]);

            if(args.length < 3) {
                inputError("add", event.getTextChannel());
            }
            else if(level < 2) {
                inputError("add_level", event.getTextChannel());
            }
            else if(args[2].isEmpty() || !dungeons.containsKey(args[2])) {
                inputError("add_dungeon", event.getTextChannel());
            }
            else {
                eb.setTitle("Creating new Keystone..");
                Message msg = event.getTextChannel().sendMessage(eb.build()).complete();
                Keystone ks;
                if (!(args.length < 4) && (args[3].equals("Y") || args[3].equals("y")))
                {
                    ks = new Keystone(args[2], level, msg,true);
                }
                else {
                    ks = new Keystone(args[2], level, msg,false);
                }
                keystones.put(msg.getId(), ks);
                ks.setCreator(event.getAuthor());
                ks.updateMessage(eb);
            }
        }
        // -- LIST
        else if (args[0].equals("list")) {
            if(this.keystones.isEmpty()) {
                eb.setTitle("No open Keystones");
            }
            else {
                eb.setTitle("List of Keystones:");
                for (String key: keystones.keySet()) {
                    if (!(keystones.get(key).isCompleted())) {
                        eb.addField("ID: " + key, keystones.get(key).getName(), false);
                    }
                }
                event.getTextChannel().sendMessage(eb.build()).queue();
            }
        }
        else if (args[0].equals("join")) {
            Member member = event.getMember();

            System.out.println(member.getUser().getName());

            System.out.println(member);

            GuildController gc = new GuildController(event.getGuild());

        }
        else if (args[0].equals("leave")) {

        }
        else if (args[0].equals("complete")) {

        }
        else if (args[0].equals("edit")) {

        }
        else {
            inputError("general", event.getTextChannel());
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
