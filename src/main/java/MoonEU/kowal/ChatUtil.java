package MoonEU.kowal;

import org.bukkit.ChatColor;

public class ChatUtil {

    public static String fixColor(String message) {
        if (message == null || message.isEmpty()) return "";

        return ChatColor.translateAlternateColorCodes('&', message.replace(">>", "»").replace("<<", "«"));
    }

}
