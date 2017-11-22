package keystoneManager;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import utils.STATIC;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class Keystone {

    private boolean completed = false;
    private Message msg;
    private int level;
    private String name;
    private boolean sellrun;
    private User creator;
    private HashMap<String, String> dungeons = STATIC.DUNGEONS;
    private HashMap<String, String> dungeon_images = STATIC.DUNGEON_IMAGE;
    private List<User> tank;
    private List<User> heal;
    private List<User> dps;
    private HashMap<String, List<User>> roles = new HashMap<String, List<User>>() {{
        put("Tank", tank);
        put("Heal", heal);
        put("DPS", dps);
    }};

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getName() {
        return name;
    }


    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void updateMessage(EmbedBuilder eb) {
        eb.setTitle(this.dungeons.get(this.name) + " +" + level);
        eb.setDescription("Key created by " + this.creator.getName() + " " + new SimpleDateFormat("dd.MM.yyy:HH.mm.ss").format(new java.util.Date()));
        if(!eb.getFields().isEmpty()){
            eb.clearFields();
        }
        for (String role : roles.keySet()) {
            try {
                if(roles.get(role).isEmpty()) {
                    eb.addField(role, "No " + role + " joined yet!", false);
                }
                else {
                    if(role.equals("DPS")) {
                        String tempDPS = "";
                        for (User user : roles.get(role)) {
                            tempDPS += "\n" + user.getName();
                        }
                        eb.addField(getRoleIcon(role) + roles.get(role).size() + "/3 DPS" +getRoleIcon(role), tempDPS, false);
                    }
                    else {
                        eb.addField(getRoleIcon(role) + "1/1" + role + getRoleIcon(role), roles.get(role).get(0).getName() , false);
                    }
                }
            }
            catch(NullPointerException e) {
                eb.addField(getRoleIcon(role) + role + getRoleIcon(role), "No " + role + " joined yet!", false);
            }
        }

        this.msg.editMessage(eb.build()).queue();
    }

    public Keystone(String name, int level, Message msg, boolean sellrun) {
        this.msg = msg;
        this.name = name;
        this.level = level;
        this.sellrun = sellrun;
    }

    public Keystone(String name, int level, Message msg) {
        this(name, level, msg, false);
    }

    private String getRoleIcon(String role) {
        switch (role){
            case "Tank": return ":shield:";
            case"Heal" : return ":pill:";
            case"DPS" : return ":crossed_swords:";
            default: return "";
        }
    }
}
