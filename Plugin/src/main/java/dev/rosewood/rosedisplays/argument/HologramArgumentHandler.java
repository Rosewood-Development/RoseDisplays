package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.manager.HologramManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;

public class HologramArgumentHandler extends ArgumentHandler<Hologram> {

    private final RosePlugin rosePlugin;

    public HologramArgumentHandler(RosePlugin rosePlugin) {
        super(Hologram.class);
        this.rosePlugin = rosePlugin;
    }

    @Override
    public Hologram handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        Hologram hologram = this.rosePlugin.getManager(HologramManager.class).getHologram(input);
        if (hologram == null)
            throw new HandledArgumentException("argument-handler-hologram", StringPlaceholders.of("input", input));

        return hologram;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return this.rosePlugin.getManager(HologramManager.class).getHologramNames();
    }

}
