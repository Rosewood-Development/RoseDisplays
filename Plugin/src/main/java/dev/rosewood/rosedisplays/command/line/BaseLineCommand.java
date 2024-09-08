package dev.rosewood.rosedisplays.command.line;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandInfo;

public class BaseLineCommand extends BaseRoseCommand {

    public BaseLineCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("line")
                .arguments(ArgumentsDefinition.builder()

                        .build())
                .build();
    }

}
