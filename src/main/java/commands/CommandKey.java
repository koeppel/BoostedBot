package commands;

import keystoneManager.Keystone;
import keystoneManager.KeystoneHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import utils.STATIC;

import java.util.*;

public class CommandKey implements Command {

    private static HashMap<Guild, KeystoneHandler> keystoneHandlerHashMap = new HashMap<>();
    private EmbedBuilder eb = new EmbedBuilder();
    private HashMap<String, String> dungeons = STATIC.DUNGEONS;

    public static void addKeystoneHandler(Guild guild, KeystoneHandler ksh) {
        if (!(keystoneHandlerHashMap.containsKey(guild) || keystoneHandlerHashMap.containsValue(ksh))) {
            keystoneHandlerHashMap.put(guild, ksh);
        }
    }

    private void setEmbed(String avatarUrl) {
        eb.setDescription("");
        eb.setImage(null);
        eb.clearFields();
        eb.setThumbnail("http://wow.zamimg.com/images/wow/icons/large/inv_relics_hourglass.jpg");
        eb.setAuthor("Keystone Manager", null, avatarUrl);
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
        Message msg = textChannel.sendMessage(eb.build()).complete();
        clearMessage(msg, 10000);
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

    private void clearMessage(Message msg, int time) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, time);
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        setEmbed(event.getJDA().getSelfUser().getAvatarUrl());
        KeystoneHandler ksh = keystoneHandlerHashMap.get(event.getGuild());

        if (args.length < 1) {
            inputError("general", event.getTextChannel());
        }
        // -- ADD
        else if (args[0].equals("add")) {
            int level = 0;
            if(args.length < 3) {
                inputError("add", event.getTextChannel());
            }
            else {
                level = getInt(args[1]);
                if (level < 2) {
                    inputError("add_level", event.getTextChannel());
                } else if (args[2].isEmpty() || !dungeons.containsKey(args[2])) {
                    inputError("add_dungeon", event.getTextChannel());
                } else {
                    eb.setTitle("Creating new Keystone..");
                    Message msg = event.getTextChannel().sendMessage(eb.build()).complete();
                    Keystone ks;
                    if (!(args.length < 4) && (args[3].equals("Y") || args[3].equals("y"))) {
                        ks = new Keystone(args[2], level, msg, true, msg.getTextChannel().getId());
                    } else {
                        ks = new Keystone(args[2], level, msg, false, msg.getTextChannel().getId());
                    }
                    ks.setCreator(event.getAuthor());
                    ksh.addKeyStone(msg, ks);
                    ksh.joinUser(msg.getId(), event.getMember(), eb);
                    ksh.saveKeystonesToFile();
                }
            }
        }
        // -- LIST
        else if (args[0].equals("list")) {
            if(ksh.getAvailableKeystones().isEmpty()) {
                eb.setTitle("No open Keystones");
            }
            else {
                eb.setTitle("List of Keystones:");
                for (Keystone key : ksh.getAvailableKeystones().values()) {
                    eb.addField(key.getFullName() + " +" + key.getLevel(), "ID: " + key.getId(), false);
                }
            }
            Message msg = event.getTextChannel().sendMessage(eb.build()).complete();
            clearMessage(msg, 10000);
        }
        // -- JOIN
        else if (args[0].equals("join")) {
            if (ksh.getAvailableKeystones().isEmpty()) {
                eb.setTitle("No open Keystones");
            }
            else if(args.length < 2) {
                eb.setTitle("No Keystone ID given!");
            }
            else if(ksh.getAvailableKeystones().get(args[1]).isCompleted()) {
                eb.setTitle("Keystone " + args[1] + " already completed");
            }
            else {
                Member member = event.getMember();
                if(args.length < 3) {
                    ksh.joinUser(args[1], member, eb);
                }
                else {
                    ksh.joinUserByRole(args[1], member, args[2], eb);
                }
                ksh.saveKeystonesToFile();
            }
        }
        // -- LEAVE
        else if (args[0].equals("leave")) {
            if (ksh.getAvailableKeystones().isEmpty()) {
                eb.setTitle("No open Keystones");
            }
            else if(args.length < 2) {
                eb.setTitle("No Keystone ID given!");
            }
            else if(ksh.getAvailableKeystones().get(args[1]).isCompleted()) {
                eb.setTitle("Keystone " + args[1] + " already completed, no need to leave.");
            }
            else {
                Keystone ks = ksh.getAvailableKeystones().get(args[1]);
                if (!(args.length < 3) && args[2].equals("F")) {
                    ks.leave(event.getMember().getUser().getId());
                }
                else if(ks.getCreator() == event.getMember().getUser()) {
                    eb.setTitle("You are the creator of the Keystone " + args[1] + "!");
                    eb.setDescription("If you realy want to leave this Keystone add a 'F' after the command.");
                    Message msg = event.getTextChannel().sendMessage(eb.build()).complete();
                    clearMessage(msg, 5000);
                }
                else {
                    ks.leave(event.getMember().getUser().getId());
                }
                ksh.saveKeystonesToFile();
                ks.updateMessage(eb, event.getGuild());
            }
        }
        // -- COMPLETE
        else if (args[0].equals("complete")) {
            if(!(args.length < 2)) {
                Keystone ks = ksh.getAvailableKeystones().get(args[1]);
                ks.setCompleted(true);
                ks.updateMessage(eb, event.getGuild());
                ksh.saveKeystonesToFile();
            }
        }
        // -- EDIT
        else if (args[0].equals("edit")) {

        }
        // -- DELETE
        else if(args[0].equals("delete")) {
            if(!(args.length < 2)) {
                ksh.deleteKeyStone(args[1]);
            }
        }
        else {
            inputError("general", event.getTextChannel());
        }

        clearMessage(event.getMessage(), 3000);
    }

    @Override
    public void executed(boolean sucess, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
