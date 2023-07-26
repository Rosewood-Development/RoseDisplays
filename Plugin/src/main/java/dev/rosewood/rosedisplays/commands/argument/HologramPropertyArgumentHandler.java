package dev.rosewood.rosedisplays.commands.argument;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;

public class HologramPropertyArgumentHandler extends RoseCommandArgumentHandler<HologramProperty<?>> {

    @SuppressWarnings("unchecked")
    public HologramPropertyArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, (Class<HologramProperty<?>>) (Class<?>) HologramProperty.class);
    }

    @Override
    protected HologramProperty<?> handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();

        Hologram hologram = argumentParser.getContextValue(Hologram.class);
        if (hologram == null)
            throw new IllegalStateException("HologramProperty argument handler called without a valid Hologram");

        HologramLine line = argumentParser.getContextValue(HologramLine.class);
        if (line == null)
            throw new IllegalStateException("HologramProperty argument handler called without a valid HologramLine");

        HologramProperty<?> property = HologramProperty.values(line.getType()).stream()
                .filter(p -> p.getName().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new HandledArgumentException("argument-handler-hologram-property", StringPlaceholders.of("input", input)));

        argumentParser.setContextValue(HologramProperty.class, property);
        return property;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        // TODO: Gets the previous argument before the subcommand, this is terrible, please fix this in RoseGarden somehow
        String lineValue = argumentParser.getContext().getArgs()[1];
        argumentParser.next();

        Hologram hologram = argumentParser.getContextValue(Hologram.class);
        if (hologram == null)
            return List.of();

        HologramLine line;
        try {
            List<HologramLine> lines = hologram.getLines();
            int lineIndex = Integer.parseInt(lineValue);
            if (lineIndex < 1 || lineIndex > lines.size())
                return List.of();

            line = lines.get(lineIndex - 1);
        } catch (NumberFormatException e) {
            return List.of();
        }

        argumentParser.setContextValue(HologramLine.class, line);
        return HologramProperty.values(line.getType()).stream().map(HologramProperty::getName).toList();
    }

}
