package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import java.util.List;

public class HologramPropertyValueArgumentHandler extends ArgumentHandler<Object> {

    public HologramPropertyValueArgumentHandler() {
        super(Object.class);
    }

    @Override
    public Object handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        return this.getArgumentHandler(context).handle(context, argument, inputIterator);
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return this.getArgumentHandler(context).suggest(context, argument, args);
    }

    private ArgumentHandler<?> getArgumentHandler(CommandContext context) {
        HologramProperty<?> hologramProperty = context.get(HologramProperty.class);
        if (hologramProperty == null)
            throw new IllegalStateException("HologramPropertyValue argument handler called without a valid HologramProperty");
        return hologramProperty.getArgumentHandler();
    }

}
