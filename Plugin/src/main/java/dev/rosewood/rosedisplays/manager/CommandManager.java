package dev.rosewood.rosedisplays.manager;

import dev.rosewood.rosedisplays.command.BaseCommand;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;
import java.util.List;
import java.util.function.Function;

public class CommandManager extends AbstractCommandManager {

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Function<RosePlugin, BaseRoseCommand>> getRootCommands() {
        return List.of(BaseCommand::new);
    }

}
