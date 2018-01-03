package Utils;

import AutoChannelManager.AutoChannelHandler;
import Commands.Admin.CommandAutoChannel;
import Commands.Everyone.CommandKey;
import KeystoneManager.KeystoneHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

import java.util.Timer;
import java.util.TimerTask;

public class UTILS {

    public static void clearMessage(Message msg, int time) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, time);
    }

    public static int getInt (String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isAdmin(Member member) {
        for (Role role: member.getRoles()) {
            if(role.getName().equals(CONFIG.ADMINROLE_NAME)){
                return true;
            }
        }
        return false;
    }

    public static void loadKeystoneManager(Guild guild) {
        KeystoneHandler ksh = new KeystoneHandler(guild);
        ksh.loadKeystonesFromFile();
        ksh.checkGuildRoles();
        CommandKey.addKeystoneHandler(guild, ksh);
    }

    public static void loadAutoChannelManager(Guild guild) {
        AutoChannelHandler ach = new AutoChannelHandler(guild);
        ach.loadAutoChannelsFromFile();
        CommandAutoChannel.addAutoChannelHandler(guild, ach);
    }

    public static void checkForAdminRole(Guild guild) {
        boolean addRole = true;
        for(Role role : guild.getRoles()) {
            if(role.getName().equals(CONFIG.ADMINROLE_NAME)) {
                addRole = false;
            }
        }
        if(addRole) {
            guild.getController().createRole().setName(CONFIG.ADMINROLE_NAME).queue();
        }
    }
}
