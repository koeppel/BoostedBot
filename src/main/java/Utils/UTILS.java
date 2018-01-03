package Utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.xml.soap.Text;
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
}
