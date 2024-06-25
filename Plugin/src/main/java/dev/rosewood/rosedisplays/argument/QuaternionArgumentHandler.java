package dev.rosewood.rosedisplays.argument;

import dev.rosewood.rosedisplays.model.Quaternion;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import java.util.List;

public class QuaternionArgumentHandler extends ArgumentHandler<Quaternion> {

    public QuaternionArgumentHandler() {
        super(Quaternion.class);
    }

    @Override
    public Quaternion handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        return new Quaternion(0, 0, 0, 0); // TODO
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return List.of("<unimplemented>"); // TODO
    }

}
