package dev.rosewood.rosedisplays.display;

import dev.rosewood.rosedisplays.data.DataSource;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ScoreboardDisplay extends Display {

    public ScoreboardDisplay(DataSource dataSource, Location location) {
        super(DisplayType.SCOREBOARD, false, dataSource, location);

        throw new IllegalStateException("Not implemented");
    }

    @Override
    protected void create(Set<Player> players, int[] frameData) {

    }

    @Override
    protected void render(Set<Player> players, int[] frameData) {

    }

    @Override
    protected void destroy(Set<Player> players) {

    }

}
