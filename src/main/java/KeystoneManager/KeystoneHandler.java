package KeystoneManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import Utils.CONFIG;
import Utils.STATIC;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class KeystoneHandler {
    // "Public" Variables
    private Guild guild;

    // Internal Variables
    private HashMap<Keystone, String> keystonesWithChannels = new HashMap<>();
    private HashMap<String, Keystone> keystonesById = new HashMap<>();
    private static final String filePath = CONFIG.SAVEPATH;
    private String fileNameKeystones;

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public boolean containsKeystone(String id) {
        return this.keystonesById.containsKey(id);
    }

    public Keystone getKeystone(String id) {
        return this.keystonesById.get(id);
    }

    public HashMap<String, Keystone> getAvailableKeystones () {
        HashMap<String, Keystone> keystones = new HashMap<>();

        for (Keystone ks: this.keystonesById.values() ) {
            if(!ks.isCompleted()) {
                keystones.put(ks.getId(), ks);
            }
        }
        return keystones;
    }

    // Constructor
    public KeystoneHandler(Guild guild) {
        this.guild = guild;
        this.fileNameKeystones = filePath + guild.getId() + "/keystones.dat";
    }

    // Adding Keystone to HashMaps / Creating new Keystone
    public void addKeyStone(Message msg, Keystone ks) {
        if (!this.keystonesById.containsValue(ks)) {
            this.keystonesById.put(msg.getId(), ks);
        }
        if(!this.keystonesWithChannels.containsKey(ks)) {
            this.keystonesWithChannels.put(ks, msg.getTextChannel().getId());
        }
    }

    // Alerting all Members in Keystone to join the Discord
    public EmbedBuilder alertKeystone(String id, MessageReceivedEvent event, EmbedBuilder errorEB) {
        if(this.containsKeystone(id)) {
            Keystone ks = this.getKeystone(id);
            if(!ks.isCompleted()) {
                if(ks.isFull() && !ks.isAlerted()){
                    // ALERT MEMBERS
                    ks.alertMembers(event.getGuild(), event.getAuthor());
                    ks.setAlerted(true);
                    errorEB.setTitle("Members alerted!");
                }
                else if(ks.isAlerted()) {
                    // ALREADY ALERTED MEMBERS
                    errorEB.setTitle("Already alerted members!");
                }
                else {
                    // NOT ENOUGH MEMBERS
                    errorEB.setTitle("Not enough members!");
                    for (String role : ks.getRoles().keySet()) {
                        if (role.equals("TANK") && ks.getRoles().get(role).size() != 1) {
                            errorEB.addField("TANK","1 TANK missing.",false);
                        }
                        else if(role.equals("HEAL") && ks.getRoles().get(role).size() != 1) {
                            errorEB.addField("HEAL","1 HEAL missing.", false);
                        }
                        else if(role.equals("DPS") && ks.getRoles().get(role).size() < 3) {
                            errorEB.addField("DPS",3 - ks.getRoles().get(role).size() + " DPS missing.", false);
                        }
                    }
                }
            }
            else {
                // KEYSTONE IS NOT AVAILABLE
                errorEB.setTitle("Keystone is not available!");
            }
        }
        else {
            // Keystone doesn't exist
            errorEB.setTitle("Keystone doesn't exist!");
        }
        return errorEB;
    }

    // Functions for some Guild and Member - Role things
    public void checkGuildRoles() {
        GuildController gc = new GuildController(this.guild);
        HashMap<String, Role> guildRolesByName = getGuildRolesByName(this.guild);
        // Add roles to Guild if not added jet
        if(!guildRolesByName.containsKey("HEAL")) {gc.createRole().setName("HEAL").queue();}
        if(!guildRolesByName.containsKey("TANK")) {gc.createRole().setName("TANK").queue();}
        if(!guildRolesByName.containsKey("DPS")) {gc.createRole().setName("DPS").queue();}
    }

    // Deleting all / all completed keystones
    public void clearKeystones(boolean all) {
        HashMap<String, Keystone> tempMap = new HashMap<>();
        for (Keystone ks : this.keystonesById.values()) {
            if(all) {
                tempMap.put(ks.getId(), ks);
            }
            else {
                if(ks.isCompleted()) {
                    tempMap.put(ks.getId(), ks);
                }
            }
        }

        for (Keystone ks : tempMap.values()) {
            this.keystonesById.remove(ks.getId(), ks);
            this.keystonesWithChannels.remove(ks, ks.getChannelId());
            ks.delete(this.guild);
        }

        this.saveKeystonesToFile();
    }

    // Deleting a single Keystone
    public void deleteKeyStone(String id) {
        Keystone ks = this.keystonesById.get(id);
        ks.delete(this.guild);
        this.keystonesById.remove(id);
        this.keystonesWithChannels.remove(ks);
    }

    // Joining User to a Keystone
    public void joinUser(String id, Member member, EmbedBuilder errorEB, EmbedBuilder keystoneEB) {
        String memberRole = "";
        String errorTitle = "";
        String errorDesc = "";
        HashMap<String, Role> memberRoles = getRelevantRoles(getMemberRolesByName(member));
        errorEB.clear();

        if(this.keystonesById.get(id).hasMember(member)) {
            errorTitle = member.getUser().getName() + " is already in keystone!";
        }
        else if(memberRoles.isEmpty()) {
            errorTitle = member.getUser().getName() + " has no relevant role! Get one before trying to enter a key!";
        }
        else if(memberRoles.size() > 1)  {
            errorTitle = "Member has more than 1 relevant Role!";
            for(String role : memberRoles.keySet()) {
                errorDesc += "\n" + role;
            }
            errorDesc += "\nChoose a role by typing '" + STATIC.PREFIX + "key join " + id + " [ROLE](DPS/TANK/HEAL)'";
        }
        else {
            memberRole = memberRoles.keySet().toString();
            memberRole = memberRole.replace("[", "");
            memberRole = memberRole.replace("]", "");
            this.keystonesById.get(id).join(member.getUser(), memberRole);
            this.keystonesById.get(id).updateMessage(keystoneEB, member.getGuild());
            errorTitle = "Joined Key!";
            errorDesc = member.getUser().getName() + " sucessfully joined Key " + id + " as " + memberRole;
            saveKeystonesToFile();
        }
        errorEB.setTitle(errorTitle);
        errorEB.setDescription(errorDesc);
    }

    // Joining User to a Keystone with given Role
    public void joinUserByRole(String id, Member member, String role, EmbedBuilder errorEB, EmbedBuilder keystoneEB) {
        HashMap<String, Role> memberRoles = getRelevantRoles(getMemberRolesByName(member));
        String ebTitle = "";
        String ebDesc = "";
        errorEB.clear();
        if(memberRoles.isEmpty()) {
            ebTitle = member.getNickname() + "has no relevant role! Get one before trying to enter a key!";
        }
        else if(memberRoles.containsKey(role)) {
            this.keystonesById.get(id).join(member.getUser(), role);
            this.keystonesById.get(id).updateMessage(keystoneEB, member.getGuild());
            saveKeystonesToFile();
            ebTitle = "Joined Key!";
            ebDesc = member.getUser().getName() + " sucessfully joined Key " + id + " as " + role;
        }
        else {
            ebTitle = "Given role " + role + " is not assigned to " + member.getUser().getName() + " make sure you are assinged to the role!";
        }
        errorEB.setTitle(ebTitle);
        errorEB.setDescription(ebDesc);
    }

    // Pin the Keystones Message
    public void pinKeystone(String id) {
        if(this.containsKeystone(id)) {
            Keystone ks = this.getKeystone(id);
            TextChannel tc = this.guild.getTextChannelById(this.keystonesWithChannels.get(ks));
            Message msg = tc.getMessageById(id).complete();
            msg.pin().queue();
            MessageHistory messageHistory = new MessageHistory(tc);
            messageHistory.retrievePast(1).complete();
            messageHistory.retrieveFuture(1).complete();

            for (Message message: messageHistory.getRetrievedHistory()) {
                message.delete().queue();
            }
        }
    }

    // UnPin the Keystones Message
    public void unpinKeystone(String id) {
        if(this.containsKeystone(id)) {
            Keystone ks = this.getKeystone(id);
            TextChannel tc = this.guild.getTextChannelById(this.keystonesWithChannels.get(ks));
            Message msg = tc.getMessageById(id).complete();
            msg.unpin().queue();
        }
    }

    // Update Key
    public void updateKeystone(String id, EmbedBuilder eb) {
        if(this.keystonesById.get(id).isCompleted()) {
            unpinKeystone(id);
        }
        else {
            pinKeystone(id);
        }
        this.keystonesById.get(id).updateMessage(eb, this.guild);
    }

    // Saving the <Keystone, String> (Keystone, TextChannelID) to a File in json format
    public void saveKeystonesToFile() {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> outPut = new HashMap<>();
        try {
            for (Map.Entry<Keystone, String> msg : keystonesWithChannels.entrySet()) {
                outPut.put(mapper.writeValueAsString(msg.getKey()), msg.getValue());
            }
        }
        catch(JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(this.fileNameKeystones)
            );
            out.writeObject(outPut);
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loading the <Keystone, String> (Keystone, TextChannelID) from a json File
    public void loadKeystonesFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(this.fileNameKeystones)
            );
            HashMap<String, String> temp = (HashMap<String, String>) in.readObject();
            for (Map.Entry<String, String> entry : temp.entrySet()) {
                Keystone ks = mapper.readValue(entry.getKey(), Keystone.class);
                this.keystonesWithChannels.put(ks, entry.getValue());
                this.keystonesById.put(ks.getId(), ks);
                ks.setChannelId(entry.getValue());
                System.out.println("Loaded Keystone: " + ks.getId() + " in Channel: " + entry.getValue());
            }
            in.close();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Role> getMemberRolesByName(Member member){
        HashMap<String, Role> memberRolesByName = new HashMap<>();
        for ( Role role : member.getRoles()) {
            memberRolesByName.put(role.getName(), role);
        }
        return memberRolesByName;
    }

    private HashMap<String, Role> getRelevantRoles(HashMap<String, Role> memberRolesByName) {
        HashMap<String, Role>relevantRoles = new HashMap<String, Role>();

        if (memberRolesByName.containsKey("HEAL")) {
            relevantRoles.put("HEAL", memberRolesByName.get("HEAL"));
        }
        if (memberRolesByName.containsKey("TANK")) {
            relevantRoles.put("TANK", memberRolesByName.get("TANK"));
        }
        if (memberRolesByName.containsKey("DPS")) {
            relevantRoles.put("DPS", memberRolesByName.get("DPS"));
        }

        return relevantRoles;
    }

    private HashMap<String, Role> getGuildRolesByName(Guild g){
        HashMap<String, Role> guildRolesByName = new HashMap<>();
        for ( Role role : g.getRoles()) {
            guildRolesByName.put(role.getName(), role);
        }
        return guildRolesByName;
    }
}
