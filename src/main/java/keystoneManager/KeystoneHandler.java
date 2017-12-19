package keystoneManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.GuildController;
import utils.CONFIG;

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

    public HashMap<String, Keystone> getAvailableKeystones () {
        HashMap<String, Keystone> keystones = new HashMap<>();

        for (Keystone ks: this.keystonesById.values() ) {
            if(!ks.isCompleted()) {
                keystones.put(ks.getId(), ks);
            }
        }
        return keystones;
    }

    public KeystoneHandler(Guild guild) {
        this.guild = guild;
        this.fileNameKeystones = filePath + guild.getId() + "_keystones.dat";
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

    // Deleting Keystones
    public void deleteKeyStone(String id) {
        Keystone ks = this.keystonesById.get(id);
        ks.delete(this.guild);
        this.keystonesById.remove(id);
        this.keystonesWithChannels.remove(ks);
    }

    // Joining Users to a Keystone
    public void joinUser(String id, Member member, EmbedBuilder eb) {
        String memberRole = "";
        HashMap<String, Role> memberRoles = getRelevantRoles(getMemberRolesByName(member));

        if(memberRoles.isEmpty()) {
            eb.setTitle(member.getNickname() + "has no relevant role! Get one before trying to enter a key!");
        }
        else if(memberRoles.size() > 1)  {
            eb.setTitle("Member has more than 1 relevant Role!");
            String desc = "";
            for(String role : memberRoles.keySet()) {
                desc += "\n" + role;
            }
            desc += "\nChoose a role by typing '!bbkey join " + id + " ROLE [DPS/TANK/HEAL]'";
            eb.setDescription(desc);
        }
        else {
            memberRole = memberRoles.keySet().toString();
            memberRole = memberRole.replace("[", "");
            memberRole = memberRole.replace("]", "");
            keystonesById.get(id).join(member.getUser(), memberRole);
            keystonesById.get(id).updateMessage(eb, member.getGuild());
            saveKeystonesToFile();
        }
    }

    public void joinUserByRole(String id, Member member, String role, EmbedBuilder eb) {
        HashMap<String, Role> memberRoles = getRelevantRoles(getMemberRolesByName(member));
        String ebTitle = "";
        if(memberRoles.isEmpty()) {
            ebTitle = member.getNickname() + "has no relevant role! Get one before trying to enter a key!";
        }
        else if(memberRoles.containsKey(role)) {
            keystonesById.get(id).join(member.getUser(), role);
            keystonesById.get(id).updateMessage(eb, member.getGuild());
            saveKeystonesToFile();
        }
        else {
            ebTitle = "Given role " + role + " is not assigned to " + member.getUser().getName() + " make sure you are assinged to the role!";
        }
        eb.setTitle(ebTitle);
    }

    // Update Key
    public void update(String id, EmbedBuilder eb) {
        this.keystonesById.get(id).updateMessage(eb, this.guild);
    }

    // Saving and Loading the <Keystone, String> (Keystone, TextChannelID) to a File in json format
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

    // Functions for some Guild and Member - Role things
    public void checkGuildRoles() {
        GuildController gc = new GuildController(this.guild);
        HashMap<String, Role> guildRolesByName = getGuildRolesByName(this.guild);
        // Add roles to Guild if not added jet
        if(!guildRolesByName.containsKey("HEAL")) {gc.createRole().setName("HEAL").queue();}
        if(!guildRolesByName.containsKey("TANK")) {gc.createRole().setName("TANK").queue();}
        if(!guildRolesByName.containsKey("DPS")) {gc.createRole().setName("DPS").queue();}
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

    private HashMap<String, Role> getMemberRolesByName(Member member){
        HashMap<String, Role> memberRolesByName = new HashMap<>();
        for ( Role role : member.getRoles()) {
            memberRolesByName.put(role.getName(), role);
        }
        return memberRolesByName;
    }

    private HashMap<String, Role> getGuildRolesByName(Guild g){
        HashMap<String, Role> guildRolesByName = new HashMap<>();
        for ( Role role : g.getRoles()) {
            guildRolesByName.put(role.getName(), role);
        }
        return guildRolesByName;
    }
}
