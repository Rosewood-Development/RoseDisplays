package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;
import java.util.stream.IntStream;

public class HologramLineArgumentHandler extends ArgumentHandler<HologramLine> {

    public HologramLineArgumentHandler() {
        super(HologramLine.class);
    }

    @Override
    public HologramLine handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        Hologram hologram = context.get(Hologram.class);
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

        return lines.get(lineNumber - 1);
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        Hologram hologram = context.get(Hologram.class);
        if (hologram == null)
            return List.of();

        List<HologramLine> lines = hologram.getLines();
        if (lines.isEmpty())
            return List.of();

        return IntStream.rangeClosed(1, lines.size()).mapToObj(String::valueOf).toList();
    }

}
