package dev.rosewood.rosedisplays.commands.argument;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.manager.HologramManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;

public class HologramArgumentHandler extends RoseCommandArgumentHandler<Hologram> {

    public HologramArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Hologram.class);
    }

    @Override
    protected Hologram handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();

        Hologram hologram = this.rosePlugin.getManager(HologramManager.class).getHologram(input);
        if (hologram == null)
            throw new HandledArgumentException("argument-handler-hologram", StringPlaceholders.of("input", input));

        argumentParser.setContextValue(Hologram.class, hologram);
        return hologram;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return this.rosePlugin.getManager(HologramManager.class).getHologramNames();
    }

}
