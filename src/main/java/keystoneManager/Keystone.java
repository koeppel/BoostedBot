package keystoneManager;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import utils.STATIC;

import java.util.HashMap;

public class Keystone {

    private boolean completed = false;
    private boolean alerted = false;
    private Message msg;
    private String id;
    private String channelId;
    private int level;
    private String name;
    private String fullName;
    private boolean sellRun;

    private String authorName;
    private String authorUrl;

    private User creator;
    private String creatorName;
    private String creatorUrl;
    private HashMap<String, String> dungeons = STATIC.DUNGEONS;
    private HashMap<String, String> dungeon_images = STATIC.DUNGEON_IMAGE;
    private HashMap<String, String> tank = new HashMap<>();
    private HashMap<String, String> heal = new HashMap<>();
    private HashMap<String, String> dps  = new HashMap<>();
    private HashMap<String, HashMap<String, String>> roles = new HashMap<String, HashMap<String, String>>() {{
        put("TANK", tank);
        put("HEAL", heal);
        put("DPS", dps);
    }};

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean hasMember(Member member) {
        for (HashMap<String, String> role : this.roles.values()) {
            if(role.containsValue(member.getUser().getName())) {
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    public boolean isAlerted() {
        return alerted;
    }

    public void setAlerted(boolean alerted) {
        this.alerted = alerted;
    }

    @JsonIgnore public boolean isFull() {
        return (this.tank.size() == 1 && this.heal.size() == 1 && this.dps.size() == 3);
    }

    public String getName() {
        return name;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorUrl() {
        return creatorUrl;
    }

    public void setCreatorUrl(String creatorUrl) {
        this.creatorUrl = creatorUrl;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    @JsonIgnore public User getCreator() {
        return creator;
    }

    @JsonIgnore public void setCreator(User creator) {
        this.creator = creator;
        this.creatorName = creator.getName();
        this.creatorUrl = creator.getAvatarUrl();
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public HashMap<String, String> getDps() {
        return dps;
    }

    public void setDps(HashMap<String, String> dps) {
        this.dps = dps;
    }

    public HashMap<String, String> getTank() {
        return tank;
    }

    public void setTank(HashMap<String, String> tank) {
        this.tank = tank;
    }

    public HashMap<String, String> getHeal() {
        return heal;
    }

    public void setHeal(HashMap<String, String> heal) {
        this.heal = heal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSellRun() {
        return sellRun;
    }

    public HashMap<String, HashMap<String, String>> getRoles() {
        return roles;
    }

    public void setRoles(HashMap<String, HashMap<String, String>> roles) {
        this.roles = roles;
    }

    public int getLevel() {
        return level;
    }

    public String getFullName() {return this.dungeons.get(this.name);}

    public void setFullName() {
        this.fullName = this.dungeons.get(this.name);
    }

    public void alertMembers(Guild guild, User user) {
        for (HashMap<String, String> role: roles.values()) {
            for (Member member : guild.getMembers()) {
                if(role.values().contains(member.getUser().getName())){
                    PrivateChannel pc = member.getUser().openPrivateChannel().complete();
                    pc.sendMessage("User " + user.getName() + " wants to play " + this.fullName + " " + this.level
                    + " check out the Discord Server " + guild.getName() + " for more information!").queue();
                }
            }
        }
    }

    public void delete (Guild g) {
        try {
            g.getTextChannelById(this.channelId).getMessageById(this.id).complete().delete().queue();
        }
        catch(NumberFormatException e) {
            System.out.println("Error: The specified ID is not a valid snowflake");
        }
    }

    public void join (User user, String role) {
        this.roles.get(role).put(user.getId(), user.getName());
    }

    public void leave (String user) {
        for (String userRole : this.roles.keySet()) {
            if(this.roles.get(userRole).containsKey(user)) {
                this.roles.get(userRole).remove(user);
            }
        }
    }

    public void updateMessage(EmbedBuilder eb, Guild g) {
        this.msg = g.getTextChannelById(this.channelId).getMessageById(this.id).complete();
        if (this.isCompleted()) {
            eb.setThumbnail(STATIC.COMPLETEDURL);
        }
        else {
            eb.setThumbnail(STATIC.KEYSTONEURL);
        }

        eb.setAuthor(this.authorName, null, this.authorUrl);

        eb.setTitle(this.dungeons.get(this.name) + " +" + level);
        // Checking for Creator == Null because it might be unset after loading from file
        eb.setFooter("Key created by " + this.creatorName, this.creatorUrl);
        eb.setDescription("ID: *" + this.id + "*");
        eb.setImage(dungeon_images.get(this.name));
        if(!eb.getFields().isEmpty()){
            eb.clearFields();
        }
        for (String role : roles.keySet()) {
            try {
                if(roles.get(role).values().isEmpty()) {
                    eb.addField(getRoleIcon(role) + role + getRoleIcon(role), "No " + role + " joined yet!", false);
                }
                else {
                    if(role.equals("DPS")) {
                        String tempDPS = "";
                        for (String user : roles.get(role).values()) {
                            tempDPS += "\n" + user;
                        }
                        eb.addField(getRoleIcon(role) + roles.get(role).size() + "/3 DPS" +getRoleIcon(role), tempDPS, false);
                    }
                    else {
                        eb.addField(getRoleIcon(role) + "1/1 " + role + getRoleIcon(role), roles.get(role).values().iterator().next() , false);
                    }
                }
            }
            catch(NullPointerException e) {
                eb.addField(getRoleIcon(role) + role + getRoleIcon(role), "No " + role + " joined yet!", false);
            }
        }

        this.msg.editMessage(eb.build()).queue();
    }

    @JsonCreator public Keystone(
            @JsonProperty("name") String name,
            @JsonProperty("level") int level,
            @JsonProperty("id") String id,
            @JsonProperty("sellRun") boolean sellRun,
            @JsonProperty("fullName") String fullName,
            @JsonProperty("channelId") String channelId) {
        this.name = name;
        this.level = level;
        this.sellRun = sellRun;
        this.id = id;
        this.fullName = fullName;
    }

    public Keystone(String name, int level, Message msg, Boolean sellRun, String channelId) {
        this.name = name;
        this.level = level;
        this.msg = msg;
        this.id = msg.getId();
        this.sellRun = sellRun;
        this.channelId = channelId;
        this.setFullName();
    }

    private String getRoleIcon(String role) {
        switch (role){
            case "TANK": return ":shield:";
            case "HEAL" : return ":pill:";
            case "DPS" : return ":crossed_swords:";
            default: return "";
        }
    }
}
