package dev.rosewood.rosedisplays.manager;

import dev.rosewood.rosedisplays.commands.DisplaysCommandWrapper;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;
import java.util.List;

public class CommandManager extends AbstractCommandManager {

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Class<? extends RoseCommandWrapper>> getRootCommands() {
        return List.of(DisplaysCommandWrapper.class);
    }

    @Override
    public List<String> getArgumentHandlerPackages() {
        return List.of("dev.rosewood.rosedisplays.commands.argument");
    }

}
