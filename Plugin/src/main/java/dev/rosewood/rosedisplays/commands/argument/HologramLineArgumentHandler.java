package dev.rosewood.rosedisplays.commands.argument;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.manager.HologramManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;
import java.util.stream.IntStream;

public class HologramLineArgumentHandler extends RoseCommandArgumentHandler<HologramLine> {

    public HologramLineArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, HologramLine.class);
    }

    @Override
    protected HologramLine handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();

        Hologram hologram = argumentParser.getContextValue(Hologram.class);
        if (hologram == null)
            throw new IllegalStateException("HologramLine argument handler called without a valid Hologram");

        int lineNumber;
        try {
            lineNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new HandledArgumentException("argument-handler-hologram-line-numeric", StringPlaceholders.of("input", input));
        }

        List<HologramLine> lines = hologram.getLines();
        if (lineNumber < 1 || lineNumber > lines.size())
            throw new HandledArgumentException("argument-handler-hologram-line", StringPlaceholders.of("input", input));

        HologramLine line = lines.get(lineNumber - 1);
        argumentParser.setContextValue(HologramLine.class, line);
        return line;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String hologramName = argumentParser.previous();
        argumentParser.next();

        Hologram hologram = this.rosePlugin.getManager(HologramManager.class).getHologram(hologramName);
        if (hologram == null)
            return List.of();

        argumentParser.setContextValue(Hologram.class, hologram);

        List<HologramLine> lines = hologram.getLines();
        if (lines.isEmpty())
            return List.of();

        return IntStream.rangeClosed(1, lines.size()).mapToObj(String::valueOf).toList();
    }

}
