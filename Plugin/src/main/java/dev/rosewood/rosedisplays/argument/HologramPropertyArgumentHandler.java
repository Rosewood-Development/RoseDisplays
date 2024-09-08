package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;

public class HologramPropertyArgumentHandler extends ArgumentHandler<HologramProperty<?>> {

    @SuppressWarnings("unchecked")
    public HologramPropertyArgumentHandler() {
        super((Class<HologramProperty<?>>) (Class<?>) HologramProperty.class);
    }

    @Override
    public HologramProperty<?> handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        Hologram hologram = context.get(Hologram.class);
        if (hologram == null)
            throw new IllegalStateException("HologramProperty argument handler called without a valid Hologram");

        return hologram.getProperties().getTag().stream()
                .filter(x -> x.getName().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new HandledArgumentException("argument-handler-hologram-property", StringPlaceholders.of("input", input)));
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        Hologram hologram = context.get(Hologram.class);
        if (hologram == null)
            return List.of();

        return hologram.getProperties().getTag().stream()
                .map(HologramProperty::getName)
                .toList();
    }

}
