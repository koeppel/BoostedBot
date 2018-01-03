package Commands.Everyone;

import Commands.Command;
import KeystoneManager.Keystone;
import KeystoneManager.KeystoneHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import Utils.STATIC;
import Utils.UTILS;

import java.util.*;

public class CommandKey implements Command {

    private static HashMap<Guild, KeystoneHandler> keystoneHandlerHashMap = new HashMap<>();
    private EmbedBuilder keystoneEB = new EmbedBuilder();
    private EmbedBuilder errorEB = new EmbedBuilder();
    private EmbedBuilder generalEB = new EmbedBuilder();
    private HashMap<String, String> dungeons = STATIC.DUNGEONS;
    private String adminRole = STATIC.ADMINROLE;

    public static void addKeystoneHandler(Guild guild, KeystoneHandler ksh) {
        if (!(keystoneHandlerHashMap.containsKey(guild) || keystoneHandlerHashMap.containsValue(ksh))) {
            keystoneHandlerHashMap.put(guild, ksh);
        }
    }

    private void setEmbed(String avatarUrl) {
        keystoneEB.clear();
        errorEB.clear();
        generalEB.clear();

        keystoneEB.setThumbnail(STATIC.KEYSTONEURL);
        // Set Author to Bot-Name and BotAvatar-Url
        errorEB.setAuthor("Keystone Manager", null, avatarUrl);
        generalEB.setAuthor("Keystone Manager", null, avatarUrl);
        keystoneEB.setAuthor("Keystone Manager", null, avatarUrl);
    }

    private void setListOfArguments(String type) {
        if (type.equals("general")) {
            generalEB.addField(new MessageEmbed.Field("addKeystone [LVL] [KEY] [SEL]",
                    "Adds a Key with the given arguments:" +
                            "\n:black_small_square:  LVL - The Level of the Key +10 / +15 etc." +
                            "\n:black_small_square:  KEY - The Name of the Key DHT / BRH etc." +
                            "\n:black_small_square:  SEL - Is it a sellrun? Y/N (default N)"
                    , false));
            generalEB.addField(new MessageEmbed.Field("joinKeystone [ID]", "Allows you to joinKeystone the Key with given ID.", false));
            generalEB.addField(new MessageEmbed.Field("joinKeystone [ID]", "Allows you to leaveKeystone the Key with given ID.", false));
            generalEB.addField(new MessageEmbed.Field("complete [ID]", "Allows you to set a Key with given ID to completed.", false));
            generalEB.addField(new MessageEmbed.Field("edit [ID]", "Allows you to edit a Key with given ID.", false));
        }
        else if (type.equals("addKeystone")) {
            generalEB.addField(new MessageEmbed.Field("addKeystone [LVL] [KEY] [SEL]",
                    "Adds a Key with the given arguments:" +
                            "\n:black_small_square:  LVL - The Level of the Key +10 / +15 etc. (min 2)" +
                            "\n:black_small_square:  KEY - The Name of the Key DHT / BRH etc." +
                            "\n:black_small_square:  SEL - Is it a sellrun? Y/N (default N)"
                    , false));
        }
        else if (type.equals("add_level")) {
            generalEB.addField(new MessageEmbed.Field("addKeystone [LVL] [KEY] [SEL]",
                    "Adds a Key with the given arguments:" +
                            "\n:black_small_square:  LVL - The Level of the Key (10 / 15 etc.; min 2)"
                    , false));
        }
        else if (type.equals("add_dungeon")) {
            String dungeonPrint = "";
            generalEB.addField(new MessageEmbed.Field("addKeystone [LVL] [KEY] [SEL]",
                    "Adds a Key with the given arguments:" +
                            "\n:black_small_square:  KEY - The Name of the Key (DHT / BRH etc.)"
                    , false));
            for (String dungeon: dungeons.keySet()) {
                dungeonPrint += ":black_small_square:" + dungeon + " - " + dungeons.get(dungeon) + "\n";
            }
            generalEB.addField(new MessageEmbed.Field("Dungeons:",dungeonPrint,false));
        }
        else if (type.equals("joinKeystone")) {

        }
        else if (type.equals("complete")) {

        }
        else if (type.equals("edit")) {

        }
    }

    private void inputError(String type, TextChannel textChannel) {
        if (type.equals("general")) {
            errorEB.setTitle(":x: Whoops, looks like something went wrong! :x:");
            errorEB.setDescription("You've entered an wrong argument or no argument at all. Here is a listKeystones of valid arguments:");
            setListOfArguments(type);
        }
        else if (type.equals("addKeystone")) {
            errorEB.setTitle(":x: Whoops, looks like something went wrong! :x:");
            errorEB.setDescription("You've entered an wrong argument!");
            setListOfArguments(type);
        }
        else if (type.equals("add_level")) {
            errorEB.setTitle(":x: Whoops, looks like something went wrong! :x:");
            errorEB.setDescription("You've entered an wrong argument! Key-Level is to low or not set at all!");
            setListOfArguments(type);
        }
        else if (type.equals("add_dungeon")) {
            errorEB.setTitle(":x: Whoops, looks like something went wrong! :x:");
            errorEB.setDescription("You've entered an wrong argument! Key-Dungeon was not found in the Dungeon listKeystones!");
            setListOfArguments(type);
        }
        else if (type.equals("joinKeystone")) {

        }
        else if (type.equals("leaveKeystone")) {

        }
        else if (type.equals("complete")) {

        }
        else if (type.equals("edit")) {

        }
        Message msg = textChannel.sendMessage(errorEB.build()).complete();
        UTILS.clearMessage(msg, 10000);
    }

    /**
     * function to create a new Keystone in the event channel
     * @param args - give by the User input args[0] has to be "add" to get into this function
     * @param event - is the event which this Command is listening to
     * @param ksh - is the KeystoneHandler of this specific guild / Discord-Server
     */
    private void addKeystone(String[] args, MessageReceivedEvent event, KeystoneHandler ksh) {
        if(args.length < 3) {
            inputError("addKeystone", event.getTextChannel());
        }
        else {
            int level = UTILS.getInt(args[1]);
            if (level < 2) {
                inputError("add_level", event.getTextChannel());
            } else if (args[2].isEmpty() || !dungeons.containsKey(args[2])) {
                inputError("add_dungeon", event.getTextChannel());
            } else {
                keystoneEB.setTitle("Creating new Keystone..");
                Message keystoneMessage = event.getTextChannel().sendMessage(keystoneEB.build()).complete();
                Keystone ks;
                if (!(args.length < 4) && (args[3].equals("Y") || args[3].equals("y"))) {
                    ks = new Keystone(args[2], level, keystoneMessage, true, keystoneMessage.getTextChannel().getId());
                } else {
                    ks = new Keystone(args[2], level, keystoneMessage, false, keystoneMessage.getTextChannel().getId());
                }
                ks.setCreator(event.getAuthor());
                ks.setAuthorName(event.getJDA().getSelfUser().getName());
                ks.setAuthorUrl(event.getJDA().getSelfUser().getAvatarUrl());
                ksh.addKeyStone(keystoneMessage, ks);
                ksh.joinUser(keystoneMessage.getId(), event.getMember(), errorEB, keystoneEB);
                ksh.pinKeystone(ks.getId());
                Message joinErrorMessage = event.getTextChannel().sendMessage(errorEB.build()).complete();
                UTILS.clearMessage(joinErrorMessage, 10000);
                ks.updateMessage(keystoneEB, event.getGuild());
                ksh.saveKeystonesToFile();
            }
        }
    }

    /**
     * Function to alert every user in the Keystone if Keystone != completed and Keystone has 5 Members
     * @param args - give by the User input args[0] has to be "alert" to get into this function
     * @param event - is the event which this Command is listening to
     * @param ksh - is the KeystoneHandler of this specific guild / Discord-Server
     */
    private void alertKeystone(String[] args, MessageReceivedEvent event, KeystoneHandler ksh) {
        if(args.length < 2) {
            // NO ID GIVEN
            errorEB.setTitle("No Keystone ID given!");
            Message msg = event.getTextChannel().sendMessage(errorEB.build()).complete();
            UTILS.clearMessage(msg, 3000);
        }
        else {
            errorEB = ksh.alertKeystone(args[1], event, errorEB);
            Message msg = event.getTextChannel().sendMessage(errorEB.build()).complete();
            UTILS.clearMessage(msg, 5000);
        }
    }

    /**
     * function to clear all / all completed Keystones
     * @param args - give by the User input args[0] has to be "clear" to get into this function
     * @param event - is the event which this Command is listening to
     * @param ksh - is the KeystoneHandler of this specific guild / Discord-Server
     */
    private void clearKeystones(String[] args, MessageReceivedEvent event, KeystoneHandler ksh) {
        if(ksh.getMemberRolesByName(event.getMember()).containsKey(this.adminRole)) {
            if (!(args.length < 2) && args[1].equals("all")) {
                ksh.clearKeystones(true);
            }
            else {
                ksh.clearKeystones(false);
            }
        }
        else {
            errorEB.setTitle("Insufficient authority!");
            errorEB.setDescription(event.getMember().getUser().getName() + " has to be " + adminRole + " to clear all Keystones!");
            Message msg = event.getTextChannel().sendMessage(errorEB.build()).complete();
            UTILS.clearMessage(msg, 5000);
        }
    }

    /**
     * function to create a new Keystone in the event channel
     * @param args - give by the User input args[0] has to be "join" to get into this function
     * @param event - is the event which this Command is listening to
     * @param ksh - is the KeystoneHandler of this specific guild / Discord-Server
     */
    private void joinKeystone(String[] args, MessageReceivedEvent event, KeystoneHandler ksh) {
        if (ksh.getAvailableKeystones().isEmpty()) {
            errorEB.setTitle("No open Keystones");
        }
        else if(args.length < 2) {
            errorEB.setTitle("No Keystone ID given!");
        }
        else if(!ksh.containsKeystone(args[1])) {
            errorEB.setTitle("Keystone " + args[1] + " doesn't exist!");
        }
        else if(ksh.getAvailableKeystones().get(args[1]).isCompleted()) {
            errorEB.setTitle("Keystone " + args[1] + " already completed");
        }
        else {
            Member member = event.getMember();
            if(args.length < 3) {
                ksh.joinUser(args[1], member, errorEB, keystoneEB);
            }
            else {
                ksh.joinUserByRole(args[1], member, args[2], errorEB, keystoneEB);
            }
            Message msg = event.getTextChannel().sendMessage(errorEB.build()).complete();
            UTILS.clearMessage(msg, 10000);
            ksh.saveKeystonesToFile();
        }
    }

    /**
     * function to create a new Keystone in the event channel
     * @param args - give by the User input args[0] has to be "leave" to get into this function
     * @param event - is the event which this Command is listening to
     * @param ksh - is the KeystoneHandler of this specific guild / Discord-Server
     */
    private void leaveKeystone(String[] args, MessageReceivedEvent event, KeystoneHandler ksh) {
        if (ksh.getAvailableKeystones().isEmpty()) {
            errorEB.setTitle("No open Keystones");
        }
        else if(args.length < 2) {
            errorEB.setTitle("No Keystone ID given!");
        }
        else if(ksh.getAvailableKeystones().get(args[1]).isCompleted()) {
            generalEB.setTitle("Keystone " + args[1] + " already completed, no need to leaveKeystone.");
        }
        else {
            Keystone ks = ksh.getAvailableKeystones().get(args[1]);
            if (!(args.length < 3) && args[2].equals("F")) {
                ks.leave(event.getMember().getUser().getId());
            }
            else if(ks.getCreator() == event.getMember().getUser()) {
                errorEB.setTitle("You are the creator of the Keystone " + args[1] + "!");
                errorEB.setDescription("If you realy want to leaveKeystone this Keystone addKeystone a 'F' after the command.");
                Message msg = event.getTextChannel().sendMessage(errorEB.build()).complete();
                UTILS.clearMessage(msg, 5000);
            }
            else {
                ks.leave(event.getMember().getUser().getId());
            }
            ksh.saveKeystonesToFile();
            ks.updateMessage(keystoneEB, event.getGuild());
        }
    }

    /**
     * function to create a new Keystone in the event channel
     * @param event - is the event which this Command is listening to
     * @param ksh - is the KeystoneHandler of this specific guild / Discord-Server
     */
    private void listKeystones(MessageReceivedEvent event, KeystoneHandler ksh) {
        if(ksh.getAvailableKeystones().isEmpty()) {
            generalEB.setTitle("No open Keystones");
        }
        else {
            generalEB.setTitle("List of Keystones:");
            for (Keystone key : ksh.getAvailableKeystones().values()) {
                generalEB.addField(key.getFullName() + " +" + key.getLevel(), "ID: " + key.getId(), false);
            }
        }
        Message msg = event.getTextChannel().sendMessage(generalEB.build()).complete();
        UTILS.clearMessage(msg, 10000);
    }


    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        setEmbed(event.getJDA().getSelfUser().getAvatarUrl());

        KeystoneHandler ksh = keystoneHandlerHashMap.get(event.getGuild());

        // -- NO COMMAND
        if (args.length < 1) {
            inputError("general", event.getTextChannel());
        }
        else {
            switch(args[0]) {
                // -- ADD --
                case("add"):
                case("create"):
                    addKeystone(args, event, ksh);
                    break;
                // -- ALERT --
                case("alert"):
                case("notify"):
                    alertKeystone(args, event, ksh);
                    break;
                // -- CLEAR --
                case("clear"):
                    clearKeystones(args, event, ksh);
                    break;
                // -- COMPLETE --
                case("complete"):
                    if(!(args.length < 2) && ksh.containsKeystone(args[1])) {
                        Keystone ks = ksh.getAvailableKeystones().get(args[1]);
                        ksh.unpinKeystone(args[1]);
                        ks.setCompleted(true);
                        ks.updateMessage(keystoneEB, event.getGuild());
                        ksh.saveKeystonesToFile();
                    }
                    break;
                // -- DELETE --
                case("delete"):
                    if(!(args.length < 2) && event.getMember().getRoles().contains(adminRole)) {
                        ksh.deleteKeyStone(args[1]);
                        ksh.saveKeystonesToFile();
                    }
                    break;
                // -- EDIT -- @TODO: ADD THE EDIT FEATURE
                case("edit"):
                    errorEB.setTitle("Feature not available yet");
                    break;
                // -- JOIN --
                case("join"):
                    joinKeystone(args, event, ksh);
                    break;
                // -- LEAVE --
                case("leave"):
                    leaveKeystone(args, event, ksh);
                    break;
                // -- LIST --
                case("list"):
                    listKeystones(event, ksh);
                    break;
                // -- UPDATE --
                case("update"):
                    if(!(args.length < 2)) {
                        ksh.updateKeystone(args[1], keystoneEB);
                        ksh.saveKeystonesToFile();
                    }
                    break;
                default:
                    inputError("general", event.getTextChannel());
            }
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
