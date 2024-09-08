package dev.rosewood.rosedisplays.command;

import dev.rosewood.rosedisplays.argument.DisplaysArgumentHandlers;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.manager.HologramManager;
import dev.rosewood.rosedisplays.manager.LocaleManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;

public class DeleteCommand extends BaseRoseCommand {

    public DeleteCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Hologram hologram) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);
        HologramManager hologramManager = this.rosePlugin.getManager(HologramManager.class);

        if (hologramManager.deleteHologram(hologram.getName())) {
            localeManager.sendMessage(context.getSender(), "command-delete-success", StringPlaceholders.of("name", hologram.getName()));
        } else {
            throw new IllegalStateException("Hologram didn't exist anymore");
        }
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("delete")
                .descriptionKey("command-delete-description")
                .permission("rosedisplays.hologram")
                .arguments(ArgumentsDefinition.builder()
                        .required("hologram", DisplaysArgumentHandlers.HOLOGRAM)
                        .build())
                .build();
    }

}
