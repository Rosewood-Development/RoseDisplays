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
            throw new HandledArgumentException("argument-handler-range", StringPlaceholders.builder()
                    .add("input", outputValue)
                    .add("min", (this.decimals ? String.valueOf(this.min) : String.valueOf((int) this.min)))
                    .add("max", (this.decimals ? String.valueOf(this.max) : String.valueOf((int) this.max))).build());
        return output;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        String rangeString = (argument.optional() ? "[" : "<") +
                (this.decimals ? this.min : (int) this.min) + " to " + (this.decimals ? this.max : (int) this.max) +
                (argument.optional() ? "]" : ">");
        return List.of(rangeString);
    }

}
