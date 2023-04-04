package dev.rosewood.rosedisplays.property;

import dev.rosewood.rosedisplays.hologram.HologramLineType;
import java.util.Map;

public interface HologramPropertyProvider {

    Map<String, HologramProperty<?>> getProperties(HologramLineType type);

    Map<String, HologramProperty<?>> getAllProperties();

}
