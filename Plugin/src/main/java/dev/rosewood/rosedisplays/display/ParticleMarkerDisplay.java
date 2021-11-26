package dev.rosewood.rosedisplays.display;

import dev.rosewood.rosedisplays.data.DataSource;
import dev.rosewood.rosedisplays.util.TextureToColorUtil;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ParticleMarkerDisplay extends Display {

    private final int spawnDelay = 0;
    private int timeUntilNextSpawn = 0;

    public ParticleMarkerDisplay(DataSource dataSource, Location location) {
        super(DisplayType.PARTICLE_MARKER, true, dataSource, location);
    }

    @Override
    protected void create(Set<Player> players, int[] frameData) {

    }

    @Override
    protected void render(Set<Player> players, int[] frameData) {
        if (this.timeUntilNextSpawn-- != 0)
            return;

        World world = this.location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("World cannot be null");

        double scale = 0.5;

        double centerX = this.location.getX() - (this.width / 2D * scale);
        double centerY = this.location.getY() + (this.height / 2D * scale);
        double centerZ = this.location.getZ();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int color = frameData[this.width * y + x];

                // Handle transparency
                int alpha = color >>> 24;
                if (alpha <= 127) {
                    continue;
                } else {
                    color &= 0x00FFFFFF;
                }

                Material material = TextureToColorUtil.getClosestMaterial(color);
                if (material != null)
                    world.spawnParticle(Particle.BLOCK_MARKER, centerX + x * scale, centerY - y * scale, centerZ, 1, 0, 0, 0, 0, material.createBlockData());
            }
        }

        this.timeUntilNextSpawn = this.spawnDelay;
    }

    @Override
    public void destroy(Set<Player> players) {

    }

}
