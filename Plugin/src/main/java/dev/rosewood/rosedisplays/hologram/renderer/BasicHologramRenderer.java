package dev.rosewood.rosedisplays.hologram.renderer;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import java.util.List;
import org.bukkit.Bukkit;

public class BasicHologramRenderer implements HologramRenderer {

    @Override
    public void render(Hologram hologram) {
        hologram.getLines().stream()
                .filter(HologramLine::isDirty)
                .forEach(line -> NMSAdapter.getHandler().sendHologramSpawnPacket(line, List.copyOf(Bukkit.getOnlinePlayers())));
    }

}
