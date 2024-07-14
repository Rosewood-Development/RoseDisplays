package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;

public class QuaternionArgumentHandler extends ArgumentHandler<Quaternion> {

    public QuaternionArgumentHandler() {
        super(Quaternion.class);
    }

    @Override
    public Quaternion handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String[] args = inputIterator.next(4);

        try {
            return new Quaternion(Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
        } catch (Exception e) {
            throw new HandledArgumentException("argument-handler-quaternion", StringPlaceholders.of("input", args[0] + " " + args[1] + " " + args[2] + " " + args[3]));
        }
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return List.of("<0 1 0.5 -0.5>", argument.parameter());
    }

}
