package utils;

import javax.management.relation.Role;
import java.util.*;

public class STATIC {

    public static final String PREFIX = "!";
    public static final String ADMINROLE = "admin";
    public static final String COMPLETEDURL = "http://t5.rbxcdn.com/78a4211079921cd8604d573ea33f48c3";
    public static final String KEYSTONEURL = "http://wow.zamimg.com/images/wow/icons/large/inv_relics_hourglass.jpg";

    public static final HashMap<String, String> DUNGEONS = new HashMap<String, String>() {{
        put("ARC", "The Arcway");
        put("BRH", "Blackrook Hold");
        put("COE", "Cathedral of Eternal Night");
        put("COS", "Court of Stars");
        put("DHT", "Darkheart Thicket");
        put("EOA", "Eye of Azshara");
        put("HOV", "Halls of Valor");
        put("MOS", "Maw of Souls");
        put("NEL", "Neltharions Lair");
        put("RKL", "Return to Karazhan Lower");
        put("RKU", "Return to Karazhan Upper");
        put("SOT", "Seat of the Triumvirate");
        put("VOW", "Vaults of the Wardens");
    }};
    public static final HashMap<String, String> DUNGEON_IMAGE = new HashMap<String, String>() {{
        put("ARC", "https://wow.gamepedia.com/media/wow.gamepedia.com/f/f0/Arcway_loading_screen.jpg");
        put("BRH", "https://wow.gamepedia.com/media/wow.gamepedia.com/2/2a/Black_Rook_Hold_loading_screen_wide.jpg");
        put("COE", "https://wow.gamepedia.com/media/wow.gamepedia.com/d/d7/Cathedral_of_Eternal_Night_loading_screen.jpg");
        put("COS", "https://wow.gamepedia.com/media/wow.gamepedia.com/4/45/Court_of_Stars_loading_screen.jpg");
        put("DHT", "https://wow.gamepedia.com/media/wow.gamepedia.com/e/e7/Darkheart_Thicket_loading_screen.jpg");
        put("EOA", "https://wow.gamepedia.com/media/wow.gamepedia.com/7/75/Eye_of_Azshara_loading_screen.jpg");
        put("HOV", "https://wow.gamepedia.com/media/wow.gamepedia.com/d/df/Halls_of_Valor_loading_screen.jpg");
        put("MOS", "https://wow.gamepedia.com/media/wow.gamepedia.com/d/d2/Maw_of_Souls_loading_screen.jpg");
        put("NEL", "https://wow.gamepedia.com/media/wow.gamepedia.com/0/00/Neltharion%27s_Lair_loading_screen.jpg");
        put("RKL", "https://wow.gamepedia.com/media/wow.gamepedia.com/3/34/Return_to_Karazhan.jpg");
        put("RKU", "https://wow.gamepedia.com/media/wow.gamepedia.com/3/34/Return_to_Karazhan.jpg");
        put("SOT", "https://wow.gamepedia.com/media/wow.gamepedia.com/e/ec/Seat_of_the_Triumvirate_loading_screen.jpg");
        put("VOW", "https://wow.gamepedia.com/media/wow.gamepedia.com/7/7d/Vault_of_the_Wardens_loading_screen.jpg");
    }};
    public static final HashMap<String, String> CLASS_COLORS = new HashMap<String, String>() {{
        put("DK","#C41F3B");
        put("DH","#A330C9");
        put("DRUID","#FF7D0A");
        put("HUNTER","#ABD473");
        put("MAGE", "#69CCF0");
        put("MONK","#00FF96");
        put("PALADIN","#F58CBA");
        put("PRIEST","#FFFFFF");
        put("ROGUE","#FFF569");
        put("SHAMAN","#0070DE");
        put("WARLOCK","#9482C9");
        put("WARRIOR","#C79C6E");
    }};
}
