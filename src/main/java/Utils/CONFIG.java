package Utils;


import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.RoleManager;
import net.dv8tion.jda.core.managers.RoleManagerUpdatable;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.core.requests.restaction.RoleAction;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class CONFIG {
    public static boolean RECONNECT = true;
    public static int TIMERMULTIPLYER = 5;
    public static String SAVEPATH = "SERVER_SETTINGS/";
    public static final String ADMINROLE_NAME = "admin";
}
