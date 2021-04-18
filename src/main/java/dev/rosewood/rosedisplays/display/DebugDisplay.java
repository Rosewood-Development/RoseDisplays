package dev.rosewood.rosedisplays.display;

import dev.rosewood.rosedisplays.data.DataSource;
import dev.rosewood.rosedisplays.util.BlockHighlight;
import dev.rosewood.rosedisplays.util.DebugPayloadUtil;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DebugDisplay extends Display {

    public DebugDisplay(DataSource dataSource, Location location) {
        super(DisplayType.DEBUG, true, dataSource, location);
    }

    @Override
    protected void create(Set<Player> players, int[] frameData) {

    }

    @Override
    protected void render(Set<Player> players, int[] frameData) {
        int centerX = (int) (this.location.getX() - (this.width / 2D));
        int centerY = (int) (this.location.getY() + (this.height / 2D));
        int centerZ = (int) (this.location.getZ());

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int color = frameData[this.width * y + x];
                BlockHighlight highlight = new BlockHighlight(centerX + x, centerY - y, centerZ, color, " ", this.dataSource.getFrameDelay() + 100);
                DebugPayloadUtil.sendBlockHighlights(players, highlight);
            }
        }
    }

    @Override
    protected void destroy(Set<Player> players) {

    }

}
