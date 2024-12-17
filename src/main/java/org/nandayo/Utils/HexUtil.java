package org.nandayo.Utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexUtil {
    private static final Pattern PATTERN = Pattern.compile(
            "<(#[a-f0-9]{6}|aqua|black|blue|dark_(aqua|blue|gray|green|purple|red)|gray|gold|green|light_purple|red|white|yellow)>",
            Pattern.CASE_INSENSITIVE
    );

    private static String color(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        final Matcher matcher = PATTERN.matcher(text);

        while (matcher.find()) {
            try {
                final ChatColor chatColor = ChatColor.of(matcher.group(1));

                if (chatColor != null) {
                    text = text.replace(matcher.group(), chatColor.toString());
                }
            } catch (IllegalArgumentException ignored) { }
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public final static String title = "<#E195AB>";
    public final static String subTitle = "<#FFCCE1>";
    public final static String star = "<#FFF5D7>";
    public final static String white = "<#F2F9FF>";
    public final static String green = "<#cbf193>";
    public final static String red = "<#f1a495>";

    public static String parse(String s) {
        return color(s
                .replaceAll("\\{STAR}", star)
                .replaceAll("\\{TITLE}", title)
                .replaceAll("\\{SUBTITLE}", subTitle)
                .replaceAll("\\{GREEN}", green)
                .replaceAll("\\{WHITE}", white)
                .replaceAll("\\{RED}", red)
        );
    }
}
