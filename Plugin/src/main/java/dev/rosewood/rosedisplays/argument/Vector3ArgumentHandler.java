package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosedisplays.model.Vector3;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import java.util.List;

public class Vector3ArgumentHandler extends ArgumentHandler<Vector3> {

    public Vector3ArgumentHandler() {
        super(Vector3.class);
    }

    @Override
    public Vector3 handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        return new Vector3(0, 0, 0); // TODO
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return List.of("<unimplemented>"); // TODO
    }

}
