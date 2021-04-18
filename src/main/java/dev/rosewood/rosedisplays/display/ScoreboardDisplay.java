package dev.rosewood.rosedisplays.display;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.data.DataSource;
import dev.rosewood.rosedisplays.util.ImageUtil;
import java.awt.Color;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardDisplay extends Display {

    private final static AtomicInteger SCOREBOARD_ID = new AtomicInteger();

    private Scoreboard scoreboard;
    private Objective objective;

    public ScoreboardDisplay(DataSource dataSource, Location location) {
        super(DisplayType.SCOREBOARD, false, dataSource, location);
    }

    @Override
    protected void create(Set<Player> players, int[] frameData) {
        Bukkit.getScheduler().runTask(RoseDisplays.getInstance(), () -> {
            if (this.scoreboard == null) {
                this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                this.objective = this.scoreboard.registerNewObjective("rd-" + SCOREBOARD_ID.getAndIncrement(), "dummy", this.dataSource.getName());
                this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                for (int i = 0; i < this.height; i++) {
                    Team team = this.scoreboard.registerNewTeam("SLOT_" + i);
                    String entry = ChatColor.values()[i].toString();
                    team.addEntry(entry);
                    this.objective.getScore(entry).setScore(15 - i);
                }
            }

            for (Player player : players)
                player.setScoreboard(this.scoreboard);

            this.render(players, frameData);
        });
    }

    @Override
    protected void render(Set<Player> players, int[] frameData) {
        Bukkit.getScheduler().runTask(RoseDisplays.getInstance(), () -> {
            for (int y = 0; y < this.height; y++) {
                Color lastColor = null;
                StringBuilder line = new StringBuilder();
                for (int x = 0; x < Math.min(64, this.width); x++) {
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

                Team team = this.scoreboard.getTeam("SLOT_" + y);
                if (team != null)
                    team.setSuffix(line.toString());
            }
        });
    }

    @Override
    protected void destroy(Set<Player> players) {
        Bukkit.getScheduler().runTaskLater(RoseDisplays.getInstance(), () -> {
            for (Player player : players)
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }, 20);
    }

}
