package dev.rosewood.rosedisplays.display;

import dev.rosewood.rosedisplays.data.DataSource;
import dev.rosewood.rosedisplays.util.ImageUtil;
import java.awt.Color;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ChatDisplay extends Display {

    public ChatDisplay(DataSource dataSource, Location location) {
        super(DisplayType.CHAT, true, dataSource, location);
    }

    @Override
    protected void create(Set<Player> players, int[] frameData) {

    }

    @Override
    protected void render(Set<Player> players, int[] frameData) {
        for (int y = 0; y < this.height; y++) {
            Color lastColor = null;
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < this.width; x++) {
                int rgba = frameData[this.width * y + x];
                // Handle transparency
                int alpha = rgba >>> 24;
                if (alpha <= 127) {
                    line.append(ChatColor.BLACK).append(ImageUtil.SPACE);
                    continue;
                } else {
                    rgba &= 0x00FFFFFF;
                }

                Color color = new Color(rgba & 0x00FFFFFF);
                if (!color.equals(lastColor))
                    line.append(ChatColor.of(color));
                line.append(ImageUtil.BLOCK);

                lastColor = color;
            }

            for (Player player : players)
                player.sendMessage(line.toString());
        }
    }

    @Override
    protected void destroy(Set<Player> players) {
        for (Player player : players)
            for (int i = 0; i < 100; i++)
                player.sendMessage("");
    }

}
