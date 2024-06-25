package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;

public class RangedArgumentHandler<T extends Number> extends ArgumentHandler<T> {

    private final ArgumentHandler<T> delegate;
    private final double min;
    private final double max;
    private final boolean decimals;

    public RangedArgumentHandler(ArgumentHandler<T> delegate, int min, int max) {
        super(delegate.getHandledType());
        this.delegate = delegate;
        this.min = min;
        this.max = max;
        this.decimals = false;
    }

    public RangedArgumentHandler(ArgumentHandler<T> delegate, double min, double max) {
        super(delegate.getHandledType());
        this.delegate = delegate;
        this.min = min;
        this.max = max;
        this.decimals = true;
    }

    @Override
    public T handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        T output = this.delegate.handle(context, argument, inputIterator);
        double outputValue;
        if (this.decimals) {
            outputValue = output.doubleValue();
        } else {
            outputValue = output.intValue();
        }
        if (outputValue < this.min || outputValue > this.max)
            throw new HandledArgumentException("argument-handler-range", StringPlaceholders.of("input", outputValue, "min", this.min, "max", this.max));
        return output;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        String builder = (argument.optional() ? "[" : "<") +
                this.min + " to " + this.max +
                (argument.optional() ? "]" : ">");
        return List.of(builder);
    }

}
