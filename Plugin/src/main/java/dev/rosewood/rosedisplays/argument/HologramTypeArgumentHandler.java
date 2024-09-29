package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosedisplays.hologram.HologramType;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.ArrayList;
import java.util.List;

public class HologramTypeArgumentHandler extends ArgumentHandler<HologramType> {

    public HologramTypeArgumentHandler() {
        super(HologramType.class);
    }

    @Override
    public HologramType handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        return HologramType.REGISTRY.stream()
                .filter(x -> x.key().toString().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new HandledArgumentException("argument-handler-hologram-type", StringPlaceholders.of("input", input)));
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return new ArrayList<>(HologramType.REGISTRY.stringKeys());
    }

}
