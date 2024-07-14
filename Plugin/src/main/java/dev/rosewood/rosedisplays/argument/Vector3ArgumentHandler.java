package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosedisplays.model.Vector3;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;

public class Vector3ArgumentHandler extends ArgumentHandler<Vector3> {

    public Vector3ArgumentHandler() {
        super(Vector3.class);
    }

    @Override
    public Vector3 handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String[] args = inputIterator.next(3);

        try {
            return new Vector3(Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]));
        } catch (Exception e) {
            throw new HandledArgumentException("argument-handler-vector3", StringPlaceholders.of("input", args[0] + " " + args[1] + " " + args[2]));
        }
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return List.of("<0 1 -0.5>", argument.parameter());
    }

}
