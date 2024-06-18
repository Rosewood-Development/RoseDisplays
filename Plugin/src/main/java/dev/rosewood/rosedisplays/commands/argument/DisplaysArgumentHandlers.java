package dev.rosewood.rosedisplays.commands.argument;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;

public final class DisplaysArgumentHandlers {

    private static final RosePlugin ROSE_PLUGIN = RoseDisplays.getInstance();
    public static final ArgumentHandler<Hologram> HOLOGRAM = new HologramArgumentHandler(ROSE_PLUGIN);
    public static final ArgumentHandler<HologramLine> HOLOGRAM_LINE = new HologramLineArgumentHandler();
    public static final ArgumentHandler<HologramProperty<?>> HOLOGRAM_PROPERTY = new HologramPropertyArgumentHandler();

    private DisplaysArgumentHandlers() { }

}
