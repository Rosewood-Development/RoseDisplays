package dev.rosewood.rosedisplays.display;

import dev.rosewood.rosedisplays.data.DataSource;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ParticleDisplay extends Display {

    private boolean globalInterlace = false; // TODO: Configurable

    public ParticleDisplay(DataSource dataSource, Location location) {
        super(DisplayType.PARTICLE, true, dataSource, location);
    }

    @Override
    protected void create(Set<Player> players, int[] frameData) {

    }

    @Override
    protected void render(Set<Player> players, int[] frameData) {
        World world = this.location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("World cannot be null");

        double scale = 0.1; // TODO: Configurable
        float size = 2F; // TODO: Configurable

        double centerX = this.location.getX() - (this.width / 2D * scale);
        double centerY = this.location.getY() + (this.height / 2D * scale);
        double centerZ = this.location.getZ();

        boolean interlace = this.globalInterlace;

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                interlace = !interlace;
                if (!interlace)
                    continue;

                int color = frameData[this.width * y + x];

                // Handle transparency
                int alpha = color >>> 24;
                if (alpha <= 127) {
                    continue;
                } else {
                    color &= 0x00FFFFFF;
                }

                DustOptions dustOptions = new DustOptions(Color.fromRGB(color), size);
                world.spawnParticle(Particle.REDSTONE, centerX + x * scale, centerY - y * scale, centerZ, 1, 0, 0, 0, 0, dustOptions);
            }
        }

        this.globalInterlace = !this.globalInterlace;
    }

    @Override
    public void destroy(Set<Player> players) {

    }

}
