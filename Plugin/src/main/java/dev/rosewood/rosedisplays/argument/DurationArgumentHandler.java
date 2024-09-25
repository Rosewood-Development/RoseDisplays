package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosedisplays.util.TimeUtils;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;

public class DurationArgumentHandler extends ArgumentHandler<Long> {

    public DurationArgumentHandler() {
        super(Long.class);
    }

    @Override
    public Long handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();

        long duration = TimeUtils.getDuration(input);
        if (duration == -1)
            throw new HandledArgumentException("argument-handler-duration", StringPlaceholders.of("input", input));

        return duration;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return List.of("500ms", "10s", "1m", "1s500ms");
    }

}
