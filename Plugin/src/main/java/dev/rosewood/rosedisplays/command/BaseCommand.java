package dev.rosewood.rosedisplays.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.HelpCommand;
import dev.rosewood.rosegarden.command.PrimaryCommand;
import dev.rosewood.rosegarden.command.ReloadCommand;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandInfo;

public class BaseCommand extends PrimaryCommand {

    public BaseCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("rosedisplays")
                .aliases("rd", "displays", "holo", "hologram")
                .arguments(ArgumentsDefinition.builder()
                        .optionalSub(
                                new CreateCommand(this.rosePlugin),
                                new DeleteCommand(this.rosePlugin),
                                new EditCommand(this.rosePlugin),
                                new HelpCommand(this.rosePlugin, this, false),
                                new ReloadCommand(this.rosePlugin)
                        ))
                .build();
    }

}
